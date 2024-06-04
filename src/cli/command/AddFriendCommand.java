package cli.command;

import app.AppConfig;
import servent.message.MessageType;
import servent.message.friendRequest.FriendRequestMessage;
import servent.message.util.MessageUtil;

public class AddFriendCommand implements CLICommand {
    @Override
    public String commandName() {
        return "add_friend";
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

            if(args.split(" ").length != 1) {
                AppConfig.timestampedErrorPrint("Invalid number of arguments for add_friend command. Should be: add_friend key");
                AppConfig.chordState.releaseCriticalSection();
                return;
            }

            int key = Integer.parseInt(args);
            FriendRequestMessage friendRequestMessage = new FriendRequestMessage(AppConfig.myServentInfo.getListenerPort(),
                    AppConfig.chordState.getNextNodeForKey(key).getListenerPort(),
                    AppConfig.myServentInfo.getChordId()+"-"+key);
            MessageUtil.sendMessage(friendRequestMessage);

            AppConfig.chordState.releaseCriticalSection();


        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Invalid argument for dht_get: " + args + ". Should be key, which is an int.");
        }
    }
}
