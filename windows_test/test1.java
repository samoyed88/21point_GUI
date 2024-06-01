package windows_test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class test1 extends JFrame {
    private ArrayList<String> poker;
    private List<Player> numPlayer;
    private JTextArea outputArea;
    private JButton startButton, drawButton, stopButton,endButton;
    private int currentPlayerIndex;// 当前玩家的索引
    private int round; // 当前游戏的回合数
    private JTextField remainingCards;
    private JTextArea scoreBoard;

    public test1() {
        setTitle("21點遊戲");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        poker = new ArrayList<>();
        numPlayer = new ArrayList<>();

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        
        startButton = new JButton("開始遊戲");
        drawButton = new JButton("抽牌");
        stopButton = new JButton("停止抽牌");
        endButton = new JButton("結束遊戲");
        
        //初始狀態可否點擊
        drawButton.setEnabled(false);
        stopButton.setEnabled(false);
        endButton.setEnabled(false);
        
        //按鈕監聽器
        startButton.addActionListener(new StartButtonListener());
        drawButton.addActionListener(new DrawButtonListener());
        stopButton.addActionListener(new StopButtonListener());
        endButton.addActionListener(new endButtonListener());
        
        // 创建用于显示剩余牌数量的文本框
        remainingCards = new JTextField(5);
        remainingCards.setEditable(false); // 设置为不可编辑
        remainingCards.setHorizontalAlignment(JTextField.CENTER); // 文本居中
        // 创建一个面板用于放置游戏信息和剩余牌数量
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(scrollPane, BorderLayout.CENTER); // 将游戏信息添加到面板中间
        infoPanel.add(remainingCards, BorderLayout.NORTH); // 将剩余牌数量文本框添加到面板顶部
        
        
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(drawButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(endButton);
        
        add(infoPanel, BorderLayout.CENTER); // 将面板添加到窗口中央
        add(buttonPanel, BorderLayout.SOUTH);
        
        //記分板
        scoreBoard = new JTextArea(5,10);
        scoreBoard.setEditable(false);
        // 将记分板放置在右侧
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(scoreBoard, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    private void createPoker() {
        poker.clear();
        String[] suits = {"C", "D", "H", "S"};
        String[] values = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        for (String suit : suits) {
            for (String value : values) {
                poker.add(suit + "-" + value);
            }
        }
    }

    private int drawCard() {
        if (poker.isEmpty()) return 0;
        int randomIndex = (int) (Math.random() * poker.size());
        String card = poker.remove(randomIndex);
        outputArea.append("抽到: " + card + "\n");
        updateRemainingCards(); // 抽牌後更新剩餘牌數量
        if (card.endsWith("J") || card.endsWith("Q") || card.endsWith("K")) {
            return 10;
        } else if (card.endsWith("1")) {
            return 1;
        } else {
            return Integer.parseInt(card.split("-")[1]);//讀取-後的文字(轉數字)
        }
    }
    
    private void updateScoreBoard() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < numPlayer.size(); i++) {
        	sb.append("玩家").append(i).append(":\r\n");
        	sb.append("  ").append(numPlayer.get(i).getScore()).append("分\r\n");
        	sb.append("  目前點數:").append(numPlayer.get(i).getNum()).append("\n");
        }
        scoreBoard.setText(sb.toString());
    }
    
    private void updateRemainingCards() {
        remainingCards.setText("剩餘牌數量: " + poker.size());
    }

    private void startGame(int players) {
        createPoker();
        updateRemainingCards(); // 在游戏开始时更新剩余牌数量
        numPlayer.clear();
        for (int i = 0; i <= players; i++) {
            numPlayer.add(new Player());
        }
        currentPlayerIndex = 1;
        round = 1;
        outputArea.append("遊戲開始\n");
        drawButton.setEnabled(true);
        stopButton.setEnabled(true);
        endButton.setEnabled(true);
        startButton.setEnabled(false);
    }

    private void grade() {
        for (int i = 1; i < numPlayer.size(); i++) {
            numPlayer.get(i).scoreCal(numPlayer.get(0).getNum());
            outputArea.append("玩家" + i + "的分數: " + numPlayer.get(i).getScore() + "\n");
            numPlayer.get(i).numReturn();
        }
        numPlayer.get(0).numReturn();
        updateScoreBoard();
    }

    private void finalScore() {
        int score = 0, winner = 0;
        for (int i = 1; i < numPlayer.size(); i++) {
            if (numPlayer.get(i).getScore() > score) {
                score = numPlayer.get(i).getScore();
                winner = i;
            }
        }
        outputArea.append("遊戲結束\n");
        outputArea.append("玩家" + winner + "是贏家，分數" + score + "分\n");
    }

    private class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = JOptionPane.showInputDialog("請輸入遊玩人數(1-4):");
            if (input != null && input.matches("[1-4]")) {
                int players = Integer.parseInt(input);
                startGame(players);
            } else {
                JOptionPane.showMessageDialog(null, "輸入錯誤，請重新輸入");
            }
        }
    }

    private class DrawButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentPlayerIndex <= numPlayer.size() - 1) {
                int num = drawCard();
                if (num == 0) {
                    JOptionPane.showMessageDialog(null, "牌已抽完");
                    finalScore();
                    drawButton.setEnabled(false);
                    stopButton.setEnabled(false);
                    endButton.setEnabled(false);
                    startButton.setEnabled(true);
                } else if (num == 1) {
                    String input = JOptionPane.showInputDialog("抽到1，選擇1請輸入a，選擇11請輸入b:");
                    if (input != null && input.equalsIgnoreCase("b")) {
                        num = 11;
                    }
                }
                numPlayer.get(currentPlayerIndex).numAdd(num);
                outputArea.append("玩家" + currentPlayerIndex + "目前點數: " + numPlayer.get(currentPlayerIndex).getNum() + "\n");
                if (numPlayer.get(currentPlayerIndex).getNum() > 21) {
                    outputArea.append("玩家" + currentPlayerIndex + "爆掉了!\n");
                    currentPlayerIndex++;
                }
            } 
            if(currentPlayerIndex>=numPlayer.size()) {
                // 莊家抽牌
            	outputArea.append("莊家開始抽牌\n");
                while (numPlayer.get(0).getNum() < 15) {
                    int num = drawCard();
                    if (num == 0) {
                        JOptionPane.showMessageDialog(null, "牌已抽完");
                        finalScore();
                        drawButton.setEnabled(false);
                        stopButton.setEnabled(false);
                        startButton.setEnabled(true);
                        return;
                    }
                    numPlayer.get(0).numAdd(num);
                }
                outputArea.append("莊家點數: " + numPlayer.get(0).getNum() + "\n");
                grade();
                currentPlayerIndex = 1;
                round++;
                outputArea.append("第" + round + "局結束，請按抽牌開始下一局\n");
            }
        	updateScoreBoard();
        }
    }

    private class endButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            currentPlayerIndex++;
            if (currentPlayerIndex > numPlayer.size() - 1) {
                drawButton.setEnabled(false);
                stopButton.setEnabled(false);
                startButton.setEnabled(true);
                finalScore();
            }
        }
    }
    private class StopButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	currentPlayerIndex++;
        }
    }
	    public static void main(String[] args) {
	        SwingUtilities.invokeLater(() -> {
	            test1 game = new test1();
	            game.setVisible(true);
	        });
	    }
}

