//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex.helpers;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/** An abstract base class for building RegexAnnotators that only act on Noun Phrases.
 * If no noun phrases are found, then this annotator will act entirely on the Regex - unless the document is entirely upper case in which case no results are returned.
 * 
 * Implement create and pass in the regex definition to the contractor.
 * 
 * 
 * 
 *
 * @param <T> the type of entity produced.
 */
public abstract class AbstractRegexNPAnnotator<T extends Entity> extends BaleenAnnotator {
	
	private Pattern pattern;
	private double confidence;
	private int matcherGroup;

	/** New instance, based on the supplied pattern. Uses the whole matched regex as the entity text.
	 * @param pattern the regex pattern
	 * @param caseSensitive should this be treated a case sensitive
	 * @param confidence the confidence to assign to created entities.
	 */
	protected AbstractRegexNPAnnotator(String pattern, boolean caseSensitive, double confidence) {
		this(Pattern.compile(pattern, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE), 0, confidence);
	}
	
	/** New instance, based on a regex pattern. Uses the whole matched regex as the entity text.
	 * @param pattern the regex pattern
	 * @param confidence the confidence to assign to created entities.
	 */
	protected AbstractRegexNPAnnotator(Pattern pattern, double confidence) {
		this(pattern, 0, confidence);
	}
	
	/** New instance, based on the supplied pattern.
	 * @param pattern the regex pattern
	 * @param matcherGroup the matcher group to use as the content of the entity
	 * @param caseSensitive should this be treated a case sensitive
	 * @param confidence the confidence to assign to created entities.
	 */
	protected AbstractRegexNPAnnotator(String pattern, int matcherGroup, boolean caseSensitive, double confidence) {
		this(Pattern.compile(pattern, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE), matcherGroup, confidence);
	}
	
	/** New instance, based on a regex pattern.
	 * @param pattern the regex pattern
	 * @param matcherGroup the matcher group to use as the content of the entity 
	 * @param confidence the confidence to assign to created entities.
	 */
	protected AbstractRegexNPAnnotator(Pattern pattern, int matcherGroup, double confidence) {
		this.pattern = pattern;
		this.matcherGroup = matcherGroup;
		this.confidence = confidence;
	}
	
	
	/** Create an entity, using the information from the matcher.
	 * 
	 * Not the implementor does not need to set the offset, confidence, or add to the jcas.
	 * See {@link AbstractRegexNPAnnotator} doProcess().
	 * 
	 * @param jCas the jcas being processed
	 * @param matcher matcher (from the pattern supplied in the constructor)
	 * @return an instance, or null if this is not a valid match.
	 */
	protected abstract T create(JCas jCas, Matcher matcher);

	@Override
	public void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		Collection<PhraseChunk> chunks = JCasUtil.select(jCas, PhraseChunk.class);
	
		if(!chunks.isEmpty()){
			for (PhraseChunk chunk: chunks ) {
				if ("NP".equals(chunk.getChunkType()) ) {
					createEntities(jCas, chunk.getCoveredText(), chunk.getBegin());
				}
			}
		}else if(!isUpperCase(jCas.getDocumentText())){
			createEntities(jCas, jCas.getDocumentText(), 0);
		}
	}
	
	private void createEntities(JCas jCas, String text, int offset){
		Matcher matcher = pattern.matcher(text);
		while(matcher.find()){
			createEntity(jCas, matcher, offset);
		}
	}
	
	private Entity createEntity(JCas jCas, Matcher matcher, int offset){
		Entity a = create(jCas, matcher);
		
		if(a != null) {
			a.setConfidence(confidence);
			a.setBegin(offset + matcher.start(matcherGroup));
			a.setEnd(offset + matcher.end(matcherGroup));
			a.setValue(matcher.group(matcherGroup));
			
			addToJCasIndex(a);
		}
		
		return a;
	}
	
	private static boolean isUpperCase(String s){
		for(char c : s.toCharArray()){
			if(Character.isLetter(c) && Character.isLowerCase(c)){
				return false;
			}
		}
		
		return true;
	}
}
