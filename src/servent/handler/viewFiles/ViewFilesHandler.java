package servent.handler.viewFiles;

import app.AppConfig;
import app.ServentInfo;
import cli.command.CLICommand;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.viewFiles.ViewFilesMessage;
import servent.message.util.MessageUtil;
import servent.message.viewFiles.ViewFilesTellMessage;
import servent.model.ChordFile;

import java.util.Map;

public class ViewFilesHandler implements MessageHandler {

    private Message clientMessage;

    public ViewFilesHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {

        String[] parts = clientMessage.getMessageText().split(":");
        if(parts.length != 2){
            AppConfig.timestampedErrorPrint("ViewFilesHandler: Invalid message format");
            return;
        }

        try{
            int senderKey = Integer.parseInt(parts[0]);
            int targetKey = Integer.parseInt(parts[1]);

            if(AppConfig.myServentInfo.getChordId() == targetKey){
                if (AppConfig.chordState.getMyFiles().size() > 0) {
                    String requestedFiles = "";
                    for (Map.Entry chordFile : AppConfig.chordState.getMyFiles().entrySet()) {

                        if (chordFile.getValue() instanceof ChordFile) {
                            //ako je public vide ga svi
                            if((((ChordFile) chordFile.getValue()).getPrivacy().equals("public"))) {
                                requestedFiles += "\n--:--:-- - " + ((ChordFile) chordFile.getValue()).getFileName() + " - chordId: " + ((ChordFile) chordFile.getValue()).getChordId();
                            } else if ((((ChordFile) chordFile.getValue()).getPrivacy().equals("private"))) { //ako je private proveri da li su prijatelji
                                if(AppConfig.chordState.isFriend(senderKey)){ //da li su prijatelji
                                    requestedFiles += "\n--:--:-- - " + ((ChordFile) chordFile.getValue()).getFileName() + " - chordId: " + ((ChordFile) chordFile.getValue()).getChordId();
                                }
                            }else{
                                throw new Exception("Invalid privacy type");
                            }
                        }

                    }
                    ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(senderKey);
                    ViewFilesTellMessage viewFilesTellMessage = new ViewFilesTellMessage(clientMessage.getSenderPort(), nextNode.getListenerPort(), requestedFiles + ";" + senderKey);
                    MessageUtil.sendMessage(viewFilesTellMessage);
                } else {
                    AppConfig.timestampedStandardPrint("No files on node " + targetKey + ".");
                }
                    return;
            }

            //u suprotnom salji poruku dalje
            ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(senderKey);
            ViewFilesMessage viewFilesMessage = new ViewFilesMessage(clientMessage.getSenderPort(), nextNode.getListenerPort(), targetKey + ":" + senderKey);
            MessageUtil.sendMessage(viewFilesMessage);


        }catch (NumberFormatException e){
            AppConfig.timestampedErrorPrint("ViewFilesHandler: Error while parsing message text");
        } catch (Exception e) {
            AppConfig.timestampedErrorPrint("Invalid privacy type.");
        }
    }
}
