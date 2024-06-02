package servent.handler.quorumHandlers;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.quorumMessages.QuorumResponseMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuorumResponseHandler implements MessageHandler {

    private Message clientMessage;

    public QuorumResponseHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.QUORUM_RESPONSE) {
            QuorumResponseMessage responseMessage = (QuorumResponseMessage) clientMessage;

            if(responseMessage.getMessageText().equalsIgnoreCase("UNLOCKED"))
                AppConfig.chordState.getQuorumResponses().put(responseMessage.getSenderPort(), true);
            else if(responseMessage.getMessageText().equalsIgnoreCase("LOCKED"))
                AppConfig.chordState.getQuorumResponses().put(responseMessage.getSenderPort(), false);

            if(checkAllResponses()) {
                AppConfig.chordState.setCheckCleared(true);
                AppConfig.chordState.getQuorumResponses().clear();
            }
        } else {
            AppConfig.timestampedErrorPrint("Quorum response handler got a message that is not QUORUM_RESPONSE");
        }
    }


    private boolean checkAllResponses() {
        //cekaj dok ne dobijes sve odgovore
        while(AppConfig.chordState.getQuorumResponses().size() < AppConfig.chordState.getQuorumSize()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (AppConfig.chordState.getQuorumResponses().values().contains(false))
            return false;

        return true;
    }

    /**
     * Enters the critical section if all responses indicate that the lock is available.
     *
     * @param key The key for which the critical section is entered.
     */
    private void enterCriticalSection(int key) {
        AppConfig.timestampedStandardPrint("Entering critical section for key " + key);
        // Perform critical section operations here...
    }
}
