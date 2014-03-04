package uk.ac.diamond.modulestats.updater;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModulesDBUpdater {

	private static Logger logger = LoggerFactory.getLogger(ModulesDBUpdater.class);

	/**
	 * args[0]: module load log file path, 
	 * args[1]: connection file path where the file content looks like the following:<br>
	 *        host:hostname<br>
	 *        database:databasename<br>
	 *        user:username<br>
	 *        password:password<br>
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Please enter two arguments where argument1 is the file path" +
					" of the moduleload_log.txt file and the argument2 is the filepath of" +
					" the connection file used to connect to the database.");
			return;
		}
		int currentModulesNumber = 0;
		List<String[]> list = null;
		String lastDateEntry = "";
		// SQL connection to read the current data in the module table and
		// prepare accordingly the data to insert
		System.out.println("Opening connection for reading and preparing data");
		SQLDriverLoader loader = new SQLDriverLoader(args[1]);
		Connection conn = loader.getConnection();
		try {
			Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery("SELECT count(*) FROM dawnstats.moduleload_record;");
			while (result.next()) {
				String str = result.getString(1);
				currentModulesNumber = Integer.valueOf(str);
				System.out.println(currentModulesNumber);
			}
			// read last row of table
			result = statement.executeQuery("SELECT * FROM dawnstats.moduleload_record WHERE id="+currentModulesNumber+";");
			while (result.next()) {
				lastDateEntry = result.getString(2);
				System.out.println(lastDateEntry);
			}
			//read module log file
			LogReader log = new LogReader(args[0]);
			list = log.read(currentModulesNumber);
			//close connection
			conn.close();
			System.out.println("Connection closed");
		} catch (SQLException e) {
			logger.error("Error during query:" +e.getMessage());
			System.out.println("Error during query:" +e.getMessage());
			e.printStackTrace();
			return;
		}

		if (list == null) {
			logger.error("No data to insert in the database");
			System.out.println("No data to insert in the database");
			return;
		}
		//list: string[0]: date, string[1]:fedid, string[2]:workstation , string[3]:module load , string[4]:release
		// create a new connection for the insertion
		SQLDriverLoader updateloader = new SQLDriverLoader(args[1]);
		System.out.println("Opening connection for insertion of data");
		Connection updateconn = updateloader.getConnection();
		try {
			String insertTableSQL = "INSERT INTO dawnstats.moduleload_record"
					+ "(id, time, user_id, workstation, module_load, release_version) VALUES"
					+ "(?,?,?,?,?,?)";
			PreparedStatement preparedStatement = updateconn.prepareStatement(insertTableSQL);
			int i = 0;
			for (String[] line : list) {
				//increment the id
				currentModulesNumber++;
				//id
				preparedStatement.setInt(1, currentModulesNumber);
				//date
				preparedStatement.setString(2, line[0]);
				//userid
				preparedStatement.setString(3, line[1]);
				//workstation
				preparedStatement.setString(4, line[2]);
				//module load
				preparedStatement.setString(5, line[3]);
				//release
				preparedStatement.setString(6, line[4]);

				// execute insert SQL stetement
				preparedStatement .executeUpdate();
				i++;
			}
			logger.debug(i + " rows have been inserted");
			System.out.println(i + " rows have been inserted");
			//close connection
			updateconn.close();
			System.out.println("Connection closed");
		} catch (SQLException e) {
			logger.error("Error during insertion:" +e.getMessage());
			System.out.println("Error during insertion:" +e.getMessage());
			e.printStackTrace();
		}
	}
}
