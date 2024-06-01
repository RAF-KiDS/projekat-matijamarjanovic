package servent.message.snapshot;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

import java.util.concurrent.atomic.AtomicInteger;

public class LYWhiteMarkerMessage extends BasicMessage {
    private static final long serialVersionUID = 388942512346636228L;

    public LYWhiteMarkerMessage(ServentInfo sender, ServentInfo receiver, int collectorId, int initiatorId, int snapshotNo) {
        super(MessageType.LY_WHITE_MARKER, sender, receiver, String.valueOf(collectorId), initiatorId, snapshotNo);
    }
}
