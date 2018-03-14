// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.relations.helpers;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.language.Interaction;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;

/**
 * A base class for building relationship extractors which work at a sentence level.
 *
 * <p>Override the {@link #extract(JCas) extract} method which provide interactions and entities
 * per-sentence.
 *
 * <p>See {@link AbstractInteractionBasedRelationshipAnnotator} for further information on options.
 */
public abstract class AbstractInteractionBasedSentenceRelationshipAnnotator
    extends AbstractInteractionBasedRelationshipAnnotator {

  @Override
  protected final void extract(JCas jCas) {

    final Map<Sentence, Collection<Interaction>> sentenceToInteraction =
        JCasUtil.indexCovered(jCas, Sentence.class, Interaction.class);
    final Map<Sentence, Collection<Entity>> sentenceToEntities =
        JCasUtil.indexCovered(jCas, Sentence.class, Entity.class);

    for (final Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {

      final Collection<Interaction> interactions = sentenceToInteraction.get(sentence);

      final Collection<Entity> entities = sentenceToEntities.get(sentence);

      // Check we have enough in the sentence to warrant further work
      if (!interactions.isEmpty() && entities.size() >= 2) {
        final Stream<Relation> relations = extract(jCas, sentence, interactions, entities);
        addRelationsToIndex(relations);
      }
    }
  }

  /**
   * Extract the relations from this sentence.
   *
   * @param jCas the j cas
   * @param sentence the sentence
   * @param interactions the interactions in the sentence
   * @param entities the entities in the sentence
   * @return the stream of relations (return null or empty for none)
   */
  protected abstract Stream<Relation> extract(
      JCas jCas,
      Sentence sentence,
      Collection<Interaction> interactions,
      Collection<Entity> entities);
}
