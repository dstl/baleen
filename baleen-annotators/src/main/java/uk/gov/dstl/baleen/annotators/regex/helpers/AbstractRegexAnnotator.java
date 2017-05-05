//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import uk.gov.dstl.baleen.types.Base;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.BaleenTextAwareAnnotator;
import uk.gov.dstl.baleen.uima.data.TextBlock;

/** An abstract base class for building RegexAnnotators.
 *
 * Implement create and pass in the regex definition to the contractor.
 *
 * 
 *
 * @param <T> the type of entity produced.
 */
public abstract class AbstractRegexAnnotator<T extends Annotation> extends BaleenTextAwareAnnotator {

	private Pattern pattern;
	private double confidence;
	private int matcherGroup;

	/** New instance, based on the supplied pattern. Uses the whole matched regex as the entity text.
	 * @param pattern the regex pattern
	 * @param caseSensitive should this be treated a case sensitive
	 * @param confidence the confidence to assign to created entities.
	 */
	protected AbstractRegexAnnotator(String pattern, boolean caseSensitive, double confidence) {
		this(Pattern.compile(pattern, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE), 0, confidence);
	}

	/** New instance, based on a regex pattern. Uses the whole matched regex as the entity text.
	 * @param pattern the regex pattern
	 * @param confidence the confidence to assign to created entities.
	 */
	protected AbstractRegexAnnotator(Pattern pattern, double confidence) {
		this(pattern, 0, confidence);
	}

	/** New instance, based on the supplied pattern.
	 * @param pattern the regex pattern
	 * @param matcherGroup the matcher group to use as the content of the entity
	 * @param caseSensitive should this be treated a case sensitive
	 * @param confidence the confidence to assign to created entities.
	 */
	protected AbstractRegexAnnotator(String pattern, int matcherGroup, boolean caseSensitive, double confidence) {
		this(Pattern.compile(pattern, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE), matcherGroup, confidence);
	}

	/** New instance, based on a regex pattern.
	 * @param pattern the regex pattern
	 * @param matcherGroup the matcher group to use as the content of the entity
	 * @param confidence the confidence to assign to created entities.
	 */
	protected AbstractRegexAnnotator(Pattern pattern, int matcherGroup, double confidence) {
		this.pattern = pattern;
		this.matcherGroup = matcherGroup;
		this.confidence = confidence;
	}


	/** Create an entity, using the information from the matcher.
	 *
	 * Not the implementor does not need to set the offset, confidence, or add to the jcas.
	 * See {@link AbstractRegexAnnotator} doProcess().
	 *
	 * @param jCas the jcas being processed
	 * @param matcher matcher (from the pattern supplied in the constructor)
	 * @return an instance, or null if this is not a valid match.
	 */
	protected abstract T create(JCas jCas, Matcher matcher);

	@Override
	public void doProcessTextBlock(TextBlock block) throws AnalysisEngineProcessException {
		String text = block.getCoveredText();

		Matcher matcher = pattern.matcher(text);
		while(matcher.find()){
			Annotation a = create(block.getJCas(), matcher);

			if(a != null) {
				block.setBeginAndEnd(a, matcher.start(matcherGroup), matcher.end(matcherGroup));
				if(a instanceof Base) {
					((Base)a).setConfidence(confidence);
				}
				if(a instanceof Entity) {
					((Entity)a).setValue(matcher.group(matcherGroup));
				}

				addToJCasIndex(a);
			}
		}
	}
}
