package cli.command;

import app.AppConfig;
import servent.model.ChordFile;

public class GetFileCommand implements CLICommand{
    @Override
    public String commandName() {
        return "get_file";
    }

    @Override
    public void execute(String args) {

        AppConfig.chordState.obtainCriticalSection(this, args);


        if (args.split(" ").length != 1) {
            AppConfig.timestampedErrorPrint("Invalid argument for get_file: " + args + ". Should be one argument, which is fileName.");
            AppConfig.chordState.releaseCriticalSection();
            return;
        }
        String name = args;
        int key = ChordFile.generateChordId(name);

        Object val = AppConfig.chordState.getValue(key);

        if ((Integer)val == -2) {
            AppConfig.timestampedStandardPrint("Please wait...");
        } else if ((Integer)val == -1) {
            AppConfig.timestampedStandardPrint("No such key: " + key);
        } else {
            AppConfig.timestampedErrorPrint("Got <" + key + "," + val + ">");
            AppConfig.timestampedStandardPrint(key + ": " + val);
        }


        AppConfig.timestampedStandardPrint("--------Critical section ending...");


        AppConfig.chordState.releaseCriticalSection();
    }
}
