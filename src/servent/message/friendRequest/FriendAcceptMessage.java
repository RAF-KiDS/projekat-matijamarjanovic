package servent.message.friendRequest;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class FriendAcceptMessage extends BasicMessage {

    private static final long serialVersionUID = 9128374650283746501L;
    public FriendAcceptMessage(int senderPort, int receiverPort, String messageText) {
        super(MessageType.FRIEND_ACCEPT, senderPort, receiverPort, messageText);
    }
}
