package cli.command;

import app.AppConfig;
import app.ServentInfo;
import servent.message.viewFiles.ViewFilesMessage;
import servent.message.util.MessageUtil;
import servent.model.ChordFile;

import java.util.Map;

public class ViewFilesCommand implements CLICommand{
    @Override
    public String commandName() {
        return "view_files";
    }

    @Override
    public void execute(String args) {
        AppConfig.chordState.obtainCriticalSection(this, args);


        String[] splitArgs = args.split(" ");
        if (splitArgs.length != 1) {
            AppConfig.timestampedErrorPrint("Invalid number of arguments for view_files command. Should be: view_files address:port");
            AppConfig.chordState.releaseCriticalSection();
            return;
        }


        try {
            String key = splitArgs[0];

            if(!AppConfig.chordState.nodeExists(key)){
                AppConfig.timestampedErrorPrint("Node " + key + " does not exist in the system.");
                AppConfig.chordState.releaseCriticalSection();
                return;
            }


            int keyInt = AppConfig.chordState.getChordIdForAddressAndPort(key);

            if(AppConfig.myServentInfo.getChordId() == keyInt){
                if (AppConfig.chordState.getMyFiles().size() > 0){
                    AppConfig.timestampedStandardPrint("Files on node " + key + ":");
                    String toPrint = "";

                    for(Map.Entry<Integer, Object> entry : AppConfig.chordState.getMyFiles().entrySet()){
                        //ako listam svoje fajlove nema provere za private i public
                        if (entry.getValue() instanceof ChordFile) {
                            toPrint += ((ChordFile) entry.getValue()).getFileName() + " - chordId: " + ((ChordFile) entry.getValue()).getChordId() + "\n";
                        }
                    }

                    AppConfig.timestampedStandardPrint(toPrint);

                }else {
                    AppConfig.timestampedStandardPrint("No files on node " + key + ".");
                }
                return;
            }

            //u suprotnom salji poruku dalje
            ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(keyInt);
            ViewFilesMessage viewFilesMessage = new ViewFilesMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(),
                    keyInt+":"+AppConfig.myServentInfo.getChordId());
            MessageUtil.sendMessage(viewFilesMessage);


        } catch (Exception e) {
            AppConfig.timestampedErrorPrint("Invalid argument.");
        }

        AppConfig.chordState.releaseCriticalSection();
    }
}
