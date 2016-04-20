package uk.gov.dstl.baleen.resources.documentchecker;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemChecker extends UriChecker {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemChecker.class);
	
	@Override
	protected boolean canCheck(String uri) {
		return uri.startsWith("file:/") || uri.startsWith("/") || 
			   uri.matches("^[a-zA-Z]:\\\\.*$") || uri.matches("^[a-zA-Z]:/.*$") || uri.startsWith("\\\\");
	}

	@Override
	protected void checkExists(String uri) {
		LOGGER.debug("Check {}", uri);
		if (!exists(uri)) {
			checker.documentRemoved(uri);
		}
	}

	/**
	 * Check that a file exists
	 * @param uri
	 * @return true if file exists
	 */
	private boolean exists(String uri) {
		if (uri.startsWith("file:/")) {
			try {
				return new File(new URI(uri).getPath()).exists();
			} catch (URISyntaxException e) {
				return false;
			}
		}
		return new File(uri).exists();
	}
}
