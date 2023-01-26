package dev4a.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dev4a.subscriber.Subscriber;
import dev4a.utils.DatabaseConnection;

public class RootManager {
	
	/**
	 * This method takes care of inserting a default pass the first time
	 */
	public static void persist(String password) throws SQLException {

		/* get the connection using the class we defined */
		Connection conn = DatabaseConnection.getConnection();

		try {
			/* we need to leave the system in a stable state so we turn off autocommit */
			conn.setAutoCommit(false);
			
			/* this statement prepares all the values to insert into the subscriber table 
			 * the structure of this table is up above for easier access
			 * */
			PreparedStatement psPersist = conn
					.prepareStatement("INSERT INTO manager(password) "
							+ "values (?)");
			/* all fields in order */
			psPersist.setString(1, password);
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
	}
	
	/** 
	 * This method finds the pass  
	 * @param username
	 * @return
	 * @throws SQLException
	 */
	public static String getPassword() throws SQLException {
		/* open the connection */
		Connection conn = DatabaseConnection.getConnection();
		/* make a querry */
		PreparedStatement psSelect = conn
				.prepareStatement("SELECT * FROM manager LIMIT 1");
		/* the result we get from the querry */
		ResultSet resultSet = psSelect.executeQuery();
		/* pass */
		String pass = null;
		/* search in the result set */
		while (resultSet.next()) {
			/* create the new sub */
			pass = resultSet.getString("password");
		}
		/* clean up */
		resultSet.close();
		psSelect.close();
		conn.close();
		/* return the foudn password */
		return pass;
	}
	
	/**
	 * This method updates the password in the db
	 * 
	 * @param newPassword
	 * @throws SQLException
	 */
	public static void update(String newPassword) throws SQLException {
		/* open the connection */
		Connection conn = DatabaseConnection.getConnection();
		/* create the update query */
		PreparedStatement psUpdate = conn
				.prepareStatement("UPDATE manager SET password=?");
		/* update all necessary fields */
		psUpdate.setString(1, newPassword);
		/* execute the query */
		psUpdate.executeUpdate();
		/* clean up */
		psUpdate.close();
		conn.close();
	}
	
}
