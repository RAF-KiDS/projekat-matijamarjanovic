package cli.command;

import app.AppConfig;
import app.ChordState;

public class DHTPutCommand implements CLICommand {

	@Override
	public String commandName() {
		return "dht_put";
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


		String[] splitArgs = args.split(" ");
		
		if (splitArgs.length == 2) {
			int key = 0;
			int value = 0;
			try {
				key = Integer.parseInt(splitArgs[0]);
				value = Integer.parseInt(splitArgs[1]);
				
				if (key < 0 || key >= ChordState.CHORD_SIZE) {
					throw new NumberFormatException();
				}
				if (value < 0) {
					throw new NumberFormatException();
				}
				
				AppConfig.chordState.putValue(key, value);
			} catch (NumberFormatException e) {
				AppConfig.timestampedErrorPrint("Invalid key and value pair. Both should be ints. 0 <= key <= " + ChordState.CHORD_SIZE
						+ ". 0 <= value.");
			}

			AppConfig.chordState.releaseCriticalSection();
		} else {
			AppConfig.timestampedErrorPrint("Invalid arguments for put");
		}

	}

}
