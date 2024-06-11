package servent.message.failProof;

import servent.message.BackUpMessage;
import servent.message.BasicMessage;
import servent.message.MessageType;

import java.util.Map;

public class BackUpFoundTellMessage extends BasicMessage {

    private static final long serialVersionUID = 16573532482291501L;
    private Map<Integer, Object> backedUpFiles;
    public BackUpFoundTellMessage(int senderPort, int receiverPort, Map<Integer, Object> backedUpFiles, String whoAskedChordId, int whoBackedUp) {
        super(MessageType.BACK_UP_FOUND_TELL, senderPort, receiverPort, whoAskedChordId+","+whoBackedUp);
        this.backedUpFiles = backedUpFiles;
    }

    public Map<Integer, Object> getBackedUpFiles() {
        return backedUpFiles;
    }
}
