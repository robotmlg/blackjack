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
		int winner=0;
		int nPlayers=0;
		int nDecks=0;
		int pval=0;
		int dval=0;
		int maxSplits=0;
		double minBet=0;
		double iniBal=0;
		double ddown=0;
		char sel=0;
		boolean play=true;//play again?
		boolean bShuffle=true;//must be true initially to trigger a shuffle
		boolean bCutCard=false;//whether or not the cut card got drawn
		boolean first=false;//first card of the round for the player
		boolean softHit=false;
		boolean showHole=false;//show the hole card?
		ArrayList<Player> players=null;
		Deck shoe=null;
		//Use a preset gamemode
		System.out.println("Select a gamemode:");
		System.out.println("A: Atlantic City\n\t-8 deck shoe\n\t-Resplit to 4\n\t-Dealer stands on all 17s");
		System.out.println("L: Vegas Strip\n\t-4 deck shoe\n\t-Resplit to 4\n\t-Dealer stands on all 17s");
		System.out.println("D: Vegas Downtown\n\t-2 deck shoe\n\t-Resplit to 4\n\t-Dealer hits soft 17s");
		System.out.println("V: Vegas Single Deck\n\t-1 deck\n\t-Resplit to 2\n\t-Dealer hits soft 17s");
		System.out.println("E: European\n\t-2 deck shoe\n\t-Resplit to 2\n\t-Dealer stands on all 17s");
		System.out.print("C: Custom\nChoice: ");
		do{
			sel=IO.readChar();
			switch(Character.toUpperCase(sel)){
				case 'A':
					nDecks=8;
					maxSplits=2;
					softHit=false;
					first=true;
					break;
				case 'L':
					nDecks=4;
					maxSplits=2;
					softHit=true;
					first=true;
					break;
				case 'D':
					nDecks=2;
					maxSplits=2;
					softHit=true;
					first=true;
					break;
				case 'V':
					nDecks=1;
					maxSplits=1;
					softHit=true;
					first=true;
					break;
				case 'E':
					nDecks=2;
					maxSplits=1;
					softHit=false;
					first=true;
					break;
				case 'C':
					first=true;
					break;
				default:
					System.out.print("Invalid selection. Choose again:");
					break;
			}
		}while(first=false);
		//Get number of players
		if(args.length>0)
			nPlayers=Integer.parseInt(args[0]);
		if(nPlayers<1){
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
		//nPlayers+1 for n players and a dealer
		players=new ArrayList<Player>();
		for(int i=0;i<nPlayers+1;++i)
			players.add(i,new Player(i));
		//Get number of decks
		if(args.length>1)
			nDecks=Integer.parseInt(args[1]);
		if(nDecks<1){
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
		//Get max number of splits
		if(args.length>2)
			maxSplits=Integer.parseInt(args[2]);
		if(maxSplits<1){
			System.out.print("E");
			do{
				System.out.print("nter max number of splits: ");
				maxSplits=IO.readInt();
				if(maxSplits<1){
					System.out.println("Number of splits must be larger than 0");
					System.out.print("Re-e");
				}
			}while(maxSplits<1);
		}
		//soft 17 hit?
		if(Character.toUpperCase(sel)=='C'){
			System.out.print("Should the dealer hit soft 17s? ");
			softHit=IO.readBoolean();
		}
		//Get initial balance
		if(args.length>3)
			iniBal=Double.parseDouble(args[3]);
		if(iniBal<=0){
			System.out.print("E");
			do{
				System.out.print("nter players' initial balance: $");
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
		if(args.length>4)
			minBet=Double.parseDouble(args[4]);
		if(minBet<=0){
			System.out.print("E");
			do{
				System.out.print("nter the minimum bet: $");
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
			if(bShuffle==true || nDecks==1){
				System.out.printf("Shuffling deck%s...\n",nDecks==1?"":"s");
				shoe=new Deck(nDecks);
				shoe.shuffle();
				bShuffle=false;
				showHole=false;
			}
			//reset players
			for(int i=0;i<players.size();++i){
				players.get(i).reset();
			}
			//get bets
			System.out.println("Place your bets!");
			for(int i=1;i<players.size();++i){
				do{
					System.out.printf("Player %d: ",players.get(i).pid);
					players.get(i).bet=IO.readDouble();
					if(players.get(i).bet<minBet)
						System.out.printf("Bet must be $%03.2f or more. Re-enter.\n",minBet);
					if(players.get(i).bet>players.get(i).money)
						System.out.printf("You cannot bet more than the amount of money you have. Re-enter.\n");
				}while(players.get(i).bet<minBet || players.get(i).bet>players.get(i).money);
				players.get(i).money-=players.get(i).bet;
			}
			System.out.println("Dealing...");
			for(int j=0;j<2;++j){
				for(int i=0;i<players.size() && !shoe.isEmpty();++i){
					bCutCard=players.get(i).addCard(shoe);
					if(bCutCard==true)bShuffle=true;
				}
			}
			if(shoe.isEmpty()){
				System.out.println("\nSHOE EMPTY. RE-SHUFFLING AND DEALING.\n");
			}
			//INSURANCE
			if(players.get(DEALER).hand[1].getFace()==Card.ACE){
				dispTable(players,false);
				System.out.println("\nThe dealer is showing an Ace. Insurance open.");
				System.out.println("Maximum insurance amount is half of your original bet.");
				System.out.println("Enter 0 for no insurance.\n");
				for(int i=1;i<players.size();++i){
					do{
						System.out.printf("Player %d: $",players.get(i).pid);
						players.get(i).ins=IO.readDouble();
						if(players.get(i).ins>players.get(i).bet/2)
							System.out.printf("Bet must be less than $%03.2f. Re-enter.\n",players.get(i).bet/2);
						else if(players.get(i).ins<0)
							System.out.printf("Bet must be larger than 0. Re-enter.\n");
						if(players.get(i).ins>players.get(i).money)
							System.out.printf("You cannot bet more than the amount of money you have. Re-enter.\n");
					}while(players.get(i).ins<0 || players.get(i).ins>players.get(i).bet/2 || players.get(i).ins>players.get(i).money);
					players.get(i).money-=players.get(i).ins;
					if(players.get(i).ins>0)showHole=true;
				}
				System.out.println("Insurance closed.");
				if(showHole==true){
					dispTable(players,true);
					if(players.get(DEALER).hasBJ()){
						System.out.println("\nDealer has blackjack!\nPaying out insurance...\n");
						for(int i=1;i<players.size();++i){
							//Insurance pays out 2:1
							players.get(i).money+=3*players.get(i).ins;
							players.get(DEALER).money-=2*players.get(i).ins;
							players.get(i).ins=0;
						}
					}
					else{
						System.out.println("\nDealer does not have blackjack!\nInsurance bets are lost...\n");
						for(int i=1;i<players.size();++i){
							players.get(DEALER).money+=players.get(i).ins;
							players.get(i).ins=0;
						}
					}
				}
				else
					System.out.println("No one bought insurance. The round continues.\n");
			}
			//players loop
			//Start with index 1, because the dealer (at index 0) goes last
			for(int i=1;!shoe.isEmpty() && i<players.size();++i){
				first=true;
				while(players.get(i).handTotal()<21 && players.get(i).stood==false){
					dispTable(players,showHole);
					System.out.printf("\nPlayer %d:  H: Hit  S: Stand  D: Double Down  ",players.get(i).pid);
					if(players.get(i).splits<maxSplits && first==true && players.get(i).hand[0].getValue()==players.get(i).hand[1].getValue())
						System.out.printf("P: Split  ");
					if(first==true && players.get(i).splits==0)
						System.out.printf("U: Surrender  ");
					System.out.printf("G: Guru  Q: Quit\nChoice: ");
					sel=IO.readChar();
					switch(Character.toUpperCase(sel)){
						case 'H':
							bCutCard=players.get(i).addCard(shoe);
							if(bCutCard==true)bShuffle=true;
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
								else if(ddown>players.get(players.get(i).pid).money){
									System.out.printf("You cannot bet more than the amount of money you have.\nRe-");
								}
							}while(ddown>players.get(i).bet || ddown<=0 || ddown>players.get(players.get(i).pid).money);
							players.get(i).bet+=ddown;
							players.get(i).money-=ddown;
							players.get(i).stood=true;
							bCutCard=players.get(i).addCard(shoe);
							if(bCutCard==true)bShuffle=true;
							first=false;
							break;
						case 'P':
							if(players.get(i).money<players.get(i).bet){
								System.out.println("\nYou do not have enough money to split\n");
								break;
							}
							if(players.get(i).splits<maxSplits && first==true && players.get(i).hand[0].equalVal(players.get(i).hand[1])){
								//make a second Player, but with the same pid
								players.add(i+1,new Player(players.get(i).pid));
								//move the player's second card to the new player
								players.get(i+1).addCard(players.get(i).hand[1]);
								//go back a card in the first hand
								players.get(i).index--;
								//deal each a new card
								bCutCard=players.get(i).addCard(shoe);
								if(bCutCard==true)bShuffle=true;
								bCutCard=players.get(i+1).addCard(shoe);
								if(bCutCard==true)bShuffle=true;
								//transfer bet
								players.get(i+1).bet=players.get(i).bet;
								players.get(i).money-=players.get(i).bet;
								//inc splits
								players.get(i+1).splits=++players.get(i).splits;
								//can't hit split aces
								if(players.get(i+1).hand[0].getFace()==Card.ACE){
									players.get(i).stood=true;
									players.get(i+1).stood=true;
									System.out.printf("\nPlayer %d can't hit on split Aces.\n\n",i);
								}
							}
							else
								System.out.println("Invalid choice. Select again.");
							break;
						case 'U':
							if(first==true && players.get(i).splits==0){
								players.get(i).money+=players.get(i).bet/2;
								players.get(DEALER).money+=players.get(i).bet/2;
								players.get(i).bet=0;
								players.get(i).stood=true;
							}
							else
								System.out.println("Invalid choice. Select again.");
							first=false;
							break;
						case 'G':
							System.out.println("\nThe Guru thinks you should " +hint(players.get(i),players.get(DEALER),softHit)+".\n");

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
				System.out.print("\nDealing to dealer - ");
				if(softHit==true)
					System.out.println("he hits on soft 17s");
				else
					System.out.println("he stands on all 17s");
				System.out.println();
				while(players.get(DEALER).handTotal()<=17){
					//if you have a soft 17, and the dealer hits on soft 17s, hit
					if(players.get(DEALER).handTotal()==17 && softHit==true && players.get(DEALER).isSoft()==true){
						System.out.println("DEALER SOFT");
						bCutCard=players.get(DEALER).addCard(shoe);
						if(bCutCard==true)bShuffle=true;
					}
					//if you have any kind of 17
					else if(players.get(DEALER).handTotal()==17)
						break;
					else{
						bCutCard=players.get(DEALER).addCard(shoe);
						if(bCutCard==true)bShuffle=true;
					}
				}
				//Calculate winnings
				dval=players.get(DEALER).handTotal();
				for(int i=1;i<players.size();++i){
					pval=players.get(i).handTotal();
					if(dval>21 && pval<=21){//Dealer bust, player win
						if(players.get(i).hasBJ()){
							players.get(i).money+=2.5*players.get(i).bet;
							players.get(DEALER).money-=1.5*players.get(i).bet;
						}
						else{
							players.get(i).money+=2*players.get(i).bet;//Dealer bust=win pays 1:1
							players.get(DEALER).money-=players.get(i).bet;
						}
					}
					else if(pval<=21 && dval<=21){//no bust
						if(pval>dval){//player wins
							if(players.get(i).hasBJ()){
								players.get(i).money+=2.5*players.get(i).bet;//Blackjack pays 3:2, except on a split
								players.get(DEALER).money-=1.5*players.get(i).bet;
							}
							else{
								players.get(i).money+=2*players.get(i).bet;//win pays 1:1
								players.get(DEALER).money-=players.get(i).bet;
							}
						}
						else if(pval==dval){//possible push
							//player has blackjack, dealer just had 21
							if(players.get(i).hasBJ() && !players.get(DEALER).hasBJ()){
								players.get(i).money+=2.5*players.get(i).bet;//Blackjack beats other 21s
								players.get(DEALER).money-=1.5*players.get(i).bet;
							}
							//dealer had blackjack, player just has 21
							else if(!players.get(i).hasBJ() && players.get(DEALER).hasBJ()){
								players.get(DEALER).money+=players.get(i).bet;
							}
							else
								players.get(i).money+=players.get(i).bet;//push: player's bet returned
						}
						else//dealer wins
							players.get(DEALER).money+=players.get(i).bet;//player loses, dealer gets bet
					}
					else if(pval>21)//player bust, lose bet
						players.get(DEALER).money+=players.get(i).bet;
					//Reconcile split hands
					if(players.get(i).pid!=i){
						players.get(players.get(i).pid).money+=players.get(i).money;
						players.remove(i);
						--i;
					}
				}
				for(int i=1;i<players.size();++i)
					players.get(i).bet=0;
				System.out.println("FINAL:");
				dispTable(players,true);
				for(int i=1;i<players.size();++i){
					if(players.get(i).money<=0){
						System.out.printf("\nPlayer %d is out of money.  Removing him...\n",players.get(i).pid);
						players.remove(i);
						--i;
					}
				}
				if(players.size()==1){
					System.out.println("All players have been eliminated.  The game is over.");
					play=false;
				}
				else{
					System.out.print("Play again? ");
					play=IO.readBoolean();
				}
			}
		}while(play==true);
		System.out.println("Thank you for playing Blackjack with us.  Goodbye.");
	}
	/**
	 * Give a hint as to the best course of action
	 *
	 * @param	p	a Player
	 * @param	d	the Dealer
	 * @param	h	hits on soft 17s?
	 * @return	a string of the recommended move
	 */
	public static String hint(Player p,Player d,boolean h){
		final String split="split";
		final String ddown="double down";
		final String hit="hit";
		final String stand="stand";
		final String surr="surrender";
		String ret="";
		int dval=d.hand[1].getValue();
		//player has a pair
		if(p.hand[2]==null && p.hand[0].equalVal(p.hand[1])){
			switch(p.hand[0].getValue()){
				case Card.ACE:
				case Card.EIGHT:
					ret=split;
					break;
				case Card.TWO:
				case Card.THREE:
					if(dval>=8 || dval==1)ret=hit;
					else ret=split;
					break;
				case Card.FOUR:
					if(dval==5 || dval==6)ret=split;
					else ret=hit;
					break;
				case Card.FIVE:
					if(dval==10 || dval==1)ret=hit;
					else ret=ddown;
					break;
				case Card.SIX:
					if(dval>=7 || dval==1)ret=hit;
					else ret=split;
					break;
				case Card.SEVEN:
					if(dval>=8 || dval==1)ret=hit;
					else ret=split;
					break;
				case Card.NINE:
					if(dval==1 || dval==10 || dval==7)ret=stand;
					else ret=split;
					break;
				case Card.TEN:
					ret=stand;
					break;
			}
		}
		//soft hands
		else if(p.isSoft()){
			switch(p.handTotal()){
				case 19: case 20:
					if(h==true && dval==6) ret=ddown;
					else ret=stand;
					break;
				case 18:
					if(h==true && dval==2) ret=ddown;
					else if(dval==2 || dval==7 || dval==8) ret=stand;
					else if(dval>=3 && dval <=6) ret=ddown;
					else ret=hit;
					break;
				case 17:
					if(dval>=3 && dval<=6) ret=ddown;
					else ret=hit;
					break;
				case 16: case 15:
					if(dval>=4 && dval<=6) ret=ddown;
					else ret=hit;
					break;
				case 14: case 13:
					if(dval==5 || dval==6) ret=ddown;
					else ret=hit;
					break;
				default:
					ret=hit;
					break;
			}
		}
		//generic totals
		else{
			switch(p.handTotal()){
				case 17: case 18: case 19: case 20:
					if(dval==1 && h==true) ret=surr;
					else ret=stand;
					break;
				case 16:
					if(dval>=9 || dval==1) ret=surr;
					else if(dval<=6) ret=stand;
					else ret=hit;
					break;
				case 15:
					if(dval<=6 && dval!=1) ret=surr;
					else if(dval==10) ret=surr;
					else if(dval==1 && h==true) ret=surr;
					else ret=hit;
					break;
				case 13: case 14:
					if(dval>=7 || dval==1) ret=hit;
					else ret=stand;
					break;
				case 12:
					if(dval>=7 || dval<=3) ret=hit;
					else ret=stand;
					break;
				case 11:
					if(dval==1){
						if(h==true) ret=ddown;
						 else ret=hit;
					}
					else ret=ddown;
					break;
				case 10:
					if(dval==1 || dval==10) ret=hit;
					else ret=ddown;
					break;
				case 9:
					if(dval>=3 && dval<=6) ret=ddown;
					else ret=hit;
					break;
				case 8: case 7: case 6: case 5:
					ret=hit;
					break;
				default:
					ret=stand;
					break;
			}
		}
		if(ret.equals(surr) && p.hand[2]!=null)
			ret=hit;
		return ret;
	}
	/**
	 * Displays the cards on the table
	 *
	 * @param	c	the Players
	 * @param	show	obscure dealer's first card?
	 */
	public static void dispTable(ArrayList<Player> c,boolean show){
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
				if(show==false && j==0 && i==0){
					System.out.printf("%-10s","????");
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
		if(show==false)
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
