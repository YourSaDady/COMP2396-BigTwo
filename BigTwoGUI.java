import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.awt.*;

/**
 * The BigTwoGUI class implements the CardGameUI interface. It is used to build a GUI for
 * the Big Two card game and handle all user actions.
 *
 * @author Yichuan Song 3035844871
 */

public class BigTwoGUI implements CardGame {

	public boolean[] exist = new boolean[4];

    private BigTwo game;
    private ArrayList<CardGamePlayer> playerList; // the list of players
    private ArrayList<Hand> handsOnTable; // the list of hands played on the table
    private boolean[] selected = new boolean[MAX_CARD_NUM];
    private int activePlayer = -1;
    private JFrame frame;
    private JPanel bigTwoPanel;
    
    private Image[][] icons;
    
    private JButton playButton;
    private JButton passButton;
    private JTextArea msgArea;
    private JTextArea chatArea;
    
    private JLabel InputboxLabel;
    private JTextField chatInput;
    
    private final static int MAX_CARD_NUM = 13; // max. no. of cards each player holds

	public void setExist(int playerID) {
		exist[playerID] = true;
	}
	public void setNotExist(int playerID) {
		exist[playerID] = false;
	}
	public boolean[] getExist() {
		return exist;
	}
	public void updates(BigTwo game) {
		this.game = game;
		this.playerList = game.getPlayerList();
	}


    /**
     * A constructor for creating a BigTwoGUI.
     * 
     * @param game  a reference to a Big Two card game associates with this GUI
     * 
     */
    public BigTwoGUI(BigTwo game) {
    	this.game = game;
    	this.playerList = game.getPlayerList();
    	this.handsOnTable = game.getHandsOnTable();
    	this.setActivePlayer(game.getCurrentPlayerIdx());
    	
    	this.frame = new JFrame("ビッグツー！！");
    	frame.setLayout(new BorderLayout());
    	
		this.bigTwoPanel = new BigTwoPanel(); 
	    bigTwoPanel.setLayout(new BoxLayout(bigTwoPanel, BoxLayout.Y_AXIS));
    	
	    chatArea = new JTextArea(15, 20);
        chatArea.setLineWrap(true);
        chatArea.setBackground(new Color(250, 240, 230));
        chatArea.setFont(new Font("Segoe Print", Font.ITALIC, 12));
        chatArea.setEditable(false);
        msgArea = new JTextArea(10,40);
        msgArea.setLineWrap(true);
        msgArea.setBackground(new Color(230, 230, 250));
        msgArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        msgArea.setEditable(false);
        chatInput = new JTextField( 40);
    	
    	this.loadImages();
    	this.go();
    }
    
    /**
     * Prepares the local images into a 2D Image array.
     */
    public void loadImages() {
    	this.icons = new Image[5][MAX_CARD_NUM];
    	// diamonds
    	this.icons[0][0] = new ImageIcon("src/ad.gif").getImage();
    	for (int i=1; i<9; i++) {// 2 - 9
    		this.icons[0][i] = new ImageIcon("src/" + (i+1) + "d" + ".gif").getImage();
    	}
    	this.icons[0][9] = new ImageIcon("src/td.gif").getImage();
    	this.icons[0][10] = new ImageIcon("src/jd.gif").getImage();
    	this.icons[0][11] = new ImageIcon("src/qd.gif").getImage();
    	this.icons[0][12] = new ImageIcon("src/kd.gif").getImage();
    	// clubs
    	this.icons[1][0] = new ImageIcon("src/ac.gif").getImage();
    	for (int i=1; i<9; i++) {// 2 - 9
    		this.icons[1][i] = new ImageIcon("src/" + (i+1) + "c" + ".gif").getImage();
    	}
    	this.icons[1][9] = new ImageIcon("src/tc.gif").getImage();
    	this.icons[1][10] = new ImageIcon("src/jc.gif").getImage();
    	this.icons[1][11] = new ImageIcon("src/qc.gif").getImage();
    	this.icons[1][12] = new ImageIcon("src/kc.gif").getImage();
    	// hearts
    	this.icons[2][0] = new ImageIcon("src/ah.gif").getImage();
    	for (int i=1; i<9; i++) {// 2 - 9
    		this.icons[2][i] = new ImageIcon("src/" + (i+1) + "h" + ".gif").getImage();
    	}
    	this.icons[2][9] = new ImageIcon("src/th.gif").getImage();
    	this.icons[2][10] = new ImageIcon("src/jh.gif").getImage();
    	this.icons[2][11] = new ImageIcon("src/qh.gif").getImage();
    	this.icons[2][12] = new ImageIcon("src/kh.gif").getImage();
    	// spades
    	this.icons[3][0] = new ImageIcon("src/as.gif").getImage();
    	for (int i=1; i<9; i++) {// 2 - 9
    		this.icons[3][i] = new ImageIcon("src/" + (i+1) + "s" + ".gif").getImage();
    	}
    	this.icons[3][9] = new ImageIcon("src/ts.gif").getImage();
    	this.icons[3][10] = new ImageIcon("src/js.gif").getImage();
    	this.icons[3][11] = new ImageIcon("src/qs.gif").getImage();
    	this.icons[3][12] = new ImageIcon("src/ks.gif").getImage();
    	// card back
    	this.icons[4][0] = new ImageIcon("src/b.png").getImage();
    	// avatars (waifus)
    	this.icons[4][1] = new ImageIcon("src/Ciki.png").getImage();
    	this.icons[4][2] = new ImageIcon("src/Hitagi.png").getImage();
    	this.icons[4][3] = new ImageIcon("src/Nadeco.png").getImage();
    	this.icons[4][4] = new ImageIcon("src/Shinobu.png").getImage();
    	this.icons[4][5] = new ImageIcon("src/Ciki1.png").getImage();
    	this.icons[4][6] = new ImageIcon("src/Hitagi1.png").getImage();
    	this.icons[4][7] = new ImageIcon("src/Nadeco1.png").getImage();
    	this.icons[4][8] = new ImageIcon("src/Shinobu1.png").getImage();
    }
    
