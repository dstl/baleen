package uk.gov.dstl.baleen.core.utils;

import org.reflections.Reflections;

/**
 * Utility class to hold a singleton Reflections object,
 * which will speed up use of reflections by the Web API
 */
public class ReflectionUtils {
	private static Reflections reflections = null;
	
	private ReflectionUtils(){
		//Private constructor
	}
	
	/**
	 * Return the singleton instance of the Reflections object
	 */
	public static Reflections getInstance(){
		if(reflections == null)
			reflections = new Reflections();
		
		return reflections;
	}
}
