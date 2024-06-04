package servent.message;

import servent.model.ChordFile;

public class PutMessage extends BasicMessage {

	private static final long serialVersionUID = 5163039209888734276L;

	public PutMessage(int senderPort, int receiverPort, int key, int value) {
		super(MessageType.PUT, senderPort, receiverPort, key + ":" + value);
	}

	public PutMessage(int senderPort, int receiverPort, int key, Object value) {
		super(MessageType.PUT, senderPort, receiverPort, key + ":" + value.toString());
	}
}
