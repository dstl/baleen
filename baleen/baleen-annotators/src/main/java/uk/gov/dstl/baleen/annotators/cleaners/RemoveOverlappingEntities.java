package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import uk.gov.dstl.baleen.annotators.cleaners.helpers.AbstractNestedEntities;
import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * Remove entities which are contained within other entities of any type.
 * <p>
 * This is useful for relations and event extraction pipelines where having overlapping entities
 * will produce poorer results.
 * <p>
 * For example the "The British Army fought in Iraq" might provide entities British[Nationality],
 * British Army[Location] and Iraq[Location]. A simple relationship extraction may relate all
 * entities in a sentence British-British Army, British Army-Iraq and British-Iraq. The final
 * relation is true in this case but not really the meaning of the text - the word British was not
 * meant to be considered in isolation.
 * 
 * @baleen.javadoc
 */
public class RemoveOverlappingEntities extends AbstractNestedEntities<Entity> {

	/**
	 * A list of types to exclude when removing nested entities.
	 *
	 * @baleen.config
	 */
	public static final String PARAM_EXCLUDED_TYPES = "excludedTypes";
	@ConfigurationParameter(name = PARAM_EXCLUDED_TYPES, defaultValue = {})
	private Set<String> excluded;

	@Override
	protected Collection<List<Entity>> compileEntities(JCas jCas) {
		Set<Entity> annotations = new HashSet<>();

		FSIterator<Annotation> iter = jCas.getAnnotationIndex(Entity.type)
				.iterator();
		while (iter.hasNext()) {
			Entity e = (Entity) iter.next();
			String type = e.getType().getName();

			if (!excluded.contains(type)) {
				annotations.add(e);
			}
		}

		return Collections.singleton(new ArrayList<>(annotations));
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
