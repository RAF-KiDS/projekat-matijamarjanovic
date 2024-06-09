package servent.message.failProof;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class PingMessage extends BasicMessage {
    private static final long serialVersionUID = 16573532482522501L;

    public PingMessage(int senderPort, int receiverPort) {
        super(MessageType.PING, senderPort, receiverPort);
    }
}
