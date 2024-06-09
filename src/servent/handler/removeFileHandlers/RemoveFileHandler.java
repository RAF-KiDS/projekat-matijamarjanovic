package servent.handler.removeFileHandlers;

import app.AppConfig;
import app.ServentInfo;
import servent.handler.MessageHandler;
import servent.message.*;
import servent.message.removeFile.RemoveFileMessage;
import servent.message.util.MessageUtil;

import java.util.Map;

public class RemoveFileHandler implements MessageHandler {

    private Message clientMessage;

    public RemoveFileHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.REMOVE_FILE) {
            try {
                int key = Integer.parseInt(clientMessage.getMessageText());
/*                if (AppConfig.chordState.isKeyMine(key)) {
                    Map<Integer, Object> valueMap = AppConfig.chordState.getValueMap();
                    Object value = -1;

                    if (valueMap.containsKey(key)) {
                        value = valueMap.remove(key);
                    }

                    AppConfig.chordState.removeValue(key);

                    if((Integer) value == -1){
                        AppConfig.timestampedStandardPrint("No such key to remove: " + key);
                    }
                } else {
                    ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(key);
                    RemoveFileMessage rfm = new RemoveFileMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), String.valueOf(key));
                    MessageUtil.sendMessage(rfm);

                }*/
                AppConfig.chordState.removeValue(key);


            } catch (NumberFormatException e) {
                AppConfig.timestampedErrorPrint("Got remove file with bad text: " + clientMessage.getMessageText());
            }

        } else {
            AppConfig.timestampedErrorPrint("Ask get handler got a message that is not REMOVE_FILE");
        }
    }
}
