package uk.ac.diamond.modulestats.updater;

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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class used to read the log file where a line is appended everytime a DAWN session is opened.
 * A line is made of the following columns: datetime, userid, workstation, module load, version and release
 * @author wqk87977
 *
 */
public class LogReader {

	private String filepath;

	public LogReader(String filepath) {
		this.filepath = filepath;
	}

	/**
	 * Returns a List of List of Strings with data starting after linestart index
	 * @param linestart
	 * @return List<String[]>
	 */
	public List<List<String>> read(int linestart) {

		List<List<String>> newDAWNStarts = new ArrayList<List<String>>();
		File file = new File(filepath);
		Charset charset = Charset.forName("UTF-8");
		try (BufferedReader reader = Files.newBufferedReader(file.toPath(), charset)) {
			String line = null;
			int linenumber = 0;
			while ((line = reader.readLine()) != null) {
				linenumber++;
				//the original module log file has a line for column headers
				if (line.length() > 0 && linenumber > linestart+1) {
					line = line.replaceAll("\\s+", " ").trim();
					String[] array = line.split("\\s");
					ArrayList<String> lineElements = new ArrayList<String>(Arrays.asList(array));
					// if the line contains the version, we dismiss it (the version)
					if (array.length == 6) {
						//remove the version from the line
						lineElements.remove(4);
					} else if (array.length == 4 && array[3].startsWith("/dls_sw")) {
						// if the line is missing the module load, we add "N/A" by default
						lineElements.add(3, "N/A");
					}
					newDAWNStarts.add(lineElements);
				}
			}
			// close buffered reader
			reader.close();
			return newDAWNStarts;
		} catch (IOException x) {
			System.out.format("IOException: %s%n", x);
			return null;
		}
	}

}

