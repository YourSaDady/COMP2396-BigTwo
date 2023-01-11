import javax.swing.*;
import java.util.ArrayList;
/**
 * This class implements the CardGame interface and is used to model a Big Two card game.
 * @author 3035844871 Yichuan Song
 *
 */

public class BigTwo {
	
	private Deck deck;
	
	private ArrayList<CardGamePlayer> playerList;
	
	private ArrayList<Hand> handsOnTable;
	
	private int currentPlayerIdx;
	
	private BigTwoGUI gui;

	private BigTwoClient client;

	public boolean[] exist = new boolean[4];
	
	 /**
	  * Constructor for creating a Big Two card game
	  */
	public BigTwo() {
		this.deck = new BigTwoDeck();
		// create 4 players and add them to the player list
		this.playerList = new ArrayList<CardGamePlayer>();
		for (int i = 0; i<4; i++) {
			CardGamePlayer player = new CardGamePlayer();
			this.playerList.add(player);
		}
		this.handsOnTable = new ArrayList<Hand>();
		// create a BigTwoUI object for providing the user interface
		this.gui = new BigTwoGUI(this);
		this.client = new BigTwoClient(this, this.gui);
		client.connect();
	}

	/**
	 * A helper method for the connectEvent in GUI.
	 */
	public void connect() {
		this.client.connect();
	}

	/**
	 *  A helper method for client to handle PLAYER_LIST message
	 * @param names an array of String of the name list in the message
	 */
	public void setNames(String[] names) {
		for (int i=0; i<4; i++) {
			System.out.println("name: "+names[i] + "\n"); //////
			CardGamePlayer player = this.playerList.get(i);
			if (names[i] != null) {
				player.setName(names[i]);
				gui.setExist(i);
			}
			else {
				player.setName("Player " + getNumOfPlayers());
			}
			player.setName(names[i]);
			this.playerList.set(i, player);
		}
		for (int i=0; i<4; i++) {
			exist[i] = gui.getExist()[i];
		}
	}

