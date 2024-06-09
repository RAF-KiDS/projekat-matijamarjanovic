package servent.handler.removeFileHandlers;

import app.AppConfig;
import app.ServentInfo;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.removeFile.RemoveFileMessage;
import servent.message.util.MessageUtil;

import java.util.List;
import java.util.Map;

public class RemoveFileFromCreatorHandler implements MessageHandler {
    private Message clientMessage;

    public RemoveFileFromCreatorHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.REMOVE_FILE_CREATOR) {
            try {
                int key = Integer.parseInt(clientMessage.getMessageText());
                AppConfig.chordState.removeValueFromCreator(key);

            } catch (NumberFormatException e) {
                AppConfig.timestampedErrorPrint("Got ask get with bad text: " + clientMessage.getMessageText());
            }

        } else {
            AppConfig.timestampedErrorPrint("Ask get handler got a message that is not REMOVE_FILE_CREATOR");
        }
    }
}
