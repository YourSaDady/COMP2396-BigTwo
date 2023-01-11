/**
 * This class is a subclass of the Card class and is used to model a card used in a Big Two card game.
 * @author 3035844871 Yichuan Song
 *
 */
public class BigTwoCard extends Card {
	//需要declare serialUID吗？？？？
	/**
	 * Builds a card with the specified suit and rank.
	 * @param suit 0 - 3 represent Diamonds, Clubs, Hearts, Spades with ascending order (same as defined in <code>Card</code>)
	 * @param rank 0 - 12 represent A, 2, 3 - 10, J, Q, K (same as defined in <code>Card</code>, but without order defined in Big Two)
	 */
	public BigTwoCard(int suit, int rank) {
		super(suit, rank);
	}
	/**
	 * Compares the order of this card with the specified card.
	 * @param card the card compared with.
	 * @return  returns a negative integer, zero, or a positive integer when this card is less than, equal to, or greater than the specified card.
	 */
	public int compareTo(Card card) {
		int this_rank, this_suit, card_rank, card_suit;
		this_suit = this.getSuit();
		card_suit = card.getSuit();
		if (this.getRank() < 2) {
			this_rank = this.getRank() + 12; // for A and 2
		}
		else {
			this_rank = this.getRank() - 2; // for 3 - K
		}
		if (card.getRank() < 2) {
			card_rank = card.getRank() + 12; // for A and 2
		}
		else {
			card_rank = card.getRank() - 2; // for 3 - K
		}
		
		if (this_rank > card_rank) {
			return 1;
		} else if (this_rank < card_rank) {
			return -1;
		} else if (this_suit > card_suit) {
			return 1;
		} else if (this_suit < card_suit) {
			return -1;
		} else {
			return 0;
		}
	}
	
	
	
	
	
	
	
}
