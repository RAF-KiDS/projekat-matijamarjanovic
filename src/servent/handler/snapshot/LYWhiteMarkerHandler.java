package servent.handler.snapshot;

import app.AppConfig;
import servent.handler.MessageHandler;

public class LYWhiteMarkerHandler implements MessageHandler {
    @Override
    public void run() {
        AppConfig.collectingInfo.set(false);
    }
}
