package dev4a.graphicalview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import dev4a.exceptions.AuthenticationException;
import dev4a.system.BettingSystem;

public class BetsManagerMenu extends Menu {
	/* statics for SWITCH statements */
	private static final int LISTBETSSUB = 1;
	private static final int LISTALL = 2;
	private static final int LISTSUBS = 3;
	private static final int LISTCOMPS = 4;
	
	/**
	 * Initialize the menu and set up the parent
	 * @param bs
	 * @param storredPass
	 */
	public BetsManagerMenu(BettingSystem bs, String storredPass, Menu parentMenu) {
		super(bs, storredPass);
		this.parentMenu = parentMenu;
	}

	@Override
	/**
	 * This method shows the appropriate menu for each type 
	 * we have the options printed in order and the user can 
	 * select one of them or a higher number to obtain a different
	 * behavior
	 * 
	 */
	public void showMenu() {
		
		System.out.println("");

		System.out.println("Bets Menu (BETA)");

		System.out.println("---------------------------");

		System.out.println("1. See bets for subscriber");
		
		System.out.println("2. List all bets");

		System.out.println("3. List all subscribers");

		System.out.println("4. List all competitions");
		
		System.out.println("*. Go back");

		System.out.println("----------------------------");

		System.out.println("");

		System.out.println("Please select an option from 1-4");
		
		System.out.println("To go back use a number higher than the ones in the list.");

		System.out.println("");

		System.out.println("");
		
	}

	@Override
	/**
	 * This method uses a simple choice selector (i.e. a switch statement)
	 * to chose the acction that is happening given the selected number
	 * 
	 * Uses the functions in the betting system given as a param to the class
	 * 
	 * @param selected (int) - the choice of the user
	 */
	protected int takeAction(int selected) {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		switch (selected) {
		case LISTBETSSUB:
			try {
				/* not for beta version */
				System.out.println("Insert username");
				String username = br.readLine();
							

			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}

			break;

		case LISTALL:
			try {
				/* not for beta version */

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			break;
			
		case LISTSUBS:
			try {

				this.bettingSystem.printSubscribers(this.storedPass);

			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}
			break;
			
		case LISTCOMPS:
			try {

				this.bettingSystem.printCompetitions();

			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}
			break;
			
		default:
			return -1;
		}
		return 0;
	}
	
}
