package dev4a.test;

import org.junit.Test;

import dev4a.subscriber.Subscriber;
import dev4a.system.BettingSystem;
import static org.junit.Assert.*;

public class TestSubscriberTwoUsersWithSameName {
	
	@Test
	public void testUsernamesUniqueSame() {
		/* users are unique and differentiated by their username */		
		Subscriber sub0 = new Subscriber("Mulleman", "Franz", "lfred","1980-02-03");
		Subscriber sub1 = new Subscriber("Lehman", "Frederic", "lfred","1980-02-03");		
		/* tests this condition */
		assertTrue(sub0.equals(sub1));
		
	}
	
	
}