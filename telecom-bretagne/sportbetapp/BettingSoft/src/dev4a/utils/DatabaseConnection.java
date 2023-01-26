package dev4a.utils;
/* 
 * @authos dev4a
 * @version 0.1
 * 
 * Separate the database connection and everything else
 * Helps connect to a JDBC driver ( system of choice POSTGRES )
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	/*----------------------------------------------------------------------------- */
	/* Connection parameters */
	private static String username        = "manager";
	private static String password        = "manager";
	private static String host            = "localhost";
	private static String numPort         = "54321";
	private static String base            = "BETTING";
	private static String connectString   = "jdbc:postgresql://" + host + ":" + numPort + "/" + base;
	/* ----------------------------------------------------------------------------- */
	/* Registration of the PostgreSQL JDBC Driver */
	static
	{
		try
		{
			/* use the JDBC driver in @lib folder */
			DriverManager.registerDriver(new org.postgresql.Driver());
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	/* ----------------------------------------------------------------------------- */
	/**
	 * Obtaining a connection to the database.
	 *  
	 * @return an instance of the Connection class.
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException
	{
		/* create the connection */
		return DriverManager.getConnection(connectString,username,password);
	}
	/*----------------------------------------------------------------------------- */
}