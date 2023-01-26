package dev4a.graphicalview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import dev4a.exceptions.AuthenticationException;
import dev4a.system.BettingSystem;

public class ClientMenu extends Menu {
	/* statics for SWITCH */
	private static final int BETWINNER = 1;
	private static final int BETPODIUM = 2;
	private static final int VIEWINFO = 3;
	private static final int CONSULTWINNER = 4;
	private static final int CHANGEPASS = 5;
	private static final int LISTCOMPETITIONS = 6;
	private static final int LISTCOMPETITORSINCOMPETITION = 7;
	
	
	/* store the user name */
	private String storedUserName = "";
	/**
	 * The main manu of the manager
	 * @param bs
	 * @param pass
	 */
	public ClientMenu (BettingSystem bs, String pass, String username) {
		super(bs, pass);
		/* the parent */
		this.parentMenu = this;
		this.storedUserName = username;
		this.storedPass = pass;
	}

	@Override
	public void showMenu() {
		System.out.println("");

		System.out.println("Client Menu");

		System.out.println("---------------------------");

		System.out.println("1. Bet on winner");

		System.out.println("2. Bet on podium");

		System.out.println("3. View my info");

		System.out.println("4. Consult winners on competition");

		System.out.println("5. Change password");

		System.out.println("6. List all competitions");
		
		System.out.println("7. List all competitiors in competition");
		
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
		case BETWINNER:
			try {
				/* insert the winner bet data */
				System.out.println("Insert number of tokens");
				long numberTokens = Long.parseLong(br.readLine());
				System.out.println("Insert competition name");
				String competition = br.readLine();
				System.out.println("Insert winner id");
				int winner = Integer.parseInt(br.readLine());
				/* persist */
				this.bettingSystem.betOnWinner(
						numberTokens, 
						competition, 
						bettingSystem.getCompetitorById(winner), 
						this.storedUserName, 
						this.storedPass
						);

			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}

			break;

		case BETPODIUM:
			try {
				/* insert the winner bet data */
				System.out.println("Insert number of tokens");
				long numberTokens = Long.parseLong(br.readLine());
				System.out.println("Insert competition name");
				String competition = br.readLine();
				System.out.println("Insert winner id");
				int winner = Integer.parseInt(br.readLine());
				System.out.println("Insert second id");
				int second = Integer.parseInt(br.readLine());
				System.out.println("Insert third id");
				int third = Integer.parseInt(br.readLine());
				/* persist */
				this.bettingSystem.betOnPodium(
						numberTokens, 
						competition, 
						bettingSystem.getCompetitorById(winner), 
						bettingSystem.getCompetitorById(second), 
						bettingSystem.getCompetitorById(third), 
						this.storedUserName, 
						this.storedPass
						);

			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}

			break;
		case VIEWINFO:
			try {
				
				this.bettingSystem.infosSubscriber(this.storedUserName, this.storedPass);

			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}
			break;
			
		case CONSULTWINNER:
			try {
				/* insert the data */
				System.out.println("Insert competition name");

				String competition = br.readLine();
				
				this.bettingSystem.printWinners(competition);
				//this.bettingSystem.consultResultsCompetition(competition);
				
				
			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}
			break;
		
		case CHANGEPASS:
			try {
				/* the new pass */
				System.out.println("Insert new password");
				String newPassword = br.readLine();
				/* change pass */
				System.out.println("this user name " + this.storedUserName + " his pass " + this.storedPass + " and the new pass " + newPassword);
				bettingSystem.changeSubsPwd(this.storedUserName, newPassword, this.storedPass);
				this.storedPass = newPassword;
			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}
			break;	
			
		case LISTCOMPETITIONS:
			try {

				this.bettingSystem.printCompetitions();

			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}
			break;
		case LISTCOMPETITORSINCOMPETITION:
			try {

				/* insert the data */
				System.out.println("Insert competition name");

				String competition = br.readLine();
				/* print */
				this.bettingSystem.printCompetitors(competition);

			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
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
