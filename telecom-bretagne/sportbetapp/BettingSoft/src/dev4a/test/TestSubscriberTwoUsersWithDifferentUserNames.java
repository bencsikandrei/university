package dev4a.test;

import org.junit.Test;

import dev4a.subscriber.Subscriber;
import dev4a.system.BettingSystem;
import static org.junit.Assert.*;

public class TestSubscriberTwoUsersWithDifferentUserNames {
	
	@Test
	public void testUsernamesUniqueDifferent() {
		/* users are unique and differentiated by their username */		
		Subscriber sub1 = new Subscriber("Lehman", "Frederic", "lfred","1980-02-03");
		Subscriber sub2 = new Subscriber("Lehman", "Frederic", "lfred1","1980-02-03");
		/* tests this condition */
		assertFalse(sub1.equals(sub2));
		
	}
	
	
}