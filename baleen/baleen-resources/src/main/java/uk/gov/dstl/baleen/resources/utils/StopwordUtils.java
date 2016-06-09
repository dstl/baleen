package uk.gov.dstl.baleen.resources.utils;

import java.util.Collection;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/**
 * Helper methods for working with stopwords
 */
public class StopwordUtils {
	/**
	 * Private constructor as this is a helper class that shouldn't be instantiated
	 */
	private StopwordUtils(){
		//Do nothing
	}
	
	/**
	 * Build a regular expression that will match any stopword
	 */
	public static Pattern buildStopwordPattern(Collection<String> stopwords, Boolean caseSensitive){
		StringJoiner sj = new StringJoiner("|");
		
		for(String s : stopwords){
			sj.add(Pattern.quote(s));
		}
		
		if(caseSensitive){
			return Pattern.compile("\\b("+sj.toString()+")\\b");
		}else{
			return Pattern.compile("\\b("+sj.toString()+")\\b", Pattern.CASE_INSENSITIVE);
		}
	}
	
	/**
	 * Returns true if word is a stopword
	 */
	public static boolean isStopWord(String word, Collection<String> stopwords, Boolean caseSensitive) {
		if(!caseSensitive){
			return stopwords.stream().filter(s -> s.equalsIgnoreCase(word)).count() >= 1;
		}else{
			return stopwords.contains(word);
		}
	}
}
