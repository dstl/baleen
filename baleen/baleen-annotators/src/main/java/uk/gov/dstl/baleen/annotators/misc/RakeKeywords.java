//Dstl (c) Crown Copyright 2016
package uk.gov.dstl.baleen.annotators.misc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.base.Strings;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultimap;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.types.common.Buzzword;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;

/**
 * Uses the RAKE (Rapid Automatic Keyword Extraction) algorithm to automatically
 * identify keywords in each document.
 * 
 * These keywords will be added as metadata to the document, and optionally can
 * also be added as Buzzwords
 * 
 * Based on the paper 'Automatic keyword extraction from individual documents' by 
 * Stuart Rose, Dave Engel, Nick Cramer and Wendy Cowley.
 * 
 * Optionally, you can choose to stem words prior to scoring, which will address
 * any variability in words caused by plurals, tense, etc.
 * This is an extension from the original paper. Essentially, the annotator maintains
 * a mapping between a stemmed version and the original version of the key phrase,
 * using the stemmed version for scoring and calculations, and then returning the
 * original version when required for output.
 * 
 * @baleen.javadoc
 */
public class RakeKeywords extends BaleenAnnotator {
	/**
	 * Should the extracted keywords be annotated as Buzzwords within the document? 
	 * 
	 * @baleen.config true
	 */
	public static final String PARAM_ADD_BUZZWORDS = "addBuzzwords";
	@ConfigurationParameter(name = PARAM_ADD_BUZZWORDS, defaultValue="true")
	private Boolean addBuzzwords;
	
	/**
	 * The maximum number of keywords to extract.
	 * 
	 * The number of keywords returned will be the smaller of this number and
	 * 1/3 the total possible keywords.
	 * 
	 * If there are a number of keywords with the same score that would take the total
	 * number of keywords over the limit, then all are included.
	 * 
	 * @baleen.config 5
	 */
	public static final String PARAM_MAX_KEYWORDS = "maxKeywords";
	@ConfigurationParameter(name = PARAM_MAX_KEYWORDS, defaultValue="5")
	private Integer maxKeywords;
	
	/**
	 * The stoplist to use. If the stoplist matches one of the enum's provided in
	 * {@link uk.gov.dstl.baleen.resources.SharedStopwordResource#StopwordList}, then
	 * that list will be loaded.
	 * 
	 * Otherwise, the string is taken to be a file path and that file is used.
	 * The format of the file is expected to be one stopword per line.
	 * 
	 * @baleen.config DEFAULT
	 */
	public static final String PARAM_STOPLIST = "stoplist";
	@ConfigurationParameter(name = PARAM_STOPLIST, defaultValue="DEFAULT")
	private String stoplist;
	
	/**
	 * The stemming algorithm to use, as defined in OpenNLP's SnowballStemmer.ALGORITHM enum, e.g. ENGLISH.
	 * If not set, or set to an undefined value, then no stemming will be used
	 * 
	 * @baleen.config
	 */
	public static final String PARAM_STEMMING = "stemming";
	@ConfigurationParameter(name = PARAM_STEMMING, defaultValue = "")
	protected String stemming;
	
	/**
	 * Connection to Stopwords Resource
	 * 
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedStopwordResource
	 */
	public static final String KEY_STOPWORDS = "stopwords";
	@ExternalResource(key = KEY_STOPWORDS)
	private SharedStopwordResource stopwordResource;
	
	private Pattern stopwordPattern;
	private Stemmer stemmer;
	
	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		Collection<String> stopwords;
		
		try{
			stopwords = stopwordResource.getStopwords(SharedStopwordResource.StopwordList.valueOf(stoplist));
		}catch(IllegalArgumentException iae){
			getMonitor().info("Value of {} does not match pre-defined list, assuming value is a file", PARAM_STOPLIST);
			getMonitor().debug("Unable to parse value of {} as StopwordList enum", PARAM_STOPLIST, iae);
			
			File f = new File(stoplist);
			
			try{
				stopwords = stopwordResource.getStopwords(f);
			}catch(IOException ioe){
				throw new ResourceInitializationException(
					new InvalidParameterException("Couldn't load stoplist", ioe)
				);
			}
		}catch(IOException ioe){
			getMonitor().warn("Unable to load Stopword list, resorting to default list", ioe);
			stopwords = stopwordResource.getStopwords();
		}
		
		if(!Strings.isNullOrEmpty(stemming)){
			try{
				ALGORITHM algo = ALGORITHM.valueOf(stemming);
				stemmer = new SnowballStemmer(algo);
			}catch(IllegalArgumentException iae){
				getMonitor().warn("Value of {} does not match pre-defined list, no stemming will be used.", PARAM_STEMMING, iae);
				stemmer = new NoOpStemmer();
			}
		}else{
			 stemmer = new NoOpStemmer();
		}
		
