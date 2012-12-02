/**
 * @author	Matt Goldman
 * @version	1.0
 * @since	2012-11-16
 */
public class Player{
	Card[] hand;
	double bet;//amount of the player's bet
	double money;//player's bank
	double ins;//insurance bet
	boolean stood;//whether or not the player has stood
	int pid;//player id (not unique)
	int splits;//how many splits this hand has undergone
	int index;//index of next card in the hand
	/**
	 * Constructs a player
	 */
	public Player(int x){
		pid=x;
		bet=0;
		money=0;
		splits=0;
		stood=false;
		index=0;
		ins=0;
		hand=new Card[11];//maximum 11 cards without busting (do the math)
	}
	/**
	 * Resets variables for a new round
	 */
	public void reset(){
		//Fill the hand with null Cards
		Arrays.fill(hand,null);
		//reset split count
		splits=0;
		//unstand
		stood=false;
		//reset index
		index=0;
	}
	/**
	 * Calculates the total hand value for the player
	 *
	 * @return	the sum of the card values
	 */
	public int handTotal(){
		int ret=0;
		int aces=0;
		//Calculate with Ace=11
		for(int i=0;i<hand.length && hand[i]!=null;++i){
			ret+=(hand[i].getValue()==1?11:hand[i].getValue());
			if(hand[i].getValue()==1)
				++aces;
		}
		//If the hand is a bust, change each Ace to value 1 until the hand is not a bust
		if(ret>21 && aces>0){
			for(;aces>0 && ret>21;--aces)
				ret-=10;
		}
		return ret;
	}
	/**
	 * Determines whether the hand is "soft" (has an Ace valued 11)
	 *
	 * @return	soft or not
	 */
	public boolean isSoft(){
		int val=0;
		int aces=0;
		boolean ret=false;
		//Calculate value with Ace=11
		for(int i=0;i<hand.length && hand[i]!=null;++i){
			val+=(hand[i].getValue()==1?11:hand[i].getValue());
			if(hand[i].getValue()==1)
				++aces;
		}
		if(aces>0)
			ret=true;
		//If the hand is a bust, change each Ace to value 1 until the hand is not a bust
		if(val>21 && aces>0){
			for(;aces>0 && val>21;--aces)
				val-=10;
			//if there are still aces at 11 left after un-busting, the hand is softs
			if(aces==0)
				ret=true;
		}
		return ret;
	}
	/**
	 * Adds a new card to the hand
	 *
	 * @param	d	the deck to deal from
	 * @return 	whether or not the cut card was drawn
	 */
	public boolean addCard(Deck d){
		boolean ret=false;
		if(index==hand.length)
			return false;
		hand[index]=d.deal();
		if(hand[index].getFace()==Card.CUT_CARD){
			System.out.println("\nCUT CARD DRAWN. LAST HAND.\n");
			ret=true;
			hand[index]=d.deal();
		}
		++index;
		return ret;
	}
	/**
	 * Sets the next card to a specific card
	 *
	 * @param	c	the deck to deal from
	 * @return 	whether or not the cut card was drawn
	 */
	public void addCard(Card c){
		hand[index]=c;
		++index;
	}
	/**
	 * Determines if a hand is a blackjack
	 *
	 * @return	true/false Blackjack status
	 */
	public boolean hasBJ(){
		//conditions to fill:
		//-value is 21
		//-only 2 cards
		//-no splits: BJs after splits only count as 21
		if(this.handTotal()==21 && this.hand[2]==null && this.splits==0)
			return true;
		return false;
	}
}
