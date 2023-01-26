package dev4a.system;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev4a.bets.Bet;
import dev4a.bets.BetException;
import dev4a.competition.Competition;
import dev4a.competition.CompetitionException;
import dev4a.competition.ExistingCompetitionException;
import dev4a.competitor.Competitor;
import dev4a.competitor.ExistingCompetitorException;
import dev4a.competitor.IndividualCompetitor;
import dev4a.competitor.Team;
import dev4a.db.BetsManager;
import dev4a.db.CompetitionsManager;
import dev4a.db.CompetitorsManager;
import dev4a.db.ParticipantsManager;
import dev4a.db.RootManager;
import dev4a.db.SubscribersManager;
import dev4a.exceptions.AuthenticationException;
import dev4a.exceptions.BadParametersException;
import dev4a.graphicalview.CompetitionsManagerMenu;
import dev4a.subscriber.ExistingSubscriberException;
import dev4a.subscriber.Subscriber;
import dev4a.subscriber.SubscriberException;
import dev4a.utils.Utils;
/**
 * 
 * @author Group 4A
 * @version 0.1 (BETA)
 * 
 * POJO !
 * This class serves the purpose of modeling
 * the system in the BETTINGSOFT application
 * for the Fil Rouge project (Spring 2016)
 * 
 */
public class BettingSystem implements Betting {
	/* attributes */
	/* the one and only managerPassword */
	private String mgrPassword = null;
	/* the utilities */
	Utils utility = new Utils();
	/* the list of all competitors in the System */
	private Map<Integer, Competitor> allCompetitors = new LinkedHashMap<>();
	/* all the competitions in the system */
	private Map<String,Competition> allCompetitions = new LinkedHashMap<>();
	/* all the subscribers in the System */
	private Map<String,Subscriber> allSubscribers = new LinkedHashMap<>();

	public String getPassword(){
		return this.mgrPassword;
	}
	/* constructor */
	public BettingSystem(String mgrPassword) {
		/* setting the pass */
		this.mgrPassword = mgrPassword; 
		try {
			//this.allCompetitions = CompetitionsManager.findAll();
			this.allCompetitors = CompetitorsManager.findAll();
			updateAllCompetitions();
			this.allSubscribers = SubscribersManager.findAll();
		} catch( SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
			System.exit(-1);
		}
	}

	public void changeManagerPassword(String mgrPassword, String newPassword) throws AuthenticationException, SQLException {
		// TODO add some logic for the setting of pswd -> maybe check OLD pass -> New PASS
		/* first authenticate */
		authenticateMngr(mgrPassword);
		/* now change pass */
		this.mgrPassword = newPassword;		
		/* now persist */
		RootManager.update(newPassword);
	}
	private String getManagerPassword() {
		String  tempPass = "";
		try {
			tempPass = RootManager.getPassword();
		} catch (SQLException e) {
			System.out.println("Error connecting to DB");
		}
		return tempPass;
	}
	@Override
	public void authenticateMngr(String managerPwd) throws AuthenticationException {
		/* get the password from the DB and then compare it 
		 * TODO
		 */
		this.mgrPassword = this.getManagerPassword();
		if ( !this.mgrPassword.equals(managerPwd) ) {
			throw new AuthenticationException();
		}
	}

	public Subscriber authenticateSub(String username, String subPwd) throws AuthenticationException , SubscriberException {
		/* get him and verify pass */
		Subscriber tempSub = getSubscriberByUserName(username);
		
		if(tempSub == null)
			throw new SubscriberException();
		
		boolean correct = tempSub.checkPassword(subPwd);

		if( correct == false ) 
			throw new AuthenticationException();

		return tempSub;
	}

	@Override
	public String subscribe(String lastName, String firstName, String username, String borndate, String managerPwd)
			throws AuthenticationException, ExistingSubscriberException, SubscriberException, BadParametersException {
		// TODO Auto-generated method stub
		/* first authenticate the manager */
		authenticateMngr(managerPwd);
		/* valid name ? */
		if ( ! (utility.checkValidFirstLastName(lastName) && utility.checkValidFirstLastName(firstName) ) )
			throw new BadParametersException();
		/* then check for the username (which is unique) */
		if ( getSubscriberByUserName(username) != null )
			throw new ExistingSubscriberException();
		/* give him a new password */
		String password = utility.randomString(8);
		/* if he does not exist, we can create him */
		Subscriber temporarySubscriber = new Subscriber(lastName, firstName, username, borndate); 
		/* we created him, now add him to the collection */
		addSubscriberToList(temporarySubscriber, password);		
		/* return the new password */
		System.out.println("Added " + temporarySubscriber + "\nNew password : " + password);
		return password;
	}

