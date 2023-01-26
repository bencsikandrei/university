package dev4a.competitor;

import dev4a.exceptions.BadParametersException;
import dev4a.subscriber.Subscriber;
import dev4a.utils.Utils;

public class IndividualCompetitor implements Competitor {

	/* attributes */
	/* Id for the DB */
	private Integer id;
	/* type = 1 --> IndividualCompetitor */
	private int type;
	/* Competitor surname */
	private String lastName;
	/* Competitor name */
	private String firstName;
	/* Competitor born date */
	private String bornDate;
	/* Id of the team, if the competitor belongs to a team */
	private int idTeam;
		
	/* constructor */
	public IndividualCompetitor(){
		/* empty for hibernate */
	}
	/* proper constructor for a competitor */
	public IndividualCompetitor(String firstName, String lastName, String bornDate){
		/* initialize */
		this.id = 0;
		this.type = TYPE_INDIVIDUAL; 
		this.lastName = lastName;
		this.firstName = firstName;
		this.bornDate = bornDate;
	}
	/* proper constructor for a competitor in team */
	public IndividualCompetitor(String firstName, String lastName, String bornDate, int id_team){
		/* initialize */
		this.id = 0;
		this.type = TYPE_INDIVIDUAL; 
		this.lastName = lastName;
		this.firstName = firstName;
		this.bornDate = bornDate;
		this.idTeam = id_team;
	}
	/*
	 * id serial primary key,    
	typeOf integer,
	first_name varchar(30),
	last_name varchar(30),
	born_date date
	team_name varchar(50),
	id_team integer references COMPETITOR(id)
	 */
	public IndividualCompetitor(int id, String firstName, String lastName, String bornDate, int idTeam) {
		this.id = id;
		this.type = TYPE_INDIVIDUAL; 
		this.firstName = firstName;
		this.lastName = lastName;
		this.bornDate = bornDate;
		this.idTeam = idTeam;
	}
	
	public int getId() {
		return id;
	}
	
	public int getType() {
		return type;
	}
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getBornDate() {
		return bornDate;
	}
	public void setBornDate(String bornDate) {
		this.bornDate = bornDate;
	}
	public Integer getIdTeam() {
		return idTeam;
	}
	public void setIdTeam(Integer id_team) {
		this.idTeam = id_team;
	}
	@Override
	public boolean hasValidName() {
		Utils utils = new Utils();
		return (utils.checkValidFirstLastName(firstName) && utils.checkValidFirstLastName(lastName)); 
	}
	
	@Override
	public void addMember(Competitor member) throws ExistingCompetitorException, BadParametersException {
		// useless...
	}
	
	@Override
	public void deleteMember(Competitor member) throws BadParametersException, ExistingCompetitorException {
		// useless...
	}
		
			
	@Override
	public void setId(int id) {
		this.id = id;
		
	}
	@Override 
	public String toString() {
		/* return the full name and borndate */
		return "Id: " + this.id + this.firstName + " " + this.lastName + " born " + this.bornDate;
		// please don't eliminate bornDate because it's useful in function createCompetitor in System
	}
	
	@Override 
	public boolean equals(Object obj) {
		/* check if it's instance of the IndividualCompetitor class */
		if (!(obj instanceof IndividualCompetitor))
			return false;
		if ( ((IndividualCompetitor) obj).getId() == this.getId() )
			return true;
		return false;
	}
}
