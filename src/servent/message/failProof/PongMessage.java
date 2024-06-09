package servent.message.failProof;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class PongMessage extends BasicMessage {
    private static final long serialVersionUID = 16573538252291501L;

    public PongMessage(int listenerPort, int senderPort) {
        super(MessageType.PONG, listenerPort, senderPort);
    }
}
