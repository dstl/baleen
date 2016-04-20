package uk.gov.dstl.baleen.annotators.cleaners;

import org.apache.uima.fit.descriptor.ConfigurationParameter;

import uk.gov.dstl.baleen.annotators.cleaners.helpers.AbstractNormalizeEntities;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;
import uk.gov.dstl.baleen.types.semantic.Entity;

/** 
 * Formats the value of the telephone entities to be in a particular form for
 * consistent representation on export. Format is +441234567890. Currently any
 * prefix is just removed so international dialling code information is lost from
 * the entity value, though it is still visible in the original text covered by the
 * range of the entity.
 * 
 * In order to prevent the phone numbers appearing as doubles in the Mongo database
 * their + prefix must be preserved by running this cleaner after the CleanPunctuation 
 * cleaner in the pipeline.
 * @baleen.javadoc
 * 
 * @author: Christopher McLean
 */
public class NormalizeTelephoneNumbers extends AbstractNormalizeEntities {
	
	/**
	 * Attach a user defined prefix to the front of the number e.g. '+44 (0)' or T.
	 * @baleen.config +44
	 */
	public static final String PARAM_PREFIX = "prefix";
	@ConfigurationParameter(name = PARAM_PREFIX, defaultValue = "+44")
	String prefix;
	
	@Override
	protected String normalize(Entity e) {
		String number = e.getValue();

		String cleanedNumber = number.replaceAll("\\s", "");
		cleanedNumber = cleanedNumber.replaceAll("[.:()-]", "");
		cleanedNumber = cleanedNumber.replaceAll("[A-Za-z]", "");
		
		//If there aren't at least 10 digits then there are too few for a valid number
		if (cleanedNumber.length() >= 10) {
			int length = cleanedNumber.length();
			number = prefix + cleanedNumber.substring(length - 10, length);
		}
		
		return number;
	}
			

	@Override
	protected boolean shouldNormalize(Entity e) {
		return ((e instanceof CommsIdentifier) && (e.getSubType().equals("telephone")));
	}
}
