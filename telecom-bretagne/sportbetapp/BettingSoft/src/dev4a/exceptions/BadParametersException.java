package dev4a.exceptions;

public class BadParametersException extends Exception{
	private static final long serialVersionUID = -385186352928310148L;

	public BadParametersException() {
        super();
     }
      public BadParametersException(String reason) {
        super(reason);
     }
}
