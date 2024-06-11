package servent.message.failProof;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class SearchForBackUpMessage extends BasicMessage {
    private static final long serialVersionUID = 16573532482522501L;
    private int whoAsked;
    public SearchForBackUpMessage(int senderPort, int receiverPort, String messageText, int whoAsked) {
        super(MessageType.SEARCH_FOR_BACK_UP, senderPort, receiverPort, messageText);
        this.whoAsked = whoAsked;
    }

    public int getWhoAsked() {
        return whoAsked;
    }
}
