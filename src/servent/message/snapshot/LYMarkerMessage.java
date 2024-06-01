package servent.message.snapshot;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

import java.util.concurrent.atomic.AtomicInteger;

public class LYMarkerMessage extends BasicMessage {

	private static final long serialVersionUID = 388942509576636228L;

	public LYMarkerMessage(ServentInfo sender, ServentInfo receiver, int collectorId, int snapshotNum , int initiatorId, int snapshotNo) {
		super(MessageType.LY_MARKER, sender, receiver, String.valueOf(collectorId) + "," +String.valueOf(snapshotNum), initiatorId, snapshotNo);
	}
}
