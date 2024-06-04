package servent.handler.friendRequestHandlers;

import app.AppConfig;
import app.ServentInfo;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.friendRequest.FriendAcceptMessage;
import servent.message.friendRequest.FriendRequestMessage;
import servent.message.util.MessageUtil;

public class FriendAcceptHandler implements MessageHandler {

    private Message clientMessage;

    public FriendAcceptHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.FRIEND_ACCEPT) {
            try {

                String[] parts = clientMessage.getMessageText().split("-");
                int senderKey = Integer.parseInt(parts[0]);
                int targetKey = Integer.parseInt(parts[1]);

                //u slucaju da cvor ne postoji
                if(clientMessage.getMessageText().contains("TTL expired")){
                    AppConfig.timestampedStandardPrint(clientMessage.getMessageText());
                    return;
                }

                if (AppConfig.myServentInfo.getChordId() == targetKey) { //ako je poruka stigla do trazenog cvora

                    AppConfig.timestampedStandardPrint("Friend accept received...Accepting...");

                    //dodaj cvor u prijatelje
                    AppConfig.chordState.addFriend(senderKey);

                } else{
                    //salji dalje
                    ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(targetKey);
                    FriendAcceptMessage fam = new FriendAcceptMessage(clientMessage.getSenderPort(), nextNode.getListenerPort(),
                            clientMessage.getMessageText());

                    MessageUtil.sendMessage(fam);
                }


            } catch (NumberFormatException e) {
                AppConfig.timestampedErrorPrint("Got ask get with bad text: " + clientMessage.getMessageText());
            }

        } else {
            AppConfig.timestampedErrorPrint("Ask get handler got a message that is not QUORUM_REQUEST");
        }
    }
}
