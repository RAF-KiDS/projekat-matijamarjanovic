package cli.command;

import app.AppConfig;
import app.ChordState;
import servent.model.ChordFile;

import java.io.FileNotFoundException;

public class DHTPutCommand implements CLICommand {

	@Override
	public String commandName() {
		return "dht_put";
	}

	@Override
	public void execute(String args) {

		AppConfig.chordState.obtainCriticalSection();


		String[] splitArgs = args.split(" ");
		
		if (splitArgs.length == 2) {
			int key = 0;
			Object value = 0;
			try {
				key = Integer.parseInt(splitArgs[0]);
				value = Integer.parseInt(splitArgs[1]);
				
				if (key < 0 || key >= ChordState.CHORD_SIZE) {
					throw new NumberFormatException();
				}

				AppConfig.chordState.putValue(key, value);
			} catch (Exception e) {
				if (e instanceof NumberFormatException)
					AppConfig.timestampedErrorPrint("Invalid key. 0 <= key <= " + ChordState.CHORD_SIZE);
				else
					AppConfig.timestampedErrorPrint("Invalid key and value pair. Both should be ints. 0 <= key <= " + ChordState.CHORD_SIZE
						+ ". 0 <= value.");
			}

			AppConfig.chordState.releaseCriticalSection();
		} else {
			AppConfig.timestampedErrorPrint("Invalid arguments for put");
		}

	}

}
