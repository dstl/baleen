package uk.gov.dstl.baleen.annotators.relations.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.patterns.data.RelationWrapper;
import uk.gov.dstl.baleen.types.language.Interaction;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;
import uk.gov.dstl.baleen.uima.utils.ComparableEntitySpanUtils;

/**
 * A base class for relationship extractors which use interaction words as a trigger.
 *
 * Implementations should override {@link #extract(JCas) extract}, and potentially
 * {@link #preExtract(JCas) preExtract} and {@link #postExtract(JCas) postExtract}, which both allow
 * for creation and clean up of objects related to extraction.
 *
 */
public abstract class AbstractInteractionBasedRelationshipAnnotator extends BaleenAnnotator {

	@Override
	protected final void doProcess(final JCas jCas) throws AnalysisEngineProcessException {

		try {
			preExtract(jCas);

			extract(jCas);

		} finally {
			postExtract(jCas);
		}

	}

	/**
	 * Extract relations from the jCas.
	 *
	 * It is the overridders responsibility to add these to the jCas Index (addRelationsToIndex)
	 *
	 * @param jCas
	 *            the j cas
	 */
	protected abstract void extract(JCas jCas);

	/**
	 * Called before extract().
	 *
	 * @param jCas
	 *            the jcas
	 */
	protected void preExtract(final JCas jCas) {
		// Do nothing
	}

	/**
	 * Called after extract (including on exception).
	 *
	 * @param jCas
	 *            the jcas
	 */
	protected void postExtract(final JCas jCas) {
		// Do nothing
	}

	/**
	 * Adds a stream of relations to index.
	 *
	 * @param relations
	 *            the relations
	 */
	protected void addRelationsToIndex(final Stream<Relation> relations) {
		if (relations != null) {
			relations
					.filter(Objects::nonNull)
					// Only add events aren't in the same
					// Prevents overlapping spans since that makes no sense
					.filter(r -> r.getSource().getInternalId() != r.getTarget().getInternalId()
							&& !ComparableEntitySpanUtils.overlaps(r.getSource(), r.getTarget()))
					// Discard anything which has no relationship type

					// TODO: Is this sensible? These are direct connection between A and
					// B for the dependency graph (you can't be more connected than
					// that) but then you have no relationship text to work with.
					.filter(r -> r.getRelationshipType() != null || !StringUtils.isBlank(r.getRelationshipType()))
					.forEach(this::addToJCasIndex);
		}
	}

	/**
	 * Creates the relation.
	 *
	 * @param jCas
	 *            the jcas
	 * @param interaction
	 *            the interaction
	 * @param source
	 *            the source the source entity
	 * @param target
	 *            the target the target entity
	 * @return the relation
	 */
	protected Relation createRelation(final JCas jCas, final Interaction interaction, final Entity source,
			final Entity target) {
		final Relation r = new Relation(jCas);
		r.setBegin(interaction.getBegin());
		r.setEnd(interaction.getEnd());
		r.setRelationshipType(interaction.getRelationshipType());
		r.setRelationSubType(interaction.getRelationSubType());
		r.setSource(source);
		r.setTarget(target);
		r.setValue(interaction.getValue());
		return r;
	}

	/**
	 * Creates the relations of the same type between from all the entities on the source list to
	 * all the entities on the target list.
	 *
	 * @param jCas
	 *            the j cas
	 * @param interaction
	 *            the interaction
	 * @param sources
	 *            the sources
	 * @param targets
	 *            the targets
	 * @return the stream of relations
	 */
	protected Stream<Relation> createPairwiseRelations(final JCas jCas, final Interaction interaction,
			final List<Entity> sources,
			final List<Entity> targets) {
		return sources.stream()
				.flatMap(l -> targets.stream()
						.map(r -> createRelation(jCas, interaction, l, r)));

	}

	/**
	 * Creates the relations between all the entities provided (but not between an entity adn
	 * itself).
	 *
	 * @param jCas
	 *            the j cas
	 * @param interaction
	 *            the interaction
	 * @param collection
	 *            the collection of entities to related
	 * @return the stream of relations
	 */
	protected Stream<Relation> createMeshedRelations(final JCas jCas, final Interaction interaction,
			final Collection<Entity> collection) {

		final List<Relation> relations = new LinkedList<>();

		List<Entity> entities;
		if (collection instanceof List) {
			entities = (List<Entity>) collection;
		} else {
			entities = new ArrayList<>(collection);
		}

		final ListIterator<Entity> outer = entities.listIterator();
		while (outer.hasNext()) {
			final Entity source = outer.next();

			final ListIterator<Entity> inner = entities.listIterator(outer.nextIndex());
			while (inner.hasNext()) {
				final Entity target = inner.next();

				relations.add(createRelation(jCas, interaction, source, target));
			}
		}

		return relations.stream();
	}

	/**
	 * Make the stream distinct (no relations of the same type, between the same entities).
	 *
	 * @param stream
	 *            the stream
	 * @return the stream
	 */
	protected Stream<Relation> distinct(final Stream<Relation> stream) {
		return stream
				.filter(Objects::nonNull)
				.map(RelationWrapper::new)
				.distinct()
				.map(RelationWrapper::getRelation);
	}

}
