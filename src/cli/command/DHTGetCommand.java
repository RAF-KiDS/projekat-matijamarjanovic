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
			AppConfig.timestampedStandardPrint("--------Requesting critical section...");
			while(!AppConfig.chordState.requestCriticalSection()){
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			AppConfig.timestampedStandardPrint("--------Critical section starting...");


			int key = Integer.parseInt(args);

			int val = AppConfig.chordState.getValue(key);

			if (val == -2) {
				AppConfig.timestampedStandardPrint("Please wait...");
			} else if (val == -1) {
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
