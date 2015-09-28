//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.testing;

import java.io.File;

public class TestingUtils {
	
	private TestingUtils() {
		// Do nothing
	}

	public static boolean deleteDirectory(String directory) {
		return deleteDirectory(new File(directory));
	}

	public static boolean deleteDirectory(File dir) {
		for (File f : dir.listFiles()) {
			f.delete();
		}
		dir.delete();
		return !dir.exists();
	}
}
