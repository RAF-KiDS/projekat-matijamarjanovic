package servent.handler.failProofHandlers;

import app.AppConfig;
import app.failProof.BuddySystem;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.failProof.PongMessage;
import servent.message.util.MessageUtil;

public class PongHandler implements MessageHandler {

    private Message clientMessage;

    public PongHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.PONG){

            if(BuddySystem.getInstance().getPredecessorPort() == clientMessage.getSenderPort()){
                BuddySystem.getInstance().setPongedPredecessor(true);
            }else if(BuddySystem.getInstance().getSuccessorPort() == clientMessage.getSenderPort()){
                BuddySystem.getInstance().setPongedSuccessor(true);
            }

        }else {
            AppConfig.timestampedErrorPrint("Ping handler got a message that is not PONG");
        }
    }
}
