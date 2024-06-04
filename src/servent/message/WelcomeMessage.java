package servent.message;

import java.util.Map;
import java.util.Objects;

public class WelcomeMessage extends BasicMessage {

	private static final long serialVersionUID = -8981406250652693908L;

	private Map<Integer, Object> values;
	
	public WelcomeMessage(int senderPort, int receiverPort, Map<Integer, Object> values) {
		super(MessageType.WELCOME, senderPort, receiverPort);
		
		this.values = values;
	}
	
	public Map<Integer, Object> getValues() {
		return values;
	}

}
