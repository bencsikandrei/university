package dev4a.graphicalview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import dev4a.exceptions.AuthenticationException;
import dev4a.exceptions.BadParametersException;
import dev4a.subscriber.ExistingSubscriberException;
import dev4a.system.BettingSystem;

public class ManagerMenu extends Menu {
	/**
	 * The main manu of the manager
	 * @param bs
	 * @param pass
	 */
	public ManagerMenu (BettingSystem bs, String pass) {
		super(bs, pass);
		/* set the possible menus */
		this.possibleMenus.add(new SubscribersManagerMenu(bs, pass, this));
		this.possibleMenus.add(new CompetitorsManagerMenu(bs, pass, this));
		this.possibleMenus.add(new CompetitionsManagerMenu(bs, pass, this));
		this.possibleMenus.add(new BetsManagerMenu(bs, pass, this));
		/* the parent */
		this.parentMenu = this;
	}

	@Override
	public void showMenu() {
		System.out.println("");

		System.out.println("Main Menu");

		System.out.println("---------------------------");

		System.out.println("1. Manage subscribers");

		System.out.println("2. Manage competitors");

		System.out.println("3. Manage competitions");

		System.out.println("4. Manage bets");

		System.out.println("5. Change password");

		System.out.println("6. List all subscribers");

		System.out.println("7. List all competitions");

		System.out.println("*. Exit system");

		System.out.println("----------------------------");

		System.out.println("");

		System.out.println("Please select an option from 1-7");
		
		System.out.println("To go back use a number higher than the ones in the list.");

		System.out.println("");

		System.out.println("");

	}
	/**
	 * Take aciton based on the 'selected' integer
	 */
	@Override
	protected int takeAction(int selected) {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		switch (selected) {
		case 5:
			String newPassword = new String(storedPass);
			try {
				/* the new pass */
				System.out.println("Insert new password");
				newPassword = br.readLine();
				/* change pass */
				bettingSystem.changeManagerPassword(this.storedPass, newPassword);
				
			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
			} catch (Exception ex) {
				System.out.println("Something went wrong.\nPlease try again!");
			}
			this.storedPass = newPassword;
			
			for (Menu menu : this.possibleMenus) {
				menu.setPassword(this.storedPass);
			}
			break;	
		case 6:
			try {

				this.bettingSystem.printSubscribers(storedPass);

			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (Exception ex) {
				System.out.println("Something went wrong.\nPlease try again!");
			}
			break;
		case 7:
			try {

				this.bettingSystem.printCompetitions();

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
