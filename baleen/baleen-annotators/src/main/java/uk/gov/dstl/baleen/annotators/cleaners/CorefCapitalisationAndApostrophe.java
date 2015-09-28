//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Find groups of entities of the same type and same value, ignoring case and apostrophes, and point them at the same ReferenceTarget.
 * 
 * <p>If two or more existing ReferenceTargets exist within a group, then the group is either ignored
 * or it's ReferenceTargets merged depending on the mergeReferents parameter.
 * If there is one existing ReferenceTarget within a group then that is used as the ReferenceTarget for all entities in that group.</p>
 * 
 * 
 * @baleen.javadoc
 */
public class CorefCapitalisationAndApostrophe extends BaleenAnnotator {
	/**
	 * If two location entities are thought to be coreferences, but they have different existing reference targets, should we merge them?
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_MERGE_REFERENTS = "mergeReferents";
	@ConfigurationParameter(name = PARAM_MERGE_REFERENTS, defaultValue = "false")
	boolean mergeReferents = false;
	
	@Override
	public void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		Map<String, List<Entity>> groups = new HashMap<>();
		Collection<Entity> entities = JCasUtil.select(jCas, Entity.class);
		
		for(Entity entity : entities){
			String value = getEntityValue(entity);
			value = normalizeValue(value);
			
			String key = entity.getType().getName().toUpperCase() + "::" + value;
			List<Entity> groupEntities = groups.containsKey(key) ? groups.get(key) : new ArrayList<>();
			
			groupEntities.add(entity);
			groups.put(key, groupEntities);
		}
		
		for(List<Entity> group : groups.values()){
			if(group.size() <= 1){
				continue;
			}

			List<ReferenceTarget> rts = new ArrayList<>();
			for(Entity e : group){
				if(e.getReferent() != null){
					rts.add(e.getReferent());
				}
			}
				
			ReferenceTarget rt = selectAppropriateReferenceTarget(jCas, rts);
			if(rt == null){
				getMonitor().info("Unable to coreference capitalised entities '{}' as they have different existing referents", getEntityValue(group.get(0)));
			}else{
				for(Entity e : group){
					e.setReferent(rt);
				}
			}
		}
	}
	
	private String getEntityValue(Entity e){
		if(e == null){
			return null;
		}
		
		String val = e.getValue();
		if(Strings.isNullOrEmpty(val)){
			val = e.getCoveredText();
		}
		
		return val;
	}
	
	private String normalizeValue(String value){
		String s = value.trim().toLowerCase();
		s = s.toLowerCase();
		
		if(s.endsWith("'s")){
			s = s.substring(0, s.length() - 2);
		}else if(s.endsWith("s'")){
			s = s.substring(0, s.length() - 1);
		}
		
		return s;
	}
	
	private ReferenceTarget selectAppropriateReferenceTarget(JCas jCas, List<ReferenceTarget> referenceTargets){
		ReferenceTarget rt = null;
		
		if(referenceTargets.isEmpty()){
			rt = createReferenceTarget(jCas);
		}else if(referenceTargets.size() == 1){
			rt = referenceTargets.get(0);
		}else if(mergeReferents){
			rt = createReferenceTarget(jCas);
			
			removeFromJCasIndex(referenceTargets);
		}
		
		return rt;
	}
	
	private ReferenceTarget createReferenceTarget(JCas jCas){
		ReferenceTarget rt = new ReferenceTarget(jCas);
		rt.setBegin(0);
		rt.setEnd(jCas.getDocumentText().length());
		addToJCasIndex(rt);
		
		return rt;
	}
}
