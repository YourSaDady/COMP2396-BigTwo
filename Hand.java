/**
 * This class is a subclass of the CardList class and is used to model a hand of cards.
 * @author 3035844871 Yichuan Song
 *
 */
public abstract class Hand extends CardList {
	private CardGamePlayer player;
	/**
	 * A constructor for building a hand with the specified player and list of cards.
	 * @param player a specified player
	 * @param cards a list of input cards
	 */
	public Hand(CardGamePlayer player, CardList cards) {
		this.player = player;
		for (int i=0; i<cards.size(); i++) {
			Card card = cards.getCard(i);
			this.addCard(card);
		}
		this.sort();  // 需要吗？？
	}
	/**
	 * Retrieves the player of this hand.
	 * @return the player of this hand
	 */
	public CardGamePlayer getPlayer() {
		return this.player;
	}
	/**
	 * Returns the actual rank of bigTwo cards in a Big Two game, where 'A' and '2' are the second largest 
	 * and the largest ranks.
	 * @param card a specific card
	 * @return the actual rank of the card in a Big Two game
	 */
	public int getRealRank(Card card) {
		int rank = card.getRank();
		if (rank < 2) {
			return rank + 12;
		}
		else {
			return rank - 2;
		}
	}
	
	/**
	 * Retrieves the top card of this hand.
	 * @return the top card of this hand
	 */
	public Card getTopCard() {
		if (this.getType() == "FullHouse") {
			if (this.getCard(0).getRank() == this.getCard(2).getRank()) { // "triplet + pair"
				return this.getCard(2);
			}
			else { // "pair + triple"
				return this.getCard(this.size()-1);
			}
		}
		else if (this.getType() == "Quad") {
			if (this.getCard(0).getRank() == this.getCard(3).getRank()) { // "quadruplet + single"
				return this.getCard(3);
			}
			else {
				return this.getCard(this.size()-1); // "single + quadruplet"
			}
		}
		else {
			return this.getCard(this.size()-1);
		}
	}
	/**
	 * Checks if this is a valid hand.
	 * @return
	 */
	abstract boolean isValid();
	/**
	 * Returns a string specifying the type of this hand.
	 * @return a hand type
	 */
	abstract String getType();
	/**
	 * Returns a boolean showing whether the hand beats the hand on the table.
	 * @param hand the hand on the table to be compared with
	 * @return true if beats, otherwise false
	 */
	public boolean beats(Hand hand) { // needs to check!!! (not abstract???)
		if (this.size() != hand.size()) {
			return false;
		}
		else if (this.getType() == hand.getType()) {
			if (getRealRank(this.getTopCard()) != getRealRank(hand.getTopCard())) {
				return (getRealRank(this.getTopCard()) > getRealRank(hand.getTopCard()));
			}
			else {
				return this.getTopCard().getSuit() > hand.getTopCard().getSuit();
			}
		}
		else {
			if (this.getType() == "Straight") {
				return false;
			}
			
			// beats() for Flush is overriding
			
			else if (this.getType() == "FullHouse") {
				if ((hand.getType() == "Straight") || (hand.getType() == "Flush")) {
					return true;
				}
				else return false;
			}
			else if (this.getType() == "Quad") {
				if (hand.getType() == "StraightFlush") {
					return false;
				}
				else return true;
			}
			else { // this is "StraightFlush"
				return true;
			}
		}
	}
 }
