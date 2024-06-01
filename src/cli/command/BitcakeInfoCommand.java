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
		collector.startCollecting();

	}

}
