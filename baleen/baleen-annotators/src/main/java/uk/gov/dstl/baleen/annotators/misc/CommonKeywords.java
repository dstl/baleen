package uk.gov.dstl.baleen.annotators.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;
import uk.gov.dstl.baleen.annotators.misc.helpers.AbstractKeywordsAnnotator;
import uk.gov.dstl.baleen.annotators.misc.helpers.NoOpStemmer;

/**
 * This annotator attempts to identify keywords using the following process:
 * 1) Split document by stop words
 * 2) For each remaining word and/or phrase produce n-grams up to a maximum length
 * 3) Stem each n-gram
 * 4) Count the occurrences of each stemmed n-gram, weighting the count based on n-gram length
 * 5) Select the most commonly occurring n-grams
 * 6) Convert back to the original words
 */
public class CommonKeywords extends AbstractKeywordsAnnotator {
	/**
	 * The maximum n-gram length
	 * 
	 * @baleen.config 3
	 */
	public static final String PARAM_NGRAM_LENGTH = "ngram";
	@ConfigurationParameter(name = PARAM_NGRAM_LENGTH, defaultValue = "3")
	protected Integer maxLength;
	
	/**
	 * The stemming algorithm to use, as defined in OpenNLP's SnowballStemmer.ALGORITHM enum, e.g. ENGLISH.
	 * If not set, or set to an undefined value, then no stemming will be used
	 * 
	 * @baleen.config ENGLISH
	 */
	public static final String PARAM_STEMMING = "stemming";
	@ConfigurationParameter(name = PARAM_STEMMING, defaultValue = "ENGLISH")
	protected String stemming;
	
	private Stemmer stemmer;
	private String stopwordPattern;
	
	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		super.doInitialize(aContext);
		
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
		
		stopwordPattern = buildStopwordsPattern("[-.!?0-9]").pattern();
	}
	
	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		List<String> phrases = Arrays.asList(jCas.getDocumentText().toLowerCase().split(stopwordPattern));
		
		phrases = phrases.stream().filter(s -> s.length() > 0).collect(Collectors.toList());
		
		Map<String, Double> stemCount = new HashMap<>();
		Map<String, Integer> wordCount = new HashMap<>();
		Multimap<String, String> stemToWord = HashMultimap.create();
		
		for(String phrase : phrases){			
			String[] terms = phrase.split("\\s+");
			
			for(int i = 0; i < terms.length; i++){
				StringJoiner sjStem = new StringJoiner(" ");
				StringJoiner sjOrig = new StringJoiner(" ");
				for(int j = 0; j < maxLength && i + j < terms.length; j++){
					String origTerm = terms[i + j].replaceAll("^[-,\"\\(\\)':;]+", "").replaceAll("[-,\"\\(\\)':;]+$", "");
					String term = stemmer.stem(origTerm.trim().replaceAll("[^a-z]", "")).toString();
					
					if(term.length() == 0)
						break;
					
					sjStem.add(term);
					sjOrig.add(origTerm);
					
					Double weight = 1.0 + j/Math.max(1.0, maxLength - 1.0);	//Boost the score of longer words
					
					String key = sjStem.toString();
					Double dVal = stemCount.getOrDefault(key, 0.0);
					stemCount.put(key, dVal + weight);
					
					String origKey = sjOrig.toString();
					Integer iVal = wordCount.getOrDefault(origKey, 0);
					wordCount.put(origKey, iVal + 1);
					
					stemToWord.put(key, origKey);
				}
			}
		}
		
		stemCount.remove("");
		
		Multimap<Double, String> countToStem = HashMultimap.create();
		Set<Double> countValues = new TreeSet<>(Collections.reverseOrder());
		
		for(String key : stemCount.keySet()){
			Double count = stemCount.get(key).doubleValue();
			
			countToStem.put(count, key);
			countValues.add(count);
		}
		
		List<String> stemmedKeywords = new ArrayList<>();
		for(Double d : countValues){
			stemmedKeywords.addAll(countToStem.get(d));
			
			if(stemmedKeywords.size() >= maxKeywords)
				break;
		}
		
		unstemAndAddKeywords(jCas, stemmedKeywords, stemToWord, wordCount);
	}

	private void unstemAndAddKeywords(JCas jCas, List<String> stemmedKeywords, Multimap<String, String> stemToWord, Map<String, Integer> wordCount){
		List<String> selectedKeywords = new ArrayList<>();
		List<String> additionalKeywords = new ArrayList<>();
		
		for(String stemmed : stemmedKeywords){
			Collection<String> keywords = stemToWord.get(stemmed);
			String bestKeyword = selectBestUnstemmedWord(keywords, wordCount);
			
			additionalKeywords.addAll(keywords);
			
			selectedKeywords.add(bestKeyword);
			additionalKeywords.remove(bestKeyword);
		}
		
		addKeywordsToJCas(jCas, selectedKeywords, additionalKeywords);
	}
	
	private String selectBestUnstemmedWord(Collection<String> keywords, Map<String, Integer> wordCount){
		String bestKeyword = "";
		Integer bestCount = 0;
		
		for(String keyword : keywords){
			Integer count = wordCount.get(keyword);
			if(count > bestCount){
				bestCount = count;
				bestKeyword = keyword;
			}
		}
		
		return bestKeyword;
	}
	
}
