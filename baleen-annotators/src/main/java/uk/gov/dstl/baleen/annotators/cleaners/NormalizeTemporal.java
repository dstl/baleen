//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.cleaners.helpers.AbstractNormalizeEntities;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Temporal;

/**
 * Edits the value field of Temporal entities and sets it to a value in a given format.
 * The Temporal entities can be filtered by temporalType, and only those with a non-zero
 * timestamp and scope of SINGLE will be considered.
 * 
 * The start timestamp will be used to produce the formatted value.
 * 
 * @baleen.javadoc
 */
public class NormalizeTemporal extends AbstractNormalizeEntities{

	/**
	 * What is the format that the temporal entities should be normalized to? The default value follows the 
	 * ISO8601 standard.
	 * @baleen.config yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'
	 */
	public static final String PARAM_DATE_FORMAT = "correctFormat";
	@ConfigurationParameter(name = PARAM_DATE_FORMAT, defaultValue = "yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'")
	String correctFormat;
	
	/**
	 * If set, then the temporal type of the Temporal entity must match this (case insensitive) to be normalized.
	 * @baleen.config 
	 */
	public static final String PARAM_TEMPORAL_TYPE = "temporalType";
	@ConfigurationParameter(name = PARAM_TEMPORAL_TYPE, defaultValue="")
	String type;
	
	DateTimeFormatter formatter;
	
	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		try{
			formatter = DateTimeFormatter.ofPattern(correctFormat).withZone(ZoneOffset.UTC);
		}catch(IllegalArgumentException iae){
			getMonitor().error("Unable to parse correctFormat pattern", iae);
			throw new ResourceInitializationException(iae);
		}
	}

	@Override
	protected String normalize(Entity e) {
		Temporal t = (Temporal) e;
		Instant i = Instant.ofEpochSecond(t.getTimestampStart());
		
		return formatter.format(i);
	}

	@Override
	protected boolean shouldNormalize(Entity e) {
		if(!(e instanceof Temporal))
			return false;
		
		Temporal t = (Temporal) e;
		
		return "SINGLE".equalsIgnoreCase(t.getScope()) && isTimestampSet(t) && matchesType(t);
	}
	
	/**
	 * Return false if we suspect the timestamp hasn't been set on a Temporal object
	 * (i.e. both the start and end are equal to 0), or true otherwise. 
	 */
	private boolean isTimestampSet(Temporal t){
		return t.getTimestampStart() != 0L && t.getTimestampStop() != 0L;
	}
	
	/**
	 * Return true if the list of types to act on has been set and it matches the type
	 * set on the Temporal object t, or if the list of types hasn't been set.
	 * Return false otherwise.
	 */
	private boolean matchesType(Temporal t){
		if(Strings.isNullOrEmpty(type))
			return true;
		
		return type.equalsIgnoreCase(t.getTemporalType());
	}
	
	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(ImmutableSet.of(Temporal.class), Collections.emptySet());
	}
}