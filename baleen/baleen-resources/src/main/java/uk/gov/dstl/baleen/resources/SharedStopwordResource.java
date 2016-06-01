//Dstl (c) Crown Copyright 2016
package uk.gov.dstl.baleen.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import uk.gov.dstl.baleen.uima.BaleenResource;

/**
 * Shared resource for providing access to lists of common stop words.
 */
public class SharedStopwordResource extends BaleenResource {
	private Set<String> stopwords = new HashSet<>();
	
	/**
	 * The available stopword lists.
	 * 
	 * <ul>
	 * <li>DEFAULT and SMART -  SMART (Salton, 1971).  Available at ftp://ftp.cs.cornell.edu/pub/smart/english.stop</li>
	 * <li>FOX - "A stop list for general text" Fox 1989</li>
	 * <li>RANKS_NL - A list of stopwords, taken from http://www.ranks.nl/stopwords</li>
	 * <li>LONG - A long list of stopwords, taken from http://www.ranks.nl/stopwords</li>
	 * <li>MYSQL - The list of stop words used by the full text search in MySQL</li>
	 * </ul>
	 */
	public enum StopwordList{
		DEFAULT,
		SMART,
		FOX,
		RANKS_NL,
		LONG,
		MYSQL
	}
	
	@Override
	protected boolean doInitialize(ResourceSpecifier specifier, Map<String, Object> additionalParams) throws ResourceInitializationException{
		try{
			stopwords = loadStoplist("SmartStoplist.txt");
		}catch(IOException ioe){
			getMonitor().error("Unable to read default stop words from SmartStoplist.txt", ioe);
			throw new ResourceInitializationException(ioe);
		}
		
		return true;
	}
	
	@Override
	protected void doDestroy() {
		stopwords = null;
	}
	
	private Set<String> loadStoplist(String name) throws IOException{
		Set<String> sw = new HashSet<>();
		try(
			InputStream is = getClass().getResourceAsStream("stoplists/"+name);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
		){
			
			reader.lines().filter(s -> !s.startsWith("#")).forEach(s -> sw.add(s.trim().toLowerCase()));
		}
		
		return sw;
	}
	
	/**
	 * Returns a lower case list of stopwords loaded by this resource,
	 * using the default stopword list
	 */
	public Collection<String> getStopwords(){
		return stopwords;
	}
	
	/**
	 * Returns a lower case list of stopwords loaded by this resource,
	 * using the specified stopword list
	 */
	public Collection<String> getStopwords(StopwordList list) throws IOException{
		if (list == StopwordList.FOX){
			return loadStoplist("FoxStoplist.txt");
		}else if (list == StopwordList.RANKS_NL){
			return loadStoplist("RanksNlStoplist.txt");
		}else if (list == StopwordList.LONG){
			return loadStoplist("LongStoplist.txt");
		}else if (list == StopwordList.MYSQL){
			return loadStoplist("MySqlStoplist.txt");
		}
		
		return stopwords;
	}
	
	/**
	 * Returns a lower case list of stopwords loaded by this resource,
	 * using a custom stopword list
	 */
	public Collection<String> getStopwords(File list) throws IOException{
		Set<String> sw = new HashSet<>();
		
		try(
			InputStream is = new FileInputStream(list);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
		){
			reader.lines().filter(s -> !s.startsWith("#") && s.trim().length() > 0).forEach(s -> sw.add(s.trim().toLowerCase()));
		}
		
		return sw;
	}
}
