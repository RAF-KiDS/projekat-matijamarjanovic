package servent.message.quorumMessages;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class QuorumRequestMessage extends BasicMessage {

    private static final long serialVersionUID = -4558031124520315033L;

    public QuorumRequestMessage(int senderPort, int receiverPort, String text) {
        super(MessageType.QUORUM_REQUEST, senderPort, receiverPort, text);
    }
}
