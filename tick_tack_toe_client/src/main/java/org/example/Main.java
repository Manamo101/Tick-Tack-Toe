package org.example;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.CountDownLatch;

public class Main {
    private static CountDownLatch waitForOpponent;
    public static void main(String[] args) throws NotBoundException {
        GameService service;
        try {
            Registry registry = LocateRegistry.getRegistry();
            service = (GameService) registry.lookup("output");

            waitForOpponent = new CountDownLatch(1);
            GameFrame game = new GameFrame(service);
            waitForOpponent.await();
            game.enableChat();
            ChatListener chatListener = new ChatListener(game.getMessagesPanel(), game.opponentMark(), game.getTokenID());
            chatListener.start();

            while (true) {
                game.prepareBoard();
                if (!service.whoseTurn(game.getTokenID()).equals(game.getMark())){
                    game.opponentTurn();
                }
                String turn;
                do {
                    waitForOpponent = new CountDownLatch(1);
                    turn = game.turnWaiter();
                    if (!service.isPared(game.getTokenID())) {
                        game.disconnectInfo();
                    }
                    waitForOpponent.await();
                    game.arrageChange();
                }
                while (turn.equals("X") || turn.equals("O"));
                game.arrangeChange(turn);
                Thread.sleep(3000);
                game.newGame();
                Thread.sleep(1000);
            }
        }
        catch (RemoteException e){
            System.out.println("Server is not available");
        } catch (InterruptedException e) {
            System.out.println("Thread problem");
        } catch (IOException e) {
            System.out.println("Chat problem");
            throw new RuntimeException(e);
        }
    }
    public static CountDownLatch getCountDownLatch(){
        return waitForOpponent;
    }
}