package org.example;

import javax.swing.*;
import java.rmi.RemoteException;

public class SessionWaiter extends Thread{
    private final String tokenID;
    private final GameService gameService;
    private final JFrame paringFrame;
    SessionWaiter(String tokenID, GameService gameService, JFrame paringFrame) {
        this.tokenID = tokenID;
        this.gameService = gameService;
        this.paringFrame = paringFrame;
    }
    @Override
    public void run() {
        try {
            while (!gameService.isPared(tokenID)){
                Thread.sleep(2000);
            }
        }
        catch (RemoteException | InterruptedException e) {
            e.printStackTrace();
        }
        paringFrame.dispose();
        Main.getCountDownLatch().countDown();
    }
}
