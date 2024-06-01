package cli.command;

import app.AppConfig;
import app.snapshot_bitcake.SnapshotCollector;

import java.sql.SQLOutput;

public class BitcakeInfoCommand implements CLICommand {

	private SnapshotCollector collector;
	
	public BitcakeInfoCommand(SnapshotCollector collector) {
		this.collector = collector;
	}
	
	@Override
	public String commandName() {
		return "bitcake_info";
	}

	@Override
	public void execute(String args) {
		if(AppConfig.getInitiatorIdList().contains(AppConfig.myServentInfo.getId())) {
			while(AppConfig.collectingInfo.get()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			AppConfig.timestampedStandardPrint("Starting to collect bitcake info");
			collector.startCollecting();
		} else
			AppConfig.timestampedErrorPrint("Only initiator can start collecting bitcake info");


	}

}
