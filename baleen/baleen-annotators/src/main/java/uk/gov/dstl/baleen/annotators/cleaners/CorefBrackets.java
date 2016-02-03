//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * Coreference entities where a series of entities of the same type appears in brackets.
 * 
 * For example, "William Tell (Bill)" would coreference William Tell and Bill if they were both person entities.
 * 
 * @baleen.javadoc
 */
public class CorefBrackets extends BaleenAnnotator {
	/**
	 * If two entities are thought to be coreferences, but they have different existing reference targets, should we merge them?
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_MERGE_REFERENTS = "mergeReferents";
	@ConfigurationParameter(name = PARAM_MERGE_REFERENTS, defaultValue = "false")
	private boolean mergeReferents = false;
	
	/**
	 * A list of the excluded entity types.
	 * 
	 * @baleen.config 
	 */
	public static final String PARAM_TYPE = "excludedTypes";
	@ConfigurationParameter(name = PARAM_TYPE, defaultValue={})
	String[] excludedTypes;
	List<Class<? extends Entity>> classTypes = new ArrayList<>();
	
	private static final Pattern BRACKETS = Pattern.compile("^\\s*\\((.*?)\\)");
	
	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException{
		JCas jCas;
		try {
			jCas = JCasFactory.createJCas();
		} catch (UIMAException e) {
			throw new ResourceInitializationException(e);
		}
		for(String type : excludedTypes){
			try{
				classTypes.add(TypeUtils.getEntityClass(type, jCas));
			}catch(BaleenException e){
				getMonitor().error("Couldn't parse type {} - type will not be excluded", type, e);
			}
		}
	}
	
	@Override
	public void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		Collection<Entity> entities = JCasUtil.select(jCas, Entity.class);
		
		Multimap<Integer, Entity> entityMap = ArrayListMultimap.create();
		entities.forEach(e -> entityMap.put(e.getBegin(), e));
				
		for(Entity e : entities){
			String followingText = jCas.getDocumentText().substring(e.getEnd());
			Matcher m = BRACKETS.matcher(followingText);
			Integer offset = e.getEnd();
			
			List<Entity> matched = new ArrayList<>();
			while(m.find()){
				final Integer end = offset + m.end(1);
				matched.addAll(
					entityMap.get(offset + m.start(1)).stream()
						.filter(f -> f.getClass().isAssignableFrom(e.getClass()) || e.getClass().isAssignableFrom(f.getClass()))
						.filter(f -> f.getEnd() == end)
						.collect(Collectors.toList())
				);
								
				//Prepare matcher to look for next bracket
				followingText = followingText.substring(m.end());
				offset += m.end();
				m = BRACKETS.matcher(followingText);
			}
			
			if(!matched.isEmpty()){
				matched.add(e);
				makeCoref(jCas, matched);
			}
		}
	}
	
	private void makeCoref(JCas jCas, Collection<Entity> entities) {
		Set<ReferenceTarget> rts = new HashSet<>();
		for(Entity e : entities){
			if(e.getReferent() != null){
				rts.add(e.getReferent());
			}
		}
		
		if(rts.isEmpty()){
			setAllReferents(createReferenceTarget(jCas), entities);
		}else if(rts.size() == 1){
			setAllReferents(rts.toArray(new ReferenceTarget[0])[0], entities);
		}else{
			if(mergeReferents){
				ReferenceTarget rt = createReferenceTarget(jCas);
				setReferents(rt, JCasUtil.select(jCas, Entity.class), rts);
				removeFromJCasIndex(rts);
			}else{
				getMonitor().warn("Multiple existing referents found, only those entities without existing referents will be modified");
				
				setNewReferentIfNull(jCas, entities);
			}
		}
	}
	
	/**
	 * Set the referents of all entities in <em>entities</em> to the ReferenceTarget specified by <em>target</em>
	 */
	private void setAllReferents(ReferenceTarget target, Collection<Entity> entities){
		for(Entity e : entities){
			e.setReferent(target);
		}
	}
	
	/**
	 * For all entities in <em>entities</em> that don't have an existing referent, set them to the same new ReferenceTarget.
	 * This ReferenceTarget is returned if created, else null is returned.
	 */
	private ReferenceTarget setNewReferentIfNull(JCas jCas, Collection<Entity> entities){
		ReferenceTarget rt = null;
		for(Entity e : entities){
			if(e.getReferent() == null){
				if(rt == null){
					rt = createReferenceTarget(jCas);
				}
				e.setReferent(rt);
			}
		}
		
		return rt;
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
