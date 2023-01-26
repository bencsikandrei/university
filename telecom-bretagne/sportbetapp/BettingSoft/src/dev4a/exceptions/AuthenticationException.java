package dev4a.exceptions;
/**
 *  
 * @author prou
 *
 */
public class AuthenticationException extends Exception {
	private static final long serialVersionUID = 9214319826285186814L;
	
	public AuthenticationException() {
         super();
      }
       public AuthenticationException(String reason) {
         super(reason);
      }
   }