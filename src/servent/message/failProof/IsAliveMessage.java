package servent.message.failProof;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class IsAliveMessage extends BasicMessage {
    private static final long serialVersionUID = 1657334548252291501L;

    public IsAliveMessage(int senderPort, int receiverPort) {
        super(MessageType.ALIVE, senderPort, receiverPort);
    }
}
