package servent.message;

import java.util.Map;

public class BackUpMessage extends BasicMessage{

    private static final long serialVersionUID = 1657353248252291501L;
    private Map<Integer, Object> filesToBackUp;
    public BackUpMessage(int senderPort, int receiverPort, Map<Integer, Object> filesToBackUp, int chordID) {
        super(MessageType.BACK_UP, senderPort, receiverPort, String.valueOf(chordID));
        this.filesToBackUp = filesToBackUp;
    }

    public Map<Integer, Object> getFilesToBackUp() {
        return filesToBackUp;
    }
}
