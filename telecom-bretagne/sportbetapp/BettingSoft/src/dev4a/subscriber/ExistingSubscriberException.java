package dev4a.subscriber;

/**
 * 
 * @author prou
 *
 */
public class ExistingSubscriberException extends Exception {
	private static final long serialVersionUID = 1L;

    public ExistingSubscriberException() {
        super();
     }
	public ExistingSubscriberException(String reason) {
        super(reason);
     } 
}



