package dev4a.competitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev4a.competition.Competition;
import dev4a.competition.ExistingCompetitionException;
import dev4a.exceptions.BadParametersException;
import dev4a.utils.Utils;

public class Team implements Competitor {
	
	/* attributes */
	/* Id for the DB */
	private int id;
	/* Name of the team */
	private String name;
	/* type = 1 --> IndividualCompetitor */
	private int type;
	/* Competitors */
	private List<Competitor> members = new ArrayList();
	
	/* constructor */
	public Team(){
		/* empty for hibernate */
	}
	/* proper constructor */
	public Team(String name) {
		this.id = id;
		this.type = TYPE_TEAM;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean hasValidName() {
		Utils utils = new Utils();
		return (utils.checkValidTeamName(name));
	}

	@Override
	public void addMember(Competitor member) throws ExistingCompetitorException, BadParametersException {
		/* Member instanciated? */
		if( member == null )
			throw new BadParametersException();
		/* Member already in team? */
		if( this.members.contains(member) )
			throw new ExistingCompetitorException();
		/* Adding it ! */
		this.members.add(member);
	}

	@Override
	public void deleteMember(Competitor member) throws BadParametersException, ExistingCompetitorException {
		/* Member instanciated? */
		if( member == null )
			throw new BadParametersException();
		/* Member not in team? */
		if( !this.members.contains(member) )
			throw new ExistingCompetitorException();
		/* Delete it ! */
		this.members.remove(member);		
	}
	
	@Override 
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override 
	public String toString() {
		/* just return the name of the team */
		return "Id :" + this.id + " Name : " + this.name;
	}
	
	@Override 
	public boolean equals(Object obj) {
		/* check if it's instance of the Team class */
		if (!(obj instanceof Team))
			return false;
		if ( ((Team) obj).getId() == this.getId() )
			return true;
		return false;
	}
}
