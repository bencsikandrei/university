package dev4a.test;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import dev4a.exceptions.AuthenticationException;
import dev4a.exceptions.BadParametersException;
import dev4a.subscriber.ExistingSubscriberException;
import dev4a.subscriber.Subscriber;
import dev4a.subscriber.SubscriberException;
import dev4a.system.Betting;
import dev4a.system.BettingSystem;
import static org.junit.Assert.*;

public class TestSubscriberPersistanceRetrieve {
	protected String userName = "lfred";
	protected String firstName = "Frederic";
	protected String lastName = "Lehman";
	protected String bornDate = "1980-02-03";
	protected String mgrPass = "1234";
	protected String wrongPass = "123";
	protected long nbTokens = 101;
	
	@Test
	public void testSubscriberPersistance() throws BadParametersException, AuthenticationException , SubscriberException , ExistingSubscriberException{
		/* create a dummy subscriber */
		Subscriber testSub = new Subscriber(lastName, firstName, userName, bornDate);
		/* betting system */
		BettingSystem bs = new BettingSystem(mgrPass);
		/* persist with a good pass */
		bs.subscribe(lastName, firstName, userName, bornDate, mgrPass);		
		/* give him some money */
		bs.creditSubscriber(userName, nbTokens, mgrPass);		
		/* get him back Should be NULL */
		assertFalse(testSub.equals(bs.getSubscriberByUserName(userName)));
		/* remove him after finishing */
		bs.unsubscribe(userName, mgrPass);
	}
	
	
}