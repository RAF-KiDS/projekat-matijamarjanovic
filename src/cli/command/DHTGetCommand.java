package cli.command;

import app.AppConfig;

public class DHTGetCommand implements CLICommand {

	@Override
	public String commandName() {
		return "dht_get";
	}

	@Override
	public void execute(String args) {
		try {
			AppConfig.chordState.obtainCriticalSection();


			int key = Integer.parseInt(args);

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


		} catch (NumberFormatException e) {
			AppConfig.timestampedErrorPrint("Invalid argument for dht_get: " + args + ". Should be key, which is an int.");
		}
	}

}
