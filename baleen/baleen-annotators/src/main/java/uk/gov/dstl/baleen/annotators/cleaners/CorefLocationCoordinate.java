//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import com.google.common.collect.Lists;

import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Coreference locations with coordinates when the coordinates are in brackets immediately following the location
 * 
 * <p>Coordinates that are found (in brackets) immediately after a Location are assumed to refer to the same place
 * and are linked to the same ReferenceTarget.</p>
 *
 * 
 * @baleen.javadoc
 */
public class CorefLocationCoordinate extends BaleenAnnotator {
	/**
	 * If two location entities are thought to be coreferences, but they have different existing reference targets, should we merge them?
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_MERGE_REFERENTS = "mergeReferents";
	@ConfigurationParameter(name = PARAM_MERGE_REFERENTS, defaultValue = "false")
	private boolean mergeReferents = false;
	
	@Override
	public void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		List<Location> locations = new ArrayList<Location>();
		
		FSIterator<Annotation> locIter = jCas.getAnnotationIndex(Location.type).iterator();
		while(locIter.hasNext()){
			locations.add((Location)locIter.next());
		}
		
		for(Location l : locations){	
			List<Coordinate> coords = JCasUtil.selectFollowing(jCas, Coordinate.class, l, 1);
			if(!coords.isEmpty()){
				Coordinate c = coords.get(0);
				String textRange = jCas.getDocumentText().substring(l.getBegin(), c.getEnd() + 1);
				
				if(textRange.matches(Pattern.quote(l.getCoveredText()) + "\\h*\\(" + Pattern.quote(c.getCoveredText()) + "\\)")){
					makeCoref(jCas, l, c);
				}				
			}
		}
	}
	
	private void makeCoref(JCas jCas, Location l, Coordinate c) {
		
		if(l.getReferent() != null && c.getReferent() == null){
			c.setReferent(l.getReferent());
		}else if(l.getReferent() == null && c.getReferent() != null){
			l.setReferent(c.getReferent());
		}else if(l.getReferent() == null && c.getReferent() == null){
			ReferenceTarget rt = createReferenceTarget(jCas);
			
			l.setReferent(rt);
			c.setReferent(rt);
		}else if(l.getReferent().equals(c.getReferent())){
			// Do nothing, they already have the same referent
		}else{
			// Both have a referent set, but they are different - should we merge them?
			if(mergeReferents){
				ReferenceTarget rt = createReferenceTarget(jCas);
				
				ReferenceTarget lRt = l.getReferent();
				ReferenceTarget cRt = c.getReferent();
				
				setReferents(rt, JCasUtil.select(jCas, Entity.class), Lists.newArrayList(lRt, cRt));

				removeFromJCasIndex(lRt);
				removeFromJCasIndex(cRt);
				
			}else{
				getMonitor().warn("Unable to coreference Location-Coordinate pair ({}, {}) as both have different existing referents", l.getInternalId(), c.getInternalId());
			}
		}
	}
	
	/**
	 * Sets the referents to <em>target</em> on all entities that either have a null referent, or a referent that matches one of the targets specified in <em>condition</em>
	 */
	private void setReferents(ReferenceTarget target, Collection<Entity> entities, Collection<ReferenceTarget> condition){
		for(Entity e : entities){
			if(e.getReferent() == null || condition.contains(e.getReferent())){
				e.setReferent(target);
			}
		}
	}

	private ReferenceTarget createReferenceTarget(JCas jCas){
		ReferenceTarget rt = new ReferenceTarget(jCas);
		rt.setBegin(0);
		rt.setEnd(jCas.getDocumentText().length());
		addToJCasIndex(rt);
		
		return rt;
	}
}
