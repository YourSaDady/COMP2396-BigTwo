/**
 * This class is a subclass of the <code>Hand</code> class and is used to model a specific <code>FullHouse</code> hand.
 * @author 3035844871 Yichuan Song
 *
 */
public class FullHouse extends Hand {
	/**
	 * An overriding constructor for building a specified <code>FullHouse</code> hand
	 * @param player the specified player
	 * @param cards a list of input cards
	 */
	public FullHouse(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	/**
	 * Specifies a valid Full House hand with a triplet of same rank and a pair with another rank
	 */
	public boolean isValid() {
		if (this.size() != 5) {
			return false;
		}
		else if (((this.getCard(0).getRank() == this.getCard(1).getRank())  // "triplet + pair"
				&& (this.getCard(1).getRank() == this.getCard(2).getRank())
				&& (this.getCard(2).getRank() != this.getCard(3).getRank())
				&& (this.getCard(3).getRank() == this.getCard(4).getRank()))
				||
				((this.getCard(0).getRank() == this.getCard(1).getRank()) // "pair + triplet"
				&& (this.getCard(1).getRank() != this.getCard(2).getRank())
				&& (this.getCard(2).getRank() == this.getCard(3).getRank())
				&& (this.getCard(3).getRank() == this.getCard(4).getRank())
				)) {
				return true;
		}
		else {
			return false;
		}
	}
	/**
	 * Gets the hand's type 
	 */
	public String getType() {
		return "FullHouse";
	}
}
