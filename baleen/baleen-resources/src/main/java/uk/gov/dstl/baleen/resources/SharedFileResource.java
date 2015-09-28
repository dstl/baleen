//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.util.FileUtils;

import uk.gov.dstl.baleen.uima.BaleenResource;

/**
 * A shared resource that can be used to read any file. This primarily exists as
 * a shared resource rather than a helper so that it can be passed into
 * IGazetteers, but could implement some shared logic such as Tika extraction in
 * the future.
 * 
 * 
 */
public class SharedFileResource extends BaleenResource {
	/** Read an entire file into a string, ignoring any leading or trailing whitespace.
	 * 
	 * @param file to load
	 * @return the content of the file as a string.
	 * @throws IOException on error reading or accessing the file.
	 */
	public static String readFile(File file) throws IOException {
		String contents = FileUtils.file2String(file);
		contents = contents.replaceAll("\r\n", "\n");
		return StringUtils.strip(contents);
	}

	/**
	 * Read the file and return all the lines, with any leading or trailing 'empty' lines omitted.
	 * Lines that consist solely of whitespace are assumed to be empty.
	 * Lines are trimmed of any leading or trailing whitespace as they are read.
	 * 
	 * Implemented as per BufferedReader.
	 * 
	 * @param the file to load
	 * @return non-null, but potentially empty, array of string (one line per string)
	 * @throws IOException on error accessing or reading from the file.
	 */
	public static String[] readFileLines(File file) throws IOException {
		List<String> lines = new LinkedList<>();
		Files.lines(file.toPath()).forEach(l -> lines.add(StringUtils.strip(l.replaceAll("\r\n","\n"))));
		
		while(StringUtils.strip(lines.get(0)).isEmpty()){
			lines.remove(0);
		}
		while(StringUtils.strip(lines.get(lines.size() - 1)).isEmpty()){
			lines.remove(lines.size() - 1);
		}

		return lines.toArray(new String[lines.size()]);
	}
}
