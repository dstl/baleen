//Dstl (c) Crown Copyright 2017
//Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.utils;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class to hold a singleton classpath scanner,
 * which will speed up use of reflection calls by the Web API
 */
public class ReflectionUtils {
	private static FastClasspathScanner scanner = null;
	private static ScanResult scanResult = null;

	private ReflectionUtils(){
		//Private constructor
	}

	/**
	 * Return the singleton instance of the classpath scanner object
	 */
	public static ScanResult getInstance(){
		if(scanner == null) {
			scanner = new FastClasspathScanner();
		}

		if(scanResult == null){
			scanResult = scanner.scan();
		}

		return scanResult;
	}

	/**
	 * Return a set of sub types for the given super type.
	 * Scans the full classpath.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Set<Class<? extends T>> getSubTypes(Class<T> superType){
		if(scanResult == null)
			getInstance();

		List<String> classNames;
		if(superType.isInterface()) {
			classNames = scanResult.getNamesOfClassesImplementing(superType);
		}else {
			classNames = scanResult.getNamesOfSubclassesOf(superType);
		}

		Set<Class<? extends T>> ret = new HashSet<>();
		scanResult.classNamesToClassRefs(classNames).forEach(c -> ret.add((Class<? extends T>)c));
		return ret;
	}

	/**
	 * Return a set of sub types for the given super type.
	 * Scans only the given package.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Set<Class<? extends T>> getSubTypes(String packageName, Class<T> superType){
		FastClasspathScanner scanner = new FastClasspathScanner(packageName);
		ScanResult sr = scanner.scan();

		List<String> classNames;
		if(superType.isInterface()) {
			classNames = sr.getNamesOfClassesImplementing(superType);
		}else {
			classNames = sr.getNamesOfSubclassesOf(superType);
		}

		Set<Class<? extends T>> ret = new HashSet<>();
		sr.classNamesToClassRefs(classNames).forEach(c -> ret.add((Class<? extends T>)c));
		return ret;
	}
}