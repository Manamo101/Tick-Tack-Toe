package org.example;

import javax.swing.*;
import java.rmi.RemoteException;

public class OpponentTurnWaiter extends Thread{
    String tokenID;
    String opponentMark;
    GameService gameService;
    JLabel label;

    OpponentTurnWaiter(String token, String opponentMark, GameService gameService, JLabel label) {
        this.tokenID = token;
        this.opponentMark = opponentMark;
        this.gameService = gameService;
        this.label = label;
    }

    @Override
    public void run() {
        try {
            while (gameService.isPared(tokenID) && gameService.whoseTurn(tokenID).equals(opponentMark)){
                label.setText("opponent's move");
                Thread.sleep(1000);
            }
            Main.getCountDownLatch().countDown();
        }
        catch (RemoteException | InterruptedException e) {
            System.out.println("Problem in OpponentTurnWaiter");
            throw  new RuntimeException(e);
        }
    }
}
