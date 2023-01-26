package dev4a.competition;

public class CompetitionException extends Exception {
	private static final long serialVersionUID = 8214319826285186814L;
	
	public CompetitionException() {
         super();
      }
       public CompetitionException(String reason) {
         super(reason);
      }
}
