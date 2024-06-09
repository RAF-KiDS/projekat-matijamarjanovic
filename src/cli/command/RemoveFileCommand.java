package cli.command;

import app.AppConfig;
import servent.model.ChordFile;

public class RemoveFileCommand implements CLICommand{
    @Override
    public String commandName() {
        return "remove_file";
    }

    @Override
    public void execute(String args) {
        AppConfig.chordState.obtainCriticalSection();


        if (args.split(" ").length != 1) {
            AppConfig.timestampedErrorPrint("Invalid argument for remove_file: " + args + ". Should one argument, which is fileName");
            AppConfig.chordState.releaseCriticalSection();
            return;
        }
        String name = args;
        int key = ChordFile.generateChordId(name);

        Object val = AppConfig.chordState.removeValue(key);

        if ((Integer)val == -2) {
            AppConfig.timestampedStandardPrint("Please wait...");
        } else if ((Integer)val == -1) {
            AppConfig.timestampedStandardPrint("No such key: " + key);
        } else {
            AppConfig.timestampedErrorPrint("Removed <" + key + "," + val + "> from system");
            AppConfig.timestampedStandardPrint("Removed <" + key + "," + val + "> from system");
        }

        AppConfig.timestampedStandardPrint("--------Critical section ending...");


        AppConfig.chordState.releaseCriticalSection();

    }
}
