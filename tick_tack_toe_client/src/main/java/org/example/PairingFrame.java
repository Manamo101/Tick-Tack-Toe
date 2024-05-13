package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class PairingFrame extends JFrame implements ActionListener {
    private final GameService gameService;
    private final JButton joinButton;
    private final JButton generateButton;
    private final JTextField tokenField;
    private final JLabel infoLabel;
    private final Component parent;
    PairingFrame(Component o, GameService gameService){
        parent = o;
        this.setLocationRelativeTo(o);
        this.gameService = gameService;
    }
    {
        JLabel text = new JLabel("paste tokenID or generate one");
        this.add(text);

        tokenField = new JTextField();
        tokenField.setPreferredSize(new Dimension(250,20));
        this.add(tokenField);

        joinButton = new JButton("join");
        joinButton.setFocusPainted(false);
        joinButton.addActionListener(this);
        this.add(Box.createRigidArea(new Dimension(30,0)));
        this.add(joinButton);

        generateButton = new JButton("Generate code");
        generateButton.setFocusPainted(false);
        generateButton.addActionListener(this);
        generateButton.setFocusPainted(false);
        this.add(generateButton);

        infoLabel = new JLabel();
        infoLabel.setVisible(false);
        this.add(Box.createRigidArea(new Dimension(30,0)));
        this.add(infoLabel);

        this.setLayout(new FlowLayout());
        this.setTitle("Pairing");
        this.setSize(300,200);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setAlwaysOnTop(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String tokenID;
        if (e.getSource() == generateButton) {
            try {
                tokenID = gameService.getToken();
                ((GameFrame) parent).setMark("X");
                ((GameFrame) parent).setTokenID(tokenID);
                tokenField.setText(tokenID);
                joinButton.setEnabled(false);
                generateButton.setEnabled(false);
                infoLabel.setText("share this code with your opponent");
                infoLabel.setVisible(true);
                this.revalidate();
                new SessionWaiter(tokenID, gameService, this).start();
            } catch (RemoteException ex) {
                ex.getMessage();
            }
        }
        if (e.getSource() == joinButton) {
            try {
                if (gameService.pair(tokenField.getText())){
                    tokenID = tokenField.getText();
                    ((GameFrame) parent).setMark("O");
                    ((GameFrame) parent).setTokenID(tokenID);
                    Main.getCountDownLatch().countDown();
                    this.dispose();
                }
                else{
                    infoLabel.setText("invalid token or occupied session");
                    infoLabel.setForeground(Color.RED);
                    infoLabel.setVisible(true);
                }
            } catch (RemoteException ex) {
                ex.getMessage();
            }

        }
    }
}
