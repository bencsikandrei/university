package dev4a.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import dev4a.competition.Competition;
import dev4a.competitor.Competitor;
import dev4a.utils.DatabaseConnection;

public class ParticipantsManager {

	/**
	 * This method takes care of inserting participants in the table
	 * It takes the password of the new subscriber and inserts the
	 * 
	 * values in the table
	 * @param subscriber - the new object (in memory)
	 * @param password - the generated password
	 * @return the subscriber object
	 * @throws SQLException
	 */
	public static void persist(Competitor competitor, Competition competition) throws SQLException {

		/* get the connection using the class we defined */
		Connection conn = DatabaseConnection.getConnection();

		try {
			/* we need to leave the system in a stable state so we turn off autocommit */
			conn.setAutoCommit(false);
			/* this statement prepares all the values to insert into the competitors table 
			 * the structure of this table is up above for easier access
			 * */
			/* 	id_competitor INT REFERENCES Competitor(id),    
	name_competition VARCHAR(50) REFERENCES Competition(name) 	
			 */
			PreparedStatement psPersist = conn
					.prepareStatement("INSERT INTO participant(id_competitor, name_competition)"
							+ "values (?, ?)");
			/*  */
			
			psPersist.setInt(1, competitor.getId());

			psPersist.setString(2, competition.getName()); // 0 for null


			psPersist.executeUpdate();

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
	}
	/**
	 * This method gets a competitior by his id
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, Competition> findAllByCompetitor(int id) throws SQLException
	{
		/* open the connection */
		Connection conn = DatabaseConnection.getConnection(); 
		/* get the select statement ready */
		PreparedStatement psSelect = conn.prepareStatement("SELECT * FROM participant WHERE id_competitor=?");
		/* the query value */
		psSelect.setInt(1, id);
		//		/* execute it */
		ResultSet resultSet = psSelect.executeQuery();
		
		/*  (int id, String firstName, String lastName, String bornDate, int idTeam) */
		Map<String, Competition> tempComps = new LinkedHashMap<>();
		String compName = "";
		while(resultSet.next()) {
			compName = resultSet.getString("name_competition");
			tempComps.put(compName, CompetitionsManager.findByName(compName));
		}
		/* clean up */
		resultSet.close();
		psSelect.close();
		conn.close();
		/* return the found (or null) */
		return tempComps;
	}

	/**
	 * This method finds all the individual competitors in the system
	 * @return
	 * @throws SQLException
	 */
	public static Map<Integer, Competitor> findAllByCompetition(String competition) throws SQLException {
		/* open the connection */
		Connection conn = DatabaseConnection.getConnection();
		/* prepare the query */
		PreparedStatement psSelect = conn
				.prepareStatement("SELECT * FROM participant WHERE name_competition like ?");
		/* set the value */
		psSelect.setString(1, competition);
		/* the results are here */
		ResultSet resultSet = psSelect.executeQuery();
		/* a container for them all */
		Map<Integer, Competitor> competitors = new LinkedHashMap<Integer, Competitor>();

		/* reference for temporary competitor */
		Competitor competitor = null;
		int tempId = 0;
		/* (int id, String firstName, String lastName, String bornDate, int idTeam) */
		while (resultSet.next()) {
			/* first get the id */
			tempId = resultSet.getInt("id_competitor");
			/* then find him by his id */
			competitor = CompetitorsManager.findById(tempId);

			competitors.put(tempId, competitor);
		}
		System.out.println("We have found " + competitors.values().size() + " competitors in " + competition);
		/* clean up */
		resultSet.close();
		psSelect.close();
		conn.close();
		/* return the map */
		return competitors;
	}
	
	/**
	 * This method updates the competitor in the db
	 * @param sub
	 * @param newPassword
	 * @throws SQLException
	 *//*
	public static void update(Competitor competitor, Competition competition) throws SQLException {
		 open the connection 
		Connection conn = DatabaseConnection.getConnection();
		 create the update query 
		PreparedStatement psUpdate = conn
				.prepareStatement("UPDATE participant "
						+ "SET id_competitor=?, name_competition=?"
						+ "WHERE id_competitor=? and name_competition like=?");
		 the two possible outcomes 
		psUpdate.setInt(1, competitor.getId());
		psUpdate.setInt(2, competition.getName());
		
		 execute the query 
		psUpdate.executeUpdate();
		 clean up 
		psUpdate.close();
		conn.close();
	}*/

	public static void delete(Competitor competitor, Competition competition) throws SQLException {
		/* open the connection */
		Connection conn = DatabaseConnection.getConnection();
		/* create the delete query */
		PreparedStatement psUpdate = conn
				.prepareStatement("DELETE FROM participant WHERE id_competitor=? and name_competition like ?");
		psUpdate.setInt(1, competitor.getId());
		psUpdate.setString(2, competition.getName());
		/* clean up */
		psUpdate.executeUpdate();
		psUpdate.close();
		conn.close();
	}


}
