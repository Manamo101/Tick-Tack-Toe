package org.example;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameService extends Remote {
    String getToken() throws RemoteException;
    boolean pair(String token) throws RemoteException;
    boolean isPared(String token) throws RemoteException;
    void disconnected(String token) throws RemoteException;
    String whoseTurn(String token) throws RemoteException;
    void setMark(String token, int index, String mark) throws RemoteException;
    String[] getBoard(String token) throws RemoteException;
    int[] winningSequence(String token) throws RemoteException;
    void newGame(String token) throws RemoteException;
}
