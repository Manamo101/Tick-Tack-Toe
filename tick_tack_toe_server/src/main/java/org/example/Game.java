package org.example;

import java.util.Random;
import java.util.UUID;

public class Game {
    private final String tokenID;
    private boolean opponentConnected = false;
    private String turn;
    private String[] board;
    private int[] winningSequence;
    private int winsX = 0;
    private int winsO = 0;
    private int draws = 0;
    private String lastXMessage = "";
    private String lastOMessage = "";

    Game(){
        tokenID = UUID.randomUUID().toString();
        board = new String[9];
        for (int i = 0; i < 9; i++) {
            board[i] = "";
        }
    }

    public void switchTurn() {
        if (turn.equals("X")){
            setTurn("O");
        }
        else {
            setTurn("X");
        }
    }

    public void newRound() {
        turn = new Random().nextInt(2) == 0 ? "X": "O";
        winningSequence = null;
        board = new String[9];
        for (int i = 0; i < 9; i++) {
            board[i] = "";
        }
    }

    public void updateStats(String s) {
        if (s.equals("X")) {
            winsX++;
        }
        else if (s.equals("O")){
            winsO++;
        }
        else {
            draws++;
        }
    }

    public void setMark(int i, String mark) {
        board[i] = mark;
    }
    public String getTokenID() {
        return tokenID;
    }
    public boolean isOpponentConnected() {
        return opponentConnected;
    }
    public void setOpponentConnected(boolean opponentConnected) {
        this.opponentConnected = opponentConnected;
    }
    public String getTurn() {
        return turn;
    }
    public void setTurn(String turn) {
        this.turn = turn;
    }
    public String[] getBoard() {
        return board;
    }
    public int[] getWinningSequence() {
        return winningSequence;
    }
    public void setWinningSequence(int[] winningSequence) {
        this.winningSequence = winningSequence;
    }
    public int getWinsX() {
        return winsX;
    }
    public int getWinsO() {
        return winsO;
    }
    public int getDraws() {
        return draws;
    }
    public synchronized String getLastXMessage() {
        return lastXMessage;
    }
    public synchronized void setLastXMessage(String lastXMessage) {
        this.lastXMessage = lastXMessage;
    }
    public synchronized String getLastOMessage() {
        return lastOMessage;
    }
    public synchronized void setLastOMessage(String lastOMessage) {
        this.lastOMessage = lastOMessage;
    }
}