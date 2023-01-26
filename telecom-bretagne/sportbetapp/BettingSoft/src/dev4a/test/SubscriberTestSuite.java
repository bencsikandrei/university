package dev4a.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/* JUnit Suite Tests for the subscriber */
@RunWith(Suite.class)
@Suite.SuiteClasses({ 
 TestSubscriberTwoUsersWithDifferentUserNames.class ,
 TestSubscriberTwoUsersWithSameName.class,
 TestSubscriberPersistanceAuthenticationFail.class,
 TestSubscriberPersistanceDelete.class,
 TestSubscriberPersistanceRetrieve.class
 
})

public class SubscriberTestSuite {
	
}
