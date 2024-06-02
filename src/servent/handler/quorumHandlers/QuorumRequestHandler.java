package servent.handler.quorumHandlers;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.quorumMessages.QuorumResponseMessage;
import servent.message.util.MessageUtil;

public class QuorumRequestHandler implements MessageHandler {
    private Message clientMessage;

    public QuorumRequestHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.QUORUM_REQUEST) {
            try {
                // Check if lock is acquired before proceeding
                if (AppConfig.chordState.isLocked()) {
                    AppConfig.timestampedStandardPrint("Lock is acquired. Waiting for release...");
                    QuorumResponseMessage responseMessage = new QuorumResponseMessage(
                            AppConfig.myServentInfo.getListenerPort(),
                            clientMessage.getSenderPort(),
                            false);

                    MessageUtil.sendMessage(responseMessage);
                    return;
                }

                QuorumResponseMessage responseMessage = new QuorumResponseMessage(
                        AppConfig.myServentInfo.getListenerPort(),
                        clientMessage.getSenderPort(),
                        true
                );
                MessageUtil.sendMessage(responseMessage);


            } catch (NumberFormatException e) {
                AppConfig.timestampedErrorPrint("Got ask get with bad text: " + clientMessage.getMessageText());
            }

        } else {
            AppConfig.timestampedErrorPrint("Ask get handler got a message that is not QUORUM_REQUEST");
        }
    }

}
