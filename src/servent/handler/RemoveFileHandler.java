package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.*;
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
                if (AppConfig.chordState.isKeyMine(key)) {
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

                }

            } catch (NumberFormatException e) {
                AppConfig.timestampedErrorPrint("Got ask get with bad text: " + clientMessage.getMessageText());
            }

        } else {
            AppConfig.timestampedErrorPrint("Ask get handler got a message that is not ASK_GET");
        }
    }
}
