package dev4a.db;
/**
 * DAO class (<i>Data Access Object</i>) for the {@link Subscriber} class. This class
 * provides the CRUD functionalities :<br>
 * <ul>
 * <li><b>C</b>: create a new subscriber in the database.
 * <li><b>R</b>: retrieve (or read) a (list of)subscriber(s) from the database.
 * <li><b>U</b>: update the values stored in the database for a subscriber.
 * <li><b>D</b>: delete a subscriber in the database.
 * </ul>
 * 
 * @author dev4a
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import dev4a.subscriber.Subscriber;
import dev4a.utils.DatabaseConnection;

public class SubscribersManager {
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
	public static Subscriber persist(Subscriber subscriber, String password) throws SQLException {

		/* get the connection using the class we defined */
		Connection conn = DatabaseConnection.getConnection();

		try {
			/* we need to leave the system in a stable state so we turn off autocommit */
			conn.setAutoCommit(false);
			/* the insert statement for the subscriber */
			/* this statement prepares all the values to insert into the subscriber table 
			 * the structure of this table is up above for easier access
			 */
			PreparedStatement psPersist = conn
					.prepareStatement("INSERT INTO subscriber(username, first_name, "
							+ "last_name,"
							+ "password, born_date, credit) "
							+ "values (?, ?, ? , ? , ? , ?)");
			/* all fields in order */
			psPersist.setString(1, subscriber.getUserName());
			psPersist.setString(2, subscriber.getFirstName());
			psPersist.setString(3, subscriber.getLastName());
			psPersist.setString(4, password );			
			psPersist.setDate(5, java.sql.Date.valueOf(subscriber.getBornDate()));
			psPersist.setLong(6, subscriber.getNumberOfTokens());			
			/* do the update */
			psPersist.executeUpdate();
			/* run the prepared statement that contains the subscribers data */
			psPersist.close();
			/* now commit the changes we made */
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
		return subscriber;
	}
	
	/** 
	 * This method finds a subscriber by his username 
	 * @param username
	 * @return
	 * @throws SQLException
	 */
	public static Subscriber findSubscriberByUserName(String username) throws SQLException {
		/* open the connection */
		Connection conn = DatabaseConnection.getConnection();
		/* make a querry */
		PreparedStatement psSelect = conn
				.prepareStatement("SELECT * FROM subscriber WHERE username LIKE ?");
		psSelect.setString(1, username);
		/* the result we get from the querry */
		ResultSet resultSet = psSelect.executeQuery();
		/* initialize */
		Subscriber sub = null;
		/* search in the result set */
		while (resultSet.next()) {
			/* create the new sub */
			sub = new Subscriber(
					
					resultSet.getString("first_name"),
					resultSet.getString("last_name"),
					resultSet.getString("username"),
					resultSet.getString("password"),
					resultSet.getDate("born_date").toString(),
					resultSet.getLong("credit")
					);
		}
		/* clean up */
		resultSet.close();
		psSelect.close();
		conn.close();
		
		return sub;
	}

	/**
	 * This method finds all the subscribers in the system
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, Subscriber> findAll() throws SQLException {
		/* open the connection */
		Connection conn = DatabaseConnection.getConnection();
		/* prepare the query */
		PreparedStatement psSelect = conn
				.prepareStatement("SELECT * FROM subscriber ORDER BY username, first_name, last_name");
		/* the results are here */
		ResultSet resultSet = psSelect.executeQuery();
		/* a container for them all */
		Map<String,Subscriber> subs = new LinkedHashMap<>();
		/* refference for temp subscriber */
		Subscriber sub = null;
		int count = 0;
		while (resultSet.next()) {
			count ++;			
			sub = new Subscriber(
					resultSet.getString("first_name"),
					resultSet.getString("last_name"),
					resultSet.getString("username"),
					resultSet.getString("password"),
					resultSet.getDate("born_date").toString(),
					resultSet.getLong("credit")
					);
			subs.put(sub.getUserName(), sub);
		}
		//System.out.println("We found " + count + " subscribers!");
		/* clean up */
		resultSet.close();
		psSelect.close();
		conn.close();

		return subs;
	}
	/**
	 * This method updates the subscriber in the db
	 * @param sub
	 * @param newPassword
	 * @throws SQLException
	 */
	public static void update(Subscriber sub) throws SQLException {
		/* open the connection */
		Connection conn = DatabaseConnection.getConnection();
		/* create the update query */
		PreparedStatement psUpdate = conn
				.prepareStatement("UPDATE subscriber SET first_name=?, last_name=?, born_date=?, credit=? WHERE username LIKE ?");
		/* update all necessary fields */
		psUpdate.setString(5, sub.getUserName());
		psUpdate.setString(1, sub.getFirstName());
		psUpdate.setString(2, sub.getLastName());		
		psUpdate.setDate(3, java.sql.Date.valueOf(sub.getBornDate()));
		psUpdate.setLong(4, sub.getNumberOfTokens());	
		/* execute the query */
		psUpdate.executeUpdate();
		/* clean up */
		psUpdate.close();
		conn.close();
	}
	
	/**
	 * This method updates the subscriber in the db
	 * @param sub
	 * @param newPassword
	 * @throws SQLException
	 */
	public static void updatePassword(Subscriber sub, String oldPassword, String newPassword) throws SQLException {
		/* open the connection */
		Connection conn = DatabaseConnection.getConnection();
		/* create the update query */
		PreparedStatement psUpdate = conn
				.prepareStatement("UPDATE subscriber SET password=? WHERE username LIKE ?");
		/* update all necessary fields */
		psUpdate.setString(2, sub.getUserName());
		
		psUpdate.setString(1, newPassword);
		
		sub.changePassword(oldPassword, newPassword);
		/* execute the query */
		psUpdate.executeUpdate();
		/* clean up */
		psUpdate.close();
		conn.close();
	}
	/**
	 * This method does the delete
	 * @param sub
	 * @throws SQLException
	 */
	public static void delete(Subscriber sub) throws SQLException {
		/* open the connection */
		Connection conn = DatabaseConnection.getConnection();
		/* create the delete query */
		PreparedStatement psUpdate = conn
				.prepareStatement("DELETE FROM subscriber WHERE username LIKE ?");
		psUpdate.setString(1, sub.getUserName() );
		System.out.println("This guy is fried : " + sub.getLastName());
		/* clean up */
		psUpdate.executeUpdate();
		psUpdate.close();
		conn.close();
	}
}