    /**
     * This setups the GUI.
     */
    public void go() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // At top is a menu bar
  		JMenuBar menuBar = new JMenuBar();
  		frame.setJMenuBar(menuBar);
  		JMenuItem connect = new JMenuItem("Connect");
  		menuBar.add(connect);
  		JMenuItem quit = new JMenuItem("Quit");
  		menuBar.add(quit);
  		connect.addActionListener(new ConnectMenuItemListener());
  		quit.addActionListener(new QuitMenuItemListener());
        
  		// At the bottom is a buttonPanel, with the Play button and the Pass button, and a window for typing inputs.
        this.playButton = new JButton("PLAY!");
        this.playButton.addActionListener(new PlayButtonListener());
        this.passButton = new JButton("PASS!");
        this.passButton.addActionListener(new PassButtonListener());
        InputboxLabel = new JLabel("Message: ");
        
        chatInput.addActionListener(new InputListener());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.gray);
        buttonPanel.add(playButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(passButton);
        buttonPanel.add(InputboxLabel);
        buttonPanel.add(chatInput);
        
        // At the right is a textPanel, with the message area and the chat area, also implemented scrolls.
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setPreferredSize(new Dimension(450,800));
        
        JScrollPane msgScroll = new JScrollPane (msgArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollPane chatScroll = new JScrollPane (chatArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        msgScroll.setPreferredSize(new Dimension(300, 0));
        chatScroll.setPreferredSize(new Dimension(300, 0));
		DefaultCaret msgCaret = (DefaultCaret)msgArea.getCaret();
		DefaultCaret chatCaret = (DefaultCaret)chatArea.getCaret();
		msgCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		chatCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		textPanel.add(msgScroll);
		textPanel.add(chatScroll);

        frame.add(BorderLayout.CENTER, bigTwoPanel);
        frame.add(BorderLayout.SOUTH, buttonPanel);
        frame.add(BorderLayout.EAST, textPanel);
       
        frame.setSize(1200, 800);
        frame.setVisible(true);
    }
    
    /**
     * Repaints the GUI.
     */
    public void repaint() {
    	this.resetSelected();
    	this.frame.repaint();
    }
    
    /**
     * An implementation for retrieving the number of players.
     * 
     * @return the number of players in this card game
     */
    public int getNumOfPlayers() {
    	return game.getNumOfPlayers();
    }
    
    /**
     * An implementation for retrieving the Big Two deck.
     * 
     * @return the deck of cards being used in this card game
     */
    public Deck getDeck() {
    	return game.getDeck();
    }
    
    /**
     * An implementation for retrieving a list of players in this game.
     * 
     * @return the list of players in this card game
     */
    public ArrayList<CardGamePlayer> getPlayerList() {
    	return game.getPlayerList();
    }
    
    /**
     * An implementation for retrieving the list of hands.
     * 
     * @return the list of hands played on the table
     */
    public ArrayList<Hand> getHandsOnTable() {
    	return game.getHandsOnTable();
    }
    
    /**
     * An implementation for retrieving the index of the current player.
     * 
     * @return the index of the current player
     */
    public int getCurrentPlayerIdx() {
    	return game.getCurrentPlayerIdx();
    }
    
    /**
     * An implementation for starting the game.
     * 
     * @param deck the deck of (shuffled) cards to be used in this game
     */
    public void start(Deck deck) {
    	game.start(game.getDeck());
    }
    
    
    /**
     * An implementation for making a move by the player.
     * 
     * @param playerIdx the index of the player who makes the move
	 * @param cardIdx   the list of the indices of the cards selected by the player
     */
    public void makeMove(int playerIdx, int[] cardIdx) {
    	game.makeMove(playerIdx, cardIdx);
    }
    
    /**
     * An implementation for checking a move made by the player
     * 
     * @param playerIdx the index of the player who makes the move
	 * @param cardIdx   the list of the indices of the cards selected by the player
     */
    public void checkMove(int playerIdx, int[] cardIdx) {
    	game.checkMove(playerIdx, cardIdx);
    }
    
    /**
     * An implementation for checking for end of game
     * 
     * @return true if the game ends; false otherwise
     */
    public boolean endOfGame() {
    	return game.endOfGame();
    }
    
    
    

    /**
     * Sets the index of the active player
     * 
     * @param activePlayer
     */
    public void setActivePlayer(int activePlayer) {
    	this.activePlayer = activePlayer;
    }

    /**
     * Prints the specified string to the message area of the GUI
     * @param msg message to be printed to the message area
     */
    public void printMsg(String msg) {
    	this.msgArea.append(msg + "\n");
    }

    /**
     * Clears the message area of the GUI
     */
    public void clearMsgArea() {
    	this.msgArea.setText(null);;
    }

//    /**
//     * Resets the GUI
//     */
//    public void reset() {
//    	//
//    	this.handsOnTable = game.getHandsOnTable();
//        // reset the list of selected cards
//    	this.resetSelected();
//        // clear the message area
//    	this.clearMsgArea();
//        // enable user interactions
//    	this.enable();
//    }

    /**
     * Enables user interactions with GUI
     */
    public void enable() {
        // enable the “Play” button and “Pass” button
    	this.playButton.setEnabled(true);
    	this.passButton.setEnabled(true);
        // enable the chat input
    	this.chatInput.setEnabled(true);
        // enable the BigTwoPanel for selection of cards
    	this.bigTwoPanel.setEnabled(true);
    }

    /**
     * Disables user interactions with GUI
     */
    public void disable() {
        // disable the “Play” button and “Pass” button
    	this.playButton.setEnabled(false);
    	this.passButton.setEnabled(false);
        // disable the chat input
    	this.chatInput.setEnabled(false);
        // disable the BigTwoPanel for selection of cards
    	this.bigTwoPanel.setEnabled(false);
    }
    
    /**
     * Converts the boolean[] selected list to int[] indexes list.
     * 
     * @return a list of indexes of selected cards
     */
    public int[] getSelected() {
    	int[] cardIdx = null;
		int count = 0;
		for (int j = 0; j < selected.length; j++) {
			if (selected[j]) {
				count++;
			}
		}

		if (count != 0) {
			cardIdx = new int[count];
			count = 0;
			for (int j = 0; j < selected.length; j++) {
				if (selected[j]) {
					cardIdx[count] = j;
					count++;
				}
			}
		}
		return cardIdx;
    }
    
    /**
	 * A method that resets the list of selected cards to an empty list.
	 */
	public void resetSelected() {
		for(int i=0;i<selected.length;i++) {
			selected[i] = false;
		}
	}

    /**
     * Prompts the active player to select cards and make his/her move.
     */
    public void promptActivePlayer() {
    	selected= new boolean[MAX_CARD_NUM];
    	String prompt = playerList.get(activePlayer).getName() + "'s turn: ";
    	printMsg(prompt);
    	repaint();
    }

    /**
     * This is an inner class that extends the JPanel class and implements the MouseListener interface.
     */
    class BigTwoPanel extends JPanel implements MouseListener {
    	
    	private int cardXStep = 40;
    	private int cardWidth = 80;
    	private int cardHeight = 100;
      
    	public BigTwoPanel() {
    		this.addMouseListener(this);
    	}
    	
    	/**
         * Overrides the method provided in the JPanel class to draw the card game table.
         */
        public void paintComponent(Graphics g) {	
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			super.paintComponent(g2);
			this.setBackground(new Color(143, 188, 143));
			g.setColor(new Color(0, 100, 0));
			
			//painting the four players' avatars and their cards
			for (int pi=0; pi<5; pi++) {
				g.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
            	if (pi == 4) {
        			ArrayList<Hand> HandsOnTable = game.getHandsOnTable();
        			Hand lastHandOnTable = (HandsOnTable.isEmpty()) ? null : HandsOnTable.get(HandsOnTable.size() - 1);
        			if (lastHandOnTable == null) {
        				g.drawString("ポーカーテーブル Table", 10, 20 + pi * 150);
        			}
        			else {
        				g.drawString("前の手 Last Hand: ", 10, 20 + pi * 150);
        				g.drawString(lastHandOnTable.getPlayer().getName() + ": ", 10, 40 + pi * 150);
        				for (int i=0; i<lastHandOnTable.size(); i++) {
        	        		int suit = lastHandOnTable.getCard(i).suit;
    		        		int rank = lastHandOnTable.getCard(i).rank;
    		        		g.drawImage(icons[suit][rank], 140 + i * cardWidth, 20 + pi * 150, cardWidth, cardHeight, this);
        				}
        			}
                	g.setColor(new Color(0, 100, 0));
                	g2.drawLine(0,  150 * (pi+1), 1200, 150 * (pi+1));
        		}
        		else if (exist[pi]) {
				//else {
        			// for the four players
        			if (pi == activePlayer) {
        				g.setColor(new Color(127, 255, 0));
        				g.drawString("あなた YOU", 10, 20 + pi * 150);
        				g.setColor(new Color(0, 100, 0));
        				g2.drawLine(0,  150 * (pi+1), 1200, 150 * (pi+1));
        				// draw funny avatar version of player i
        				g.drawImage(icons[4][pi+5], 5, 20 + pi * 150, this);
        				// draw cards' faces of player i
        				CardList cards = game.getPlayerList().get(pi).getCardsInHand();
        				for (int i=0; i<cards.size(); i++) {
    		        		int suit = cards.getCard(i).suit;
    		        		int rank = cards.getCard(i).rank;
    		        		if (selected[i]) {
    		        			g.drawImage(icons[suit][rank], 140 + i * cardXStep, 20 + pi * 150, cardWidth, cardHeight, this);
    		        		}
    		        		else {
    		        			g.drawImage(icons[suit][rank], 140 + i * cardXStep, 40 + pi * 150, cardWidth, cardHeight, this);
    		        		}
    		        	}
        			}
        			else {
        				// draw card back
						if (game.getPlayerList().get(pi).getName() != null) {
							g.drawString(game.getPlayerList().get(pi).getName(), 10, 20 + pi * 150);
						}
        				else {
							g.drawString("プレーヤー Player " + pi, 10, 20 + pi * 150);
						}
						g.setColor(new Color(0, 100, 0));
        				g2.drawLine(0,  150 * (pi+1), 1200, 150 * (pi+1));
        	        	// draw avatar of player i
        	        	g.drawImage(icons[4][pi+1], 5, 20 + pi * 150, this);
        	        	// draw cards' backs of player i
        	        	CardList cards = game.getPlayerList().get(pi).getCardsInHand();
        	        	for (int i=0; i<cards.size(); i++) {
        	        		g.drawImage(icons[4][0], 140 + i * cardXStep, 40 + pi * 150, cardWidth, cardHeight, this);
        	        	}
        			} // end of inactive players
        		} // end of judging player or table
            }
			frame.repaint();
        }

        /**
         * This is an ancillary method for the <code>mouseReleased</code> method used to judge 
         * whether a point is in the area of cardWidth and cardHeight, given the x- and y-coordinates of the point 
         * and the upper-left vertex of the area of a card.
         * 
         * The intuition is that if overlapping card number is two, then the cursor is in the right card area
         * iff the cursor is both area of left and right cards.
         * 
         * @param px x-coordinate of the cursor point
         * @param py y-coordinate of the cursor point
         * @param AreaX x-coordinate of the upper-left point of a card area (same as the location to paint the card)
         * @param AreaY y-coordinate of the upper-left point of a card area (same as the location to paint the card)
         * @return true if yes, otherwise no.
         */
        public boolean inArea(int px, int py, int AreaX, int AreaY) {
        	if ((px >= AreaX && px <= AreaX+80) && (py >= AreaY && py <= AreaY+100)) {
        		return true;
        	}
        	else return false;
        }
        
        
        /**
         * A dummy for implementation.
         */
        public void mouseClicked(MouseEvent e) {
    	}
        /**
         * A dummy for implementation.
         */
    	public void mousePressed(MouseEvent e) {
    	}
    	/**
         * Play safe to choose mouseReleased. (move up a card if it is selected, and move down if it's already selected)
         */
    	public void mouseReleased(MouseEvent e) {
    		/*
    		 * First traverses all the cards the activated player have from left to right, 
    		 * use the method in InArea to judge whether to move a card or not.
    		 */
    		int numOfCards = game.getPlayerList().get(activePlayer).getNumOfCards();
			int AreaX, AreaY;
			boolean inThis, inNext;
    		for (int idx = 0; idx<numOfCards; idx++) {
    			if (selected[idx]) {
    				AreaY = 20 + activePlayer*150;
    			}
    			else {
    				AreaY = 40 + activePlayer*150;
    			}
    			AreaX = 140 + idx * 40;
    			inThis = inArea(e.getX(), e.getY(), AreaX, AreaY);
    			if (idx==numOfCards-1) {
    				inNext = false;
    			}
    			else {
    				if (selected[idx+1]) {
        				AreaY = 20 + activePlayer*150;
        			}
        			else {
        				AreaY = 40 + activePlayer*150;
        			}
    				AreaX = 140 + (idx+1) * 40;
    				inNext = inArea(e.getX(), e.getY(), AreaX, AreaY);
    			}
    		
    			if (inThis && !inNext) {
    				selected[idx] = !selected[idx];
    				break;
    			}
    		}
			this.repaint();
    	}
    	public void mouseEntered(MouseEvent e) {
    	}
    	public void mouseExited(MouseEvent e) {
    	}
    }
    
    /**
     * This is an inner class for listening chat inputs and then printing in the chat area.
     * @author 3035844871 Yichuan Song
     *
     */
    public class InputListener implements ActionListener{
        /**
         * append the message to the chat box, and reset the input box
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
			CardGameMessage message = new CardGameMessage(CardGameMessage.MSG, activePlayer, chatInput.getText());
			game.sendMsgToServer(message);
			chatInput.setText("");
        }
    }
	/*
	TO BE DELETED
	 */
	public String getMessageInput() {
		return this.chatInput.getText();
	}

	public void printChat(String chat) {
		chatArea.append(chat + "\n");
	}
    

    /**
     * This is an inner listener class to implement the <code>play()</code> method of CardGamePlayer in GUI.
     * (i.e. to remove the valid selected hand from the player and paint the hand on the table area)
     * 
     * @author 3035844871 Yichuan Song
     */
    public class PlayButtonListener implements ActionListener {
        /*
         * Handles button-click events for the “Play” button
         */
        public void actionPerformed(ActionEvent e) {
            // when clicked, call makeMove()
        	if (getSelected() == null) {
				printMsg("Invalid hand! Please select again.\n");
			}
			else {
				game.makeMove(game.getCurrentPlayerIdx(), getSelected());
			}
        }
    }

    /**
     * This is an inner listener class that allows a player to pass and show relevant infos in GUI.
     * 
     * @author 3035844871 Yichuan Song
     */
    public class PassButtonListener implements ActionListener {
        /**
         * Handles button-click events for the “Pass” button
         */
        public void actionPerformed(ActionEvent e) {
            // when clicked, call makeMove()
        	resetSelected();
        	game.makeMove(game.getCurrentPlayerIdx(), null);
        }
    }

    /**
     * This is an inner listener class that allows players to restart a game with GUI.
     * 
     * @author 3035844871 Yichuan Song
     */
    public class ConnectMenuItemListener implements ActionListener {
         // Handles menu-item-click events for the “Connect” menu item.
        public void actionPerformed(ActionEvent e) {
        	game.connect();
        }
    }

    /**
     * This is an inner listener class that allows players to quit the game with GUI.
     * 
     * @author 3035844871 Yichuan Song
     */
    public class QuitMenuItemListener implements ActionListener {
        // Handles menu-item-click events for the “Quit” menu item
        public void actionPerformed(ActionEvent e) {
        	System.exit(0);	
        }
    }

}
