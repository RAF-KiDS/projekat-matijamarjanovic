package servent;

import java.util.concurrent.atomic.AtomicInteger;

public class SnapshotPair {

    private AtomicInteger initiatorId;
    private AtomicInteger snapshotNo;

    public SnapshotPair(int initiatorId, int snapshotNo) {
        this.initiatorId = new AtomicInteger(initiatorId);
        this.snapshotNo = new AtomicInteger(snapshotNo);
    }

    public AtomicInteger getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(int initiatorId) {
        this.initiatorId = new AtomicInteger(initiatorId);
    }

    public AtomicInteger getSnapshotNo() {
        return snapshotNo;
    }

    public void setSnapshotNo(int snapshotNo) {
        this.snapshotNo = new AtomicInteger(snapshotNo);
    }
}
