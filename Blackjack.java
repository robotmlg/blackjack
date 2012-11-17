/**
 * @author	Matt Goldman
 * @version	2.0
 * @since	2012-11-06
 */
import java.util.*;

public class Blackjack{
	static final int MAX_CARDS=11;//most cards you can have w/o busting
	static final int DEALER=0;//position of dealer in array
	/**
	 * A CLI game of Blackjack
	 *
	 * arguments optional
	 * @param	args[0]	number of players
	 * @param	args[1]	number of decks in shoe
	 * @param	args[2]	initial balance
	 * @param	args[3]	minimum bet
	 */
	public static void main(String[] args){
		int n=0;
		int winner=0;
		int nPlayers=0;
		int nDecks=0;
		int pval=0;
		int dval=0;
		double minBet=0;
		double iniBal=0;
		double ddown=0;
		char sel=0;
		boolean play=true;//play again?
		boolean lastHand=true;//must be true initially to trigger a shuffle
		boolean first=true;//first card of the round for the player
		ArrayList<Player> players=null;
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
		//n+1 for n players and a dealer
		players=new ArrayList<Player>();
		for(int i=0;i<nPlayers+1;++i)
			players.add(i,new Player(i));
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
			iniBal=Double.parseDouble(args[2]);
		else{
			System.out.print("E");
			do{
				System.out.print("nter players' initial balance: ");
				iniBal=IO.readDouble();
				if(iniBal<=0){
					System.out.println("Initial balance must be larger than $0.");
					System.out.print("Re-e");
				}
			}while(iniBal<=0);
		}
		//set each player's initial balance, leave dealer's bank at 0
		for(int i=1;i<players.size();++i)
			players.get(i).money=iniBal;
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
				shoe=new Deck(nDecks);
				shoe.shuffle();
				lastHand=false;
			}
			//Fill the players array with null Cards
			for(int i=0;i<players.size();++i)
				Arrays.fill(players.get(i).hand,null);
			//get bets
			System.out.println("Place your bets!");
			for(int i=1;i<players.size();++i){
				do{
					System.out.printf("Player %d: ",i);
					players.get(i).bet=IO.readDouble();
					if(players.get(i).bet<minBet)
						System.out.printf("Bet must be $%03.2f or more. Re-enter.\n",minBet);
				}while(players.get(i).bet<minBet);
				players.get(i).money-=players.get(i).bet;
			}
			System.out.println("Dealing...");
			for(int j=0;j<2;++j){
				for(int i=0;i<players.size() && !shoe.isEmpty();++i){
					players.get(i).hand[j]=shoe.deal();
					if(players.get(i).hand[j].getFace()==Card.CUT_CARD){
						System.out.println("\nCUT CARD DRAWN. LAST HAND\n");
						players.get(i).hand[j]=shoe.deal();
						lastHand=true;
					}
				}
			}
			if(shoe.isEmpty()){
				System.out.println("\nSHOE EMPTY. RE-SHUFFLING AND DEALING.\n");
			}
			//players loop
			//Start with index 1, because the dealer (0) goes last
			for(int i=1;!shoe.isEmpty() && i<players.size();++i){
				n=2;
				players.get(i).stood=false;
				first=true;
				while(players.get(i).handTotal()<21 && players.get(i).stood==false && n<MAX_CARDS){
					dispTable(players,true);
					System.out.printf("\nPlayer %s:  H: Hit  S: Stand  D: Double Down  ",players.get(i).pid);
					System.out.printf("%s",first==true?"P: Split  ":"");
					System.out.printf("Q: Quit\nChoice: ");
					sel=IO.readChar();
					switch(Character.toUpperCase(sel)){
						case 'H':
							players.get(i).hand[n]=shoe.deal();
							if(players.get(i).hand[n].getFace()==Card.CUT_CARD){
								System.out.println("\nCUT CARD DRAWN. LAST HAND.\n");
								lastHand=true;
								players.get(i).hand[n]=shoe.deal();
							}
							++n;
							players.get(i).stood=false;
							first=false;
							break;
						case 'S':
							players.get(i).stood=true;
							first=false;
							break;
						case 'D':
							System.out.print("E");
							do{
								System.out.printf("nter amount to increase bet (up to $%03.2f): ",players.get(i).bet);
								ddown=IO.readDouble();
								if(ddown>players.get(i).bet){
									System.out.print("Bet cannot be larger than original bet.\n");
									System.out.print("Re-e");
								}
								else if(ddown<=0){
									System.out.print("Bet cannot be negative.\n");
									System.out.print("Re-e");
								}
							}while(ddown>players.get(i).bet || ddown<=0);
							players.get(i).bet+=ddown;
							players.get(i).stood=true;
							players.get(i).hand[n]=shoe.deal();
							if(players.get(i).hand[n].getFace()==Card.CUT_CARD){
								System.out.println("\nCUT CARD DRAWN. LAST HAND.\n");
								lastHand=true;
								players.get(i).hand[n]=shoe.deal();
							}
							first=false;
							break;
						case 'P':
							first=false;
							break;
						case 'Q':
							System.out.println("Goodbye.");
							return;
						default:
							System.out.println("Invalid choice. Select again.");
							break;
					}
				}
				if(players.get(i).handTotal()==21 && players.get(i).hand[2]==null)
					System.out.printf("\nPlayer %d has blackjack!\n\n",i);
				else if(players.get(i).handTotal()==21)
					System.out.printf("\nPlayer %d has 21!\n\n",i);
			}
			if(!shoe.isEmpty()){
				dispTable(players,false);
				System.out.println("Dealing to dealer... (He stands on all 17s)");
				for(int i=2;players.get(DEALER).handTotal()<17 && i<MAX_CARDS;++i){
					players.get(DEALER).hand[i]=shoe.deal();
					if(players.get(DEALER).hand[i].getFace()==Card.CUT_CARD){
						System.out.println("\nCUT CARD DRAWN. LAST HAND.\n");
						lastHand=true;
						players.get(DEALER).hand[i]=shoe.deal();
					}
				}

				/*
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
				*/
				//Calculate winnings
				dval=players.get(DEALER).handTotal();
				for(int i=1;i<players.size();++i){
					pval=players.get(i).handTotal();
					if(dval>21 && pval<=21){//Dealer bust, player win
						if(pval==21 && players.get(i).hand[2]==null){
							players.get(i).money+=2.5*players.get(i).bet;
							players.get(DEALER).money-=1.5*players.get(i).bet;
						}
						else{
							players.get(i).money+=2*players.get(i).bet;//Dealer bust=win pays 1:1
							players.get(DEALER).money-=players.get(i).bet;
						}
					}
					else if(pval<=21 && dval<=21){//no bust
						if(pval>dval){
							if(pval==21 && players.get(i).hand[2]==null){
								players.get(i).money+=2.5*players.get(i).bet;//Blackjack pays 3:2
								players.get(DEALER).money-=1.5*players.get(i).bet;
							}
							else{
								players.get(i).money+=2*players.get(i).bet;//win pays 1:1
								players.get(DEALER).money-=players.get(i).bet;
							}
					}
						else if(pval==dval){
							if(pval==21 && players.get(i).hand[2]==null && players.get(DEALER).hand[2]!=null){
								players.get(i).money+=2.5*players.get(i).bet;//Blackjack beats other 21s
								players.get(DEALER).money-=1.5*players.get(i).bet;
							}
							else if(dval==21 && players.get(DEALER).hand[2]==null && players.get(i).hand[2]!=null){
								players.get(DEALER).money+=players.get(i).bet;
							}
							else
								players.get(i).money+=players.get(i).bet;//push: player's bet returned
						}
						else
							players.get(DEALER).money+=players.get(i).bet;//player loses, dealer gets bet
					}
					else if(pval>21)
						players.get(DEALER).money+=players.get(i).bet;
				}
				for(int i=1;i<players.size();++i)
					players.get(i).bet=0;
				System.out.println("FINAL:");
				dispTable(players,false);
				System.out.print("Play again? ");
				play=IO.readBoolean();
			}
		}while(play);
		System.out.println("Thank you for playing Blackjack with us.  Goodbye.");
	}
	/**
	 * Displays the cards on the table
	 *
	 * @param	c	the cards
	 * @param	m	the money
	 * @param	b	the bets
	 * @param	hide	obscure dealer's first card?
	 */
	public static void dispTable(ArrayList<Player> c,boolean hide){
		int blankCount=0;
		int ht=0;
		System.out.println();
		//Print balances (above player labels)
		for(int i=0;i<c.size();++i)
			System.out.printf("%-10s",String.format("$%03.2f",c.get(i).money));
		System.out.println();
		//Print player labels
		System.out.print("Dealer:   ");
		for(int i=1;i<c.size();++i)
			System.out.printf("Player %d: ",c.get(i).pid);
		System.out.print("\n");
		//Print cards
		for(int i=0;i<MAX_CARDS;++i){
			blankCount=0;
			for(int j=0;j<c.size();++j){
				if(hide==true && j==0 && i==0){
					System.out.printf("%-10s","XX");
					continue;
				}
				if(c.get(j).hand[i]!=null)
					System.out.printf("%-10s",c.get(j).hand[i].toString());
				else
					System.out.print("          ");
				//Keep track of how many blank cards are in the next row
				if(c.get(j).hand[i+1]==null)
					++blankCount;
			}
			System.out.print("\n");
			//if all the cards in the next row are blank, stop displaying
			if(blankCount==c.size())
				break;
		}
		for(int i=0;i<c.size();++i)
			System.out.print("----------");
		System.out.println();
		if(hide)
			System.out.printf("%-10s","??");
		else{
			ht=c.get(DEALER).handTotal();
			if(ht<=21)
				System.out.printf("%-10d",ht);
			else
				System.out.printf("%-10s","BUSTED");
		}
		for(int i=1;i<c.size();++i){
			ht=c.get(i).handTotal();
			if(ht<=21)
				System.out.printf("%-10d",ht);
			else
				System.out.printf("%-10s","BUSTED");
		}
		System.out.println();
		System.out.print("Bets:     ");
		for(int i=1;i<c.size();++i)
			System.out.printf("%-10s",String.format("$%03.2f",c.get(i).bet));
		System.out.println();
	}
}
