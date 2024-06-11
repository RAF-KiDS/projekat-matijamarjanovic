package app.failProof;

import app.AppConfig;
import servent.message.BackUpMessage;
import servent.message.util.MessageUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BackUpSystem implements Runnable {

    private volatile boolean working = true;
    private static final int BACK_UP_IN_MILISECONDS = 2500;
    @Override
    public void run() {
        while(working){
            try {
                Thread.sleep(BACK_UP_IN_MILISECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //ako se sistem konstruise nista
            if(AppConfig.chordState == null || AppConfig.chordState.getSuccessorTable()[0] == null)
                continue;

            int backupPort = AppConfig.chordState.getSuccessorTable()[0].getListenerPort();
            Map<Integer, Object> myValues = new ConcurrentHashMap<>(AppConfig.chordState.getMyFiles()); //dodaj moje fajlove

            BackUpMessage bm = new BackUpMessage(AppConfig.myServentInfo.getListenerPort(), backupPort, myValues, AppConfig.myServentInfo.getChordId());
            MessageUtil.sendMessage(bm);
        }
    }
    public void stop(){
        working = false;
    }
}
