package servent.handler.viewFiles;

import app.AppConfig;
import app.ServentInfo;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.util.MessageUtil;
import servent.message.viewFiles.ViewFilesTellMessage;

public class ViewFilesTellHandler implements MessageHandler {

    private final Message clientMessage;

    public ViewFilesTellHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        try{
            String requestedFiles = clientMessage.getMessageText().split(";")[0];
            int targetKey = Integer.parseInt(clientMessage.getMessageText().split(";")[1]);

            //ako sam ja trazio - print
            if(AppConfig.myServentInfo.getChordId() == targetKey){
                AppConfig.timestampedStandardPrint("Files on node " + targetKey + ":");
                AppConfig.timestampedStandardPrint(requestedFiles);
                return;
            }

            //ako ne - salji dalje
            ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(targetKey);
            ViewFilesTellMessage viewFilesTellMessage = new ViewFilesTellMessage(clientMessage.getSenderPort(), nextNode.getListenerPort(), clientMessage.getMessageText());
            MessageUtil.sendMessage(viewFilesTellMessage);

        }catch (Exception e){
            AppConfig.timestampedErrorPrint("ViewFilesTellHandler: Error while parsing message.");
        }
    }
}
