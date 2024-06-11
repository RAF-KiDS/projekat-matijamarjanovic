package servent.handler.failProofHandlers;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.failProof.BackUpFoundTellMessage;
import servent.message.util.MessageUtil;

import java.util.Map;

public class BackUpFoundTellHandler implements MessageHandler {
    private Message clientMessage;
    public BackUpFoundTellHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.BACK_UP_FOUND_TELL){

            try{
                if(clientMessage.getMessageText().split(",").length != 2){
                    AppConfig.timestampedErrorPrint("BackUpFoundTellHandler got a message with bad text: " + clientMessage.getMessageText());
                    return;
                }
                String parts[] = clientMessage.getMessageText().split(",");

                int targetKey = Integer.parseInt(parts[0]);
                int originalOwnerKey = Integer.parseInt(parts[1]);

                if(AppConfig.myServentInfo.getChordId() == targetKey){
                   AppConfig.timestampedStandardPrint("Backup found for key: " + originalOwnerKey + ": ");
                   Map<Integer, Object> values = ((BackUpFoundTellMessage)clientMessage).getBackedUpFiles();

                   for (Map.Entry<Integer, Object> entry : values.entrySet()) {
                       AppConfig.timestampedStandardPrint("Key: " + entry.getKey() + " File: " + entry.getValue());
                   }
                }else {
                    //salji dalje
                    BackUpFoundTellMessage backUpFoundTellMessage = new BackUpFoundTellMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodeForKey(targetKey).getListenerPort(), ((BackUpFoundTellMessage)clientMessage).getBackedUpFiles(), parts[0], originalOwnerKey);
                    MessageUtil.sendMessage(backUpFoundTellMessage);
                }
            }catch (NumberFormatException e) {
                AppConfig.timestampedErrorPrint("BackUpFoundTellHandler got a message with bad text: " + clientMessage.getMessageText());
            }
        }else {
            AppConfig.timestampedErrorPrint("BackUpFoundTellHandler got a message that is not BACK_UP_FOUND_TELL");
        }
    }
}
