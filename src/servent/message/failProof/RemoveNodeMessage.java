package servent.message.failProof;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class RemoveNodeMessage extends BasicMessage {
    private static final long serialVersionUID = 1657353248252291501L;
    private int toRemovePort;
    public RemoveNodeMessage(int senderPort, int receiverPort, int toRemovePort) {
        super(MessageType.REMOVE_NODE, senderPort, receiverPort);
        this.toRemovePort =toRemovePort;
    }
    public int getToRemovePort() {
        return toRemovePort;
    }
}
