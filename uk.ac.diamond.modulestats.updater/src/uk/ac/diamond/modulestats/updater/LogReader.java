package uk.ac.diamond.modulestats.updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class LogReader {
	
	private String filepath;

	public LogReader(String filepath) {
		this.filepath = filepath;
	}

	/**
	 * Returns a List of String arrays with data starting after linestart index
	 * @param linestart
	 * @return List<String[]>
	 */
	public List<String[]> read(int linestart) {
		List<String[]> newModules = new ArrayList<String[]>();
		File file = new File(filepath);
		Charset charset = Charset.forName("UTF-8");
		try (BufferedReader reader = Files.newBufferedReader(file.toPath(), charset)) {
			String line = null;
			int linenumber = 0;
			while ((line = reader.readLine()) != null) {
				linenumber++;
				if (linenumber > linestart+1) { //the original module log file has a line for column headers
					line = line.replaceAll("\\s+", " ").trim();
					String[] array = line.split("\\s");
					newModules.add(array);
				}
			}
			// close burrered reader
			reader.close();
			return newModules;
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
			return null;
		}
	}

}
