package dev4a.graphicalview;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

import dev4a.exceptions.AuthenticationException;
import dev4a.exceptions.BadParametersException;
import dev4a.system.BettingSystem;
/**
 * 
 * @author g4ab2
 *
 */
public class CLIBettingSoft {
	/* the system */
	private BettingSystem bettingSystem;
	/* the choice */
	private int selected;
	/* the password we type when we try to authenticate */
	private String storedPassword = "1234567";
	/* we read input from the keyboard */
	private BufferedReader br;
	/* the menu we are in at the moment, so we can keep track */
	private Menu currentMenu;
	/**
	 * 
	 * @param bettingSys
	 */
	public CLIBettingSoft(BettingSystem bettingSys) {
		/* our new system */
		this.bettingSystem = bettingSys;
		
		/* to read the input */
		br = new BufferedReader(new InputStreamReader(System.in));
		/* ask for authentication as long as the user porovides a wrong pass */
		while(!askForAuthentication());
		/* show the first menu */
		this.currentMenu = new ManagerMenu(bettingSys, storedPassword);
		/* while we still want to to stuff */
		while(true) {
			/* show the instructions for the menu in a list fashion */
			showCurrentMenu();
			
			try {
				/* save the choice */
				selected = Integer.parseInt(br.readLine());
				/* if the input is negative or 0 we break */
				if ( selected <= 0) {
					
					throw new BadParametersException();
				}
				/* else see if the menu has submenus */
				if( (selected) < currentMenu.possibleMenus.size()) {
					/* we go into the next menu ..*/
					currentMenu = currentMenu.possibleMenus.get(selected-1);
				}
				/* choice was higher */
				else 
					if ( currentMenu.takeAction(selected) == -1 ) {
						currentMenu = currentMenu.getParent();
					}
			} catch (Exception ioe) {
				/* show the user he did something wrong */
				System.out.println("Wrong input, try something else..");

			}

		}
	}
	/* dummy for tests */
	private boolean askForAuthentication1() {
		// TODO Auto-generated method stub
		return true;
	}
	/* prints the options for the current menu */
	private void showCurrentMenu() {
		this.currentMenu.showMenu();
		
	}
	/**
	 *  authentication for the manager 
	 *  */
	private boolean askForAuthentication() {
		/* get the console */
		Console console = System.console();
		/* problems getting the console ? IDE maybe ?*/
		if (console == null) {
			System.out.println("Couldn't get Console instance");
			System.exit(0);
		}
		/* get the pass into an array */
		char [] passwordArray = console.readPassword("Enter your password: ");
		
		try {
			/*System.out.println("Password you entered was :" + new String(passwordArray));
			System.out.println("Password expected was :" + bettingSystem.getPassword());*/
			/* authenticate the manager */
			this.bettingSystem.authenticateMngr(new String(passwordArray)); 
			this.storedPassword = new String(passwordArray);
			return true;
		} catch (AuthenticationException ex) {
			System.out.println("Authentication failed!");
			return false;
		}
	}

}
