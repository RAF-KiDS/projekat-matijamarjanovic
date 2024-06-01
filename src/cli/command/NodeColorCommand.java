package cli.command;

import app.AppConfig;

public class NodeColorCommand implements CLICommand {
    @Override
    public String commandName() {
        return "node_color";
    }

    @Override
    public void execute(String args) {
        AppConfig.timestampedStandardPrint("Servent is white: " + AppConfig.isWhite.get());
    }
}
