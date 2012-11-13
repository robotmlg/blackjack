/**
 * @author	Matt Goldman
 * @version	1.0
 * @since	2012-11-06
 */
import java.util.Arrays;

public class Blackjack{
	public static final int MAX_CARDS=11;//most cards you can have w/o busting
	/**
	 * 
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		int n=0;
		int winner=0;
		int nPlayers=0;
		int nDecks=0;
		int[] money=null;
		double minBet=0;
		double[] bets=null;
		char sel=0;
		boolean stood=false;//whether the player stands
		boolean play=true;//play again?
		boolean lastHand=true;//must be true initially to trigger a shuffle
		Card[] cards=null;
		Card[][] players=null;
		Deck shoe=null;
		//Get number of players
		if(args.length>0)
			nPlayers=Integer.parseInt(args[0]);
		else{
			System.out.print("E");
			do{
				System.out.print("nter number of players: ");
				nPlayers=IO.readInt();
				if(nPlayers<1){
					System.out.println("Number of players must be larger than 0.");
					System.out.print("Re-e");
				}
			}while(nPlayers<1);
		}
		//Setup array of players
		//n+1 rows for n players and a dea
		players=new Card[nPlayers+1][MAX_CARDS];
		//setup arrays for storing player balances and bets
		money=new int[nPlayers+1];
		bets=new double[nPlayers+1];
		Arrays.fill(bets,0);
		//Get number of decks
		if(args.length>1)
			nDecks=Integer.parseInt(args[1]);
		else{
			System.out.print("E");
			do{
				System.out.print("nter number of decks: ");
				nDecks=IO.readInt();
				if(nDecks<1){
					System.out.println("Number of decks must be larger than 0");
					System.out.print("Re-e");
				}
			}while(nDecks<1);
		}
		//Get initial balance
		if(args.length>2)
			Arrays.fill(money,Integer.parseInt(args[2]));
		else{
			System.out.print("E");
			do{
				System.out.print("nter players' initial balance: ");
				n=IO.readInt();
				if(n<=0){
					System.out.println("Initial balance must be larger than $0.");
					System.out.print("Re-e");
				}
			}while(n<=0);
			Arrays.fill(money,n);
		}
		//get minimum bet
		if(args.length>3)
			minBet=Double.parseDouble(args[3]);
		else{
			System.out.print("E");
			do{
				System.out.print("nter the minimum bet: ");
				minBet=IO.readDouble();
				if(minBet<=0){
					System.out.println("Minimum bet must be larger than $0.");
					System.out.print("Re-e");
				}
			}while(minBet<=0);
		}
		//game loop
		do{
			System.out.println();
			if(lastHand==true || nDecks==1){
				System.out.printf("Shuffling deck%s...\n",nDecks==1?"":"s");
				cards=makeCardArr(nDecks);
				shuffle(cards);
				shoe=new Deck(cards);
				lastHand=false;
			}
			//Fill the players[][] array with blank Cards
			for(int i=0;i<players.length;++i)
				Arrays.fill(players[i],null);
			System.out.println("Dealing...");
			for(int j=0;j<2;++j){
				for(int i=0;i<players.length && !shoe.isEmpty();++i){
					players[i][j]=shoe.deal();
					if(players[i][j].getFace()==Card.CUT_CARD){
						System.out.println("\nCUT CARD DRAWN. LAST HAND\n");
						players[i][j]=shoe.deal();
						lastHand=true;
					}
				}
			}
			if(shoe.isEmpty()){
				System.out.println("\nSHOE EMPTY. RE-SHUFFLING AND DEALING.\n");
				lastHand=true;
			}
			//players loop
			//Start with index 1, because the dealer (0) goes last
			for(int i=1;!shoe.isEmpty() && i<players.length;++i){
				n=2;
				stood=false;
				while(handTotal(players[i])<21 && stood==false && n<MAX_CARDS){
					dispTable(players,true);
					System.out.printf("Player %d:\tH: Hit\t\tS: Stand\tQ: Quit\nChoice: ",i);
					sel=IO.readChar();
					switch(Character.toUpperCase(sel)){
						case 'H':
							players[i][n]=shoe.deal();
							if(players[i][n].getFace()==Card.CUT_CARD){
								System.out.println("\nCUT CARD DRAWN. LAST HAND.\n");
								lastHand=true;
								players[i][n]=shoe.deal();
							}
							++n;
							stood=false;
							break;
						case 'S':
							stood=true;
							break;
						case 'Q':
							System.out.println("Goodbye.");
							return;
						default:
							System.out.println("Invalid choice. Select again.");
							break;
					}
				}
			}
			if(!shoe.isEmpty()){
				dispTable(players,false);
				System.out.println("Dealing to dealer... (He stands on all 17s)");
				for(int i=2;handTotal(players[0])<17 && i<MAX_CARDS;++i){
					players[0][i]=shoe.deal();
					if(players[0][i].getFace()==Card.CUT_CARD){
						System.out.println("\nCUT CARD DRAWN. LAST HAND.\n");
						lastHand=true;
						players[0][i]=shoe.deal();
					}
				}
				System.out.println("FINAL:");
				dispTable(players,false);
				winner=0;
				for(int i=0;i<players.length;++i){
					if(handTotal(players[i])<=21 && handTotal(players[i])>=handTotal(players[winner])){
						winner=i;
					}
					else if(handTotal(players[i])>21)
						winner++;
				}
				if(winner==0)
					System.out.println("Dealer wins!");
				else
					System.out.printf("Player %d wins!\n",winner);
				System.out.print("Play again? ");
				play=IO.readBoolean();
			}
		}while(play);
		System.out.println("Thank you for playing Blackjack with us.  Goodbye.");
	}
	/**
	 * Creates an array of cards, from a variable number of decks
	 *
	 * @param	n	number of decks to use
	 * @return	an array of 52 Cards, representing a standard deck
	 */
	public static Card[] makeCardArr(int n){
		Card[] ret=new Card[52*n+(n==1?0:1)];
		//?: op above makes room for a cut card iff more than 1 deck
		int ind=0;
		//Iterate through decks
		for(int k=0;k<n;++k){
			//Iterate through each suit
			for(int i=0;i<4;++i){
				//Iterate through each value
				for(int j=1;j<=13;++j){
					//create a new card
					ret[ind]=new Card(i,j);
					++ind;
				}
			}
		}
		//put the cut card in the last spot, iff more than 1 deck
		if(n>1){
			ret[52*n]=new Card(0,Card.CUT_CARD);
			//System.out.println("CUT CARD INSERTED");
		}
		return ret;
	}
	/**
	 * Shuffles an array of Cards in place
	 *
	 * @param	c	an array of Cards
	 */
	public static void shuffle(Card[] c){
		Card temp=null;
		int j=0;
		//Fisher-Yates-Knuth shuffle, except the cut card in the last index
		//There is only a cut card if there is more than one deck
		for(int i=c.length-(c.length>52?2:1);i>0;--i){
			j=(int)(Math.random()*(i));
			temp=c[i];
			c[i]=c[j];
			c[j]=temp;
		}
		//put the cut card 70% to 80% into the shoe, iff there's more than one deck
		if(c.length>52){
			j=(int)(0.70*c.length+Math.random()*0.10*(c.length));
			//System.out.printf("CUT CARD AT %d.\n",j);
			temp=c[j];
			c[j]=c[c.length-1];
			c[c.length-1]=temp;
		}
	}
	/**
	 * Displays the cards on the table
	 *
	 * @param	c	the cards
	 * @param	hide	obscure dealer's first card?
	 */
	public static void dispTable(Card[][] c,boolean hide){
		int blankCount=0;
		int ht=0;
		//Print player labels
		System.out.print("\nDealer:\t\t");
		for(int i=1;i<c.length;++i)
			System.out.printf("Player %d:\t",i);
		System.out.print("\n");
		//Print cards
		for(int i=0;i<MAX_CARDS;++i){
			blankCount=0;
			for(int j=0;j<c.length;++j){
				if(hide==true && j==0 && i==0){
					System.out.print("XX\t\t");
					continue;
				}
				dispCard(c[j][i]);
				System.out.print("\t\t");
				//Keep track of how many blank cards are in the next row
				if(c[j][i+1]==null)
					++blankCount;
			}
			System.out.print("\n");
			//if all the cards in the next row are blank, stop displaying
			if(blankCount==c.length)
				break;
		}
		for(int i=0;i<c.length;++i)
			System.out.print("----------------");
		System.out.println();
		if(hide)
			System.out.print("???\t\t");
		else{
			ht=handTotal(c[0]);
			if(ht<=21)
				System.out.printf("%d\t\t",ht);
			else
				System.out.print("BUSTED\t\t");
		}
		for(int i=1;i<c.length;++i){
			ht=handTotal(c[i]);
			if(ht<=21)
				System.out.printf("%d\t\t",ht);
			else
				System.out.printf("BUSTED\t\t");
		}
		System.out.println("\n");
	}
	/**
	 * Displays a single Card
	 *
	 * @param	c	a Card
	 */
	public static void dispCard(Card c){
		if(c==null)
			return;
		switch(c.getFace()){
			case Card.ACE:
				System.out.print("A");
				break;
			case Card.JACK:
				System.out.print("J");
				break;
			case Card.QUEEN:
				System.out.print("Q");
				break;
			case Card.KING:
				System.out.print("K");
				break;
			default:
				System.out.print(c.getFace());
				break;
		}
		switch(c.getSuit()){
			case Card.SPADES:
				System.out.print('\u2660');
				break;
			case Card.HEARTS:
				System.out.print('\u2665');
				break;
			case Card.CLUBS:
				System.out.print('\u2663');
				break;
			case Card.DIAMONDS:
				System.out.print('\u2666');
				break;
			default:
				return;
		}
	}
	/**
	 * Calculates the total hand value for a given player
	 *
	 * @param	h	an array of Cards
	 * @return	the sum of the card values
	 */
	public static int handTotal(Card[] h){
		int ret=0;
		for(int i=0;i<h.length && h[i]!=null;++i)
			ret+=(h[i].getValue()==1?11:h[i].getValue());
		if(ret>21){
			ret=0;
			for(int i=0;i<h.length && h[i]!=null;++i)
				ret+=h[i].getValue();
		}
		return ret;
	}
}
