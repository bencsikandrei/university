package dev4a.graphicalview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import dev4a.exceptions.AuthenticationException;
import dev4a.exceptions.BadParametersException;
import dev4a.subscriber.ExistingSubscriberException;
import dev4a.subscriber.SubscriberException;
import dev4a.system.BettingSystem;

public class SubscribersManagerMenu extends Menu {
	private static final int SUBSCRIBE = 1;
	private static final int UNSUBSCRIBE = 2;
	private static final int CREDIT = 3;
	private static final int DEBIT = 4;
	private static final int LIST = 5;

	
	
	/**
	 * Initialize the menu and set up the parent
	 * @param bs
	 * @param storredPass
	 */
	public SubscribersManagerMenu(BettingSystem bs, String pass, Menu parentMenu) {
		super(bs, pass);
		this.parentMenu = parentMenu;
	}
	/**
	 * This method shows the appropriate menu for each type 
	 * we have the options printed in order and the user can 
	 * select one of them or a higher number to obtain a different
	 * behavior
	 * 
	 */
	@Override
	public void showMenu() {
		System.out.println("");

		System.out.println("Subscribers Menu");

		System.out.println("---------------------------");

		System.out.println("1. Add subscriber");

		System.out.println("2. Delete subscriber");

		System.out.println("3. Credit subscriber");

		System.out.println("4. Debit subscriber");

		System.out.println("5. List subscribers");
		
		System.out.println("*. Go back");			
		
		System.out.println("----------------------------");

		System.out.println("");

		System.out.println("Please select an option from 1-5");
		
		System.out.println("To go back use a number higher than the ones in the list.");

		System.out.println("");

		System.out.println("");

	}
	
	/**
	 * This method uses a simple choice selector (i.e. a switch statement)
	 * to chose the acction that is happening given the selected number
	 * 
	 * Uses the functions in the betting system given as a param to the class
	 * 
	 * @param selected (int) - the choice of the user
	 */
	@Override
	protected int takeAction(int selected) {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		switch (selected) {
		case SUBSCRIBE:
			try {

				System.out.println("Insert last name"); 
				String lastName = br.readLine();
				System.out.println("Insert first name");
				String firstName = br.readLine();
				System.out.println("Insert username");
				String username = br.readLine();
				System.out.println("Insert born date (format yyyy-MM-dd)");
				String borndate = br.readLine();
				
				this.bettingSystem.subscribe(lastName, firstName, username, borndate, this.storedPass);

			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (ExistingSubscriberException e) {
				System.out.println("The subscriber already exists in the DB.\nPlease try another username.");
			} catch (SubscriberException e) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (BadParametersException e) {
				System.out.println("A wrong paramater was given.\nPlease try again.");
			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
			} catch (Exception ex) {
				System.out.println("Something went wrong.\nPlease try again!");
			}

			break;

		case UNSUBSCRIBE: 
			try {

				System.out.println("Insert username");

				String username = br.readLine();
				
				this.bettingSystem.unsubscribe(username, this.storedPass);
			} catch (AuthenticationException e) {
				System.out.println("A wrong password was given.\nPlease try again.");
			} catch (ExistingSubscriberException e) {
				System.out.println("The subscriber does not exist.\nPlease try again");
			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
			} catch (Exception ex) {
				System.out.println("Something went wrong.\nPlease try again!");
			}
			
		case CREDIT:
			try {

				System.out.println("Insert username");

				String username = br.readLine();
				
				System.out.println("Insert number of tokens");

				long numberOfTokens = Long.parseLong(br.readLine());
				
				this.bettingSystem.creditSubscriber(username, numberOfTokens, this.storedPass);
			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (ExistingSubscriberException e) {
				System.out.println("The subscriber already exists in the DB.\nPlease try another username.");
			} catch (BadParametersException e) {
				System.out.println("A wrong paramater was given.\nPlease try again.");
			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
			} catch (Exception ex) {
				System.out.println("Something went wrong.\nPlease try again!");
			}
			break;
			
		case DEBIT:
			try {

				System.out.println("Insert username");

				String username = br.readLine();
				
				System.out.println("Insert number of tokens");

				long numberOfTokens = Long.parseLong(br.readLine());
				
				this.bettingSystem.debitSubscriber(username, numberOfTokens, this.storedPass);

			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (ExistingSubscriberException e) {
				System.out.println("The subscriber already exists in the DB.\nPlease try another username.");
			} catch (BadParametersException e) {
				System.out.println("A wrong paramater was given.\nPlease try again.");
			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
			} catch (Exception ex) {
				System.out.println("Something went wrong.\nPlease try again!");
			}
			break;
			
		case LIST:
			try {

				this.bettingSystem.printSubscribers(this.storedPass);
				
			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (Exception ex) {
				System.out.println("Something went wrong.\nPlease try again!");
			}		
			break;
			
		default:
			return -1;
		}
		return 0;
	}
	
}
