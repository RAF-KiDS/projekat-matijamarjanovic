package servent.handler.snapshot;

import app.AppConfig;
import servent.handler.MessageHandler;

public class LYMarkerHandler implements MessageHandler {

	@Override
	public void run() {
		//Don't actually need this.
		//Everything is done in SimpleServentListener.
		AppConfig.collectingInfo.set(true);
		;
	}

}
