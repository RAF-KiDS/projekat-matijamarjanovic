package servent.message.viewFiles;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class ViewFilesMessage extends BasicMessage {

    private static final long serialVersionUID = 112233445566778899L;
    public ViewFilesMessage(int senderPort, int receiverPort, String messageText) {
        super(MessageType.VIEW_FILES, senderPort, receiverPort, messageText);
    }
}
