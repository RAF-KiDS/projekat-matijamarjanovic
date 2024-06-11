package servent.handler.failProofHandlers;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.BackUpMessage;
import servent.message.Message;
import servent.message.MessageType;

import java.util.Map;

public class BackUpHandler implements MessageHandler {

    private Message clientMessage;
    public BackUpHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.BACK_UP){
            Map<Integer, Object> filesToBackUp = ((BackUpMessage)clientMessage).getFilesToBackUp();
            int chordID = Integer.parseInt(clientMessage.getMessageText());

            AppConfig.chordState.insertBackUp(filesToBackUp, chordID);
        }else {
            AppConfig.timestampedErrorPrint("BackUp handler got a message that is not BACK_UP");
        }
    }
}
