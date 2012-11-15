// This class represents the deck of cards from which cards are dealt to players.
public class Deck
{
	// define fields here
	Card[] d;
	int index;
	/** This constructor builds a deck of cards
	 *
	 * @param	n	number of standard decks to use
	 */
	public Deck(int n)
	{
		index = 0;
		d=new Card[52*n+(n==1?0:1)];
		//?: op above makes room for a cut card iff more than 1 deck
		int ind=0;
		//Iterate through decks
		for(int k=0;k<n;++k){
			//Iterate through each suit
			for(int i=0;i<4;++i){
				//Iterate through each value
				for(int j=1;j<=13;++j){
					//create a new card
					d[ind]=new Card(i,j);
					++ind;
				}
			}
		}
		//put the cut card in the last spot, iff more than 1 deck
		if(n>1){
			d[52*n]=new Card(0,Card.CUT_CARD);
			//System.out.println("CUT CARD INSERTED");
		}
	}


	// This method takes the top card off the deck and returns it.
	public Card deal()
	{
		if (index == d.length)
			return null;
		else {
			return d[index++];
		}
	}


	public boolean isEmpty()
	{
		return index == d.length ;
	}

	/**
	 * Shuffles an array of Cards in place
	 *
	 * @param	c	an array of Cards
	 */
	public void shuffle(){
		Card temp=null;
		int j=0;
		//Fisher-Yates-Knuth shuffle, except the cut card in the last index
		//There is only a cut card if there is more than one deck
		for(int i=d.length-(d.length>52?2:1);i>0;--i){
			j=(int)(Math.random()*(i));
			temp=d[i];
			d[i]=d[j];
			d[j]=temp;
		}
		//put the cut card 70% to 80% into the shoe, iff there's more than one deck
		if(d.length>52){
			j=(int)(0.70*d.length+Math.random()*0.10*(d.length));
			//System.out.printf("CUT CARD AT %d.\n",j);
			temp=d[j];
			d[j]=d[d.length-1];
			d[d.length-1]=temp;
		}
	}
}

