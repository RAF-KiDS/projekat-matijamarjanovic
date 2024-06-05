package servent.message.viewFiles;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class ViewFilesTellMessage extends BasicMessage {

    private static final long serialVersionUID = 111222333444555666L;
    public ViewFilesTellMessage(int senderPort, int receiverPort, String messageText) {
        super(MessageType.VIEW_FILES_TELL, senderPort, receiverPort, messageText);
    }
}
