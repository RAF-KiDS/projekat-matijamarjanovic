package cli.command;

import app.AppConfig;
import app.ChordState;
import servent.model.ChordFile;

import java.io.File;
import java.io.FileNotFoundException;

public class AddFileCommand implements CLICommand{
    @Override
    public String commandName() {
        return "add_file";
    }

    @Override
    public void execute(String args) {
        AppConfig.chordState.obtainCriticalSection(this, args);


        String[] splitArgs = args.split(" ");
        if (splitArgs.length != 2) {
            AppConfig.timestampedErrorPrint("Invalid number of arguments for add_file command. Should be: add_file filePath privacySettings");
            AppConfig.chordState.releaseCriticalSection();
            return;
        }


        try {
            String filePath = splitArgs[0];
            String privacySettings = splitArgs[1];

            if(!privacySettings.equalsIgnoreCase("public") && !privacySettings.equalsIgnoreCase("private")) {
                AppConfig.timestampedErrorPrint("Invalid privacy settings. Should be: public or private.");
                AppConfig.chordState.releaseCriticalSection();
                return;
            }

            ChordFile chordFile = new ChordFile(privacySettings, filePath, AppConfig.myServentInfo.getChordId());

            int key = chordFile.getChordId();

            if (key < 0 || key >= ChordState.CHORD_SIZE) {
                throw new NumberFormatException();
            }
            if (chordFile.getFile() == null) {
                throw new FileNotFoundException();
            }

            AppConfig.chordState.putValue(key, chordFile);
            AppConfig.chordState.addToMyFiles(chordFile);

        } catch (Exception e) {
            AppConfig.timestampedErrorPrint("Invalid file path.");
        }

        AppConfig.chordState.releaseCriticalSection();
    }
}
