package servent.message.friendRequest;

import app.AppConfig;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class FriendRequestMessage extends BasicMessage {
    private static final long serialVersionUID = 3471140796318957555L;
    private static final int MAX_TTL = AppConfig.chordState.getChordLevel();
    public FriendRequestMessage(int senderPort, int receiverPort, String messageText) {
        super(MessageType.FRIEND_REQUEST, senderPort, receiverPort, messageText + "-" + MAX_TTL);
    }
}
