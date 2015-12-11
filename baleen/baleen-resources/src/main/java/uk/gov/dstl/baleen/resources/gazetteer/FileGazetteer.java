//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.resources.gazetteer;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.uima.resource.Resource;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.resources.SharedFileResource;

/**
 * Read from a file as the back-end of a gazetteer
 * 
 * 
 */
public class FileGazetteer extends AbstractMultiMapGazetteer<Integer> {
	public static final String CONFIG_FILE = "fileName";
	public static final String CONFIG_TERM_SEPARATOR = "termSeparator";

	private File file;
	private String termSeparator = ",";


	/**
	 * Configure a new instance of FileGazetteer. The following config parameters are expected/allowed:
	 * <ul>
	 * <li><b>fileName</b> - What file are we using for the gazetteer; defaults to gazetteer.txt</li>
	 * <li><b>termSeparator</b> - The string that separates aliases of the same entity on a single line in the gazetteer. Defaults to ","</li>
	 * </ul>
	 * 
	 * @param connection A SharedFileResource object to read the file with
	 * @param config A map of additional configuration options
	 */
	@Override
	public void init(Resource connection, Map<String, Object> config) throws BaleenException {
		if (config.containsKey(CONFIG_FILE)) {
			file = new File(config.get(CONFIG_FILE).toString());
		} else {
			file = new File("gazetteer.txt");
		}

		if (!file.exists() || !file.canRead()) {
			throw new InvalidParameterException("Unable to read file " + file.getPath());
		}

		super.init(connection, config);
	}


	@Override
	public void destroy() {
		file = null;
		
		super.destroy();
	}

	@Override
	public void reloadValues() throws BaleenException {
		reset();

		String[] content;
		try {
			content = SharedFileResource.readFileLines(file);
		} catch (IOException e) {
			throw new BaleenException(e);
		}
		int lineNumber = 0;
		for (String line : content) {
			lineNumber++;
			if (line.trim().isEmpty()) {
				continue;
			}

			if (!caseSensitive) {
				line = line.toLowerCase();
			}

			String[] termsArray = line.split(termSeparator);
			for (String t : termsArray) {
				if (t.trim().isEmpty()) {
					continue;
				}

				addTerm(lineNumber, t.trim());
			}
		}
	}

}
