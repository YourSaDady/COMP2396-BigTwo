/**
 * This class provides a subclass of <code>Hand</code> and is used to return an invalid hand dummy in <code>composeHand()</code> of the <code>BigTwo</code> class.
 * @author 3035844871 Yichuan Song
 *
 */
public class InvalidHand extends Hand {
	/**
	 * An overriding constructor for building a specified <code>Invalid</code> hand
	 * @param player the specified player
	 * @param cards a list of input cards
	 */
	public InvalidHand(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	/**
	 * invalid (false).
	 */
	public boolean isValid() {
		return false;
	}
	/**
	 * returns an "Invalid" string to suit the logic of <code>checkMove()</code> in the <code>BigTwo</code> class
	 */
	public String getType() {
		return "Invalid";
	}
	/**
	 * since it is invalid, it cannot beat any hand.
	 */
	public boolean beats(Hand hand) {
		return false;
	}
}
