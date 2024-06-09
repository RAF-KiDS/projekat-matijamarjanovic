package servent.message.quorumMessages;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class QuorumResponseMessage extends BasicMessage {

    private static final long serialVersionUID = -5528031124520315033L;
    private final long timestamp;

    public QuorumResponseMessage(int senderPort, int receiverPort, boolean unlocked) {
        super(MessageType.QUORUM_RESPONSE, senderPort, receiverPort, unlocked?"UNLOCKED":"LOCKED");
        timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }
}
