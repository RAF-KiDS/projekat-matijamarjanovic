package servent.message.viewFiles;

import app.ChordState;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class ViewFilesTellMessage extends BasicMessage {

    private static final long serialVersionUID = 111222333444555666L;
    public ViewFilesTellMessage(int senderPort, int receiverPort, String messageText) {
        super(MessageType.VIEW_FILES_TELL, senderPort, receiverPort, messageText);
    }

    @Override
    public String toString() {
        return "[" + ChordState.chordHash(getSenderPort()) + "|" + getSenderPort() + "|" + getMessageId() + "|" +
                ((getMessageText().length()>10)?"text too long":getMessageText()) + "|" + getMessageType() + "|" +
                getReceiverPort() + "|" + ChordState.chordHash(getReceiverPort()) + "]";
    }
}
