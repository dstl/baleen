//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers.utils;

import java.io.File;

import org.apache.commons.lang.StringUtils;

/** Helper for dealing with sourceUrl. 
 * 
 * 
 *
 */
public class SourceUtils {
	private SourceUtils() {
		// Singleton
	}
	
	/** Convert a url into a directory structure to save a file to.
	 * 
	 * This is a very basic implementation and will likely need to be made more robust to different
	 * url types.
	 * 
	 * @param basePath the parent to save into
	 * @param url the url (to save as)
	 * @return
	 */
	public static final File urlToFile(File basePath, String url) {
		String subUrl;
		if(url.startsWith("http") || url.startsWith("ftp")) {
			// Looks lie an actual URL
			int indexOf = url.indexOf("//");
			if(indexOf == -1) {
				subUrl = url;
			} else {
				subUrl = url.substring(indexOf+2);
			}
		} else if(url.startsWith("\\\\")) {
			// Looks like a network path
			subUrl = url.substring(2);
		} else if(url.length() > 2 && url.charAt(1) == ':') {
			// Looks like C:\
			subUrl = url.substring(2);
		} else {
			// Just use the raw
			subUrl = url;
		}
		
		subUrl = subUrl.replaceAll("\\\\+", "/")
				.replaceAll(File.pathSeparator, "/");
		
		subUrl = subUrl.replace("/", File.separator);
		
		subUrl = StringUtils.strip(subUrl, File.separator);
		
		if(basePath != null) {
			return new File(basePath, subUrl);
		} else {
			return new File(subUrl);
		}
	}
}
