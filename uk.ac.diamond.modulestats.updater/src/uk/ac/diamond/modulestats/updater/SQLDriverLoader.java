package uk.ac.diamond.modulestats.updater;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLDriverLoader {
	private Connection conn = null;

	public SQLDriverLoader(String connectionfilepath) {
		//Retrieve the connection info
		ConnectionFileReader myConnect = new ConnectionFileReader(connectionfilepath);
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver loaded");
		} catch (ClassNotFoundException e) {
			System.out.println("Could not load driver:"+e.getMessage());
		}
		try {
			conn = DriverManager.getConnection("jdbc:mysql://"+myConnect.getHost()+"/"
					+myConnect.getDatabase()+"?" + "user="+myConnect.getUsername()
					+"&password="+myConnect.getPassword());

			// Do something with the Connection

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}

	public Connection getConnection() {
		return conn;
	}
}
