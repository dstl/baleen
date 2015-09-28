//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import uk.gov.dstl.baleen.annotators.cleaners.helpers.AbstractNestedEntities;
import uk.gov.dstl.baleen.types.semantic.Entity;
/**
 * Remove entities which are contained within other entities of the same type
 * 
 * <p>All potential merges area made for all types (bearing those set in the exclusion configuration parameter).
 * Information may be lost by the removal of entities. Some kind of merging of entities might be a better option.</p>
 * 
 * 
 * @baleen.javadoc
 */
public class RemoveNestedEntities extends AbstractNestedEntities<Entity> {

	/**
	 * A list of types to exclude when removing nested entities.
	 * 
	 * @baleen.config
	 */
	public static final String PARAM_EXCLUDED_TYPES = "excludedTypes";
	@ConfigurationParameter(name=PARAM_EXCLUDED_TYPES,defaultValue={})
	private Set<String> excluded;
	
	@Override
	protected Collection<List<Entity>> compileEntities(JCas jCas) {
		Map<String, List<Entity>> annotations = new HashMap<String, List<Entity>>();

		FSIterator<Annotation> iter = jCas.getAnnotationIndex(Entity.type)
				.iterator();
		while (iter.hasNext()) {
			Entity e = (Entity) iter.next();
			String type = e.getType().getName();

			if (!excluded.contains(type)) {
				List<Entity> typeAnnotations = annotations.get(type);
				if (typeAnnotations == null) {
					typeAnnotations = new ArrayList<Entity>();
					annotations.put(type, typeAnnotations);
				}
				typeAnnotations.add(e);
			}
		}
		
		return annotations.values();
	}
	
	@Override
	protected boolean shouldMerge(Entity keep, Entity remove) {
		// Merge everything
		return true;
	}


	@Override
	public void doDestroy() {
		excluded = null;
	}

}
