package servent.handler.failProofHandlers;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.failProof.RemoveNodeMessage;

public class RemoveNodeHandler implements MessageHandler {
    private Message clientMessage;

    public RemoveNodeHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.REMOVE_NODE) {
            AppConfig.chordState.removeNode(((RemoveNodeMessage)clientMessage).getToRemovePort());
        }else {
            AppConfig.timestampedErrorPrint("Remove node handler got a message that is not REMOVE_NODE");
        }
    }
}
