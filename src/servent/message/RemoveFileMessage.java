package servent.message;

public class RemoveFileMessage extends BasicMessage {

    private static final long serialVersionUID = 1657353248252291501L;
    public RemoveFileMessage(int senderPort, int receiverPort, String messageText) {
        super(MessageType.REMOVE_FILE, senderPort, receiverPort, messageText);
    }
}
