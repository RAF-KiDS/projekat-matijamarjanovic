package cli.command;

import app.AppConfig;

public class InfoCommand implements CLICommand {

	@Override
	public String commandName() {
		return "info";
	}

	@Override
	public void execute(String args) {
		AppConfig.timestampedStandardPrint("My info: " + AppConfig.myServentInfo);
		AppConfig.timestampedStandardPrint("Neighbors:");
		String neighbors = "";
		for (Integer neighbor : AppConfig.myServentInfo.getNeighbors()) {
			neighbors += neighbor + " ";
		}
		
		AppConfig.timestampedStandardPrint(neighbors);

		if (!AppConfig.getSnapshots().isEmpty()){
			AppConfig.timestampedStandardPrint(AppConfig.getSnapshots().size() + " snapshots made by " + AppConfig.snapshotInfo.getInitiatorId() + " last snapshotNo: "+ AppConfig.snapshotInfo.getSnapshotNo());
		}
	}

}
