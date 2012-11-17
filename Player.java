/**
 * @author	Matt Goldman
 * @version	1.0
 * @since	2012-11-16
 */
public class Player{
	Card[] hand;
	double bet;//amount of the player's bet
	double money;//player's bank
	boolean stood;//whether or not the player has stood
	int pid;//player id (not unique)
	/**
	 * Constructs a player
	 */
	public Player(int x){
		pid=x;
		bet=0;
		money=0;
		stood=false;
		hand=new Card[11];//maximum 11 cards without busting (do the math)
	}
	/**
	 * Calculates the total hand value for a given player
	 *
	 * @param	h	an array of Cards
	 * @return	the sum of the card values
	 */
	public int handTotal(){
		int ret=0;
		//Calculate with Ace=11
		for(int i=0;i<hand.length && hand[i]!=null;++i)
			ret+=(hand[i].getValue()==1?11:hand[i].getValue());
		//If the hand is a bust, recalculate with Ace=1
		if(ret>21){
			ret=0;
			for(int i=0;i<hand.length && hand[i]!=null;++i)
				ret+=hand[i].getValue();
		}
		return ret;
	}
}
