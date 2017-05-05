//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.misc.helpers.AbstractRootWordAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.military.MilitaryPlatform;

/**
 * This class attempts to identify generically described military platforms
 * 
 * Even though this class extends {@link AbstractRootWordAnnotator}, it overrides
 * the sentence processing logic to allow for phrases with up to two words (e.g. aircraft carrier)
 * 
 * @baleen.javadoc
 */
public class GenericMilitaryPlatform extends AbstractRootWordAnnotator<MilitaryPlatform> {
	//Define list of words, which can be either one or two words
	//Two word phrases should not contain words from one word phrases
	protected static final List<String> GROUND = Arrays.asList("tank", "armoured vehicle", "armoured vehicle", "humvee", "military vehicle", "tactical vehicle");
	protected static final List<String> NAVAL = Arrays.asList("aircraft carrier", "assault ship", "frigate", "destroyer", "submarine", "minesweeper", "warship");
	protected static final List<String> AIR = Arrays.asList("attack aircraft", "attack helicopter", "drone", "fighter jet", "fighter plane", "uav", "warplane");
	
	@Override
	protected void processSentence(JCas jCas, Collection<WordToken> wordTokens){
		WordToken wtPrev = null;
		
		for(WordToken wt : wordTokens){
			String word = wt.getCoveredText().toLowerCase();
			
			String platformType = isEntity(word);
			if(platformType != null){
				createMilitaryPlatform(jCas, wt.getBegin(), wt.getEnd(), platformType);
			}else if(wtPrev != null){
				String words = wtPrev.getCoveredText().toLowerCase() + " " + wt.getCoveredText().toLowerCase();
				platformType = isEntity(words);
				
				if(platformType != null){
					createMilitaryPlatform(jCas, wtPrev.getBegin(), wt.getEnd(), platformType);
				}
			}
			
			wtPrev = wt;
		}
	}
	
	@Override
	protected String isEntity(String word) {
		String ret = null;
		String singular = word;
		if(word.endsWith("s")){
			singular = singular.substring(0,  singular.length() - 1);
		}
		
		if(GROUND.contains(singular)){
			ret = "GROUND";
		}else if(NAVAL.contains(singular)){
			ret = "NAVAL";
		}else if(AIR.contains(singular)){
			ret = "AIR";
		}
		
		return ret;
	}

	@Override
	protected boolean isDescriptiveWord(String word) {
		// No descriptive words for military platforms
		return false;
	}

	private void createMilitaryPlatform(JCas jCas, Integer begin, Integer end, String type){
		MilitaryPlatform mp = createEntity(jCas);
		
		mp.setBegin(begin);
		mp.setEnd(end);
		mp.setSubType(type);
		
		addToJCasIndex(mp);
	}
	
	@Override
	protected MilitaryPlatform createEntity(JCas jCas) {
		return new MilitaryPlatform(jCas);
	}
	
	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(ImmutableSet.of(Sentence.class, WordToken.class), ImmutableSet.of(MilitaryPlatform.class));
	}
}