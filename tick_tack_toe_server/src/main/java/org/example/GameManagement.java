package org.example;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

public class GameManagement implements GameService{
    private final ArrayList<Game> games;
    GameManagement(){
        games = new ArrayList<>();
    }
    @Override
    public String getToken() throws RemoteException {
        games.add(new Game());
        String token = games.get(games.size()-1).getTokenID();
        games.get(games.size()-1).setTurn(new Random().nextInt(2) == 0 ? "X": "O");
        System.out.println("new game generated with token: " + token);
        return token;
    }


    @Override
    public boolean pair(String token) throws RemoteException {
        Game game = findGame(token);
        if (game != null){
            game.setOpponentConnected(true);
            System.out.println("Players paired. Token: " + game.getTokenID());
            return true;
        }
        return false;
    }

    @Override
    public boolean isPared(String token) throws RemoteException {
        Game game = findGame(token);
        return game != null && game.isOpponentConnected();
    }

    @Override
    public String whoseTurn(String token) throws RemoteException {
        Game game = findGame(token);
        return game != null ? game.getTurn() : null;
    }

    @Override
    public int[] winningSequence(String token) throws RemoteException {
        Game game = findGame(token);
        return game != null ? game.getWinningSequence() : null;
    }

    @Override
    public void newGame(String token) throws RemoteException {
        findGame(token).newRound();
    }
    @Override
    public void disconnected(String token) throws RemoteException {
        Game game = findGame(token);
        if (game.isOpponentConnected()){
            game.setOpponentConnected(false);
            System.out.println("player disconnected. Token: " + game.getTokenID());
        }
        else {
            System.out.println("Both players disconnected. Game ended. Token: " + game.getTokenID());
        }
    }

    @Override
    public String[] getBoard(String token) throws RemoteException {
        Game game = findGame(token);
        return game != null ? game.getBoard() : null;
    }

    @Override
    public void setMark(String token, int index, String mark) throws RemoteException {
        Game game = findGame(token);
        game.setMark(index, mark);
        int[] winningSequence;
        if ( (winningSequence = GameEnding.doesWin("X", game.getBoard())) != null) {
            game.setWinningSequence(winningSequence);
            game.setTurn("wins X");
            game.updateStats("X");
            System.out.println("Game wins X. Stats: player X wins: " + game.getWinsX() + ", player O wins: " + game.getWinsO()
                    + ", draws: " + game.getDraws() + " Token: " + game.getTokenID());
        }
        else if ((winningSequence = GameEnding.doesWin("O", game.getBoard())) != null) {
            game.setWinningSequence(winningSequence);
            game.setTurn("wins O");
            game.updateStats("O");
            System.out.println("Game wins O. Stats: player X wins: " + game.getWinsX() + ", player O wins: " + game.getWinsO()
                    + ", draws: " + game.getDraws() + " Token: " + game.getTokenID());
        }
        else if (GameEnding.isDraw(game.getBoard())) {
            game.setTurn("draw");
            game.updateStats("draw");
            System.out.println("Draw. Stats: player X wins: " + game.getWinsX() + ", player O wins: " + game.getWinsO()
                    + ", draws: " + game.getDraws() + " Token: " + game.getTokenID());
        }
        else {
            game.switchTurn();
        }
    }

    private Game findGame(String token){
        Game returns = null;
        for (Game game: games){
            if (game.getTokenID().equals(token)){
                returns = game;
            }
        }
        return returns;
    }

    public ArrayList<Game> getGames() {
        return games;
    }
}
