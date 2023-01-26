package dev4a.graphicalview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import dev4a.competitor.Competitor;
import dev4a.exceptions.AuthenticationException;
import dev4a.exceptions.BadParametersException;
import dev4a.system.BettingSystem;
import dev4a.utils.Utils;

public class CompetitionsManagerMenu extends Menu {
	
	/* to better differentiate the Menu choices we use constants */
	private static final int ADD = 1;
	private static final int CANCEL = 2;
	private static final int DELETE = 3;
	private static final int SETWINNER = 4;
	private static final int SETPODIUM = 5;
	private static final int LISTALLSUBS = 6;
	private static final int LISTALLCOMPETITIONS = 7;	
	
	/* the utilities */
	Utils utility = new Utils();

	/**
	 * Initialize the menu and set up the parent
	 * @param bs
	 * @param storredPass
	 */
	public CompetitionsManagerMenu(BettingSystem bs, String storredPass, Menu parentMenu) {
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

		System.out.println("Competitions Menu");

		System.out.println("---------------------------");

		System.out.println("1. Add competition");

		System.out.println("2. Cancel competition");

		System.out.println("3. Delete competition");

		System.out.println("4. Set winner for competition");

		System.out.println("5. Set podium for competition");

		System.out.println("6. List all subscribers");

		System.out.println("7. List all competitions");

		System.out.println("*. Go back");		

		System.out.println("----------------------------");

		System.out.println("");

		System.out.println("Please select an option from 1-7");
		
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
		case ADD:
			try {

				System.out.println("Insert competition name");
				String competition = br.readLine();
				System.out.println("Insert closing date (format yyyy-MM-dd)");
				String closingDate = br.readLine();
				utility.checkValidDate2(closingDate);
				/* to get the calendar from a string ..*/
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = sdf.parse(closingDate);
				Calendar cal = sdf.getCalendar();

				System.out.println("Insert competitor id (-1 to stop adding)");
				int compId = Integer.parseInt(br.readLine());
				ArrayList<Competitor> competitors = new ArrayList<>();
				do {
					competitors.add(bettingSystem.getCompetitorById(compId));
					System.out.println("got " + bettingSystem.getCompetitorById(compId));
					System.out.println("Insert competitor id (-1 to stop adding)");
				} while( (compId = Integer.parseInt(br.readLine())) != -1 );

				this.bettingSystem.addCompetition(competition, cal, competitors, this.storedPass);

			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (BadParametersException ex){
				System.out.println("Wrong date format.");
			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}

			break;

		case CANCEL:
			try {
				/* name of the compeittion to cancel */
				System.out.println("Insert competition name");

				String competition = br.readLine();

				this.bettingSystem.cancelCompetition(competition, this.storedPass);

			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}
			break;
		case DELETE:
			try {
				/* name of the competition to delete */
				System.out.println("Insert competition name");

				String competition = br.readLine();

				this.bettingSystem.deleteCompetition(competition, this.storedPass);

			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}
			break;
			
		case SETWINNER: 
			try {
				/* competition name */
				System.out.println("Insert competition name");

				String competition = br.readLine();

				/* winner id */
				System.out.println("Insert winner id");

				int winner = Integer.parseInt(br.readLine());
				
				this.bettingSystem.settleWinner(
						competition, 
						bettingSystem.getCompetitorById(winner),						
						this.storedPass);

			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}
			break;

		
		case SETPODIUM:
			try {
				/* competition name */
				System.out.println("Insert competition name");

				String competition = br.readLine();

				/* winner id */
				System.out.println("Insert winner id");

				int winner = Integer.parseInt(br.readLine());

				/* second id */
				System.out.println("Insert second id");

				int second = Integer.parseInt(br.readLine());

				/* third id */
				System.out.println("Insert third id");

				int third = Integer.parseInt(br.readLine());
				
				/* seal the deal */
				this.bettingSystem.settlePodium(
						competition, 
						bettingSystem.getCompetitorById(winner),
						bettingSystem.getCompetitorById(second),
						bettingSystem.getCompetitorById(third),
						this.storedPass);

			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (IOException e) {
				System.out.println("Wrong input.\nPlease try again.");
			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}
			break;

		case LISTALLSUBS:
			try {
				/* print everybody */
				this.bettingSystem.printSubscribers(this.storedPass);

			} catch (AuthenticationException ex) {
				System.out.println("Authentication error!\nPlease try again.");
			} catch (Exception e) {
				System.out.println("Something went wrong.\nPlease try again");
			}
			break;
			
		case LISTALLCOMPETITIONS:
			try {
				/* all competitions */
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
