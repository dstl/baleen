//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.relations.helpers;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.language.Interaction;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;

/**
 * A base class for relationship extractors which use interaction words as a
 * trigger.
 *
 * Implementations should override {@link #extract(JCas) extract}, and
 * potentially {@link #preExtract(JCas) preExtract} and
 * {@link #postExtract(JCas) postExtract}, which both allow for creation and
 * clean up of objects related to extraction.
 *
 */
public abstract class AbstractInteractionBasedRelationshipAnnotator extends AbstractRelationshipAnnotator {

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
	 * @param confidence
	 *            the confidence
	 * @return the relation
	 */
	protected Relation createRelation(final JCas jCas, final Interaction interaction, final Entity source,
			final Entity target, Float confidence) {
		return createRelation(jCas, source, target, interaction.getBegin(), interaction.getEnd(),
				interaction.getRelationshipType(), interaction.getRelationSubType(), interaction.getValue(),
				confidence);
	}

	/**
	 * Creates the relations of the same type between from all the entities on
	 * the source list to all the entities on the target list.
	 *
	 * @param jCas
	 *            the j cas
	 * @param interaction
	 *            the interaction
	 * @param sources
	 *            the sources
	 * @param targets
	 *            the targets
	 * @param confidence
	 *            the confidence
	 * @return the stream of relations
	 */
	protected Stream<Relation> createPairwiseRelations(final JCas jCas, final Interaction interaction,
			final List<Entity> sources, final List<Entity> targets, Float confidence) {
		return createPairwiseRelations(jCas, sources, targets, interaction.getBegin(), interaction.getEnd(),
				interaction.getRelationshipType(), interaction.getRelationSubType(), interaction.getValue(),
				confidence);
	}

	/**
	 * Creates the relations between all the entities provided (but not between
	 * an entity and itself).
	 *
	 * @param jCas
	 *            the j cas
	 * @param interaction
	 *            the interaction
	 * @param collection
	 *            the collection of entities to related
	 * @param confidence
	 *            the confidence
	 * @return the stream of relations
	 */
	protected Stream<Relation> createMeshedRelations(final JCas jCas, final Interaction interaction,
			final Collection<Entity> collection, Float confidence) {
		return createMeshedRelations(jCas, collection, interaction.getBegin(), interaction.getEnd(),
				interaction.getRelationshipType(), interaction.getRelationSubType(), interaction.getValue(),
				confidence);
	}

}