		StringJoiner sj = new StringJoiner("|");
		for(String s : stopwords){
			sj.add(Pattern.quote(s));
		}
		stopwordPattern = Pattern.compile("\\b("+sj.toString()+")\\b", Pattern.CASE_INSENSITIVE);
	}
	
	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		List<StemmedString> candidates = new ArrayList<>();
		
		//The definition of sentence as required by RAKE is different to that used by Baleen,
		//so we can't use the existing Sentence annotation.
		for(String sentence : splitSentences(jCas.getDocumentText())){
			candidates.addAll(generateCandidates(sentence));
		}
		
		Map<StemmedString, Double> scores = calculateScores(candidates);
		Map<StemmedString, Double> keywords = generateKeywordScores(candidates, scores);
		
		Multimap<Double, StemmedString> keywordsByValue = TreeMultimap.create();
		keywords.forEach((k, v) -> keywordsByValue.put(v, k));
		
		Integer numKeywords = Integer.min(maxKeywords, keywords.size()/3);
		
		List<Double> scoreValues = new ArrayList<>(keywordsByValue.keySet());
		Integer index = scoreValues.size() - 1;
		
		List<StemmedString> finalKeywords = new ArrayList<>();
		while(finalKeywords.size() < numKeywords && index >= 0){
			finalKeywords.addAll(keywordsByValue.get(scoreValues.get(index)));
			index--;
		}
		
		Metadata md = new Metadata(jCas);
		md.setKey("keywords");
		md.setValue(finalKeywords.stream().map(s -> s.getOriginalString()).collect(Collectors.joining(";")));
		addToJCasIndex(md);
		
		if(addBuzzwords){
			for(StemmedString keyword : finalKeywords){
				Matcher m = Pattern.compile("\\b"+Pattern.quote(keyword.getOriginalString())+"\\b", Pattern.CASE_INSENSITIVE).matcher(jCas.getDocumentText());
				while(m.find()){
					Buzzword bw = new Buzzword(jCas, m.start(), m.end());
					bw.setTags(UimaTypesUtils.toArray(jCas, Arrays.asList("keyword")));
					addToJCasIndex(bw);
				}
			}
		}
	}
	
	private List<StemmedString> generateCandidates(String sentence){
		String[] candidates = stopwordPattern.split(sentence);
	
		List<StemmedString> normalizedCandidates = new ArrayList<>();
		
		for(String c : candidates){
			if(c.trim().length() > 0){
				String normalized = c.trim().toLowerCase();
				
				normalizedCandidates.add(new StemmedString(normalized, stemmer.stem(normalized)));
			}
		}
		
		return normalizedCandidates;
	}
	
	private Map<StemmedString, Double> calculateScores(List<StemmedString> candidates){
		Map<StemmedString, Integer> degree = new HashMap<>();
		Map<StemmedString, Double> score = new HashMap<>();
		
		Multiset<StemmedString> words = HashMultiset.create();
		
		for(StemmedString candidate : candidates){
			List<StemmedString> splitWords = splitCandidate(candidate);
			Integer listDegree = splitWords.size();
			
			words.addAll(splitWords);
			
			for(StemmedString word : splitWords){
				int currDegree = degree.getOrDefault(word, 0);
				degree.put(word, currDegree + listDegree);
			}
		}
		
		for(StemmedString word : words){
			score.put(word, degree.get(word) / (words.count(word) * 1.0));
		}
		
		return score;
	}
	
	private Map<StemmedString, Double> generateKeywordScores(List<StemmedString> candidates, Map<StemmedString, Double> scores){
		Map<StemmedString, Double> keywords = new HashMap<>();
		
		for(StemmedString candidate : candidates){
			List<StemmedString> splitWords = splitCandidate(candidate);
			Double candidateScore = 0.0;
			
			for(StemmedString word : splitWords){
				candidateScore += scores.getOrDefault(word, 0.0);
			}
			
			keywords.put(candidate, candidateScore);
		}
		
		return keywords;
	}
	
	private List<String> splitSentences(String text){
		String[] sentences = text.split("[-.!?,;:\\n\\t\\\"\\'\\(\\)\u2019\u2013\\\\\\/]");
		
		List<String> returnedSentences = new ArrayList<>();
		for(String sentence : sentences){
			if(sentence.trim().length() > 0){
				returnedSentences.add(sentence.trim().toLowerCase());
			}
		}
		
		return returnedSentences;
	}
	
	private List<StemmedString> splitCandidate(StemmedString candidate){
		String[] splitOrig = candidate.getOriginalString().split("\\s+");
		String[] splitStemmed = candidate.getStemmedString().split("\\s+");
		
		List<StemmedString> split = new ArrayList<>();
		
		for(int i = 0; i < splitOrig.length; i++){
			split.add(new StemmedString(splitOrig[i], splitStemmed[i]));
		}
		
		return split;
	}
	
}

/**
 * A class to hold two versions of a string in parallel - an original version and a stemmed version
 */
class StemmedString implements Comparable<StemmedString>{
	private String strOrig;
	private String strStemmed;
	
	/**
	 * Create a StemmedString from two strings
	 */
	public StemmedString(String orig, String stemmed){
		strOrig = orig;
		strStemmed = stemmed;
	}
	
	/**
	 * Create a StemmedString from one CharSequence (original) and one String (stemmed)
	 */
	public StemmedString(CharSequence orig, String stemmed){
		strOrig = orig.toString();
		strStemmed = stemmed;
	}
	/**
	 * Create a StemmedString from one CharSequence (stemmed) and one String (original)
	 */
	public StemmedString(String orig, CharSequence stemmed){
		strOrig = orig;
		strStemmed = stemmed.toString();
	}
	
	/**
	 * Create a StemmedString from two CharSequences
	 */
	public StemmedString(CharSequence orig, CharSequence stemmed){
		strOrig = orig.toString();
		strStemmed = stemmed.toString();
	}
	
	/**
	 * Get the original string
	 */
	public String getOriginalString(){
		return strOrig;
	}
	
	/**
	 * Get the stemmed string
	 */
	public String getStemmedString(){
		return strStemmed;
	}
	
	@Override
	public String toString(){
		return strStemmed;
	}

	@Override
	public int compareTo(StemmedString s) {
		return strStemmed.compareTo(s.strStemmed);
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof StemmedString || o instanceof String){
			return strStemmed.equals(o.toString());
		}
		
		return false;
	}
	
	@Override
	public int hashCode(){
		return strStemmed.hashCode();
	}
}

/**
 * A no-op Stemmer which returns the same string as it is passed.
 */
class NoOpStemmer implements Stemmer{
	@Override
	public CharSequence stem(CharSequence cs) {
		return cs;
	}
}