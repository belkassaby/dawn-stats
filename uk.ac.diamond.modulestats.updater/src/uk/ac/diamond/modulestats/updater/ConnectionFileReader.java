package uk.ac.diamond.modulestats.updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionFileReader {

	private Logger logger = LoggerFactory.getLogger(ConnectionFileReader.class);
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
			logger.error("IOException: %s%n", x);
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
