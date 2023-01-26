package dev4a.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import dev4a.competitor.Competitor;
import dev4a.competitor.IndividualCompetitor;
import dev4a.competitor.Team;
import dev4a.utils.DatabaseConnection;

public class CompetitorsManager {

	/**
	 * This method takes care of inserting subscribers in the table
	 * It takes the password of the new subscriber and inserts the
	 * 
	 * values in the table
	 * @param subscriber - the new object (in memory)
	 * @param password - the generated password
	 * @return the subscriber object
	 * @throws SQLException
	 */
	public static Competitor persist(Competitor competitor) throws SQLException {

		/* get the connection using the class we defined */
		Connection conn = DatabaseConnection.getConnection();

		try {
			/* we need to leave the system in a stable state so we turn off autocommit */
			conn.setAutoCommit(false);
			/* this statement prepares all the values to insert into the competitors table 
			 * the structure of this table is up above for easier access
			 * */
			PreparedStatement psPersist = conn
			.prepareStatement("INSERT INTO competitor(typeOf, first_name, "
				+ "last_name, born_date, team_name, id_team)"
				+ "values (?, ?, ?, ?, ?, ?)");
			/*  */
			psPersist.setInt(1, competitor.getType());
			if (competitor.getType() == Competitor.TYPE_INDIVIDUAL) {
				psPersist.setString(2, ((IndividualCompetitor) competitor).getFirstName());
				psPersist.setString(3, ((IndividualCompetitor) competitor).getLastName());
				psPersist.setDate(4, Date.valueOf(((IndividualCompetitor) competitor).getBornDate()));
				psPersist.setString(5, null);
				psPersist.setNull(6, Types.INTEGER);//((IndividualCompetitor) competitor).getIdTeam());
			} 
			else 
				if (competitor.getType() == Competitor.TYPE_TEAM) {
				psPersist.setString(2, null);
				psPersist.setString(3, null);
				psPersist.setNull(4, Types.DATE);
				psPersist.setString(5, ((Team) competitor).getName());
				psPersist.setNull(6, Types.INTEGER); // 0 for null
			}
		
			psPersist.executeUpdate();
			
			psPersist.close();
			
			// Retrieving the value of the id with a request on the
			// sequence (subscribers_id_seq).
			PreparedStatement psIdValue = conn
					.prepareStatement("SELECT currval('competitor_id_seq') AS value_id");

			ResultSet resultSet = psIdValue.executeQuery();

			Integer id = null;

			while (resultSet.next()) {
				id = resultSet.getInt("value_id");
			}
			
			resultSet.close();
			psIdValue.close();
			conn.commit();
			
			competitor.setId(id);
			
		} catch (SQLException e) {
			try {
				/* if something occured do not commit anything and rollback !*/
				conn.rollback();
			} catch (SQLException e1) {
				System.out.println("Error conencting to DB.");
			}
			/* reset the state to the default */
			conn.setAutoCommit(true);
			throw e;
		}
		/* back to default */
		conn.setAutoCommit(true);
		conn.close();
		/* return if for convinience */
		return competitor;
	}
	/**
	 * This method gets a competitior by his id
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public static Competitor findById(int id) throws SQLException
	{
		/* open the connection */
		Connection conn = DatabaseConnection.getConnection(); 
		/* get the select statement ready */
		PreparedStatement psSelect = conn.prepareStatement("SELECT * FROM competitor WHERE id=?");
		/* the query value */
		psSelect.setInt(1, id);
//		/* execute it */
		ResultSet resultSet = psSelect.executeQuery();
		/* declare the competitor if we don't fine him, return a null */
		Competitor competitor = null;
		/*  (int id, String firstName, String lastName, String bornDate, int idTeam) */
		while(resultSet.next())
		{
			if(resultSet.getInt("typeOf") == Competitor.TYPE_INDIVIDUAL) {
				/* it's an individual */
				competitor = new IndividualCompetitor(
						id,
						resultSet.getString("first_name"), 
						resultSet.getString("last_name"), 
						(resultSet.getDate("born_date")).toString(), 
						resultSet.getInt("id_team")
						);
				
			} 
			else 
				/* it's a team */
				if(resultSet.getInt("typeOf") == Competitor.TYPE_TEAM) {
					competitor = new Team(
							resultSet.getString("team_name")
							);
					competitor.setId(id);
				}
		}
		/* clean up */
		resultSet.close();
		psSelect.close();
		conn.close();
		/* return the found (or null) */
		return competitor;
	}

		/**
		 * This method finds all the individual competitors in the system
		 * @return
		 * @throws SQLException
		 */
		public static Map<Integer, Competitor> findAllIndividualCompetitors() throws SQLException {
			/* open the connection */
			Connection conn = DatabaseConnection.getConnection();
			/* prepare the query */
			PreparedStatement psSelect = conn
			.prepareStatement("SELECT * FROM competitor WHERE typeOf=? ORDER BY id ");
			/* set the value */
			psSelect.setInt(1, Competitor.TYPE_INDIVIDUAL);
			/* the results are here */
			ResultSet resultSet = psSelect.executeQuery();
			/* a container for them all */
			Map<Integer, Competitor> competitors = new LinkedHashMap<Integer, Competitor>();
			/* reference for temporary competitor */
			IndividualCompetitor competitor = null;
			/* (int id, String firstName, String lastName, String bornDate, int idTeam) */
			while (resultSet.next()) {
				competitor = new IndividualCompetitor(
						resultSet.getInt("id"),
						resultSet.getString("first_name"), 
						resultSet.getString("last_name"), 
						resultSet.getDate("born_date").toString(),
						resultSet.getInt("id_team"));
				competitor.setId(resultSet.getInt("id"));
				competitors.put(competitor.getId(), competitor);
			}
			
			/* clean up */
			resultSet.close();
			psSelect.close();
			conn.close();
			/* return the map */
			return competitors;
		}
		
		/**
		 * This method finds all the teams in the system
		 * @return
		 * @throws SQLException
		 */
		public static Map<Integer, Competitor> findAllTeams() throws SQLException {
			/* open the connection */
			Connection conn = DatabaseConnection.getConnection();
			/* prepare the query */
			PreparedStatement psSelect = conn
			.prepareStatement("SELECT * FROM competitor WHERE typeOf=" + Integer.toString(Competitor.TYPE_TEAM) + " ORDER BY id");
			/* the results are here */
			ResultSet resultSet = psSelect.executeQuery();
			/* a container for them all */
			Map<Integer, Competitor> competitors = new LinkedHashMap<Integer, Competitor>();			
			/* reference for temporary competitor */
			Team competitor = null;
			/* loop through */
			while (resultSet.next()) {
				competitor = new Team(
						resultSet.getString("team_name")
						);
				competitor.setId(resultSet.getInt("id"));
				competitors.put(competitor.getId(), competitor);
			}
			
			/* clean up */
			resultSet.close();
			psSelect.close();
			conn.close();
			/* return all of them */
			return competitors;
		}

		/**
		 * This method finds all the teams in the system
		 * @return
		 * @throws SQLException
		 */
		public static Map<Integer, Competitor> findAll() throws SQLException {
			/* get all teams */
			Map<Integer, Competitor> allComps = findAllIndividualCompetitors();
			allComps.putAll(findAllTeams());
			return allComps;
		}
		/**
		 * This method updates the competitor in the db
		 * @param sub
		 * @param newPassword
		 * @throws SQLException
		 */
		public static void update(Competitor competitor) throws SQLException {
			/* open the connection */
			Connection conn = DatabaseConnection.getConnection();
			/* create the update query */
			PreparedStatement psUpdate = conn
					.prepareStatement("UPDATE competitor "
							+ "SET first_name=?, last_name=?, born_date=?, team_name=?, id_team=? "
							+ "WHERE id=?");
			/* the two possible outcomes */
			if (competitor.getType() == Competitor.TYPE_INDIVIDUAL) {
				psUpdate.setString(1, ((IndividualCompetitor) competitor).getFirstName());
				psUpdate.setString(2, ((IndividualCompetitor) competitor).getLastName());
				psUpdate.setDate(3, Date.valueOf(((IndividualCompetitor) competitor).getBornDate()));
				psUpdate.setString(4, null);
				psUpdate.setInt(5, ((IndividualCompetitor) competitor).getIdTeam());
			} 
			else 
				if (competitor.getType() == Competitor.TYPE_TEAM) {
					psUpdate.setString(1, null);
					psUpdate.setString(2, null);
					psUpdate.setString(3, null);
					psUpdate.setString(4, ((Team) competitor).getName());
					psUpdate.setInt(5, -1); // 0 for null
			}
			psUpdate.setInt(6,competitor.getId());
			/* execute the query */
			psUpdate.executeUpdate();
			/* clean up */
			psUpdate.close();
			conn.close();
		}
		
		public static void delete(Competitor competitor) throws SQLException {
			/* open the connection */
			Connection conn = DatabaseConnection.getConnection();
			/* create the delete query */
			PreparedStatement psUpdate = conn
					.prepareStatement("DELETE FROM competitor WHERE id=?");
			psUpdate.setInt(1, competitor.getId());
			/* clean up */
			psUpdate.executeUpdate();
			psUpdate.close();
			conn.close();
		}
		
		
	}
