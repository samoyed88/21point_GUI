package windows_test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class test1 extends JFrame {
    private ArrayList<String> poker;//一副牌
    private List<Player> numPlayer;//玩家物件陣列
    private JTextArea outputArea;//主顯示介面
    private JButton startButton, drawButton, stopButton,endButton;//四個按鈕
    private int currentPlayerIndex;// 玩家的索引值
    private int round; // 當前遊戲的回合數
    private int count=1;
    private JTextField remainingCards;//卡片剩餘面板
    private JTextArea scoreBoard;//記分板

    public test1() {
        setTitle("21點遊戲");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        poker = new ArrayList<>();
        numPlayer = new ArrayList<>();

        outputArea = new JTextArea();// 創建一個文字區域，用於顯示遊戲信息
        outputArea.setEditable(false);// 設置文本區域為不可編輯
        JScrollPane scrollPane = new JScrollPane(outputArea);// 將文本區域放入滾動窗格中
        
        //創建按鈕
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
        
        // 創建一個文字框，用於顯示剩餘牌數量
        remainingCards = new JTextField(5);
        remainingCards.setEditable(false); // 設置文字框為不可編輯
        remainingCards.setHorizontalAlignment(JTextField.CENTER); // 文本居中
        
        // 創建一個面板，用於顯示剩餘牌數量
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(scrollPane, BorderLayout.CENTER); // 將遊戲信息添加到面板中間
        infoPanel.add(remainingCards, BorderLayout.NORTH); // 將剩餘牌數量文字框添加到面板頂部
        
        
        //按鈕面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(drawButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(endButton);
        
        add(infoPanel, BorderLayout.CENTER); // 将面板添加到窗口中央
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 創建一個面板，用於顯示各玩家分數及即時牌點數
        scoreBoard = new JTextArea(5,10);
        scoreBoard.setEditable(false);
        // 將記分板放置於右側中間
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(scoreBoard, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }
    
    //創建一副牌
    private void createPoker() {
        poker.clear();//清空poker陣列
        String[] suits = {"C", "D", "H", "S"};
        String[] values = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        for (String suit : suits) {
            for (String value : values) {
                poker.add(suit + "-" + value);
            }
        }
    }

    //抽牌方法
    private int drawCard() {
        if (poker.isEmpty()) return 0;//沒牌傳回0
        int randomIndex = (int) (Math.random() * poker.size());//隨機一個數字(0~陣列的最大值)
        String card = poker.remove(randomIndex);
        outputArea.append("抽到: " + card + "\n");
        updateRemainingCards(); // 抽牌後更新剩餘牌數量
        updateScoreBoard();//抽牌後更新記分板
        if (card.endsWith("J") || card.endsWith("Q") || card.endsWith("K")) {
            return 10;
        } else if (card.endsWith("1")) {
            return 1;
        } else {
            return Integer.parseInt(card.split("-")[1]);//讀取-後的文字(轉數字)
        }
    }
    
    //更新記分板
    private void updateScoreBoard() {
        StringBuilder sb = new StringBuilder();
        sb.append("莊家").append(":\r\n");
    	sb.append("  ").append(numPlayer.get(0).getScore()).append("分\r\n");
    	sb.append("  目前點數:").append(numPlayer.get(0).getNum()).append("\n");
        for (int i = 1; i < numPlayer.size(); i++) {
        	sb.append("玩家").append(i).append(":\r\n");
        	sb.append("  ").append(numPlayer.get(i).getScore()).append("分\r\n");
        	sb.append("  目前點數:").append(numPlayer.get(i).getNum()).append("\n");
        }
        scoreBoard.setText(sb.toString());
    }
    //更新剩餘牌數
    private void updateRemainingCards() {
        remainingCards.setText("剩餘牌數量: " + poker.size());
    }
    
    //開始遊戲
    private void startGame(int players) {
        createPoker();
        updateRemainingCards(); // 更新剩餘牌數
        numPlayer.clear(); //清空numPlayer物件陣列
        for (int i = 0; i <= players; i++) {
            numPlayer.add(new Player());//根據玩家數建立物件
        }
        currentPlayerIndex = 1;
        round = 1;
        count=1;
        outputArea.append("遊戲開始\n");
        drawButton.setEnabled(true);
        stopButton.setEnabled(true);
        endButton.setEnabled(true);
        startButton.setEnabled(false);
    }
    //分數計算
    private void grade() {
        for (int i = 1; i < numPlayer.size(); i++) {
            numPlayer.get(i).scoreCal(numPlayer.get(0).getNum());
            outputArea.append("玩家" + i + "的分數: " + numPlayer.get(i).getScore() + "\n");
            numPlayer.get(i).numReturn();
        }
        numPlayer.get(0).numReturn();
        updateScoreBoard();
    }
    //最終分數及贏家顯示
    private void finalScore() {
        int score=0;
        List<Integer> winners = new ArrayList<>();
        for (int i = 1; i < numPlayer.size(); i++) {
            int playerScore = numPlayer.get(i).getScore();
            if (playerScore > score) {
                score = playerScore;
                winners.clear();//清除之前的最高分玩家
                winners.add(i); //新增新的最高分玩家
            } else if (playerScore == score) {
                winners.add(i); //增加新的最高分玩家
            }
        }
        outputArea.append("遊戲結束\n");
        if (winners.size() == 1) {
            int winner = winners.get(0);
            outputArea.append("玩家" + winner + "是贏家，分數：" + score + "分\n");
        } else {
            outputArea.append("玩家");
            for (int i = 0; i < winners.size(); i++) {
                int winner = winners.get(i);
                outputArea.append(" " + winner);
                if (i < winners.size() - 1) {
                    outputArea.append(" 和");
                }
            }
            outputArea.append(" 平手，分數：" + score + "分\n");
        }
    }
    
    //開始遊戲按鈕被點擊後動作
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
    
    //抽牌按鈕被點擊後動作
    private class DrawButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	//第一輪及第二輪為自動發牌
        	//第一輪抽牌(莊家及玩家各發一張)
        	if(count==1) {
        		for(int i=0;i<numPlayer.size();i++) {
        			if(i==0) outputArea.append("莊家");
        			else outputArea.append("玩家"+i);
        			int num = drawCard();
            		if (num == 0) {
                        JOptionPane.showMessageDialog(null, "牌已抽完");
                        finalScore();
                        drawButton.setEnabled(false);
                        stopButton.setEnabled(false);
                        endButton.setEnabled(false);
                        startButton.setEnabled(true);
                    } else if (num == 1) {
                    	JOptionPane.showMessageDialog(null,"玩家"+i+"抽到A點數暫時為11，若爆牌自動變為1");
                        numPlayer.get(i).A(1);
                        num=11;
                    }
            		numPlayer.get(i).numAdd(num);
        		}
        		//第二輪抽牌：玩家各發一張
        		for(int i=1;i<numPlayer.size();i++) {
        			outputArea.append("玩家"+i);
        			int num = drawCard();
            		if (num == 0) {
                        JOptionPane.showMessageDialog(null, "牌已抽完");
                        finalScore();
                        drawButton.setEnabled(false);
                        stopButton.setEnabled(false);
                        endButton.setEnabled(false);
                        startButton.setEnabled(true);
                        return;
                    } else if (num == 1) {
                    	JOptionPane.showMessageDialog(null,"玩家"+i+"抽到A點數暫時為11，若爆牌自動變為1");
                        numPlayer.get(i).A(1);
                        num=11;
                    }
            		numPlayer.get(i).numAdd(num);
        		}
        		count++;
        		outputArea.append("玩家1選擇是否要牌\n");
        	}
        	//第三輪為各玩家自行選擇是否要牌
        	else if (currentPlayerIndex <= numPlayer.size() - 1) {
                int num = drawCard();
                if (num == 0) {
                    JOptionPane.showMessageDialog(null, "牌已抽完");
                    finalScore();
                    drawButton.setEnabled(false);
                    stopButton.setEnabled(false);
                    endButton.setEnabled(false);
                    startButton.setEnabled(true);
                    return;
                } else if (num == 1) {
                	JOptionPane.showMessageDialog(null,"玩家"+currentPlayerIndex+"抽到A，點數暫時為11，若爆牌自動變為1");
                    numPlayer.get(currentPlayerIndex).A(1);
                    num=11;
                }
                numPlayer.get(currentPlayerIndex).numAdd(num);
                outputArea.append("玩家" + currentPlayerIndex + "目前點數: " + numPlayer.get(currentPlayerIndex).getNum() + "\n");
                if (numPlayer.get(currentPlayerIndex).getNum() > 21) {
                    outputArea.append("玩家" + currentPlayerIndex + "爆掉了!\n");
                    if((currentPlayerIndex+1)<numPlayer.size())outputArea.append("換下一位玩家:玩家"+(currentPlayerIndex+1)+"\n");
                    else outputArea.append("按抽牌結束此回合\n");
                    currentPlayerIndex++;
                }
            } 
        	else if(currentPlayerIndex>=numPlayer.size()) {
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
                outputArea.append("第" + round + "局結束，請按抽牌開始下一局\n");
                round++;
                count=1;
            }
        	updateScoreBoard();
        }
    }
    
    //結束按鈕被點擊後的動作
    private class endButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
                drawButton.setEnabled(false);
                stopButton.setEnabled(false);
                startButton.setEnabled(true);
                finalScore();
        }
    }
    
    //停止抽牌被點擊後的動作
    private class StopButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	currentPlayerIndex++;
        	if(currentPlayerIndex<numPlayer.size())outputArea.append("換下一位玩家\n");
        	else outputArea.append("按抽牌結束此回合\n");
        }
    }
    
    //主程式
	    public static void main(String[] args) {
	        SwingUtilities.invokeLater(() -> {
	            test1 game = new test1();
	            game.setVisible(true);
	        });
	    }
}

