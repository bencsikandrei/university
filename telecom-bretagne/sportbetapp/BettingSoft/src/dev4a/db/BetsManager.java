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
 * @author dev4a
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import dev4a.bets.Bet;
import dev4a.competition.Competition;
import dev4a.subscriber.Subscriber;
import dev4a.utils.DatabaseConnection;

public class BetsManager {

	public static Bet persist(Bet bet) throws SQLException {
		/* 	Two steps in this method which must be managed in an atomic
			(unique) transaction:
			1 - insert the new bet;
			2 - once the insertion is OK, in order to set up the value
			of the id, a request is done to get this value by
			requesting the sequence (bets_id_seq) in the
			database.
		 */
		/* get the connection */
		Connection conn = DatabaseConnection.getConnection();

		try {
			conn.setAutoCommit(false);
			PreparedStatement psPersist = conn
					.prepareStatement("insert into bet(username, "
							+ "name_comp, bet_date, type, id_winner, "
							+ "id_second, id_third, nb_tokens, "
							+ "status, earnings)  "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			/* */
			psPersist.setString(1, bet.getUserName());
			psPersist.setString(2, bet.getCompetition());
			
			psPersist.setDate(3, java.sql.Date.valueOf(bet.getBetDate()));
			int tempType = bet.getType();
			psPersist.setInt(4, tempType);
			psPersist.setInt(5, bet.getWinner().getId());
			/* now we act accordingly for bet type */
			if( tempType == Bet.TYPE_WINNER ) {
				
				psPersist.setNull(6, Types.INTEGER);
				psPersist.setNull(7, Types.INTEGER);
			}
			else
				if( tempType == Bet.TYPE_PODIUM ) {
					psPersist.setInt(6, bet.getSecond().getId());
					psPersist.setInt(7, bet.getThird().getId());
				}
			psPersist.setLong(8, bet.getNumberOfTokens());
			psPersist.setInt(9, bet.getState());
			psPersist.setLong(10, bet.getEarnings());

			/* do the updare */
			psPersist.executeUpdate();
			/* */
			psPersist.close();

			// Retrieving the value of the id with a request on the
			// sequence (subscribers_id_seq).
			PreparedStatement psIdValue = conn
					.prepareStatement("SELECT currval('bet_id_seq') AS value_id");

			ResultSet resultSet = psIdValue.executeQuery();

			Integer id = null;

			while (resultSet.next()) {
				id = resultSet.getInt("value_id");
			}

			resultSet.close();
			psIdValue.close();
			conn.commit();

			bet.setIdentifier(id);

		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			conn.setAutoCommit(true);
			throw e;
		}

		conn.setAutoCommit(true);
		conn.close();

		return bet;
	}

	// -----------------------------------------------------------------------------
	/**
	 * Find a bet by his id.
	 * 
	 * @param id
	 *            the id of the bet to retrieve.
	 * @return the bet or null if the id does not exist in the database.
	 * @throws SQLException
	 */
	public static Bet findById(Integer id) throws SQLException {
		/* open the connection */
		Connection conn = DatabaseConnection.getConnection();

		PreparedStatement psSelect = conn
				.prepareStatement("SELECT * FROM bet WHERE id=?");

		psSelect.setInt(1, id.intValue());

		ResultSet resultSet = psSelect.executeQuery();

		Bet bet = null;

		while (resultSet.next()) {
			/* the id */
			int tempId = resultSet.getInt("id");

			String tempUserName = resultSet.getString("username");

			String tempCompName = resultSet.getString("name_comp");

			String tempDate = (resultSet.getDate("bet_date")).toString();

			int tempType = resultSet.getInt("type");

			long tempNbTokens = resultSet.getLong("nb_tokens");

			int tempStatus = resultSet.getInt("status");

			long tempEarnings = resultSet.getLong("earnings");

			int tempIdWinner = resultSet.getInt("id_winner");

			if( tempType == 1 ) {
				/* this is a winner bet 
				 * long nbOfTokens, String competition, 
			Competitor winner,
			String username, Date betDate)*/
				bet = new Bet(
						tempNbTokens, 
						tempCompName, 
						CompetitorsManager.findById(tempIdWinner), 
						tempUserName, 
						tempDate);
				/* */

			} 
			else {
				/* */
				int tempSecond = resultSet.getInt("id_second");
				/**/
				int tempThird = resultSet.getInt("id_third");

				bet = new Bet(tempNbTokens, 
						tempCompName, 
						CompetitorsManager.findById(tempIdWinner), 
						CompetitorsManager.findById(tempSecond), 
						CompetitorsManager.findById(tempThird), 
						tempUserName, 
						tempDate);
			}
			bet.setIdentifier(tempId);
		}
		
		
		/* clean up */
		resultSet.close();
		psSelect.close();
		conn.close();

		return bet;
	}

	// -----------------------------------------------------------------------------
	/**
	 * Find all the bets for a specific subscriber in the database.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Map<Integer, Bet> findBySubscriber(Subscriber subscriber)
			throws SQLException {

		Connection conn = DatabaseConnection.getConnection();

		PreparedStatement psSelect = conn
				.prepareStatement("SELECT * FROM bet WHERE username=? ORDER BY username");

		psSelect.setString(1, subscriber.getUserName());

		ResultSet resultSet = psSelect.executeQuery();

		Map<Integer, Bet> bets = new LinkedHashMap<>();

		Bet bet = null;
		while (resultSet.next()) {
			int tempId = resultSet.getInt("id");
			
			bets.put(new Integer(tempId), findById(tempId));
		}
		resultSet.close();
		psSelect.close();
		conn.close();

		return bets;
	}
	
// -----------------------------------------------------------------------------
		/**
		 * Find all the bets for a specific competition in the database.
		 * 
		 * @return
		 * @throws SQLException
		 */
		public static Map<Integer, Bet> findByCompetition(Competition competition)
				throws SQLException {
			
			Connection conn = DatabaseConnection.getConnection();

			PreparedStatement psSelect = conn
					.prepareStatement("SELECT * FROM bet WHERE name_comp=? ORDER BY name_comp");

			psSelect.setString(1, competition.getName());

			ResultSet resultSet = psSelect.executeQuery();

			Map<Integer, Bet> bets = new LinkedHashMap<>();
			
			while (resultSet.next()) {
				int tempId = resultSet.getInt("id");
				
				bets.put(new Integer(tempId), findById(tempId));
			}
			resultSet.close();
			psSelect.close();
			conn.close();

			return bets;
		}

	// -----------------------------------------------------------------------------
	/**
	 * Find all the bets in the database.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Map<Integer, Bet> findAll() throws SQLException {
		Connection c = DatabaseConnection.getConnection();
		PreparedStatement psSelect = c
				.prepareStatement("SELECT * FROM bet ORDER BY username");
		
		ResultSet resultSet = psSelect.executeQuery();
		
		Map<Integer, Bet> bets = new LinkedHashMap<Integer, Bet>();
		
		while (resultSet.next()) {
			int tempId = resultSet.getInt("id");
			
			bets.put(new Integer(tempId), findById(tempId));
		}
		resultSet.close();
		psSelect.close();
		c.close();

		return bets;
	}

	// -----------------------------------------------------------------------------
	/**
	 * Update on the database the values from a bet. Useful?
	 * 
	 * @param bet
	 *            the bet to be updated.
	 * @throws SQLException
	 */
	public static void update(Bet bet) throws SQLException {
		Connection c = DatabaseConnection.getConnection();
		PreparedStatement psUpdate = c
				.prepareStatement("UPDATE bet "
						+ "SET username=?, "
							+ "name_comp=?, type=?, id_winner=?, "
							+ "id_second=?, id_third=?, nb_tokens=?, "
							+ "status=?, earnings=?"
						+ "WHERE id=?");
		psUpdate.setInt(10, bet.getIdentifier());
		psUpdate.setString(1, bet.getUserName());
		psUpdate.setString(2, bet.getCompetition());
		psUpdate.setInt(3, bet.getType());
		psUpdate.setInt(4, bet.getWinner().getId());
		if( bet.getType() == bet.TYPE_PODIUM ) {
			psUpdate.setInt(5, bet.getSecond().getId());
			psUpdate.setInt(6, bet.getThird().getId());
		}
		else {
			psUpdate.setNull(5, Types.INTEGER);
			psUpdate.setNull(6, Types.INTEGER);
		}
		psUpdate.setLong(7, bet.getNumberOfTokens());
		psUpdate.setInt(8, bet.getState());
		psUpdate.setLong(9, bet.getEarnings());
		
		psUpdate.executeUpdate();
		psUpdate.close();
		c.close();
	}

	// -----------------------------------------------------------------------------
	/**
	 * Delete from the database a specific bet.
	 * 
	 * @param bet
	 *            the bet to be deleted.
	 * @throws SQLException
	 */
	public static void delete(Bet bet) throws SQLException {
		/* open the connection */
		Connection conn = DatabaseConnection.getConnection();
		/* */
		PreparedStatement psUpdate = conn
				.prepareStatement("DELETE FROM bet WHERE id=?");
		psUpdate.setInt(1, bet.getIdentifier());
		/* clean up */
		psUpdate.executeUpdate();
		psUpdate.close();
		conn.close();
	}
	// -----------------------------------------------------------------------------
}