	/**
	 * A helper method for client to handle JOIN message
	 * @param pi index of player
	 * @param name
	 */
	public void setPlayerName(int pi, String name) {
		CardGamePlayer player = playerList.get(pi);
		player.setName(name);
		playerList.set(pi, player);
		gui.setExist(pi);
		for (int i=0; i<4; i++) {
			exist[i] = gui.getExist()[i];
		}
	}


	
	/**
	 * Gets the number of players
	 * @return the size of <code>playerList</code>
	 */
	public int getNumOfPlayers() {
		return this.playerList.size();
	}
	/**
	 * Retrieves the deck of cards being used.
	 * @return the deck of cards being used
	 */
	public Deck getDeck() {
		return this.deck;
	}
	/**
	 * Retrieves the list of players.
	 * @return the list of players
	 */
	public ArrayList<CardGamePlayer> getPlayerList() {
		return this.playerList;
	}
	/**
	 * Retrieves the list of hands.
	 * @return the list of hands on table
	 */
	public ArrayList<Hand> getHandsOnTable() {
		return this.handsOnTable;
	}
	/**
	 * Retrieves the index of the current player.
	 * @return the index of the current player
	 */
	public int getCurrentPlayerIdx() {
		return this.currentPlayerIdx;
	}
	/**
	 * Starts/Restarts the game.
	 * @param deck a given shuffled deck of cards
	 */
	public void start(Deck deck) { // the input deck is already shuffled
		
		// remove all cards from the players and from the table
		for (CardGamePlayer player : this.playerList) {
			player.removeAllCards();
		}
		this.handsOnTable = new ArrayList<Hand>();
		// distribute the cards to the players
		int i = 0;
		for (CardGamePlayer player : this.playerList) {
			for (int j=(i * (int)(deck.size()/4)); j<((i+1) * (int)(deck.size()/4)); j++) {
				player.addCard(deck.getCard(j)); // ????
			}
			player.sortCardsInHand();
			i++;
		}
		// identify the player who holds the Three of Diamonds
		Card three_of_diamonds = new Card(0, 2);
		i = 0;
		for (CardGamePlayer player : this.playerList) {
			if (player.getCardsInHand().contains(three_of_diamonds)) {
				break;
			}
			i++;
		}
		// set both the currentPlayerIdx of the BigTwo object and the activePlayer of the BigTwoGUI object 
		// to the index of the player who holds the Three of Diamonds
//		this.gui.reset();
		this.currentPlayerIdx = i;
		this.gui.setActivePlayer(i);
		// call the repaint() method of the BigTwoUI object to show the cards on the table
		this.gui.repaint();
		// call the promptActivePlayer() method of the BigTwoGUI object to prompt user 
		// to select cards and make his/her move
		this.gui.promptActivePlayer();
	}
	/**
	 * Makes a move by a player.
	 * @param playerIdx the index of the player who makes a move
	 * @param cardIdx a specified list of indices of cards that the player intends to use for his / hers hand
	 */
	public void makeMove(int playerIdx, int[] cardIdx) {
//		checkMove(playerIdx, cardIdx); //?

		CardGameMessage message = new CardGameMessage(CardGameMessage.MOVE, -1, getPlayerList().get(playerIdx).getCardsInHand());
		sendMsgToServer(message);
	}
	/**
	 * Checks a move by a player.
	 * @param playerIdx the index of the player who makes a move
	 * @param cardIdx a specified list of indices of cards that the player intends to use for his / hers hand
	 */
	public void checkMove(int playerIdx, int[] cardIdx) {
		// the 1st play
		if (this.handsOnTable.isEmpty()) { // passing 1st play is illegal
			if (cardIdx == null) {
				gui.printMsg("Not a legal move!!!");
			}
			else {
				CardGamePlayer player = this.playerList.get(playerIdx);
				CardList cards = player.play(cardIdx);
				Hand hand = composeHand(player, cards);
				Card three_of_diamonds = new Card(0, 2);
				
				if (hand.size()!=0 && (hand.contains(three_of_diamonds))) { // the 1st play must be a valid hand containing three-of-diamonds
					this.handsOnTable.add(hand);
					player.removeCards(cards);
					player.sortCardsInHand();
					this.playerList.set(playerIdx, player);
					this.currentPlayerIdx = (this.currentPlayerIdx + 1) % 4;
					this.gui.setActivePlayer(this.currentPlayerIdx);
					
					Hand lastHandOnTable = handsOnTable.get(handsOnTable.size() - 1);
					this.gui.printMsg("{" + lastHandOnTable.getType() + "} ");
					lastHandOnTable.print(true, false);
					this.gui.printMsg("\n");
					if (!this.endOfGame()) {this.gui.repaint();}
				}
				else {
					gui.printMsg("Not a legal move!!!"); // an invalid or weak play is illegal
				}
			}
		}
		// the following play
		else {
			if (cardIdx == null) { // a pass play
				if (this.handsOnTable.get(this.handsOnTable.size()-1).getPlayer() == this.playerList.get(playerIdx)) { // If the player is the same player who played the last hand on table, 
																							   // the player must not pass but can play any valid hand.
					gui.printMsg("Not a legal move!!!"); 
				}
				else {
					this.currentPlayerIdx = (this.currentPlayerIdx + 1) % 4;
					this.gui.setActivePlayer(this.currentPlayerIdx);
					
					this.gui.printMsg("{Pass}");
					this.gui.printMsg("\n\n");
					if (!this.endOfGame()) {this.gui.repaint();}
				}
			}
			else {
				CardGamePlayer player = this.playerList.get(playerIdx);
				CardList cards = player.play(cardIdx);
				Hand hand = composeHand(player, cards);
				if ((hand.size()!=0 && hand.beats(this.handsOnTable.get(this.handsOnTable.size()-1))) || ((hand.size()!=0) 
						&& (this.handsOnTable.get(this.handsOnTable.size()-1).getPlayer() == this.playerList.get(playerIdx)))) { 
					if (hand.getType() == "Invalid") {
						gui.printMsg("Not a legal move!!!"); // an invalid or weak play is illegal
					}
					else {
						this.handsOnTable.add(hand);
						player.removeCards(cards);
						player.sortCardsInHand();
						this.playerList.set(playerIdx, player);
						this.currentPlayerIdx = (this.currentPlayerIdx + 1) % 4;
						this.gui.setActivePlayer(this.currentPlayerIdx);
						
						Hand lastHandOnTable = handsOnTable.get(handsOnTable.size() - 1);
						this.gui.printMsg("{" + lastHandOnTable.getType() + "} ");
						lastHandOnTable.print(true, false);
						this.gui.printMsg("\n");
						if (!this.endOfGame()) {this.gui.repaint();}
					}
				}
				else {
					gui.printMsg("Not a legal move!!!"); // an invalid or weak play is illegal
				}
			}
		}
		if (!this.endOfGame()) {
			this.gui.promptActivePlayer();
		}
		else {
			String result;
			result = "\nGame ends\n";
			this.gui.setActivePlayer(-1);
			this.gui.repaint();
			for (int i=0; i<4; i++) {
				CardGamePlayer player = this.playerList.get(i);
				if (player.getNumOfCards() == 0) {
					result += "Player " + i + " wins the game.\n";
				}
				else {
					result += "Player " + i + " has " + player.getNumOfCards() + " cards in hand.\n";
				}
			}

			JOptionPane.showMessageDialog(null, result, "Game Ends~", JOptionPane.PLAIN_MESSAGE);
			CardGameMessage reply = new CardGameMessage(CardGameMessage.READY, -1, null);
			sendMsgToServer(reply);
		}
	}
	/**
	 * Checks if the game ends.
	 * @return true if one of the four players don't have any cards, false otherwise.
	 */
	public boolean endOfGame() {
		for (CardGamePlayer player : this.playerList) {
			if (player.getNumOfCards() == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Starts a Big Two card game.
	 * @param args
	 */
	public static void main(String[] args) {
//		 if (args.length != 2) {
//			 clientName = args[0];
//		 }
//		 else {
//			 clientName = null;
//		 }
//		 serverIP = args[args.length-2];
//		 serverPort = Integer.parseInt(args[args.length-1]);

		// creates a Big Two card game
		BigTwo game = new BigTwo();
//
//		// creates and shuffle a deck of cards
//		BigTwoDeck deck = new BigTwoDeck();
//		deck.shuffle();
//		// starts the game with the deck of cards
//		game.start(deck);
	}
	
	/**
	 * Returns a valid hand from the specified list of cards of the player.
	 * @param player the owner of the list of the cards
	 * @param cards the specified list of cards
	 * @return a valid hand 
	 */
	public static Hand composeHand(CardGamePlayer player, CardList cards) {
			if (cards.size() == 1) {
				return new Single(player, cards);
			}
			else if (cards.size() == 2) {
				Pair pair = new Pair(player, cards);
				if (pair.isValid()) {
					return pair ;
				}
			}
			else if (cards.size() == 3) {
				Triple triple = new Triple(player, cards);
				if (triple.isValid()) {
					return triple;
				}
			}
			else if (cards.size() == 5) {
				Straight straight = new Straight(player, cards);
				Flush flush = new Flush(player, cards);
				FullHouse fullHouse = new FullHouse(player, cards);
				Quad quad = new Quad(player, cards);
				StraightFlush sf = new StraightFlush(player, cards);
				if (straight.isValid()) {
					return straight;
				}
				else if (flush.isValid()) {
					return flush;
				}
				else if (fullHouse.isValid()) {
					return fullHouse;
				}
				else if (quad.isValid()) {
					return quad;
				}
				else if (sf.isValid()) {
					return sf;
				}
			}
			return new InvalidHand(player, cards);
		}


		public void sendMsgToServer(CardGameMessage message) {
			this.client.sendMessage(message);
		}

	}


