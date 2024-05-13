package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.Arrays;

public class GameFrame extends JFrame {
    private GameService gameService;
    private String tokenID;
    private String mark;
    private int wins = 0, draws = 0, defeats = 0;
    private final JButton[] cells;
    private final JLabel winScoreLabel;
    private final JLabel drawScoreLabel;
    private final JLabel defeatScoreLabel;
    private final JLabel waitingLabel;
    private final JTextField message;
    private final JPanel messagesPanel;

    GameFrame(GameService gameService){
        this.gameService = gameService;
        new PairingFrame(this, gameService);
    }

    {
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.setPreferredSize(new Dimension(500,600));

        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setPreferredSize(new Dimension(300,600));

        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.PAGE_AXIS));
        JScrollPane scrollPane = new JScrollPane(messagesPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        message = new JTextField();
        message.setEnabled(false);
        message.addActionListener(e -> {
            JLabel msg = new JLabel(mark+ ": " + message.getText());
            msg.setForeground(Color.GREEN);

            try {
                Socket clientSocket = new Socket("localhost", 6666);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out.println(tokenID + " " + "new message" + " " + mark + " " + message.getText());
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException ex) {
                System.out.println("chat listening error");
                throw new RuntimeException(ex);
            }
            messagesPanel.add(msg);
            message.setText("");
            messagesPanel.revalidate();
        });

        JLabel chatTitle = new JLabel("Chat", JLabel.CENTER);
        chatTitle.setFont(new Font(Font.DIALOG, Font.PLAIN, 30));

        chatPanel.add(chatTitle, BorderLayout.NORTH);
        chatPanel.add(message, BorderLayout.SOUTH);
        chatPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        gamePanel.add(infoPanel, BorderLayout.NORTH);

        JLabel title = new JLabel("Tick Tack Toe");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setAlignmentY(Component.CENTER_ALIGNMENT);
        title.setFont(new Font(Font.DIALOG,Font.PLAIN,30));
        infoPanel.add(title);

        JPanel scoreInfo = new JPanel(new GridLayout(1,3));
        winScoreLabel = new JLabel("wins: " + wins);
        drawScoreLabel = new JLabel("draws: " + draws);
        defeatScoreLabel = new JLabel("defeats: " + defeats);
        scoreInfo.add(winScoreLabel);
        scoreInfo.add(drawScoreLabel);
        scoreInfo.add(defeatScoreLabel);
        infoPanel.add(scoreInfo);

        waitingLabel = new JLabel();
        waitingLabel.setPreferredSize(new Dimension(100,50));
        waitingLabel.setFont(new Font(Font.DIALOG,Font.PLAIN,30));
        waitingLabel.setForeground(Color.BLACK);
        infoPanel.add(waitingLabel);

        JPanel boardPanel = new JPanel(new GridLayout(3,3));
        cells = new JButton[9];
        for (int i = 0; i < 9; i++){
            JButton button = new JButton();
            button.setFont(new Font(Font.DIALOG, Font.PLAIN, 80));
            button.addActionListener(e -> {
                button.setText(mark);
                button.setEnabled(false);
                button.setBackground(Color.WHITE);
                try {
                    gameService.setMark(tokenID, boardPanel.getComponentZOrder(button), mark);
                    disableButtons();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            });
            button.setFocusable(false);
            cells[i] = button;
            boardPanel.add(cells[i]);
        }
        gamePanel.add(boardPanel, BorderLayout.CENTER);
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(gamePanel);
        this.add(chatPanel);
        this.setVisible(true);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                try {
                    gameService.disconnected(tokenID);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
    public void enableChat() {
        message.setEnabled(true);
    }

    public String turnWaiter() throws InterruptedException, RemoteException {
        new OpponentTurnWaiter(tokenID, opponentMark(), gameService, waitingLabel).start();
        return gameService.whoseTurn(tokenID);
    }

    public void opponentTurn() {
        waitingLabel.setText("Opponent's move");
        disableButtons();
    }

    public void prepareBoard() {
        for (JButton cells: cells) {
            cells.setText("");
            cells.setBackground(Color.WHITE);
            waitingLabel.setForeground(Color.BLACK);
            cells.setEnabled(true);
        }
    }

    public String opponentMark(){
        return mark.equals("X") ? "O" : "X";
    }
    public void disableButtons() {
        Arrays.stream(cells).forEach(e -> e.setEnabled(false));
    }

    public void arrageChange() {
        waitingLabel.setText("Your move. You're " + mark);
        try {
            String[] board = gameService.getBoard(tokenID);
            for (int i = 0; i < 9; i++){
                if (board[i].isEmpty()){
                    cells[i].setEnabled(true);
                }
                    else if (board[i].equals("X")){
                    cells[i].setText("X");
                    cells[i].setEnabled(false);
                }
                    else if (board[i].equals("O")){
                    cells[i].setText("O");
                    cells[i].setEnabled(false);
                }
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void arrangeChange(String result) {
        Color color = Color.BLUE;
        if (result.equals("wins X")) {
            if (mark.equals("X")) {
                color = Color.GREEN;
                wins++;
                waitingLabel.setText("You win!");
                winScoreLabel.setText("wins:" + wins);
            }
            if (mark.equals("O")) {
                color = Color.RED;
                defeats++;
                waitingLabel.setText("You lose!");
                defeatScoreLabel.setText("defeats :" + defeats);

            }

            disableButtons();

            try {
                int[] winningSequence = gameService.winningSequence(tokenID);
                for (int i : winningSequence) {
                    cells[i].setBackground(color);
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        else if (result.equals("wins O")) {
            if (mark.equals("O")) {
                color = Color.GREEN;
                wins++;
                waitingLabel.setText("You win!");
                winScoreLabel.setText("wins :" + wins);
            }
            if (mark.equals("X")) {
                color = Color.RED;
                defeats++;
                waitingLabel.setText("You lose!");
                defeatScoreLabel.setText("defeats :" + defeats);
            }
            disableButtons();
            try {
                int[] winningSequence = gameService.winningSequence(tokenID);
                for (int i : winningSequence) {
                    cells[i].setBackground(color);
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            draws++;
            drawScoreLabel.setText("draws:" + draws);
            waitingLabel.setText("Draw!");
        }
        waitingLabel.setForeground(color);
    }
    public void newGame() {
        if (mark.equals("X")) {
            try {
                gameService.newGame(tokenID);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void disconnectInfo() {
        JOptionPane.showMessageDialog(this, "Opponent disconnected :/", "ERROR", JOptionPane.ERROR_MESSAGE);
        try {
            gameService.disconnected(tokenID);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        System.exit(1);
    }

    public String getTokenID() {
        return tokenID;
    }
    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }
    public String getMark() {
        return mark;
    }
    public void setMark(String mark) {
        this.mark = mark;
    }
    public JPanel getMessagesPanel() {
        return messagesPanel;
    }
}
