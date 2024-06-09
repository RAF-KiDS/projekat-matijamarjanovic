package servent.handler.failProofHandlers;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.failProof.PongMessage;
import servent.message.util.MessageUtil;

public class PingHandler implements MessageHandler {

    private Message clientMessage;
    public PingHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.PING){

            PongMessage pm = new PongMessage(AppConfig.myServentInfo.getListenerPort(), clientMessage.getSenderPort());
            MessageUtil.sendMessage(pm);

        }else {
            AppConfig.timestampedErrorPrint("Ping handler got a message that is not PING");
        }
    }
}
