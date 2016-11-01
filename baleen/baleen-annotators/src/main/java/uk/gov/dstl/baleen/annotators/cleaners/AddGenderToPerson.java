package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.resources.SharedGenderMultiplicityResource;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Add gender information to a person, using the SharedGenderMultiplicityResource
 * 
 * Checks each part of the name (i.e. for John Smith, it would check John and Smith) to determine the gender.
 * If at least one part is Male, and the other parts are Unknown or Neutral, then the person is marked as Male.
 * If at least one part is Female, and the other parts are Unknown or Neutral, then the person is marked as Female.
 * Otherwise, the person is marked as Unknown.
 * 
 * Entities that already have an assigned gender (ignoring Unknown) are not modified.
 *
 * @baleen.javadoc
 */
public class AddGenderToPerson extends BaleenAnnotator {
	/**
	 * Access to Gender and Multiplicity Resource
	 * 
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedGenderMultiplicityResource
	 */
	public static final String KEY_GENDER_MULTIPLICITY = "genderMultiplicity";
	@ExternalResource(key = KEY_GENDER_MULTIPLICITY)
	private SharedGenderMultiplicityResource genderResource;
	
	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		for (Person p : JCasUtil.select(jCas, Person.class)) {
			Integer mCount = 0;
			Integer fCount = 0;
			
			if(Strings.isNullOrEmpty(p.getGender()) || "UNKNOWN".equalsIgnoreCase(p.getGender())){
				List<String> nameParts = new ArrayList<>();
				
				if(!Strings.isNullOrEmpty(p.getValue())){
					nameParts.addAll(Arrays.asList(p.getValue().split("\\s+")));
				}else{
					nameParts.addAll(Arrays.asList(p.getCoveredText().split("\\s+")));
				}
				
				for(String namePart : nameParts){
					switch(genderResource.lookupGender(namePart)){
					case M:
						mCount++;
						break;
					case F:
						fCount++;
						break;
					default:
						//Do nothing, if it's neutral or unknown we can ignore it for now
					}
				}
				
				if(mCount > 0 && fCount == 0){
					p.setGender("MALE");
				}else if(fCount > 0 && mCount == 0){
					p.setGender("FEMALE");
				}else{
					p.setGender("UNKNOWN");
				}
			}
		}
	}
}
