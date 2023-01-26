package dev4a.graphicalview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import dev4a.competition.ExistingCompetitionException;
import dev4a.competitor.ExistingCompetitorException;
import dev4a.exceptions.AuthenticationException;
import dev4a.exceptions.BadParametersException;
import dev4a.subscriber.ExistingSubscriberException;
import dev4a.system.BettingSystem;

public class CompetitorsManagerMenu extends Menu {
	
	private static final int CREATEINDIV = 1;
	private static final int CREATETEAM = 2;
	private static final int DELETE = 3;
	private static final int ADD = 4;
	private static final int ADDMEMBER = 5;
	private static final int LISTCOMPETITORSINCOMPETITION = 6;
	private static final int LISTALL = 7;
	private static final int LISTCOMPETITIONS = 8;
	private static final int DELETECOMPETITORFROMDB = 9;
	
	
	/**
	 * Initialize the menu and set up the parent
	 * @param bs
	 * @param storredPass
	 */
	public CompetitorsManagerMenu(BettingSystem bs, String storredPass, Menu parentMenu) {
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

		System.out.println("Competitors Menu");

		System.out.println("---------------------------");

		System.out.println("1. Create individual competitor");
		
		System.out.println("2. Create team");
		
		System.out.println("3. Delete competitor from comptition");

		System.out.println("4. Add to competition");
		
		System.out.println("5. Add member to team");
		
		System.out.println("6. List competitors in competition");
		
		System.out.println("7. List all competitors");
		
		System.out.println("8. List competitions");	
		
		System.out.println("9. Delete competitor from DB");	
		
		System.out.println("*. Go back");
		
		System.out.println("----------------------------");

		System.out.println("");

		System.out.println("Please select an option from 1-9");
		
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
		case CREATEINDIV:
			try {
				/* insert the data */
				System.out.println("Insert last name");
				String lastName = br.readLine();
				System.out.println("Insert first name");
				String firstName = br.readLine();
				System.out.println("Insert born date (format yyyy-MM-dd)");
				String borndate = br.readLine();
				/* persist */
				this.bettingSystem.createCompetitor(lastName, firstName, borndate, this.storedPass);

			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (BadParametersException e) {
				System.out.println("A wrong paramater was given.\nPlease try again.");
			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}

			break;

		case CREATETEAM:
			try {
				/* insert the data */
				System.out.println("Insert team name");

				String teamName = br.readLine();
				/* persist */
				this.bettingSystem.createCompetitor(teamName, this.storedPass);

			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (BadParametersException e) {
				System.out.println("A wrong paramater was given.\nPlease try again.");
			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}
			break;
			
		case DELETE:
			try {
				/* insert the data */
				System.out.println("Insert id");

				String id = br.readLine();
				
				System.out.println("Insert competition name");

				String competition = br.readLine();
				/* persist */
				this.bettingSystem.deleteCompetitor(competition, bettingSystem.getCompetitorById(Integer.valueOf(id)), this.storedPass);

			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
			} catch (ExistingCompetitionException e) {
				System.out.println("Non existing competition.\nPlease try again.");
			} catch (ExistingCompetitorException e) {
				System.out.println("Competitor does not exist!.\nPlease try again.");
			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}
			break;
			
		case ADD:
			try {
				/* insert the data */
				System.out.println("Insert competition name");

				String competition = br.readLine();
				
				System.out.println("Insert id of the competitor");

				int competitor = Integer.parseInt(br.readLine());
				/* persist */
				
				this.bettingSystem.addCompetitor(competition, bettingSystem.getCompetitorById(competitor), this.storedPass);

			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
			} catch (ExistingCompetitionException e) {
				System.out.println("Non existing competition.\nPlease try again.");
			} catch (ExistingCompetitorException e) {
				System.out.println("Competitor does not exist!.\nPlease try again.");
			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}
			break;
			
		case ADDMEMBER:
			try {
				/* insert the data */
				System.out.println("Insert team id");

				int teamId = Integer.parseInt(br.readLine());
				
				System.out.println("Insert individual competitor id");

				int competitorId = Integer.parseInt(br.readLine());
				
				System.out.println("NOTE! This function is in BETA!");
				
				bettingSystem.addCompetitiorToTeam(
						bettingSystem.getCompetitorById(competitorId), 
						bettingSystem.getCompetitorById(teamId), 
						this.storedPass);
				
			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
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
			
		case LISTALL:
			try {
				/* print */
				this.bettingSystem.printAllCompetitors();

			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}
			break;
			
		case LISTCOMPETITIONS:
			try {
				/* print */
				this.bettingSystem.printCompetitions();

			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}
			break;
			
		case DELETECOMPETITORFROMDB:
			try {
				/* insert the data */
				System.out.println("Insert competitor id");

				int competitorId = Integer.parseInt(br.readLine());
				
				System.out.println("NOTE! This function is in BETA!");
				
				bettingSystem.deleteCompetitorFromDB(
						bettingSystem.getCompetitorById(competitorId), 
						this.storedPass);
				
			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
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
