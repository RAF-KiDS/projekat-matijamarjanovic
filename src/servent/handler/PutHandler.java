package servent.handler;

import app.AppConfig;
import app.ChordState;
import servent.message.Message;
import servent.message.MessageType;
import servent.model.ChordFile;

import java.io.FileNotFoundException;

public class PutHandler implements MessageHandler {

	private Message clientMessage;
	
	public PutHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}
	
	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.PUT) {
			String[] splitText = clientMessage.getMessageText().split(":");
			if (splitText.length == 2) {
				int key = 0;
				Object value = 0;
				
				try {
					key = Integer.parseInt(splitText[0]);

					if(splitText[1].split(",").length == 3) {
						String privacy = splitText[1].split(",")[0];
						String path = splitText[1].split(",")[1];
						String creatorId = splitText[1].split(",")[2];
						value = new ChordFile(privacy, path, Integer.parseInt(creatorId));

					}else if (splitText[1].split(",").length == 1) {
						value = Integer.parseInt(splitText[1]);
					}

					if (key < 0 || key >= ChordState.CHORD_SIZE) {
						throw new NumberFormatException();
					}
					if (value instanceof ChordFile && ((ChordFile) value).getFile() == null) {
						throw new FileNotFoundException();
					}
					AppConfig.chordState.putValue(key, value);
				} catch (Exception e) {

					if (e instanceof NumberFormatException)
						AppConfig.timestampedErrorPrint("Invalid key. 0 <= key <= " + ChordState.CHORD_SIZE);
					else if (e instanceof FileNotFoundException)
						AppConfig.timestampedErrorPrint("Invalid file path.");
					else
						AppConfig.timestampedErrorPrint("Got put message with bad text: " + clientMessage.getMessageText());

				}
			} else {
				AppConfig.timestampedErrorPrint("Got put message with bad text: " + clientMessage.getMessageText());
			}
			
			
		} else {
			AppConfig.timestampedErrorPrint("Put handler got a message that is not PUT");
		}

	}

}
