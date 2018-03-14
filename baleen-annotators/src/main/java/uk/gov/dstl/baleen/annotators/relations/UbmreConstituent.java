// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.relations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.relations.helpers.AbstractInteractionBasedSentenceRelationshipAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.language.Interaction;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.grammar.ParseTree;
import uk.gov.dstl.baleen.uima.grammar.ParseTreeNode;

/**
 * Unsupervised (originally Biomedical) Relationship Extractor.
 *
 * <p>A relationship extraction algorithm based on constituent parsing.
 *
 * <p>Given an interaction word we look for the covering VP. Then look in the parse tree above the
 * VP for a NP+VP (ie Noun Phrase followed by our VP). If that is the case then we consider the
 * entities covered by the NP to be related to the entities covered by the VP by the interaction
 * word.
 *
 * <p>For example John went to London. interaction words is went. VP is "went to London". NP is
 * John. Relationship is "John", "went", "London".
 *
 * <p>Formally this is defined as two conditions from the paper:
 *
 * <ul>
 *   <li>RP1: Entity1 and Entity2 have a NP+VP phrase structure. One is in covered by an NP and //
 *       the other in a VP.
 *   <li>RP2: Interaction word A in the VP (in NP+VP)
 * </ul>
 *
 * Our implementation approach is:
 *
 * <ol>
 *   <li>1. For each interaction word, find the immediate covering VP chunk.
 *   <li>2. Look through the parents to find if any any are a NP+VP relation
 *   <li>3. If any are we output all the entities within the NP and all the entities under the
 *       original covering VP chunk (from 1)
 * </ol>
 *
 * Here 3 seems to be stricter than the original paper. In the original they imply that you might
 * use the VP from the NP+VP relation. But the VP and NP can be huge and frankly largely unrelated
 * to the interaction word that initiated the search. In fact in many cases you might find a NP+VP
 * for the whole sentence, even though the interaction word which triggered the NP+VP search was in
 * a small subclause. By focusing only on the immediate VP we include entities which at least have
 * direct relevance with the VP and hence the interaction words.
 *
 * <p>Note this requires the following annotations: Sentence, WordToken, PhraseChunk, Entity,
 * Interaction.
 *
 * @baleen.javadoc
 */
public class UbmreConstituent extends AbstractInteractionBasedSentenceRelationshipAnnotator {

  /**
   * Limit the search to the first NP+VP structure.
   *
   * <p>If set to false then the entire tree will be traversed looking for NP+VP matches, and thus
   * the number of relationships will be greated (although those relationships might be more
   * tenuous). If false then only the closest (lowest in the tree) NP+VP will be used.
   *
   * @baleen.config true
   */
  public static final String KEY_LIMIT = "limit";

  @ConfigurationParameter(name = KEY_LIMIT, defaultValue = "true")
  private Boolean limitedSearch;

  private Map<Interaction, Collection<WordToken>> interactionCoveringTokens;

  private ParseTree parseTree;

  @Override
  protected void preExtract(JCas jCas) {
    super.preExtract(jCas);

    parseTree = ParseTree.build(jCas);

    interactionCoveringTokens = JCasUtil.indexCovering(jCas, Interaction.class, WordToken.class);
  }

  @Override
  protected void postExtract(JCas jCas) {
    super.postExtract(jCas);

    parseTree = null;
  }

  @Override
  protected Stream<Relation> extract(
      JCas jCas,
      Sentence sentence,
      Collection<Interaction> interactions,
      Collection<Entity> entities) {

    final Stream<Relation> phrase = extractPhrases(jCas, interactions);

    return distinct(phrase);
  }

  /**
   * Extract phrases.
   *
   * @param jCas the j cas
   * @param interactions the interactions
   * @param entities the entities
   * @return the stream
   */
  private Stream<Relation> extractPhrases(JCas jCas, Collection<Interaction> interactions) {
    return interactions.stream().flatMap(interaction -> extractPhrase(jCas, interaction));
  }

  private Stream<? extends Relation> extractPhrase(JCas jCas, Interaction i) {
    // NOTE: This still links entities high up (in a great-grandparent with those below). They
    // have little to do with those at the lower levels. See TODO comment below to address this
    // at recall costs.
    final Collection<WordToken> tokens = interactionCoveringTokens.get(i);

    final Set<ParseTreeNode> nodes =
        tokens
            .stream()
            .map(parseTree::getParent)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

    if (nodes.isEmpty()) {
      // Very unlikely to arrive here - it would be a word without a covering chunk!
      return Stream.empty();
    } else {
      // NOTE: We only pick the first VP, but what if there are more than one?
      // (For better quality, though less results, we should pick the best smallest VP
      // ... which will be the first as used here (nearest parent to the word in the parse
      // tree.

      final ParseTreeNode node = nodes.iterator().next();

      final List<Relation> relations = new ArrayList<>();

      node.traverseParent(
          (parent, child) -> {
            final int childIndex = parent.getChildren().indexOf(child);
            if (childIndex > 0 && isVerbPhrase(child)) {
              final ParseTreeNode sibling = parent.getChildren().get(childIndex - 1);

              if (isNounPhrase(sibling)) {
                // We are in a NP+VP, with an interaction word.
                // We add the entities covered by NP and the by the original node's children
                addRelations(jCas, i, sibling.getChunk(), node).forEach(relations::add);

                // If limited search we stop now
                return !limitedSearch;
              }
            }

            // If we didn't find it keep looking
            return true;
          });

      return relations.stream();
    }
  }

  private Stream<Relation> addRelations(
      JCas jCas, Interaction interaction, PhraseChunk nounPhrase, ParseTreeNode verbNode) {
    final List<Entity> nounEntities = JCasUtil.selectCovered(jCas, Entity.class, nounPhrase);
    final List<Entity> verbEntities = new ArrayList<>();

    // WE depart from the paper again, we don't want to look in NP+VP structures under our VP.
    // This because they are self contained and and we just grab there entities 'because they
    // are under the VP' we get all sorts of unrelated subclauses.

    // We can't use verbNode.traverse here as it doesn't allow us to be selective about the
    // children we want to decend into

    extractEntitiesFromVerbPhrase(jCas, verbEntities, verbNode);

    return createPairwiseRelations(jCas, interaction, nounEntities, verbEntities, 1.0f);
  }

  private void extractEntitiesFromVerbPhrase(
      JCas jCas, List<Entity> verbEntities, ParseTreeNode node) {

    List<ParseTreeNode> children = node.getChildren();
    if (children == null || children.isEmpty()) {
      // We are ok to pull out any entities from here
      verbEntities.addAll(JCasUtil.selectCovered(jCas, Entity.class, node.getChunk()));
    } else {
      for (int i = 0; i < children.size(); i++) {

        // Check if we are an NP and the next is an VP
        if (isNounPhrase(children.get(i))
            && i + 1 < children.size()
            && isVerbPhrase(children.get(i + 1))) {
          // Don't go into NP+VP structures
          break;
        }

        extractEntitiesFromVerbPhrase(jCas, verbEntities, children.get(i));
      }
    }
  }

  private boolean isNounPhrase(ParseTreeNode node) {
    return "NP".equals(node.getChunk().getChunkType());
  }

  private boolean isVerbPhrase(ParseTreeNode node) {
    return "VP".equals(node.getChunk().getChunkType());
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(
            Sentence.class, WordToken.class, PhraseChunk.class, Interaction.class, Entity.class),
        ImmutableSet.of(Relation.class));
  }
}
