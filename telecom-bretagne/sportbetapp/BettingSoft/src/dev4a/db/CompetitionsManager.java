package dev4a.db;
/**
 * DAO class (<i>Data Access Object</i>) for the {@link Bet} class. This class
 * provides the CRUD functionalities :<br>
 * <ul>
 * <li><b>C</b>: create a new bet in the database.
 * <li><b>R</b>: retrieve (or read) a (list of)bet(s) from the database.
 * <li><b>U</b>: update the values stored in the database for a bet.
 * <li><b>D</b>: delete a bet in the database.
 * </ul>
 * 
 * @author Group 4A
 */
import java.sql.*;
import java.sql.Date;
import java.util.*;

import dev4a.competition.Competition;
import dev4a.competition.States;
import dev4a.competitor.Competitor;
import dev4a.competitor.IndividualCompetitor;
import dev4a.competitor.Team;
import dev4a.subscriber.Subscriber;
import dev4a.utils.DatabaseConnection;

public class CompetitionsManager {


	public static Competition persist(Competition competition) throws SQLException {

		/* get the connection using the class we defined */
		Connection conn = DatabaseConnection.getConnection();

		try {
			/* we need to leave the system in a stable state so we turn off autocommit */
			conn.setAutoCommit(false);
			/* this statement prepares all the values to insert into the subscriber table 
			 * the structure of this table is up above for easier access
			 * */
			/*
			 * 	name VARCHAR(50) PRIMARY KEY,    
				starting_date timestamp,    
				closing_date timestamp,    
				status INT NOT NULL,    
			    sport  VARCHAR(30)  NOT NULL,
			    id_winner INT REFERENCES Competitor(id),
				id_second INT REFERENCES Competitor(id),    
				id_third INT REFERENCES Competitor(id)
			 */
			PreparedStatement psPersist = conn
					.prepareStatement("INSERT INTO competition(name, starting_date, "
							+ "closing_date, status, sport, id_winner, id_second, id_third)"
							+ "values (?, ?, ?, ?, ?, ?, ?, ?)");
			/*  */

			psPersist.setString(1, competition.getName());
			psPersist.setDate(2, new java.sql.Date(competition.getStartDate().getTime().getTime()));
			psPersist.setDate(3, new java.sql.Date(competition.getClosingDate().getTime().getTime()));
			psPersist.setInt(4, competition.getStatus());
			psPersist.setString(5, competition.getSport());

			Map<Integer, Competitor> tempMap = competition.getWinners();

			switch (tempMap.values().size()) {
			case 3:
				int count = 6;
				for( Competitor comp : tempMap.values() ) {
					psPersist.setInt(count, comp.getId());
					++count;
				}		
				break;
			case 1:
				for( Competitor comp : tempMap.values()) 
					psPersist.setInt(6, comp.getId());
				for( int i = 7; i <= 8; i++ ) {
					psPersist.setNull(i, Types.INTEGER);
					
				}
			case 0:
				for( int i = 6; i <= 8; i++ ) {
					psPersist.setNull(i, Types.INTEGER);
					
				}
			default:
				break;


			}

			/* insert them */
			psPersist.executeUpdate();
			/* clean up */
			psPersist.close();
			conn.commit();
		} catch (SQLException e) {
			try {
				/* if something occured do not commit anything and rollback !*/
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			/* reset the state to the default */
			conn.setAutoCommit(true);
			throw e;
		}
		/* back to default */
		conn.setAutoCommit(true);
		conn.close();
		/* return if for convinience */
		return competition;
	}

	/**
	 * Find a competition by his id.
	 * 
	 * @param id the id of the competition to retrieve.
	 * @return the competition or null if the id does not exist in the database.
	 * @throws SQLException
	 */
	public static Competition findByName(String name) throws SQLException
	{
		// 1 - Get a database connection from the class 'DatabaseConnection' 
		Connection c = DatabaseConnection.getConnection();

		// 2 - Creating a Prepared Statement with the SQL instruction.
		//     The parameters are represented by question marks. 
		PreparedStatement psSelect = c.prepareStatement("SELECT * FROM competition WHERE name LIKE ?");

		// 3 - Supplying values for the prepared statement parameters (question marks).
		psSelect.setString(1, name);

		// 4 - Executing Prepared Statement object among the database.
		//     The return value is a Result Set containing the data.
		ResultSet resultSet = psSelect.executeQuery();

		// 5 - Retrieving values from the Result Set.
		Competition tempCompetition = null;
		/*
		 * 	name VARCHAR(50) PRIMARY KEY,    
			starting_date timestamp,    
			closing_date timestamp,    
			status INT NOT NULL,    
		    sport  VARCHAR(30)  NOT NULL,
		    id_winner INT REFERENCES Competitor(id),
			id_second INT REFERENCES Competitor(id),    
			id_third INT REFERENCES Competitor(id)
		 */
		Map<Integer, Competitor> tempComps = new LinkedHashMap<>();
		int winner, second, third;
		int status;
		while(resultSet.next())
		{	/* public Competition(String name, Calendar startDate, Calendar closingDate, 
			String sport, Map<Integer, Competitor> allCompetitors, String betType) */
			// Get competitor 1, 2 and 3 from Ids
			// resultSet.getInt("id_winner");
			// resultSet.getInt("id_second");
			// resultSet.getInt("id_third");
			/* get the winners */
			winner = resultSet.getInt("id_winner");
			second = resultSet.getInt("id_second");
			third = resultSet.getInt("id_third");
			status = resultSet.getInt("status");
			/* store them */
			tempComps.put(new Integer(winner), CompetitorsManager.findById(winner));
			tempComps.put(new Integer(second), CompetitorsManager.findById(second));
			tempComps.put(new Integer(third), CompetitorsManager.findById(third));
			/* get the calendar instances */
			Calendar stCal = Calendar.getInstance();
			stCal.setTime(resultSet.getDate("starting_date"));
			Calendar clCal = Calendar.getInstance();
			clCal.setTime(resultSet.getDate("closing_date"));
			/* the new object can now be formed */
			tempCompetition = new Competition(
					resultSet.getString("name"),
					stCal,
					clCal,
					resultSet.getString("sport"),
					tempComps,
					"pw"
					);
			tempCompetition.setStatus(status);

		}

		// 6 - Closing the Result Set
		resultSet.close();

		// 7 - Closing the Prepared Statement.
		psSelect.close();

		// 8 - Closing the database connection.
		c.close();
		/* return the found object */
		return tempCompetition;
	}

	public static Map<String, Competition> findAll() throws SQLException {
		/* open the connection */
		Connection conn = DatabaseConnection.getConnection();
		/* prepare the query */
		PreparedStatement psSelect = conn
				.prepareStatement("SELECT * FROM competition ORDER BY name");
		/* the results are here */
		ResultSet resultSet = psSelect.executeQuery();
		/* a container for them all */
		Map<String, Competition> comps = new LinkedHashMap<>();
		/* refference for temp subscriber */
		Competition tempCompetition = null;

		Map<Integer, Competitor> tempWins = new LinkedHashMap<>();
		int winner, second, third;
		int count = 0;
		
		while (resultSet.next()) {
			count++;
			winner = resultSet.getInt("id_winner");
			second = resultSet.getInt("id_second");
			third = resultSet.getInt("id_third");
			/* store them */
			tempWins.put(new Integer(winner), CompetitorsManager.findById(winner));
			tempWins.put(new Integer(second), CompetitorsManager.findById(second));
			tempWins.put(new Integer(third), CompetitorsManager.findById(third));
			/* get the calendar instances */
			Calendar stCal = Calendar.getInstance();
			stCal.setTime(resultSet.getDate("starting_date"));
			Calendar clCal = Calendar.getInstance();
			clCal.setTime(resultSet.getDate("closing_date"));
			/* the new object can now be formed */
			tempCompetition = new Competition(
					resultSet.getString("name"),
					stCal,
					clCal,
					resultSet.getString("sport"),
					tempWins,
					"pw"
					);

			comps.put(tempCompetition.getName(), tempCompetition);
		}
		
		//System.out.println("We found " + count + " competitions!");
		
		/* clean up */
		resultSet.close();
		psSelect.close();
		conn.close();

		return comps;
	}

	public static void update(Competition competition) throws SQLException {
		/* open the connection */
		Connection conn = DatabaseConnection.getConnection();
		/* create the update query */
		PreparedStatement psUpdate = conn
				.prepareStatement("UPDATE competition "
						+ "SET name=?, starting_date=?, closing_date=?, "
						+ "status=?, sport=?, id_winner=?, id_second=?, id_third=? "
						+ "WHERE name=?");
		/* update all necessary fields
		 * name, starting_date, "
				+ "closing_date, status, sport, id_winner, id_second, id_third */
		psUpdate.setString(1, competition.getName());		

		psUpdate.setDate(2, new java.sql.Date(competition.getStartDate().getTime().getTime()));
		psUpdate.setDate(3, new java.sql.Date(competition.getClosingDate().getTime().getTime()));
		
		psUpdate.setInt(4, competition.getStatus());
		psUpdate.setString(5, competition.getSport());

		Map<Integer, Competitor> tempMap = competition.getWinners();
		for (Competitor comp : tempMap.values()) {
			System.out.println(comp);
		}

		switch (tempMap.values().size()) {
		case 3:
			int count = 6;
			for( Competitor comp : tempMap.values() ) {
				psUpdate.setInt(count, comp.getId());
				++count;
			}		
			break;
		case 1:
			for( Competitor comp : tempMap.values()) 
				psUpdate.setInt(6, comp.getId());
			for( int i = 7; i <= 8; i++ ) {
				psUpdate.setNull(i, Types.INTEGER);
				
			}
		case 0:
			for( int i = 6; i <= 8; i++ ) {
				psUpdate.setNull(i, Types.INTEGER);
				
			}
		default:
			break;


		}
		psUpdate.setString(9, competition.getName());
		
		/* execute the query */
		psUpdate.executeUpdate();
		/* clean up */
		psUpdate.close();
		conn.close();
	}

	public static void delete(Competition competition) throws SQLException {
		/* open the connection */
		Connection conn = DatabaseConnection.getConnection();
		/* create the delete query */
		PreparedStatement psUpdate = conn
				.prepareStatement("DELETE FROM competition WHERE name=?");
		psUpdate.setString(1, competition.getName());
		/* clean up */
		psUpdate.executeUpdate();
		psUpdate.close();
		conn.close();
	}
}