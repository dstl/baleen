//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Quantity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.uima.BaleenTextAwareAnnotator;
import uk.gov.dstl.baleen.uima.data.TextBlock;

/**
 * Where a Location is preceded by text such as "20km east of", or "100m north west of",
 * expand the Location to cover the description.
 * 
 * The distance is optional, but if it is included it must be a Quantity annotator,
 * with subtype 'distance'. Optionally, the Quantity annotation can be removed.
 * 
 * Text such as 'the area of' or 'near to' will also be detected,
 * and distances aren't required for these.
 * 
 * Any GeoJSON associated with the location is removed, as it is unlikely to be correct.
 * 
 * @baleen.javadoc
 */
public class ExpandLocationToDescription extends BaleenTextAwareAnnotator{

	/**
	 * Should we remove the distance quantity from the JCas?
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_REMOVE_QUANTITY = "removeQuantity";
	@ConfigurationParameter(name = PARAM_REMOVE_QUANTITY, defaultValue = "false")
	private boolean removeQuantity = false;
	
	private static final Pattern DIRECTION_DESCRIPTION = Pattern.compile(".*?(north([- ]?(east|west))?|south([- ]?(east|west))?|east|west|N|E|S|W|NE|SE|SW|NW|NNE|ENE|ESE|SSE|SSW|WSW|WNW|NNW) of$", Pattern.CASE_INSENSITIVE);
	private static final Pattern AREA_DESCRIPTION = Pattern.compile(".*?(the (area|region|territory|vicinity|outskirts) (of|surrounding)|(close|near) to|parts of|(northern|eastern|southern|western)( part of)?)$", Pattern.CASE_INSENSITIVE);
	
	@Override
	protected void doProcessTextBlock(TextBlock block) throws AnalysisEngineProcessException {
		Collection<Quantity> quantities = block.select(Quantity.class);
		Set<Quantity> quantitiesToRemove = new HashSet<>();
		
		for(Location l : block.select(Location.class)){
			String precedingText = block.getCoveredText().substring(0, block.toBlockOffset(l.getBegin())).trim();

			Matcher ma = AREA_DESCRIPTION.matcher(precedingText);
			if(ma.matches()){
				l.setBegin(ma.start(1));
				l.setGeoJson(null);
				
				continue;
			}
			
			Matcher md = DIRECTION_DESCRIPTION.matcher(precedingText);
			if(md.matches()){
				l.setBegin(md.start(1));
				l.setGeoJson(null);
				
				Quantity q = findQuantity(quantities, l);
				if(q != null)
					quantitiesToRemove.add(q);
			}
		}
		
		if(removeQuantity)
			removeFromJCasIndex(quantitiesToRemove);
	}
	
	/**
	 * Finds a quantity prepending a location, and expand the location to include
	 * the quantity if found.
	 * 
	 * Returns the quantity, or null.
	 */
	private Quantity findQuantity(Collection<Quantity> quantities, Location l){
		for(Quantity q : quantities){
			if(("distance".equalsIgnoreCase(q.getSubType())) && (q.getEnd() == l.getBegin() || q.getEnd() == l.getBegin() - 1)){
				l.setBegin(q.getBegin());
				
				return q;
			}
		}
		
		return null;
	}
	
	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(ImmutableSet.of(Location.class, Quantity.class), Collections.emptySet());
	}
}