package servent.message.quorumMessages;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class QuorumRequestMessage extends BasicMessage {

    private static final long serialVersionUID = -4558031124520315033L;
    private final long timestamp;

    public QuorumRequestMessage(int senderPort, int receiverPort, String text) {
        super(MessageType.QUORUM_REQUEST, senderPort, receiverPort, text);
        timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }
}
