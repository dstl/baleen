//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import java.util.Arrays;
import java.util.List;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Organisation;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Annotate singular pronouns (e.g. he, she, I) as Person entities,
 * and plural pronouns (e.g. they, them) as Organisation entities.
 * 
 * This annotator does not need to be TextBlock aware, as we are
 * only dealing with a single word at a time.
 * 
 * @baleen.javadoc
 */
public class Pronouns extends BaleenAnnotator {

	private List<String> singularPronouns = Arrays.asList("i", "me", "myself", "you", "yourself", "he", "she", "him", "himself", "her", "herself");
	private List<String> pluralPronouns = Arrays.asList("we", "they", "them", "ourselves", "yourselves", "themselves");
	
	@Override
	protected void doProcess(JCas jCas) {
		for(WordToken wt : JCasUtil.select(jCas, WordToken.class)){
			String text = wt.getCoveredText().toLowerCase();
			
			if(singularPronouns.contains(text)){
				//Special case for I, which must be uppercase
				if("i".equals(text) && !"I".equals(wt.getCoveredText()))
					continue;
				
				Person p = new Person(jCas);
				p.setBegin(wt.getBegin());
				p.setEnd(wt.getEnd());
				p.setConfidence(1.0);
				
				addToJCasIndex(p);
			}else if(pluralPronouns.contains(text)){
				Organisation o = new Organisation(jCas);
				o.setBegin(wt.getBegin());
				o.setEnd(wt.getEnd());
				o.setConfidence(1.0);
				
				addToJCasIndex(o);
			}
		}
	}
	
	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(ImmutableSet.of(WordToken.class), ImmutableSet.of(Person.class, Organisation.class));
	}
}