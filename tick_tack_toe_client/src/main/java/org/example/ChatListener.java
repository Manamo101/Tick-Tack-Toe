package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ChatListener extends Thread{
    private String previousMsg = "";
    private final JPanel messagePanel;
    private final String opponentMark;
    private final String token;
    public ChatListener(JPanel messagePanel, String opponentMark, String token) throws IOException {
        this.messagePanel = messagePanel;
        this.opponentMark = opponentMark;
        this.token = token;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket clientSocket = new Socket("localhost", 6666);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out.println(token + " " + "get message" + " " + opponentMark);
                String msg = in.readLine();
                if (!msg.equals(previousMsg)) {
                    JLabel msgLabel = new JLabel(opponentMark + ": " + msg);
                    msgLabel.setForeground(Color.BLUE);
                    messagePanel.add(msgLabel);
                    messagePanel.validate();
                    previousMsg = msg;
                }
                out.close();
                in.close();
                Thread.sleep(2000);
            } catch (IOException e) {
                System.out.println("chat listener thread problem");
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
