package servent.handler.friendRequestHandlers;

import app.AppConfig;
import app.ServentInfo;
import servent.handler.MessageHandler;
import servent.message.AskGetMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.TellGetMessage;
import servent.message.friendRequest.FriendAcceptMessage;
import servent.message.friendRequest.FriendRequestMessage;
import servent.message.quorumMessages.QuorumResponseMessage;
import servent.message.util.MessageUtil;

import java.util.Map;

public class FriendRequestHandler implements MessageHandler {

    private Message clientMessage;

    public FriendRequestHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.FRIEND_REQUEST) {
            try {

                String[] parts = clientMessage.getMessageText().split("-");
                int senderKey = Integer.parseInt(parts[0]);
                int targetKey = Integer.parseInt(parts[1]);
                int ttl = Integer.parseInt(parts[2]); // Times-To-Live odnosno broj skokova koji poruka moze da napravi

                if(AppConfig.myServentInfo.getChordId() == senderKey){
                    AppConfig.timestampedStandardPrint("There is no node with the key: " + targetKey);
                    return;
                }

                if (AppConfig.myServentInfo.getChordId() == targetKey) { //ako je poruka stigla do trazenog cvora

                    AppConfig.timestampedStandardPrint("Friend request received...Accepting...");

                    //dodaj cvor u prijatelje
                    AppConfig.chordState.addFriend(senderKey);

                    FriendAcceptMessage fam = new   FriendAcceptMessage(AppConfig.myServentInfo.getListenerPort(),
                            clientMessage.getSenderPort(), targetKey + "-" + senderKey); //accept salje samo terget kljuc
                    MessageUtil.sendMessage(fam);

                } else if(ttl > 0){
                    ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(targetKey);
                    FriendRequestMessage frm = new FriendRequestMessage(clientMessage.getSenderPort(), nextNode.getListenerPort(),
                            senderKey + "-" + targetKey + "-" + (ttl - 1)
                    );

                    MessageUtil.sendMessage(frm);
                }else {
                    FriendAcceptMessage fam = new FriendAcceptMessage(AppConfig.myServentInfo.getListenerPort(),
                            AppConfig.chordState.getNextNodeForKey(senderKey).getListenerPort(), "TTL expired. There is no node with the key: " + targetKey + " no friend to add.");
                    MessageUtil.sendMessage(fam);
                }


            } catch (NumberFormatException e) {
                AppConfig.timestampedErrorPrint("Got friend request with bad text: " + clientMessage.getMessageText());
            }

        } else {
            AppConfig.timestampedErrorPrint("Ask get handler got a message that is not QUORUM_REQUEST");
        }
    }
}
