package servent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.AppConfig;
import app.Cancellable;
import servent.handler.*;
import servent.handler.friendRequestHandlers.FriendAcceptHandler;
import servent.handler.friendRequestHandlers.FriendRequestHandler;
import servent.handler.quorumHandlers.QuorumRequestHandler;
import servent.handler.quorumHandlers.QuorumResponseHandler;
import servent.handler.removeFileHandlers.RemoveFileFromCreatorHandler;
import servent.handler.removeFileHandlers.RemoveFileHandler;
import servent.handler.viewFiles.ViewFilesHandler;
import servent.handler.viewFiles.ViewFilesTellHandler;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class SimpleServentListener implements Runnable, Cancellable {

	private volatile boolean working = true;
	
	public SimpleServentListener() {
		
	}

	/*
	 * Thread pool for executing the handlers. Each client will get it's own handler thread.
	 */
	private final ExecutorService threadPool = Executors.newWorkStealingPool();
	
	@Override
	public void run() {
		ServerSocket listenerSocket = null;
		try {
			listenerSocket = new ServerSocket(AppConfig.myServentInfo.getListenerPort(), 100);
			/*
			 * If there is no connection after 1s, wake up and see if we should terminate.
			 */
			listenerSocket.setSoTimeout(1000);
		} catch (IOException e) {
			AppConfig.timestampedErrorPrint("Couldn't open listener socket on: " + AppConfig.myServentInfo.getListenerPort());
			System.exit(0);
		}
		
		
		while (working) {
			try {
				Message clientMessage;
				
				Socket clientSocket = listenerSocket.accept();
				
				//GOT A MESSAGE! <3
				clientMessage = MessageUtil.readMessage(clientSocket);
				
				MessageHandler messageHandler = new NullHandler(clientMessage);
				
				/*
				 * Each message type has it's own handler.
				 * If we can get away with stateless handlers, we will,
				 * because that way is much simpler and less error prone.
				 */
				switch (clientMessage.getMessageType()) {
				case NEW_NODE:
					messageHandler = new NewNodeHandler(clientMessage);
					break;
				case WELCOME:
					messageHandler = new WelcomeHandler(clientMessage);
					break;
				case SORRY:
					messageHandler = new SorryHandler(clientMessage);
					break;
				case UPDATE:
					messageHandler = new UpdateHandler(clientMessage);
					break;
				case PUT:
					messageHandler = new PutHandler(clientMessage);
					break;
				case ASK_GET:
					messageHandler = new AskGetHandler(clientMessage);
					break;
				case TELL_GET:
					messageHandler = new TellGetHandler(clientMessage);
					break;
				case QUORUM_REQUEST:
					messageHandler = new QuorumRequestHandler(clientMessage);
					break;
				case QUORUM_RESPONSE:
					messageHandler = new QuorumResponseHandler(clientMessage);
					break;
				case FRIEND_REQUEST:
					messageHandler = new FriendRequestHandler(clientMessage);
					break;
				case FRIEND_ACCEPT:
					messageHandler = new FriendAcceptHandler(clientMessage);
					break;
				case REMOVE_FILE:
					messageHandler = new RemoveFileHandler(clientMessage);
					break;
				case REMOVE_FILE_CREATOR:
					messageHandler = new RemoveFileFromCreatorHandler(clientMessage);
					break;
				case VIEW_FILES:
					messageHandler = new ViewFilesHandler(clientMessage);
					break;
				case VIEW_FILES_TELL:
					messageHandler = new ViewFilesTellHandler(clientMessage);
					break;
				case POISON:
					break;
				}
				
				threadPool.submit(messageHandler);
			} catch (SocketTimeoutException timeoutEx) {
				//Uncomment the next line to see that we are waking up every second.
//				AppConfig.timedStandardPrint("Waiting...");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stop() {
		this.working = false;
	}

}
