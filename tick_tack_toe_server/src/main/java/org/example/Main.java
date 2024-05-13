package org.example;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Main {
    public static void main(String[] args) throws IOException {
        // RMI
        GameManagement service = new GameManagement();
        GameService stub = (GameService) UnicastRemoteObject.exportObject( service, 0);
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("output", stub);

        // TCP\IP
        new ChatManager(6666, service.getGames()).start();
    }
}