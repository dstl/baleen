//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.grammatical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.atteo.evo.inflector.English;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Find noun phrases that contain a word that might indicate it's an location.
 */
public class NPLocation extends BaleenAnnotator {
	private static final List<String> LOCATION_KEYWORDS = Arrays.asList(
			"continent", "america", "europe", "africa", "asia", "australasia", "oceania",
			"country", "nation", "county", "state", "province", "region", "area", "neighbourhood", "neighborhood", "territory",
			"capital", "city", "town", "vilage", "hamlet", "suburb",
			"river", "stream", "lake", "pond", "ocean", "sea", "beach", "coast",
			"mountain", "valley", "hill",
			"field", "farm",
			"church", "cathedral", "mosque", "synagogue", "synagog",
			"shop", "shopping centre", "shopping center", "mall", "market",
			"business park", "science park",
			"university", "college", "campus", "school", "nursery", "library",
			"street", "road", "motorway", "car park", "petrol station", "gas station",
			"bridge", "tunnel", 
			"house", "home", "flat", "apartment", "compound", "castle", "hotel",
			"hospital", "surgery", "clinic",
			"station", "airport",
			"barracks", "check point", "checkpoint", "out post", "outpost", "front line",
			"building", "room",
			"restaurant", "cafe", "café", "diner",
			"park",
			"camp", "campsite", "tent",
			"factory", "warehouse", "facility"
		);
	
	private List<String> locationsInclPlurals;
	
	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		locationsInclPlurals = new ArrayList<>(LOCATION_KEYWORDS.size() * 2);
		
		for(String loc : LOCATION_KEYWORDS){
			locationsInclPlurals.add(loc);
			locationsInclPlurals.add(English.plural(loc));
		}
	}
	
	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		for(PhraseChunk chunk : JCasUtil.select(jCas, PhraseChunk.class)){
			if(!"NP".equals(chunk.getChunkType()))
				continue;
			
			processNP(jCas, chunk);
		}
	}
	
	/**
	 * Process a noun phrase to find entities
	 */
	public void processNP(JCas jCas, PhraseChunk chunk){
		String coveredText = chunk.getCoveredText().toLowerCase();
		
		for(String loc : locationsInclPlurals){
			if(coveredText.contains(loc)){
				//Check that word isn't part of another word
				Pattern p = Pattern.compile("\\b"+loc+"\\b", Pattern.CASE_INSENSITIVE);

				Matcher m = p.matcher(coveredText);
				if(m.find()){
					Location l = new Location(jCas);
					l.setBegin(chunk.getBegin());
					l.setEnd(chunk.getEnd());
					addToJCasIndex(l);
					return;	//Phrase chunk shouldn't contain more than one location
				}
			}
		}
	}
	
	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(ImmutableSet.of(PhraseChunk.class), ImmutableSet.of(Location.class));
	}

}