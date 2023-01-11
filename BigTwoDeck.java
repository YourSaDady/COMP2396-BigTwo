/**
 * This class is a subclass of the Deck class and is used to model a deck of cards used in a Big Two card game.
 * @author 3035844871 Yichuan Song
 *
 */
public class BigTwoDeck extends Deck {
	/**
	 * Initializes a deck of Big Two cards.
	 */
	public void initialize() {
		// removes all cards from the deck
		this.removeAllCards();
		// creates 52 Big Two cards and add them to the deck
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 13; j++) {
				BigTwoCard card = new BigTwoCard(i, j);
				this.addCard(card);
			}
		}
	}
}
