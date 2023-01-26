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

public class TestSubscriberPersistanceAuthenticationFail {
	protected String userName = "lfred";
	protected String firstName = "";
	protected String lastName = "";
	protected String bornDate = "";
	protected String mgrPass = "1234";
	protected String wrongPass = "123";
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test(expected = AuthenticationException.class)
	public void testSubscriberPersistance() throws BadParametersException, AuthenticationException , SubscriberException , ExistingSubscriberException{
		
		/* users are unique and differentiated by their username */		
		Subscriber sub1 = new Subscriber("Lehman", "Frederic", "lfred","1980-02-03");
		/* betting system */
		BettingSystem bs = new BettingSystem(mgrPass);
		/* persist with a wrong pass */
		bs.subscribe(lastName, firstName, userName, bornDate, wrongPass);
		
	}
	
	
}