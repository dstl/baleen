package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.Collection;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.grammatical.NPTitleEntity;
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
 * @baleen.javadoc
 */
public class AddTitleToPerson extends BaleenAnnotator {

	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		// We copy this array as we'll modify people as we go
		Collection<Person> people = JCasUtil.select(jCas, Person.class);

		for (Person p : people) {
			while(makeReplacement(jCas, p)){
				//Make as many replacements as possible, to capture things like Sir Major General Smith. 
			}
		}
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
		if (title == null || title.isEmpty()) {
			return prefix;
		} else {
			return prefix + " " + title;
		}
	}

}
