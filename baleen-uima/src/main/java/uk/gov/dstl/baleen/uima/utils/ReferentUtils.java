//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import uk.gov.dstl.baleen.types.Base;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

/**
 * Helper for working with reference targets.
 */
public class ReferentUtils {

	public static final Predicate<? super Base> NOT_ENTITY_OR_REFERENT = e -> !(e instanceof Entity)
			&& e.getReferent() == null;

	public static final Predicate<? super Base> ENTITY_OR_REFERENT = e -> e instanceof Entity
			|| e.getReferent() != null;

	/**
	 * Instantiates a new referent utils.
	 */
	private ReferentUtils() {
		// Singleton
	}

	/**
	 * Creates the referent map - map of referent target to entities.
	 *
	 * @param <T>
	 *            the generic type
	 * @param jCas
	 *            the j cas
	 * @param clazz
	 *            the clazz
	 * @return the multimap
	 */
	public static <T extends Base> Multimap<ReferenceTarget, T> createReferentMap(JCas jCas, Class<T> clazz) {
		final Collection<T> potentialReferences = JCasUtil.select(jCas, clazz);

		final Multimap<ReferenceTarget, T> targets = HashMultimap.create();

		potentialReferences.stream()
				.filter(p -> p.getReferent() != null)
				.forEach(e -> {
					final ReferenceTarget referent = e.getReferent();
					targets.put(referent, e);
				});

		return targets;
	}

	/**
	 * Convert a multimap to a standard map (each with the same value type)
	 *
	 * @param <T>
	 *            the generic type
	 * @param referentMap
	 *            the referent map
	 * @param convert
	 *            the conversion function which will convert a list to a single entry. This does not
	 *            have to be a value from the list, and may be null (though that will drop the
	 *            corresponding key).
	 * @return the map
	 */
	public static <T> Map<ReferenceTarget, T> filterToSingle(Multimap<ReferenceTarget, T> referentMap,
			Function<Collection<T>, T> convert) {
		final Map<ReferenceTarget, T> singleMap = new HashMap<>(referentMap.size());

		referentMap.asMap().entrySet().stream()
				.forEach(e -> {
					final T t = convert.apply(e.getValue());
					if (t != null) {
						singleMap.put(e.getKey(), t);
					}
				});

		return singleMap;
	}

	/**
	 * Gets the all annotation which are an entity or which have a referent target which is also the
	 * referent target of an entity.
	 *
	 * Effectively get any annotation where the covered text is considered to be an entity.
	 *
	 * @param jCas
	 *            the j cas
	 * @param referentMap
	 *            the referent map
	 * @return the all entity or referent to entity
	 */
	public static List<Base> getAllEntityOrReferentToEntity(JCas jCas, Map<ReferenceTarget, Entity> referentMap) {
		return getAllAndReferents(jCas, Entity.class, referentMap);
	}

	/**
	 * Gets the all the annotation type and all the other annotations which have a referent target
	 * which is the referent target for an annotation of this type..
	 *
	 * @param <T>
	 *            the generic type
	 * @param jCas
	 *            the j cas
	 * @param clazz
	 *            the clazz
	 * @param referentMap
	 *            the referent map
	 * @return the all and referents
	 */
	public static <T extends Base> List<Base> getAllAndReferents(JCas jCas, Class<T> clazz,
			Map<ReferenceTarget, T> referentMap) {
		final List<Base> list = new ArrayList<>();

		// Add all of the original class
		list.addAll(JCasUtil.select(jCas, clazz));

		// Now find all the referents which point to the same entity
		streamReferent(jCas, referentMap)
				// Filter out any existing classes
				.filter(p -> clazz.isAssignableFrom(p.getClass()))
				.map(referentMap::get)
				.forEach(list::add);

		return list;
	}

	/**
	 * Stream all annotations which have a referent.
	 *
	 * @param jCas
	 *            the j cas
	 * @param referentMap
	 *            the referent map
	 * @return the stream
	 */
	public static Stream<Base> streamReferent(JCas jCas,
			Map<ReferenceTarget, ?> referentMap) {
		return JCasUtil.select(jCas, Base.class).stream()
				// Filter out anything we can't reference
				.filter(p -> p.getReferent() != null && referentMap.get(p.getReferent()) != null);
	}

	/**
	 * Gets the longest annotation (longest by coveed text size).
	 *
	 * @param <T>
	 *            the generic type
	 * @param list
	 *            the list
	 * @return the longest single
	 */
	public static <T extends Base> T getLongestSingle(Collection<T> list) {
		return singleViaCompare(list,
				(a, b) -> Integer.compare(a.getCoveredText().length(), b.getCoveredText().length()));
	}

	/**
	 * Get a single variable based on the compare (picks the highest).
	 *
	 * @param <T>
	 *            the generic type
	 * @param list
	 *            the list
	 * @param compare
	 *            the compare
	 * @return the t
	 */
	public static <T> T singleViaCompare(Collection<T> list, Comparator<T> compare) {
		return list.stream().reduce((a, b) -> compare.compare(a, b) < 0 ? b : a).get();
	}

	/**
	 * Replace the mentins with the principal coreferent entity (if there is one).
	 *
	 * @param entities
	 *            the entities
	 * @param referentMap
	 *            the referent map
	 * @return the set
	 */
	public static Set<Entity> replaceWithCoreferent(Collection<Entity> entities,
			Map<ReferenceTarget, Entity> referentMap) {
		final Set<Entity> set = new HashSet<>(entities.size());

		for (final Entity t : entities) {
			if (t.getReferent() == null) {
				set.add(t);
			} else {
				final Entity entity = referentMap.get(t.getReferent());
				if (entity != null) {
					set.add(entity);
				} else {
					// Add the other in
					set.add(t);
				}
			}
		}

		return set;
	}

}