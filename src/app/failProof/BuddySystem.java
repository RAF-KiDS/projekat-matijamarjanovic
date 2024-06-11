package app.failProof;

import app.AppConfig;
import app.ServentInfo;
import servent.message.MessageType;
import servent.message.failProof.IsAliveMessage;
import servent.message.failProof.PingMessage;
import servent.message.failProof.RemoveNodeMessage;
import servent.message.util.MessageUtil;

public class BuddySystem implements Runnable{

    private static BuddySystem instance = new BuddySystem();

    private static final int softFailureThreshold = AppConfig.sft;
    private static final int hardFailureThreshold = AppConfig.hft;

    private int predecessorPort; //levi sused
    private int successorPort; //desni

    private volatile boolean working = true;
    private volatile boolean pongedPredecessor;
    private volatile boolean pongedSuccessor;

    public static BuddySystem getInstance() {
        return instance;
    }

    public int getPredecessorPort() {
        return predecessorPort;
    }

    public int getSuccessorPort() {
        return successorPort;
    }

    public void setPongedPredecessor(boolean pongedPredecessor) {
        this.pongedPredecessor = pongedPredecessor;
    }

    public void setPongedSuccessor(boolean pongedSuccessor) {
        this.pongedSuccessor = pongedSuccessor;
    }

    @Override
    public void run() {
        while (working) {

            //ako je sistem u izgradnji, nema prethodnika, nema sledbenika, nema stanja, nema sledeceg cvora
            if(AppConfig.chordState == null
                    ||  AppConfig.chordState.getPredecessor() == null
                    || AppConfig.chordState.getSystemInConstruction().get()
                    || AppConfig.chordState.getSuccessorTable()[0] == null){
                continue;
            }

            pongedPredecessor = false;
            pongedSuccessor = false;


            predecessorPort = AppConfig.chordState.getPredecessor().getListenerPort();
            successorPort = AppConfig.chordState.getSuccessorTable()[0].getListenerPort();

            //pinguj
            PingMessage pmPred = new PingMessage(AppConfig.myServentInfo.getListenerPort(), predecessorPort);
            MessageUtil.sendMessage(pmPred);
            PingMessage pmSucc = new PingMessage(AppConfig.myServentInfo.getListenerPort(), successorPort);
            MessageUtil.sendMessage(pmSucc);

            //sacekaj sft
            try {
                Thread.sleep(softFailureThreshold);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(pongedPredecessor && pongedSuccessor) {
                continue;
            }
            //ako je konstrukcija sistema u toku
            if(predecessorPort != AppConfig.chordState.getPredecessor().getListenerPort()) {
                pongedPredecessor = true;
            }
            if(successorPort != AppConfig.chordState.getNextNodePort()) {
                pongedSuccessor = true;
            }

            //ako ga nema
            if(!pongedPredecessor){
                //AppConfig.timestampedErrorPrint("SFT fail for: " + predecessorPort);
                IsAliveMessage isAliveLeft = new IsAliveMessage(AppConfig.myServentInfo.getListenerPort(), predecessorPort);
                MessageUtil.sendMessage(isAliveLeft);
            }

            if(!pongedSuccessor){
                //AppConfig.timestampedErrorPrint("SFT fail for: " + predecessorPort);
                IsAliveMessage isAliveLeft = new IsAliveMessage(AppConfig.myServentInfo.getListenerPort(), successorPort);
                MessageUtil.sendMessage(isAliveLeft);
            }

            //ako ga jos nema sacekaj jos htf
            try {
                Thread.sleep(hardFailureThreshold - softFailureThreshold);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(pongedPredecessor && pongedSuccessor) {
                continue;
            }
            //ako je konstrukcija sistema u toku
            if(predecessorPort != AppConfig.chordState.getPredecessor().getListenerPort()) {
                pongedPredecessor = true;
            }
            if(successorPort != AppConfig.chordState.getNextNodePort()) {
                pongedSuccessor = true;
            }

            //ako ga nema ni sad rekonstrukcija sistema
            if(!pongedPredecessor){
                AppConfig.timestampedErrorPrint("HFT fail for: " + predecessorPort);
                AppConfig.chordState.removeNode(predecessorPort);

                // javi ostalima
                for(ServentInfo se : AppConfig.chordState.getAllNodeInfo()){
                    int portToSend = se.getListenerPort();
                    if(portToSend == AppConfig.myServentInfo.getListenerPort())
                        continue;
                    if(portToSend == predecessorPort)
                        continue;

                    //odradi komandu koja je izgubljena
                    AppConfig.chordState.executeCommandInProgress();

                    RemoveNodeMessage rnm = new RemoveNodeMessage(AppConfig.myServentInfo.getListenerPort(), portToSend, predecessorPort);
                    MessageUtil.sendMessage(rnm);
                }
            }


        }
    }

    public void stop() {
        working = false;
    }
}
