package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;
import servent.model.ChordFile;

public class TellGetHandler implements MessageHandler {

	private Message clientMessage;
	
	public TellGetHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}
	
	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.TELL_GET) {
			String parts[] = clientMessage.getMessageText().split(":");
			
			if (parts.length == 2) {
				try {
					int key = Integer.parseInt(parts[0]);

					if(parts[1].split(",").length == 3) {
						String privacy = parts[1].split(",")[0];
						String path = parts[1].split(",")[1];
						String creatorId = parts[1].split(",")[2];
						Object value = new ChordFile(privacy, path, Integer.parseInt(creatorId));

						AppConfig.timestampedStandardPrint(key + " -> " + value);
						AppConfig.timestampedErrorPrint("Got <" + key + "," + value + ">");

					} else if(parts[1].split(",").length == 1){
						int value = Integer.parseInt(parts[1]);
						if (value == -1) {
							AppConfig.timestampedStandardPrint("No such key: " + key);
						} else {
							AppConfig.timestampedErrorPrint("Got <" + key + "," +
									((clientMessage.getMessageText().split(":").length>1) ? clientMessage.getMessageText().split(":")[1]:"") + ">");
							AppConfig.timestampedStandardPrint(clientMessage.getMessageText());
						}
					}

				} catch (NumberFormatException e) {
					AppConfig.timestampedErrorPrint("Got TELL_GET message with bad text: " + clientMessage.getMessageText());
				}
			} else {
				AppConfig.timestampedErrorPrint("Got TELL_GET message with bad text: " + clientMessage.getMessageText());
			}
		} else {
			AppConfig.timestampedErrorPrint("Tell get handler got a message that is not TELL_GET");
		}
	}

}
