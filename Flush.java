/**
 * This class is a subclass of the <code>Hand</code> class and is used to model a specific <code>Flush</code> hand.
 * @author 3035844871 Yichuan Song
 *
 */
public class Flush extends Hand {
	/**
	 * An overriding constructor for building a specified <code>Flush</code> hand
	 * @param player the specified player
	 * @param cards a list of input cards
	 */
	public Flush(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	/**
	 * Specifies a valid <code>Flush</code> hand with five cards of same suit
	 */
	public boolean isValid() {
		return ((this.getCard(0).getSuit() == this.getCard(1).getSuit()) 
				&& (this.getCard(1).getSuit() == this.getCard(2).getSuit())
				&& (this.getCard(2).getSuit() == this.getCard(3).getSuit())
				&& (this.getCard(3).getSuit() == this.getCard(4).getSuit()));
	}
	/**
	 * Gets the hand's type 
	 */
	public String getType() {
		return "Flush";
	}
	/**
	 * This is an overriding method which compares the suits of the top cards first
	 * and returns a boolean showing whether the hand beats the hand on the table.
	 * @param hand the hand on the table to be compared with
	 * @return true if beats, otherwise false
	 */
	public boolean beats(Hand hand) {
		if (this.size() != hand.size()) {
			return false;
		}
		if (hand.getType() == "Straight") {
			return true;
		}
		else if (hand.getType() == "Flush") {
			if (this.getTopCard().getSuit() != hand.getTopCard().getSuit()) {
				return this.getTopCard().getSuit() > hand.getTopCard().getSuit();
			}
			else {
				return (getRealRank(this.getTopCard()) > getRealRank(hand.getTopCard()));
			}
		}
		else {
			return false;
		}
	}
}
