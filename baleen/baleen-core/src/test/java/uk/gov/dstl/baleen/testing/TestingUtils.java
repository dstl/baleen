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
		File[] files = dir.listFiles();
		
		if(files != null){
			for (File f : files) {
				f.delete();
			}
			dir.delete();
		}
		return !dir.exists();
	}
}
