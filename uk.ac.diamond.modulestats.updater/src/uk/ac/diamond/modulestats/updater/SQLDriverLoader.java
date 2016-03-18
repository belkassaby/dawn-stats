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
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLDriverLoader {

	private Connection conn = null;
	private String databaseName;

	public SQLDriverLoader(String connectionfilepath) {
		//Retrieve the connection info
		ConnectionFileReader myConnect = new ConnectionFileReader(connectionfilepath);
		setDatabaseName(myConnect.getDatabase());
		try {
			Class.forName("com.mysql.jdbc.Driver");
			//System.out.println("Driver loaded");
		} catch (ClassNotFoundException e) {
			System.out.println("Could not load driver:"+e.getMessage());
		}
		try {
			conn = DriverManager.getConnection("jdbc:mysql://"+myConnect.getHost()+"/"
					+databaseName+"?" + "user="+myConnect.getUsername()
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

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
}
