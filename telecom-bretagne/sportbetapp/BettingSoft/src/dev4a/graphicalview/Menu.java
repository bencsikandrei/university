package dev4a.graphicalview;

import java.util.ArrayList;

import dev4a.system.BettingSystem;
/**
 * Parent class of the menus for the CLI (client and manager)
 * Constains the main methods and some of the attributes to
 * be found in all of these classes
 * 
 * @author Andrei
 *
 */
public class Menu {
	/* the parent menu of this object */
	protected Menu parentMenu = null;
	/* system used for acting */
	protected BettingSystem bettingSystem;
	/* the pass we intorudce by hand */
	protected String storedPass;
	/* menus to which we can navigate */
	protected ArrayList<Menu> possibleMenus = new ArrayList<>();
	
	public Menu(BettingSystem bs, String storredPass) {
		this.bettingSystem = bs;
		this.storedPass = storredPass;
	}
	/* print method */
	protected void showMenu(){
		
	}
	/* actions method -> chose the action to be done */
	protected int takeAction(int selected){
		return 0;
	}
	/* parent for going back through the menu */
	protected Menu getParent() {
		return this.parentMenu;
	}
	
	protected void setPassword(String newPassword) {
		this.storedPass = newPassword;
	}
}
