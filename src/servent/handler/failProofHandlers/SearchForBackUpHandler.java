package servent.handler.failProofHandlers;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.failProof.SearchForBackUpMessage;

public class SearchForBackUpHandler implements MessageHandler {
    private Message clientMessage;
    public SearchForBackUpHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.SEARCH_FOR_BACK_UP) {
            try{
                int key = Integer.parseInt(clientMessage.getMessageText());
                AppConfig.chordState.searchForBackup(key, ((SearchForBackUpMessage)clientMessage).getWhoAsked());
            } catch (NumberFormatException e) {
                AppConfig.timestampedErrorPrint("Got SEARCH_FOR_BACK_UP message with bad text: " + clientMessage.getMessageText());
            }
        }else {
            AppConfig.timestampedErrorPrint("BackUp handler got a message that is not BACK_UP");
        }
    }
}
