package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ChatManager extends Thread{
    private final ArrayList<Game> games;
    private final ServerSocket serverSocket;

    public ChatManager(int port,ArrayList<Game> games) throws IOException {
        this.games = games;
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String line = in.readLine();
                String token = getToken(line);
                for (Game game : games) {
                    if (game.getTokenID().equals(token)){
                        String call = getCall(line);
                        if (call.equals("get message X")) {
                            out.println(game.getLastXMessage());
                        }
                        else if (call.equals("get message O")) {
                            out.println(game.getLastOMessage());
                        }
                        else if (call.contains("new message X")) {
                            game.setLastXMessage(getMessage(call));
                        }
                        else if (call.contains("new message O")) {
                            game.setLastOMessage(getMessage(call));
                        }
                        else {
                            out.println("something goes wrong. Wrong call format");
                        }
                    }
                }
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("tcp ip socket problem in Thread ChatManager");
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
    private String getToken(String line) {
        return new Scanner(line).next();
    }
    private String getCall(String line) {
        return line.replace(getToken(line), "").trim();
    }
    private String getMessage(String message) {
        return message.replace("new message X", "").replace("new message O", "").trim();
    }
}
