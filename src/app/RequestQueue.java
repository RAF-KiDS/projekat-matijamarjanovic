package app;

import servent.message.quorumMessages.QuorumRequestMessage;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class RequestQueue {

    private final PriorityBlockingQueue<QuorumRequestMessage> queue;

    public RequestQueue() {
        queue = new PriorityBlockingQueue<>(11, Comparator.comparingLong(QuorumRequestMessage::getTimestamp));
    }

    public void addRequest(QuorumRequestMessage request) {
        queue.add(request);
    }

    public QuorumRequestMessage getNextRequest() {
        return queue.peek();
    }

    public void removeRequest(QuorumRequestMessage request) {
        queue.remove(request);
    }

    public boolean isFirstRequest(QuorumRequestMessage request) {
        return request.equals(queue.peek());
    }
}
