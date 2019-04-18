// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.relations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Identifies relationships that have the NP-V(W*P)?-NP pattern, where NP is a Noun Phrase, V is a
 * Verb, W is any word, and P is a preposition.
 *
 * <p>Where one or more entities exists within the NP, these entities will be used to form the
 * relationships. Where no entities exist within the NP, and assuming that onlyExisting is false, a
 * new Entity will be created covering the whole NP.
 *
 * @baleen.javadoc
 */
public class NPVNP extends BaleenAnnotator {
  /**
   * Should we only find relations between existing entities (true), or should we add new entities
   * where we find relations between entities that haven't already been extracted (false)
   *
   * @baleen.config false
   */
  public static final String PARAM_ONLY_EXISTING = "onlyExisting";

  @ConfigurationParameter(name = PARAM_ONLY_EXISTING, defaultValue = "false")
  private Boolean onlyExisting = false;

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
    List<PhraseChunk> nounPhrases =
        JCasUtil.select(jCas, PhraseChunk.class).stream()
            .filter(c -> "NP".equals(c.getChunkType()))
            .collect(Collectors.toList());

    for (int i = 0; i < nounPhrases.size() - 1; i++) {
      PhraseChunk np = nounPhrases.get(i);
      PhraseChunk next = nounPhrases.get(i + 1);

      processNounPhrase(jCas, np, next);
    }
  }

  private void processNounPhrase(JCas jCas, PhraseChunk current, PhraseChunk next) {
    List<Entity> currentEntities =
        new ArrayList<>(JCasUtil.selectCovered(jCas, Entity.class, current));
    List<Entity> nextEntities = new ArrayList<>(JCasUtil.selectCovered(jCas, Entity.class, next));

    if (onlyExisting && (currentEntities.isEmpty() || nextEntities.isEmpty())) return;

    List<PhraseChunk> middle = JCasUtil.selectBetween(jCas, PhraseChunk.class, current, next);

    if (middle.isEmpty() || !"VP".equals(middle.get(0).getChunkType())) return;

    if (middle.size() == 1 || "PP".equals(middle.get(middle.size() - 1).getChunkType())) {
      if (currentEntities.isEmpty()) {
        Entity e1 = new Entity(jCas, current.getBegin(), current.getEnd());
        currentEntities.add(e1);
      }

      if (nextEntities.isEmpty()) {
        Entity e2 = new Entity(jCas, next.getBegin(), next.getEnd());
        nextEntities.add(e2);
      }

      createRelations(
          jCas,
          currentEntities,
          nextEntities,
          jCas.getDocumentText().substring(current.getEnd(), next.getBegin()).trim());
    }
  }

  private void createRelations(JCas jCas, List<Entity> source, List<Entity> target, String text) {
    for (Entity eSource : source) {
      for (Entity eTarget : target) {
        Relation relation = new Relation(jCas);
        relation.setBegin(eSource.getBegin());
        relation.setEnd(eTarget.getEnd());
        relation.setValue(trimPunctuation(text));
        relation.setSource(eSource);
        relation.setTarget(eTarget);
        relation.setRelationshipType("unknown");

        addToJCasIndex(relation);
      }
    }
  }

  /**
   * Trim punctuation (anything that isn't an alphanumeric character) from the start and end of a
   * String
   */
  public static String trimPunctuation(String s) {
    return s.replaceAll("^[^a-zA-Z0-9]*", "").replaceAll("[^a-zA-Z0-9]*$", "");
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(PhraseChunk.class, Entity.class), ImmutableSet.of(Relation.class));
  }
}
