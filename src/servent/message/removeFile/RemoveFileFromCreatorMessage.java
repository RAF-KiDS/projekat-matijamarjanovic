package servent.message.removeFile;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class RemoveFileFromCreatorMessage extends BasicMessage {

    private static final long serialVersionUID = 3899854286642127636L;
    public RemoveFileFromCreatorMessage(int senderPort, int receiverPort, String messageText) {
        super(MessageType.REMOVE_FILE_CREATOR, senderPort, receiverPort, messageText);
    }
}
