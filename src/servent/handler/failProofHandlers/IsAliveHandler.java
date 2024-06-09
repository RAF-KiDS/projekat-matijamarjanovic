package servent.handler.failProofHandlers;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.failProof.PongMessage;
import servent.message.util.MessageUtil;

public class IsAliveHandler implements MessageHandler {
    private Message clientMessage;
    public IsAliveHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.ALIVE){
            PongMessage pm = new PongMessage(AppConfig.myServentInfo.getListenerPort(), clientMessage.getSenderPort());
            MessageUtil.sendMessage(pm);
        }else {
            AppConfig.timestampedErrorPrint("IsAlive handler got a message that is not IS_ALIVE");
        }
    }
}
