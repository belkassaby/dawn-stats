/*-
 * Copyright 2014 Diamond Light Source Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.diamond.modulestats.updater;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import uk.ac.diamond.modulestats.updater.logger.MyLogger;

public class ModulesDBUpdater {

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

		new MyLogger();

		if (args.length != 2) {
			System.out.println("Please enter two arguments where argument1 is the file path" +
					" of the moduleload_log.txt file and the argument2 is the filepath of" +
					" the connection file used to connect to the database.");
			return;
		}
		int currentModulesNumber = 0;
		List<List<String>> list = null;
//		String lastDateEntry = "";
		// SQL connection to read the current data in the module table and
		// prepare accordingly the data to insert
//		System.out.println("Opening connection for reading and preparing data");
		SQLDriverLoader loader = new SQLDriverLoader(args[1]);
		Connection conn = loader.getConnection();
		try {
			Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery("SELECT count(*) FROM " + loader.getDatabaseName() +".moduleload_record;");
			while (result.next()) {
				String str = result.getString(1);
				currentModulesNumber = Integer.valueOf(str);
				System.out.println("Last module in DB: " + currentModulesNumber);
			}
			// read last row of table
//			result = statement.executeQuery("SELECT * FROM dawnstats.moduleload_record WHERE id="+currentModulesNumber+";");
//			while (result.next()) {
//				lastDateEntry = result.getString(2);
//				//System.out.println(lastDateEntry);
//			}
			//read module log file
			LogReader log = new LogReader(args[0]);
			list = log.read(currentModulesNumber);
			//close connection
			conn.close();
//			System.out.println("Connection closed");
		} catch (SQLException e) {
			System.out.println("Error during query:" +e.getMessage());
			e.printStackTrace();
			return;
		}

		if (list == null) {
			System.out.println("No data to insert in the database");
			return;
		}
		//list: string[0]: date, string[1]:fedid, string[2]:workstation , string[3]:module load , string[4]:release
		// create a new connection for the insertion
		SQLDriverLoader updateloader = new SQLDriverLoader(args[1]);
//		System.out.println("Opening connection for insertion of data");
		Connection updateconn = updateloader.getConnection();
		try {
			String insertTableSQL = "INSERT INTO " + updateloader.getDatabaseName() + ".moduleload_record"
					+ "(id, time, user_id, workstation, module_load, release_version) VALUES"
					+ "(?,?,?,?,?,?)";
			PreparedStatement preparedStatement = updateconn.prepareStatement(insertTableSQL);
			int i = 0;
			for (List<String> line : list) {
				//increment the id
				currentModulesNumber++;
				//id
				preparedStatement.setInt(1, currentModulesNumber);
				//date
				preparedStatement.setString(2, line.get(0));
				//userid
				preparedStatement.setString(3, line.get(1));
				//workstation
				preparedStatement.setString(4, line.get(2));
				//module load
				preparedStatement.setString(5, line.get(3));
				//release
				preparedStatement.setString(6, line.get(4));

				// execute insert SQL stetement
				preparedStatement .executeUpdate();
				i++;
			}
			System.out.println(i + " rows have been inserted");
			//close connection
			updateconn.close();
//			System.out.println("Connection closed");
		} catch (SQLException e) {
			System.out.println("Error during insertion:" +e.getMessage());
			e.printStackTrace();
		}
	}

}
