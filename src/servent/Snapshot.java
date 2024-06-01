package servent;

import app.snapshot_bitcake.LYSnapshotResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Snapshot {

    private final int initiatorId;
    private final int snapshotNumber;

    //todo snapshot information
    private Map<Integer, LYSnapshotResult> collectedLYValues;
    private int messagesInTransit;
    private int totalMessages;


    //todo constructor, getters, setters

    public Snapshot(int initiatorId, int snapshotNumber) {
        this.initiatorId = initiatorId;
        this.snapshotNumber = snapshotNumber;
        collectedLYValues = new ConcurrentHashMap<>();
        messagesInTransit = 0;
    }

    public Map<Integer, LYSnapshotResult> getCollectedLYValues() {
        return collectedLYValues;
    }

    public void setCollectedLYValues(Map<Integer, LYSnapshotResult> collectedLYValues) {
        this.collectedLYValues = collectedLYValues;
    }

    public int getMessagesInTransit() {
        return messagesInTransit;
    }

    public void setMessagesInTransit(int messagesInTransit) {
        this.messagesInTransit = messagesInTransit;
    }

    public int getTotalMessages() {
        return totalMessages;
    }

    public void setTotalMessages(int totalMessages) {
        this.totalMessages = totalMessages;
    }

    @Override
    public String toString() {

        return "InitiatorId : " + initiatorId + "\nSnapshotNumber : " + snapshotNumber
                + "\nCollectedLYValues : " + collectedLYValues + "\nMessagesInTransit : " + messagesInTransit + "\n";
    }
}
