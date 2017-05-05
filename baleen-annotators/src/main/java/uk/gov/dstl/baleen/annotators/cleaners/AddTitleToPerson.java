//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.Collection;
import java.util.Collections;
import java.util.StringJoiner;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.grammatical.NPTitleEntity;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Add title (mr, president, etc) information to previously found people.
 * <p>
 * Often with NLP models we find a person, e.g. John Smith but omit the title information, e.g.
 * General John Smith, General Sir John Smith. This annotator adds that information back onto the
 * entity, thus improving the quality of person extraction and reducing the number of unannotated
 * words in a document.
 * 
 * This will also find cases where the title is included in the name, but has not been set as a property.
 * 
 * This cleaner will overwrite existing titles.
 *
 * @baleen.javadoc
 */
public class AddTitleToPerson extends BaleenAnnotator {

	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		// We copy this array as we'll modify people as we go
		Collection<Person> people = JCasUtil.select(jCas, Person.class);

		
		for(Person p : people){
			//Find existing titles
			findExistingTitle(p);
			
			while(makeReplacement(jCas, p)){
				//Make as many replacements as possible, to capture things like Sir Major General Smith. 
			}
		}
	}

	private void findExistingTitle(Person p){
		String[] tokens = p.getCoveredText().split("[\\.\\s]+");
		
		StringJoiner sj = new StringJoiner(" ");
		for(String token : tokens){
			if(NPTitleEntity.TITLES.contains(token.toLowerCase())){
				sj.add(token);
			}else if(token.length() > 0){
				break;
			}
		}
		
		p.setTitle(sj.toString());
	}
	
	private boolean makeReplacement(JCas jCas, Person p){
		boolean replacementMade = false;
		
		for(String title : NPTitleEntity.TITLES){
			if(p.getBegin() - title.length() - 1 < 0)
				continue;
			
			String precedingText = jCas.getDocumentText().substring(p.getBegin() - title.length() - 1, p.getBegin() - 1);
			
			if(title.equalsIgnoreCase(precedingText)){
				p.setBegin(p.getBegin() - title.length() - 1);
				p.setTitle(extendTitle(precedingText, p.getTitle()));
				
				replacementMade = true;
			}else if(p.getBegin() - title.length() - 2 >= 0 && p.getBegin() -1 >= 0){
				String precedingTextPlusOne = jCas.getDocumentText().substring(p.getBegin() - title.length() - 2, p.getBegin() - 1);
				
				if((title + ".").equalsIgnoreCase(precedingTextPlusOne)){
					p.setBegin(p.getBegin() - title.length() - 2);
					p.setTitle(extendTitle(precedingTextPlusOne, p.getTitle()));
					
					replacementMade = true;
				}
			}
		}
		
		return replacementMade;
	}
	
	/**
	 * Add the prefix to the existing title.
	 *
	 * @param prefix
	 *            the prefix
	 * @param title
	 *            the title
	 * @return the string
	 */
	private String extendTitle(String prefix, String title) {
		String cleanedPrefix = prefix.replaceAll("\\.", "");	//Get rid of periods,
		if (title == null || title.isEmpty()) {
			return cleanedPrefix;
		} else {
			return cleanedPrefix + " " + title;
		}
	}

	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(ImmutableSet.of(Person.class), Collections.emptySet());
	}
}