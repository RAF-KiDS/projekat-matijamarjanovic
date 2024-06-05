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
        AppConfig.timestampedStandardPrint("--------Requesting critical section...");
        while(!AppConfig.chordState.requestCriticalSection()){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        AppConfig.timestampedStandardPrint("--------Critical section starting...");


        if (args.split(" ").length != 1) {
            AppConfig.timestampedErrorPrint("Invalid argument for get_file: " + args + ". Should be key, which is an int.");
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
