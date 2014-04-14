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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * Class used to load the file containing the Database login information
 * @author wqk87977
 *
 */
public class ConnectionFileReader {

	private String username;
	private String password;
	private String host;
	private String database;

	/**
	 * A Connection file looks like the following:<br>
	 *        host=hostname<br>
	 *        database=databasename<br>
	 *        user=username<br>
	 *        password=password<br>
	 * #is for comments
	 * @param filepath
	 */
	public ConnectionFileReader(String filepath) {

		File file = new File(filepath);
		Charset charset = Charset.forName("UTF-8");
		try (BufferedReader reader = Files.newBufferedReader(file.toPath(), charset)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.contains("#")) {
					if (line.contains("host")) {
						host = line.split("=")[1];
					} else if (line.contains("database")) {
						database = line.split("=")[1];
					} else if (line.contains("user")) {
						username = line.split("=")[1];
					} else if (line.contains("password")) {
						password = line.split("=")[1];
					}
				}
			}
			// close buffered reader
			reader.close();
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getHost() {
		return host;
	}

	public String getDatabase() {
		return database;
	}
}
