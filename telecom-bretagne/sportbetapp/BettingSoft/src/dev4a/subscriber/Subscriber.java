package dev4a.subscriber;
import java.util.LinkedHashMap;
import java.util.Map;

import dev4a.bets.Bet;
/**
 * 
 * 
 * @author Group 4AB Ahmed and Andrei
 * @version 0.1 (BETA)
 * 
 * POJO !
 * This class serves the purpose of modeling
 * subscribers in the BETTINGSOFT application
 * for the Fil Rouge project (Spring 2016)
 * 
 * Contains all the manipulations tied to Subscriber
 * Debit, Credit, Change pass, Number of tokens details
 * 
 */
public class Subscriber {

	/* The last name of the Subscriber 
	 * UTF-8 String 
	 */
	private String lastName;

	/* The first name of the Subscriber 
	 * UTF-8 String 
	 */
	private String firstName;

	/* The username of the Subscriber 
	 * UTF-8 String used to identify him UNIQUELY
	 */
	private String userName;

	/* The password of the Subscriber 
	 * UTF-8 String TODO HASH this!!
	 */
	private String password = "";

	/* The date of birth of the Subscriber 
	 * UTF-8 String ! Serves for checking age restrictions
	 */
	private String bornDate;

	/* The amount in the account of the Subscriber 
	 * a long integer ( he can be really rich if he bets right )
	 */
	private long numberOfTokens;
	/* 
	 * list of bets mapped by their ID
	 */
	private Map<Integer, Bet> bets = new LinkedHashMap<>();
	
	
	/* Constructors of this class */
	public Subscriber(){
		/*
		 * Empty constructor for convinience and TESTS
		 */
	}

	public Subscriber( String lastName, String firstName, String userName, String bornDate) {
		/* the constructor with all params */
		this.lastName = lastName;
		this.firstName = firstName;
		this.userName = userName;
		this.bornDate = bornDate;
		this.numberOfTokens = 0l;
		this.password = "";
	}
	/*
	 * a simple version of the constructor for TEST purposes
	 */
	public  Subscriber( String lastName, String firstName, String userName ) {
		this(lastName, firstName, userName, null);
	}
	/*
	 * Complete subscriber constructor
	 */
	public  Subscriber( String lastName, String firstName, String userName, String password, String bornDate, long credit) {
		this(lastName, firstName, userName, bornDate);
		this.setNumberOfTokens(credit);
		this.changePassword(this.password, password);
	}
	/* account balancing functions */
	public long credit(long amount) {
		if (amount > 0) 
			this.numberOfTokens += amount;
		else 
			System.out.println("Wrong amount. Account unchanged.");
		return this.numberOfTokens;
	}
	/* debiting */
	public long debit(long amount) {
		/* check if we are respecting constraints */
		if (amount > 0 && amount <= this.numberOfTokens ) 
			this.numberOfTokens -= amount;
		/* just a check to see if the correct number was drawn */
		return this.numberOfTokens;
	}
	
	/* cancel a bet */
	public long cancelBet(Bet betToCancel) {
		/* find the bet we want to cancel */
		this.bets.remove(betToCancel.getIdentifier());
		/* show the amount we get back */
		return betToCancel.getNumberOfTokens();
	}
	
	/* password management */
	public boolean changePassword(String oldPassword, String newPassword) {
		/* check validity of old pass */
		if ( oldPassword != getPassword() )
			return false;
		/* now set the new one */
		setPassword(newPassword);
		/* modify pass and return a confirmation */
		return true;
	}
	
	/* check password */
	public boolean checkPassword(String pass) {
		return this.password.equals(pass);
	}
	
	/* getters and setters REQUIRED for the POJO  (hibernate as well) */
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getBornDate() {
		return bornDate;
	}
	
	public void setBornDate(String bornDate) {
		this.bornDate = bornDate;
	}
	
	public long getNumberOfTokens() {
		return numberOfTokens;
	}
	
	public void setNumberOfTokens(long numberOfTokens) {
		if( numberOfTokens > 0)
			this.numberOfTokens = numberOfTokens;
	}
	
	private String getPassword() {
		return password;
	}
	
	private void setPassword(String password) {
		this.password = password;
	}
	/* return the collection of bets */
	public Map<Integer, Bet> getBets() {
		return bets;
	}
	
	public void setBets(Map<Integer, Bet> bets) {
		this.bets = bets;
	}
	/* place a bet */
	public void placeBet(Bet bet) {
		/* add the bet to the list */
		this.bets.put(bet.getIdentifier(), bet);
		
	}
	
	/**
	 * The unique identifier for the subscriber is the 
	 * username
	 */
	@Override
	public String toString() {
		/* simply return the username, since it is unique */
		return this.userName;
	}
	/**
	 * In conformity with the specification, our Subscribers
	 * are equal if their usernames are the same.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override 
	public boolean equals(Object obj) {
		/* check if it's instance of the subscriber class */
		if (!(obj instanceof Subscriber))
			return false;
		/* they are equal only if their usernames are the same */
		if ( ((Subscriber) obj).getUserName() == this.getUserName() )
			return true;
		return false;
	}
}
