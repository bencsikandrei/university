package dev4a.competition;

public class ExistingCompetitionException extends Exception {
	private static final long serialVersionUID = 7214319826285186814L;
	
	public ExistingCompetitionException() {
         super();
      }
       public ExistingCompetitionException(String reason) {
         super(reason);
      }
}
