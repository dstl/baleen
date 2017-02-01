//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * Remove entities that appear on a blacklist supplied by the user
 * 
 * <p>Loops through a list of blacklisted entity values supplied by the user,
 * and if an entity value matches one on the blacklist that entity is removed.
 * This can be done either case sensitive or case insensitive (the default),
 * and for specific entity types or all entity types.</p>
 * 
 * 
 * @baleen.javadoc
 */
public class Blacklist extends BaleenAnnotator {

	/**
	 * A list of blacklisted entity values to remove from the CAS
	 * 
	 * @baleen.config
	 */
	public static final String PARAM_BLACKLIST = "blacklist";
	@ConfigurationParameter(name = PARAM_BLACKLIST, defaultValue={})
	String[] terms;
	List<String> thingsToRemove = null;

	/**
	 * Should the comparison of the blacklist with entity values be done case sensitively?
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_CASE_SENSITIVE = "caseSensitive";
	@ConfigurationParameter(name = PARAM_CASE_SENSITIVE, defaultValue="false")
	Boolean caseSensitive;
	
	/**
	 * The entity type to compare against the blacklist, including subclasses of this type
	 * 
	 * @baleen.config uk.gov.dstl.baleen.types.semantic.Entity
	 */
	public static final String PARAM_TYPE = "type";
	@ConfigurationParameter(name = PARAM_TYPE, defaultValue="uk.gov.dstl.baleen.types.semantic.Entity")
	String type;
	Class<?> et = null;
	
	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		try{
			et = TypeUtils.getEntityClass(type, JCasFactory.createJCas(TypeSystemSingleton.getTypeSystemDescriptionInstance()));
		}catch(UIMAException | BaleenException e){
			throw new ResourceInitializationException(e);
		}
		
		thingsToRemove = Arrays.asList((String[]) terms);
		
		if(!caseSensitive)
			thingsToRemove = toLowerCase(thingsToRemove);
	}
	
	@Override
	public void doProcess(JCas aJCas) throws AnalysisEngineProcessException {
		Entity e;
		try {
			e = (Entity) et.getConstructor(JCas.class).newInstance(aJCas);
		} catch (Exception ex) {
			throw new AnalysisEngineProcessException(ex);
		}
		
		Set<Entity> toRemove = new HashSet<Entity>();
		
		FSIndex<Annotation> index = aJCas.getAnnotationIndex(e.getType());
		for(Annotation a : index){
			Entity entity = (Entity) a;
			
			String val = entity.getCoveredText();
			if(!caseSensitive)
				val = val.toLowerCase();
						
			if(thingsToRemove.contains(val)){
				getMonitor().info("Removing entity '{}' because it appears on the blacklist", entity.getCoveredText());
				toRemove.add(entity);
			}
		}
		
		getMonitor().debug("{} has removed {} entities", this.getClass().getName(), toRemove.size());
		for(Entity ent : toRemove){
			removeFromJCasIndex(ent);
		}
	}
	
	@Override
	public void doDestroy(){
		thingsToRemove = null;
		et = null;
	}
	
	private List<String> toLowerCase(List<String> list){
		List<String> l = new ArrayList<String>();
		
		for(String s : list){
			l.add(s.toLowerCase());
		}
		
		return l;
	}

}