	private void addSubscriberToList(Subscriber temporarySubscriber, String password) {
		/* 
		 *  hide the implementation
		 *  add the subscirber to any collection 
		 */
		try {
			SubscribersManager.persist(temporarySubscriber, password);
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		this.allSubscribers.put(temporarySubscriber.getUserName(), temporarySubscriber);
	}

	public Subscriber getSubscriberByUserName(String username) {
		// TODO make a method to get the sub from the List
		Subscriber sub = null;
		try {
			sub = SubscribersManager.findSubscriberByUserName(username);
		} catch(SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		return sub;
	}

	@Override
	public long unsubscribe(String username, String managerPwd)
			throws AuthenticationException, ExistingSubscriberException {
		/* first authenticate the manager */
		authenticateMngr(managerPwd);
		/* proceed to remove him */
		Subscriber toBeRemoved = getSubscriberByUserName(username);
		/* check if the username exists */
		if ( toBeRemoved == null )
			throw new ExistingSubscriberException("The user does not exist!");
		/* remove all his bets */
		try {

			for ( Bet bet : BetsManager.findBySubscriber(toBeRemoved).values()) {
				System.out.println("deleting his bets.." + bet);
				BetsManager.delete(bet);
			}

		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		/* remove him from the collection */
		removeSubscriberFromList(toBeRemoved);
		/* return the number of tokens he had left */
		return toBeRemoved.getNumberOfTokens();
	}

	private void removeSubscriberFromList(Subscriber toRemove) {
		/* 
		 *  hide the implementation
		 *  remove the subscirber from any collection 
		 */
		try {
			SubscribersManager.delete(toRemove);
		} catch (SQLException sqlex ) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}

		this.allSubscribers.remove(toRemove.getUserName());
		java.lang.System.out.println("Removing " + toRemove.getUserName());
	}

	@Override
	public List<List<String>> listSubscribers(String managerPwd) throws AuthenticationException {
		/* first authenticate the manager */
		authenticateMngr(managerPwd);
		/* iterate the container and get all names and attributes */
		List<List<String>> printableSubs = new ArrayList<>();
		/* get the updated list */
		try {
			this.allSubscribers = SubscribersManager.findAll();
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		for( Subscriber sub : this.allSubscribers.values() ) {
			/* store the details for each subscriber */
			List<String> subDetails = new ArrayList<>();
			/* adding the details about them */
			subDetails.add(sub.getUserName());
			subDetails.add(sub.getFirstName());
			subDetails.add(sub.getLastName());
			subDetails.add(sub.getBornDate());
			subDetails.add( String.valueOf( sub.getNumberOfTokens() ) );
			/* insert it in the big list */
			printableSubs.add(subDetails);
		}
		return printableSubs;
	}

	public void printSubscribers(String mgrPassword) throws AuthenticationException {
		this.utility.printList(this.listSubscribers(mgrPassword));
	}

	@Override
	public void addCompetition(String competition, Calendar closingDate, Collection<Competitor> competitors,
			String managerPwd) throws AuthenticationException, ExistingCompetitionException, CompetitionException,
			BadParametersException {
		/* first authenticate the manager */
		authenticateMngr(managerPwd);
		/* valid name? */
		if ( ! (utility.checkValidCompetition(competition) ) || competitors == null ) 
			// we do not validate competitor because we already did it when we created it.
			throw new BadParametersException();
		/* now check if it exists */
		Competition tempCompetition = getCompetitionByName(competition);
		/* the test */
		if( tempCompetition != null )
			throw new ExistingCompetitionException();
		/* check if the date is correct, if the number of competitors is >2 and if there is no repeated competitors */
		Set<Competitor> set = new HashSet<Competitor>(competitors);
		if ( closingDate.before(Calendar.getInstance()) || competitors.size() < 2
				|| set.size() != competitors.size() ) {
			throw new CompetitionException();
		}
		/* Checks if all competitors have the same type */
		int type = 0;
		boolean sameType = true;
		for (Competitor c : competitors){
			if (type==0)
				type = c.getType();
			if (c.getType() != type){
				sameType = false;
				break;
			}
		}
		if (!sameType){
			throw new CompetitionException("Competitors have different types!");
		}
		/* create the Map */
		Map<Integer, Competitor> tempCompetitors = new LinkedHashMap<>();
		for (Competitor c : competitors){
			tempCompetitors.put(c.getId(), c);
		}
		/* create it ! */
		tempCompetition = new Competition(
				competition,
				Calendar.getInstance(),
				closingDate,
				"other",
				tempCompetitors,
				"pw"
				);
		/* freshly created add it to our collection */
		addCompetitionToList(tempCompetition);

		for (Competitor c : competitors){
			try {
				ParticipantsManager.persist(c, tempCompetition);
			} catch (SQLException sqlex) {
				System.out.println("Error connecting to Databse.\nPlease check your connection.");
			}
		}
		//TODO check BadParametresException
		System.out.println("Added " + tempCompetition);
	}
	/**
	 * 
	 * @param tempCompetition
	 */
	private void addCompetitionToList(Competition tempCompetition) {
		// hide the implementation
		try {
			CompetitionsManager.persist(tempCompetition);
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		this.allCompetitions.put(tempCompetition.getName(), tempCompetition);
	}


	/**
	 * 
	 * @param competition
	 */
	private void removeCompetitionFromList(Competition competition) {
		// hide the implementation
		try {
			CompetitionsManager.delete(competition);
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}

		this.allCompetitions.remove(competition.getName());
	}
	/**
	 * 
	 * @param competition
	 * @return
	 */
	private Competition getCompetitionByName(String competition) {
		// hide the implementation
		Competition temp = null;
		try {
			/* get the competition */
			temp = CompetitionsManager.findByName(competition);
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		if(temp == null) 
			return null;
		try {
			/* set its competititors */
			temp.setAllCompetitors(ParticipantsManager.findAllByCompetition(competition));
			/* set its bets */
			Map<Integer,Bet> bets = new LinkedHashMap<Integer,Bet>(BetsManager.findByCompetition(temp));
			temp.setBets(new ArrayList<Bet>(bets.values()));	
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}

		System.out.println("algo " + temp.getWinners().size());
		return temp;
	}

	@Override
	public void cancelCompetition(String competition, String managerPwd)
			throws AuthenticationException, ExistingCompetitionException, CompetitionException {
		/* first authenticate the manager */
		authenticateMngr(managerPwd);
		/* now checks if it exists */
		Competition toBeCanceled = getCompetitionByName(competition);
		/* checks if it exists */
		if( toBeCanceled == null )
			/* does not exist */
			throw new ExistingCompetitionException();
		/* checks closing date */
		if (toBeCanceled.getClosingDate().before(Calendar.getInstance()))
			throw new CompetitionException();
		/* Checks it's not soldout */
		if (toBeCanceled.getStatus() == Competition.SOLDOUT)
			throw new CompetitionException();
		/* take care of all the ongoing bets ! */
		List<Bet> listBets = getBetsByCompetition(toBeCanceled);
		for (int i = 0; i < listBets.size(); i++){
			Subscriber subscriber = getSubscriberByUserName(listBets.get(i).getUserName());
			/* returns tokens */
			subscriber.credit(listBets.get(i).getNumberOfTokens());
			try {
				/* update his account in the DB */
				SubscribersManager.update(subscriber);
			} catch (SQLException sqlex) {
				System.out.println("Error connecting to Databse.\nPlease check your connection.");
			}
			/* deletes the bets from subscriber */
			subscriber.getBets().remove(listBets.get(i));
			try {
				BetsManager.delete(listBets.get(i));
			} catch (SQLException sqlex ) {
				System.out.println("Error connecting to Databse.\nPlease check your connection.");
			}
		}
		/* deletes the bets from Competition */
		toBeCanceled.getBets().removeAll(listBets);
		/* cancels it */
		toBeCanceled.setStatus(Competition.CANCELED);
		try {
			/* update competition in the DB */
			CompetitionsManager.update(toBeCanceled);
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		this.allCompetitions.get(toBeCanceled.getName()).setStatus(Competition.CANCELED);
	}

	public List<Bet> getBetsByCompetition(Competition competition){
		List<Bet> bets = new ArrayList<Bet>();
		try {
			for ( Bet bet : BetsManager.findByCompetition(competition).values()) {
				bets.add(bet);
			}
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		return bets;
	}

	@Override
	public void deleteCompetition(String competition, String managerPwd)
			throws AuthenticationException, ExistingCompetitionException, CompetitionException {
		/* first authenticate the manager */
		authenticateMngr(managerPwd);
		/* now check if it exists */
		Competition toBeRemoved = getCompetitionByName(competition);
		/* check if it exists */
		if( toBeRemoved == null )
			/* does not exist */
			throw new ExistingCompetitionException();
		/* check if the competition is in a proper state */
		if(toBeRemoved.getStatus()!=Competition.CANCELED){
			//if (toBeRemoved.getClosingDate().after(Calendar.getInstance()))
			//	throw new CompetitionException("Competition is still open!");
			if (toBeRemoved.getStatus()!=Competition.SOLDOUT)
				throw new CompetitionException("First set winner(s)!");
		}
		/* clear relations between the competition and each of its competitors */
		List<Competitor> myCompetitors = new ArrayList<Competitor> (toBeRemoved.getAllCompetitors().values());
		for(Competitor aCompetitor : myCompetitors){
			try {
				ParticipantsManager.delete(aCompetitor,toBeRemoved);
			} catch (SQLException sqlex) {
				System.out.println("Error connecting to Databse.\nPlease check your connection.");
			}
		}
		/* now we can safely delete */
		removeCompetitionFromList(toBeRemoved);
		System.out.println("Removed " + toBeRemoved);
	}

	private void updateAllCompetitions(){
		try {
			this.allCompetitions = CompetitionsManager.findAll();
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		for (Competition comp : allCompetitions.values()){
			try {
				/* set its competitors */
				Map<Integer,Competitor> competitors = new LinkedHashMap<Integer,Competitor>(ParticipantsManager.findAllByCompetition(comp.getName()));
				this.allCompetitors.putAll(competitors);
				comp.setAllCompetitors(competitors);			
			} catch (SQLException sqlex) {
				System.out.println("Error connecting to Databse.\nPlease check your connection.");
			}
			try {
				/* set its bets */
				Map<Integer,Bet> bets = new LinkedHashMap<Integer,Bet>(BetsManager.findByCompetition(comp));
				comp.setBets(new ArrayList<Bet>(bets.values()));			
			} catch (SQLException sqlex) {
				System.out.println("Error connecting to Databse.\nPlease check your connection.");
			}
		}
	} 

	@Override
	public void addCompetitor(String competition, Competitor competitor, String managerPwd)
			throws AuthenticationException, ExistingCompetitionException, CompetitionException,
			ExistingCompetitorException, BadParametersException {
		/* first authenticate the manager */
		authenticateMngr(managerPwd);
		/* competition where the competitor is to be added */
		Competition myCompetition = getCompetitionByName(competition);
		/* check if it exists */
		if( myCompetition == null )
			/* does not exist */
			throw new ExistingCompetitionException();
		/* check if the competition is in a proper state */
		if( myCompetition.getClosingDate().before(Calendar.getInstance()) ){
			//if( !(myCompetition.getStatus() == Competition.STARTED) ) {
			throw new CompetitionException();
		}
		if( !(myCompetition.getStatus() == Competition.STARTED) )
			throw new CompetitionException();
		/* check if the competitor is already in the competition */
		if(myCompetition.hasCompetitor(competitor)){
			throw new ExistingCompetitorException();
		}
		/* Checks if the competitor has the same type */
		List<Competitor> listOfCompetitors = new ArrayList<Competitor>(myCompetition.getAllCompetitors().values());
		if (competitor.getType()!=listOfCompetitors.get(0).getType()){
			throw new CompetitionException("Competitor type mismatch!");
		}
		/* now we can add the competitor */  
		myCompetition.addCompetitor(competitor); 
		try {
			ParticipantsManager.persist(competitor, myCompetition);
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		// TODO BadParametersExcaption
	}

	@Override
	public Competitor createCompetitor(String lastName, String firstName, String borndate, String managerPwd)
			throws AuthenticationException, BadParametersException {
		/* first authenticate the manager */
		authenticateMngr(managerPwd);
		/* check validity of the name */
		if( !(utility.checkValidFirstLastName(lastName) && utility.checkValidFirstLastName(firstName) && utility.checkValidDate(borndate) ) ) {
			throw new BadParametersException();
		}
		/* Checks if the competitor already exists */
		List<Competitor> listAllCompetitors = new ArrayList<Competitor>(allCompetitors.values());
		for(Competitor c: listAllCompetitors){
			if(c instanceof IndividualCompetitor){
				IndividualCompetitor indComp = (IndividualCompetitor) c;
				if(indComp.getFirstName().equals(firstName) 
						&& indComp.getLastName().equals(lastName)
						&& indComp.getBornDate().equals(borndate)){
					// It exists! Returned!
					System.out.println("The competitor already exists!");
					return c;
				}
			}
		}
		/* It does not exist so the competitor is created  */
		Competitor tempCompetitor = new IndividualCompetitor(firstName,lastName,borndate);
		/* add him to the list */
		addCompetitorToList(tempCompetitor);
		/* return the object we created in memory */
		System.out.println("Added " + tempCompetitor);
		return tempCompetitor;
	}
	/**
	 * 
	 */
	@Override
	public Competitor createCompetitor(String name, String managerPwd)
			throws AuthenticationException, BadParametersException {
		/* first authenticate the manager */
		authenticateMngr(managerPwd);
		/* check validity of the name */
		if( !(utility.checkValidTeamName(name)) ) {
			throw new BadParametersException();
		}
		/* Checks if the competitor already exists */
		List<Competitor> listAllCompetitors = new ArrayList<Competitor>(allCompetitors.values());
		for(Competitor c: listAllCompetitors){
			if (c instanceof Team){
				Team team = (Team) c;
				if (team.getName().equals(name)){
					// It exists! Returned!
					System.out.println("The team already exists!");
					return c;
				}
			}
		}
		/* It does not exist so the competitor is created  */
		Competitor tempCompetitor = new Team(name);
		/* add him to the list */
		addCompetitorToList(tempCompetitor);
		System.out.println("Added " + tempCompetitor);
		return tempCompetitor;

	}	
	/**
	 * 
	 * @param competitor
	 */
	private void addCompetitorToList(Competitor competitor) {
		/* hide implementation */

		try {
			CompetitorsManager.persist(competitor);
		} catch( SQLException sqlex ) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		this.allCompetitors.put(competitor.getId(), competitor);
	}

	@Override
	public void deleteCompetitor(String competition, Competitor competitor, String managerPwd)
			throws AuthenticationException, ExistingCompetitionException, CompetitionException,
			ExistingCompetitorException {
		/* first authenticate the manager */
		authenticateMngr(managerPwd);
		/* competition to remove competitor from */
		Competition myCompetition = getCompetitionByName(competition);
		/* checks if it exists */
		if( myCompetition == null )
			/* does not exist */
			throw new ExistingCompetitionException();
		/* checks if the competition is in a proper state */
		if( myCompetition.getClosingDate().before(Calendar.getInstance()) || myCompetition.getAllCompetitors().size() == 2) {
			// if((myCompetition.getStatus() != Competition.STARTED))
			throw new CompetitionException();
		}
		if((myCompetition.getStatus() != Competition.STARTED))
			throw new CompetitionException();
		/* checks if the competitor is in the competition */
		if(!myCompetition.hasCompetitor(competitor)){
			throw new ExistingCompetitorException();
		}
		/* Returns tokens */
		List<Bet> listBets = myCompetition.getBets();
		for (int i = 0; i < listBets.size(); i++){
			/* Looks if the bet was done to the competitor to remove */
			if(listBets.get(i).getListCompetitor().contains(competitor.getId())){
				Subscriber subscriber = getSubscriberByUserName(listBets.get(i).getUserName());
				/* returns tokens */
				subscriber.credit(listBets.get(i).getNumberOfTokens());
				try {
					/* update his account in the DB */
					SubscribersManager.update(subscriber);
				} catch (SQLException sqlex) {
					System.out.println("Error connecting to Databse.\nPlease check your connection.");
				}
				System.out.println("To eliminate the competitor, " + subscriber + " is credited with " + listBets.get(i).getNumberOfTokens());
				/* deletes the bet from subscriber */
				subscriber.getBets().remove(listBets.get(i));
				/* deletes the bet from the competition */
				myCompetition.getBets().remove(listBets.get(i));
				/* deletes the bet in DB */
				try {
					BetsManager.delete(listBets.get(i));
				} catch (SQLException sqlex ) {
					System.out.println("Error connecting to Databse.\nPlease check your connection.");
				}
			}
		}
		/* clear the relation between the competition and the competitor */
		try {
			ParticipantsManager.delete(competitor,myCompetition);
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		/* now we can delete the competitor */  
		//myCompetition.removeCompetitor(competitor); 
		this.allCompetitions.get(myCompetition.getName()).removeCompetitor(competitor);
		System.out.println("Removed " + competitor);
	}

	@Override
	public void creditSubscriber(String username, long numberTokens, String managerPwd)
			throws AuthenticationException, ExistingSubscriberException, BadParametersException {
		/* first authenticate the manager */
		authenticateMngr(managerPwd);
		/* check existence */
		Subscriber tempSubscriber = getSubscriberByUserName(username);
		/* if he does not exist */
		if ( tempSubscriber == null ){
			/* does not exist */
			throw new ExistingSubscriberException("Subscriber does not exist!");

		}
		if ( numberTokens < 0 ) {
			/* can't credit with negative value */
			throw new BadParametersException();
		}
		/* credit the money */
		tempSubscriber.credit(numberTokens);
		try {
			/* update his account in the DB */
			SubscribersManager.update(tempSubscriber);
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		System.out.println("Credited " + tempSubscriber + " with " + numberTokens);
	}

	@Override
	public void debitSubscriber(String username, long numberTokens, String managerPwd)
			throws AuthenticationException, ExistingSubscriberException, SubscriberException, BadParametersException {
		/* first authenticate the manager */
		authenticateMngr(managerPwd);
		/* check existence */
		Subscriber tempSubscriber = getSubscriberByUserName(username);
		/* if he does not exist */
		if ( tempSubscriber == null ){
			/* does not exist */
			throw new ExistingSubscriberException("Subscriber does not exist!");

		}
		if ( numberTokens < 0 || numberTokens > tempSubscriber.getNumberOfTokens()) {
			/* can't credit with negative value */
			throw new BadParametersException();
		}
		/* credit the money */
		tempSubscriber.debit(numberTokens);
		try {
			/* update his account in the DB */
			SubscribersManager.update(tempSubscriber);
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		System.out.println("Debited " + tempSubscriber + " for " + numberTokens);
	}

	@Override
	public void settleWinner(String competition, Competitor winner, String managerPwd)
			throws AuthenticationException, ExistingCompetitionException, CompetitionException {
		/* first authenticate the manager */
		authenticateMngr(managerPwd);
		/* competition where the competitor should be winner */
		Competition myCompetition = getCompetitionByName(competition);
		/* check if it exists */
		if( myCompetition == null )
			/* does not exist */
			throw new ExistingCompetitionException();
		/* checks if the competition is in a proper state */
		//if(myCompetition.getClosingDate().after(Calendar.getInstance())){
		if(myCompetition.getStatus() == (Competition.SOLDOUT)){
			throw new CompetitionException();
		}
		/* Check if the competition has more than 2 competitors */
		if(myCompetition.getAllCompetitors().size() > 2){
			throw new CompetitionException("You should try settling podium!");
		}
		/* check if the competitor is in the competition */
		if(!myCompetition.hasCompetitor(winner) || winner == null){
			throw new CompetitionException();
		}
		winner = myCompetition.getAllCompetitors().get(winner.getId());
		/* now we can set the winner */  
		Map<Integer, Competitor> listOfWinners = new LinkedHashMap<>();
		listOfWinners.put(new Integer(winner.getId()), winner);
		myCompetition.setWinners(listOfWinners);
		this.allCompetitions.get(myCompetition.getName()).setWinners(listOfWinners);

		// TODO check type of competition? in this case the type of bet should be w
		pay(myCompetition);
		myCompetition.setStatus(Competition.SOLDOUT);
		this.allCompetitions.put(myCompetition.getName(), myCompetition);
		try {
			/* update competition in the DB */
			CompetitionsManager.update(myCompetition);
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		/* Deletes the competition */
		//deleteCompetition(competition, managerPwd);
	}

	@Override
	public void settlePodium(String competition, Competitor winner, Competitor second, Competitor third,
			String managerPwd) throws AuthenticationException, ExistingCompetitionException, CompetitionException {
		/* first authenticate the manager */
		authenticateMngr(managerPwd);
		/* competition where the competitor should be winner */
		Competition myCompetition = getCompetitionByName(competition);
		/* check if it exists */
		if( myCompetition == null )
			/* does not exist */
			throw new ExistingCompetitionException(); 
		/* check if the competition is in a proper state */
		if(myCompetition.getStatus() == (Competition.SOLDOUT)){
			//if(myCompetition.getClosingDate().after(Calendar.getInstance())){
			throw new CompetitionException();
		}
		/* check if the competitor is in the competition */
		if(!myCompetition.hasCompetitor(winner) || !myCompetition.hasCompetitor(second) || !myCompetition.hasCompetitor(third)
				|| winner == null || second == null || third == null){
			throw new CompetitionException();
		}
		if (winner.equals(second) || second.equals(third) || winner.equals(third))
			throw new CompetitionException();
		/*		winner = myCompetition.getAllCompetitors().get(winner.getId());
		second = myCompetition.getAllCompetitors().get(second.getId());
		third = myCompetition.getAllCompetitors().get(third.getId());*/
		/* now we can set the winner */  
		Map<Integer, Competitor> listOfWinners = new LinkedHashMap<>();

		listOfWinners.put(new Integer(winner.getId()), winner);
		listOfWinners.put(new Integer(second.getId()), second);
		listOfWinners.put(new Integer(third.getId()), third);

		for (Competitor comp : listOfWinners.values()) {
			System.out.println(" Settle " + comp);
		}

		myCompetition.setWinners(listOfWinners); 
		this.allCompetitions.get(myCompetition.getName()).setWinners(listOfWinners);
		// TODO check type of competition?
		pay(myCompetition);
		myCompetition.setStatus(Competition.SOLDOUT);
		this.allCompetitions.put(myCompetition.getName(), myCompetition);
		try {
			/* update competition in the DB */
			CompetitionsManager.update(myCompetition);
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		/* Deletes the competition */
		//deleteCompetition(competition, managerPwd);
	}

	@Override
	public void betOnWinner(long numberTokens, String competition, Competitor winner, String username, String pwdSubs)
			throws AuthenticationException, CompetitionException, ExistingCompetitionException, SubscriberException,
			BadParametersException, BetException {
		/* first authenticate the subscriber */
		Subscriber tempSubscriber = authenticateSub(username, pwdSubs);
		/* get the competition */
		Competition tempComp = getCompetitionByName(competition);
		/* if the competition does not exist */
		if( tempComp == null ) {
			throw new ExistingCompetitionException();
		}
		/* if we have given wrong values */
		if( (numberTokens <= 0))// || (tempComp.hasCompetitor(winner) == false) )
			throw new BadParametersException();
		if(numberTokens > tempSubscriber.getNumberOfTokens())
			throw new BetException();
		/* the time of the bet */
		java.util.Date currentTime = new java.util.Date();

		Format formatter = new SimpleDateFormat("yyyy-MM-dd");
		String betDate = formatter.format(currentTime);

		/* place the bet */

		Bet tempBet = new Bet(numberTokens, competition, winner, username, betDate);
		/* add it to the player */
		tempSubscriber.placeBet( tempBet );

		/* debit the money */
		tempSubscriber.debit(numberTokens);
		try {
			/* update his account in the DB */
			SubscribersManager.update(tempSubscriber);
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		System.out.println("Debited " + tempSubscriber + " for " + numberTokens);

		/* add to the system bet list */
		this.addBetToList(tempBet);
	}

	private void addBetToList(Bet bet) {
		try {
			BetsManager.persist(bet);
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}

	}

	@Override
	public void betOnPodium(long numberTokens, String competition, Competitor winner, Competitor second,
			Competitor third, String username, String pwdSubs) throws AuthenticationException, CompetitionException,
			ExistingCompetitionException, SubscriberException, BadParametersException, BetException {
		/* first authenticate the subscriber */
		Subscriber tempSubscriber = authenticateSub(username, pwdSubs);
		/* get the competition */
		Competition tempComp = getCompetitionByName(competition);
		/* if the competition does not exist */
		if( tempComp == null ) {
			throw new ExistingCompetitionException();
		}
		/* if we have given wrong values */
		if( (numberTokens <= 0))// || (tempComp.hasCompetitor(winner) == false) || (tempComp.hasCompetitor(second) == false) || (tempComp.hasCompetitor(third) == false))
			throw new BadParametersException();
		/* if we have given wrong values */
		if(numberTokens > tempSubscriber.getNumberOfTokens())
			throw new BetException();
		/* the time of the bet */
		java.util.Date currentTime = new java.util.Date();

		Format formatter = new SimpleDateFormat("yyyy-MM-dd");
		String betDate = formatter.format(currentTime);
		/* place the bet */
		Bet tempBet = new Bet(numberTokens, competition, winner, second, third, username, betDate);
		/* add it to the player */
		tempSubscriber.placeBet( tempBet );

		/* debit the money */
		tempSubscriber.debit(numberTokens);
		try {
			/* update his account in the DB */
			SubscribersManager.update(tempSubscriber);
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		System.out.println("Debited " + tempSubscriber + " for " + numberTokens);

		/* add it to our lists  */
		this.addBetToList(tempBet);

	}

	@Override
	public void changeSubsPwd(String username, String newPwd, String currentPwd)
			throws AuthenticationException, BadParametersException {
		/* first authenticate the subscriber */
		Subscriber tempSubscriber = null;
		try {
			tempSubscriber = authenticateSub(username, currentPwd);
		} catch(SubscriberException subex){
			subex.printStackTrace();
		}

		tempSubscriber.changePassword(currentPwd, newPwd);

		try {
			SubscribersManager.updatePassword(tempSubscriber, currentPwd, newPwd);
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
	}

	@Override
	public ArrayList<String> infosSubscriber(String username, String pwdSubs) throws AuthenticationException {
		/* first authenticate the subscriber */
		Subscriber tempSubscriber = null;
		try {
			tempSubscriber = authenticateSub(username, pwdSubs);
		} catch(SubscriberException subex){
			subex.printStackTrace();
		}
		/* then get the info */
		ArrayList<String> tempList = new ArrayList<>();

		tempList.add(tempSubscriber.getUserName());
		tempList.add(tempSubscriber.getLastName());
		tempList.add(tempSubscriber.getFirstName());
		tempList.add(tempSubscriber.getBornDate());
		tempList.add(String.valueOf(tempSubscriber.getNumberOfTokens()));

		/* make it printable */
		List<List<String>> printable = new ArrayList<>();
		printable.add(tempList);
		/* print it as a side effect */
		utility.printList(printable);

		return tempList;
	}

	@Override
	public void deleteBetsCompetition(String competition, String username, String pwdSubs)
			throws AuthenticationException, CompetitionException, ExistingCompetitionException {
		/* first authenticate the subscriber */
		Subscriber tempSubscriber = null;
		try {
			tempSubscriber = authenticateSub(username, pwdSubs);
		} catch(SubscriberException subex){
			subex.printStackTrace();
		}
		Map<Integer, Bet> bets = new LinkedHashMap<Integer,Bet>();
		try {
			bets = BetsManager.findBySubscriber(tempSubscriber);
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		tempSubscriber.setBets(bets);
		/* get the competition with that name */
		Competition tempCompetition = getCompetitionByName(competition);
		/* check if competition exists */
		if ( tempCompetition == null ) {
			throw new ExistingCompetitionException();
		}
		/* Checks if the competition is closed */
		if (tempCompetition.getClosingDate().before(Calendar.getInstance()))
			throw new CompetitionException();
		/* get the bets */
		for ( Bet b : tempSubscriber.getBets().values() ) {
			/* if it's the right competition */
			if( b.getCompetition().equals(competition) ){
				/* Returning money to subscriber... */
				tempSubscriber.credit(b.getNumberOfTokens());
				this.allSubscribers.get(tempSubscriber.getUserName()).credit(b.getNumberOfTokens());
				try {
					/* update his account in the DB */
					SubscribersManager.update(tempSubscriber);
				} catch (SQLException sqlex) {
					System.out.println("Error connecting to Databse.\nPlease check your connection.");
				}
				/* Now we can eliminate the bet */
				try {
					BetsManager.delete(b);
				} catch (SQLException sqlex) {
					System.out.println("Error connecting to Databse.\nPlease check your connection.");
				}
				this.allSubscribers.get(tempSubscriber.getUserName()).getBets().remove(b.getIdentifier());
			}
		}
	}

	public void printCompetitions() {
		this.utility.printList(this.listCompetitions());
	}

	private List<String> consultCompetitors(Competition competition){
		List<String> competitors = new ArrayList<String>();
		List<Competitor> listCompetitors = new ArrayList<Competitor> (competition.getAllCompetitors().values());
		for (Competitor c : listCompetitors)
			competitors.add(c.toString());
		return competitors;
	}

	@Override
	public List<List<String>> listCompetitions() {
		/* iterate the container and get all names and attributes */
		List<List<String>> printableCompetitions = new ArrayList<>();
		/* get the updated list */
		this.updateAllCompetitions();
		for( Competition comp : this.allCompetitions.values() ) {
			/* store the details for each subscriber */
			List<String> compDetails = new ArrayList<>();
			/* get the competition b*/
			compDetails.add(comp.getName());
			//compDetails.add(comp.getSport());
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
			String stringDate = formatter.format(comp.getClosingDate().getTime()); 
			compDetails.add(stringDate);
			compDetails.add("For winner bets: " + comp.getTotalNumberOfTokens(Bet.TYPE_WINNER) + " tokens in " + comp.getTotalNumberOfBets(Bet.TYPE_WINNER) + " bets.");
			compDetails.add("For podium bets: " + comp.getTotalNumberOfTokens(Bet.TYPE_PODIUM) + " tokens in " + comp.getTotalNumberOfBets(Bet.TYPE_PODIUM) + " bets.");
			compDetails.addAll(consultCompetitors(comp));
			//compDetails.add(String.valueOf(comp.getStatus()));
			//compDetails.add( String.valueOf( comp.getStartDate().getTime() ) );
			/* insert it in the big list */
			printableCompetitions.add(compDetails);
		}
		return printableCompetitions;
	}

	public void printCompetitors(String competition) {
		try {
			this.utility.printList(this.listCompetitors(competition));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public Collection<Competitor> listCompetitors(String competition)
			throws ExistingCompetitionException, CompetitionException {
		/* get the competition with that name */
		Competition tempCompetition = getCompetitionByName(competition);
		/* check if competition exists */
		if ( tempCompetition == null ) {
			throw new ExistingCompetitionException();
		}
		/* Checks if the competition is closed */
		if (tempCompetition.getClosingDate().before(Calendar.getInstance()))
			throw new CompetitionException();
		Collection<Competitor> competitors = new ArrayList<>(tempCompetition.getAllCompetitors().values());
		return competitors;
	}

	@Override
	public ArrayList<String> consultBetsCompetition(String competition) 
			throws ExistingCompetitionException {
		/* competition where the competitor is to be added */
		Competition myCompetition = getCompetitionByName(competition);
		/* check if it exists */
		if( myCompetition == null )
			/* does not exist */
			throw new ExistingCompetitionException();
		ArrayList<String> listOfStrings = new ArrayList<String>();
		List<Bet> listOfBets = myCompetition.getBets();
		String string = "";
		String names = "";
		for(Bet b:listOfBets){
			//listOfStrings.add(b.toString());
			if (b.getType()==1){ //type winner
				names = "the winner: " + b.getWinner().toString();
			}
			if (b.getType()==2){ //type podium
				names = "the podium: " + b.getWinner().toString() + ", " + b.getSecond().toString() + " and " + b.getThird().toString();
			}
			string = b.getUserName() + " has bet " + b.getNumberOfTokens() + " tokens on " + names + " in " + competition;
			listOfStrings.add(string);
		}
		return listOfStrings;
	}

	@Override
	public ArrayList<Competitor> consultResultsCompetition(String competition) 
			throws ExistingCompetitionException {
		/* */
		Competition tempCompetition = getCompetitionByName(competition);
		if (tempCompetition == null )
			throw new ExistingCompetitionException();
		/* Can't throw CompetitionException because it's not in the interface. */
		ArrayList<Competitor> competitorsReturn = new ArrayList<Competitor>();
		competitorsReturn.addAll(tempCompetition.getWinners().values());
		return competitorsReturn;
	}

	/* distribution of tokens for a competition */
	private void pay(Competition competition){
		List<Bet> listOfBets = competition.getBets();
		/* list of subscribers that bet on winner and won */
		List<Subscriber> winningSubscribersOnWinner = new ArrayList<Subscriber>();
		/* list of tokens that the winning subscribers bet on winner */
		List<Long> tokensBetOnWinner = new ArrayList<>();
		/* list of subscribers that bet on podium and won */
		List<Subscriber> winningSubscribersOnPodium = new ArrayList<Subscriber>();
		/* list of tokens that the winning subscribers bet on podium */
		List<Long> tokensBetOnPodium = new ArrayList<>();
		long winningTokensOnWinner=0;
		long winningTokensOnPodium=0;
		long payMe;
		/* search in every bet done on the competition */
		/* this will work no matter what type of bets the competition accepts*/
		for (Bet b:listOfBets){
			if(b.getType() == Bet.TYPE_WINNER){ // type winner
				List<Competitor> list = new ArrayList<Competitor> (competition.getWinners().values());
				if(list.get(0).equals(b.getWinner())){
					winningSubscribersOnWinner.add(getSubscriberByUserName(b.getUserName()));
					tokensBetOnWinner.add(b.getNumberOfTokens());
					winningTokensOnWinner += b.getNumberOfTokens();
					b.setState(Bet.WON);

				}
				else
					b.setState(Bet.LOST);
			}
			else if(b.getType()==2){ // type podium
				List<Competitor> list = new ArrayList<Competitor> (competition.getWinners().values());
				if(list.get(0).equals(b.getWinner()) && 
						list.get(1).equals(b.getSecond()) && 
						list.get(2).equals(b.getThird())){
					winningSubscribersOnPodium.add(getSubscriberByUserName(b.getUserName()));
					tokensBetOnPodium.add(b.getNumberOfTokens());
					winningTokensOnPodium += b.getNumberOfTokens();
					b.setState(Bet.WON);
				}
				else
					b.setState(Bet.LOST);
			}
			try {
				/* update bet in the DB */
				System.out.println("we are in pay and we have a bet " + b);
				BetsManager.update(b);
			} catch (SQLException sqlex) {
				System.out.println("Error connecting to Databse.\nPlease check your connection.");
			}

		}
		/* pays to the winners that won on winner bet */
		if(winningSubscribersOnWinner.size()!=0){
			for (int i=0; i<winningSubscribersOnWinner.size();i++){
				payMe = competition.getTotalNumberOfTokens(Bet.TYPE_WINNER)*tokensBetOnWinner.get(i)/winningTokensOnWinner;  
				Subscriber toCredit = null;
				try {
					toCredit = SubscribersManager.findSubscriberByUserName(winningSubscribersOnWinner.get(i).getUserName());
				} catch (SQLException sqlex) {
					System.out.println("Error connecting to Databse.\nPlease check your connection.");
				}
				toCredit.credit(payMe);
				try {
					/* update his account in the DB */
					SubscribersManager.update(toCredit);
				} catch (SQLException sqlex) {
					System.out.println("Error connecting to Databse.\nPlease check your connection.");
				}
			}
		}
		/* Since it is a nonprofit organization... money is returned if nobody won */
		else{
			for (Bet b:listOfBets){
				getSubscriberByUserName(b.getUserName()).credit(b.getNumberOfTokens());
				try {
					/* update his account in the DB */
					SubscribersManager.update(getSubscriberByUserName(b.getUserName()));
				} catch (SQLException sqlex) {
					System.out.println("Error connecting to Databse.\nPlease check your connection.");
				}
			}
		}
		/* pays to the winners that won on podium bet */
		if(winningSubscribersOnPodium.size()!=0){
			for (int i=0; i<winningSubscribersOnPodium.size();i++){
				payMe = competition.getTotalNumberOfTokens(Bet.TYPE_PODIUM)*tokensBetOnPodium.get(i)/winningTokensOnPodium;  
				Subscriber toCredit = null;
				try {
					toCredit = SubscribersManager.findSubscriberByUserName(winningSubscribersOnPodium.get(i).getUserName());
				} catch (SQLException sqlex) {
					System.out.println("Error connecting to Databse.\nPlease check your connection.");
				}
				toCredit.credit(payMe);
				try {
					/* update his account in the DB */
					SubscribersManager.update(toCredit);
				} catch (SQLException sqlex) {
					System.out.println("Error connecting to Databse.\nPlease check your connection.");
				}
			}
		}
		/* Since it is a nonprofit organization... money is returned if nobody won */
		else{
			for (Bet b:listOfBets){
				getSubscriberByUserName(b.getUserName()).credit(b.getNumberOfTokens());
				try {
					/* update his account in the DB */
					SubscribersManager.update(getSubscriberByUserName(b.getUserName()));
				} catch (SQLException sqlex) {
					System.out.println("Error connecting to Databse.\nPlease check your connection.");
				}
			}
		}
	}


	public Competitor getCompetitorById(Integer id) {
		Competitor tempComp = null;
		try {
			tempComp = CompetitorsManager.findById(id);
		} catch (SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		//System.out.println("we are in getCompetitorByID :" + tempComp);
		return tempComp;
	}

	/* THE FOLLOWING FUNCTION DOES THE SAME AS settleWinner AND settlePodium, PLEASE TRY TO USE THOSE, AS THEY ARE IN THE INTERFACE */

	//public void setWinnersForCompetition(String competition, Competitor winner, Competitor second, Competitor third, String mgrPass) 
	//		throws ExistingCompetitionException, AuthenticationException, CompetitionException {	
	//	/* first authenticate the manager */
	//	authenticateMngr(mgrPass);
	//	/* proceed to remove him */
	//	Competition tempCompetition = getCompetitionByName(competition);
	//	/* check if the username exists */
	//	if ( tempCompetition == null )
	//		throw new ExistingCompetitionException("The competition does not exist!");
	//	
	//	if( !tempCompetition.hasCompetitor(winner) || 
	//			!tempCompetition.hasCompetitor(second) ||
	//			!tempCompetition.hasCompetitor(third)){
	//		throw new CompetitionException();
	//	}
	//	
	//	Map<Integer, Competitor> winners = new LinkedHashMap<>();
	//	try {
	//		winners.put(winner.getId(), getCompetitorById(winner.getId()));
	//		winners.put(second.getId(), getCompetitorById(second.getId()));
	//		winners.put(third.getId(), getCompetitorById(third.getId()));			
	//	} catch (Exception e) {
	//		System.out.println("BETA! some of the names in here could not have been winners !\nPodium vs Winner..");
	//	}
	//	
	//	tempCompetition.setWinners(winners);
	//	
	//	try { 
	//		CompetitionsManager.update(tempCompetition);
	//	} catch(SQLException sqlex) {
	//		sqlex.printStackTrace();
	//	}
	//	
	//}

	public void printAllCompetitors() throws SQLException {

		utility.printList(CompetitorsManager.findAll().values());

	}

	public void deleteCompetitorFromDB(Competitor competitor , String managerPwd)
			throws AuthenticationException, BadParametersException {
		/* first authenticate the manager */
		authenticateMngr(managerPwd);
		/* Checks if the competitor doesn't participate in any competition */
		Map<String,Competition> competitions = new LinkedHashMap<String,Competition>();
		try { 
			competitions = ParticipantsManager.findAllByCompetitor(competitor.getId());
		} catch(SQLException sqlex) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
		if (!competitions.isEmpty()){
			new BadParametersException("This competitor still participates in competitions!");
		}
		/* Checks if the competitor is a team */
		if (competitor instanceof Team){
			Collection<Competitor> listIndComp = null;
			try {
				listIndComp = CompetitorsManager.findAllIndividualCompetitors().values();
			} catch (SQLException sqlex) {
				System.out.println("Error connecting to Databse.\nPlease check your connection.");
			}
			for (Competitor c : listIndComp){
				IndividualCompetitor indComp = (IndividualCompetitor) c;
				if (indComp.getIdTeam().equals(competitor.getId())){
					indComp.setIdTeam(null);
					try {
						CompetitorsManager.update(indComp);
					} catch (SQLException sqlex) {
						System.out.println("Error connecting to Databse.\nPlease check your connection.");
					}
				}
			}
		}
		/* Now we can delete the competitor */
		Competitor competitorToDelete = this.getCompetitorById(competitor.getId());
		try { 
			CompetitorsManager.delete(competitorToDelete);
		} catch(SQLException sqlex) {
			System.out.println("Error accessing the Database.\nPlease check your connection.");
		}	
	}

	public void addCompetitiorToTeam(Competitor indComp, Competitor team, String managerPwd) 
			throws AuthenticationException {

		/* first authenticate the manager */

		authenticateMngr(managerPwd);

		/* do some tests before starting */
		if ( !(indComp instanceof IndividualCompetitor) || !(team instanceof Team) ) {
			System.out.println("Wrong IDs. These competitors do not fit the required action.");
			return;
		}
		/* now do the action */
		((IndividualCompetitor)indComp).setIdTeam(((Team)team).getId());
		/* update */
		try {
			CompetitorsManager.update(indComp);
		} catch (SQLException e) {
			System.out.println("Error connecting to Databse.\nPlease check your connection.");
		}
	}
	public void printWinners(String competition) throws ExistingCompetitionException {
		List<Competitor> winners = new ArrayList<Competitor>();
		winners.addAll(consultResultsCompetition(competition));
		
		System.out.println("Quiero hacer el print Lista " + consultResultsCompetition(competition).size());
		for (Competitor w : winners){
			System.out.println("En el for:");
			System.out.println(w.toString());
		}
	}


}
