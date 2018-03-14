// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.coreference.impl.sieves;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.uima.jcas.JCas;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.annotators.coreference.impl.data.Cluster;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.Mention;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.MentionType;
import uk.gov.dstl.baleen.types.language.PhraseChunk;

/**
 * Base class for coreference sieves.
 *
 * <p>Provides helper functions to manage clusters, etc.
 */
public abstract class AbstractCoreferenceSieve implements CoreferenceSieve {

  private final JCas jCas;
  private final List<Cluster> clusters;
  private final List<Mention> mentions;

  protected AbstractCoreferenceSieve(JCas jCas, List<Cluster> clusters, List<Mention> mentions) {
    this.jCas = jCas;
    this.clusters = clusters;
    this.mentions = mentions;
  }

  protected List<Cluster> getClusters() {
    return clusters;
  }

  protected List<Mention> getMentions() {
    return mentions;
  }

  protected List<Mention> getMentions(MentionType mentionType) {
    return mentions
        .stream()
        .filter(m -> m.getType().equals(mentionType))
        .collect(Collectors.toList());
  }

  protected List<Mention> getMentions(MentionType... mentionTypes) {
    List<MentionType> mentionTypesList = Arrays.asList(mentionTypes);
    return mentions
        .stream()
        .filter(m -> mentionTypesList.contains(m.getType()))
        .collect(Collectors.toList());
  }

  protected List<Mention> getMentionsWithHead(MentionType mentionType) {
    return mentions
        .stream()
        .filter(m -> m.getType().equals(mentionType) && hasHead(m))
        .collect(Collectors.toList());
  }

  protected List<Mention> getMentionsWithHead(MentionType... mentionTypes) {
    List<MentionType> mentionTypesList = Arrays.asList(mentionTypes);

    return mentions
        .stream()
        .filter(m -> mentionTypesList.contains(m.getType()) && hasHead(m))
        .collect(Collectors.toList());
  }

  protected JCas getJCas() {
    return jCas;
  }

  protected void addToCluster(Mention a, Mention b) {
    if (a.hasClusters()) {
      final Cluster cluster = a.getAnyCluster();
      cluster.add(b);
    } else if (b.hasClusters()) {
      final Cluster cluster = b.getAnyCluster();
      cluster.add(a);
    } else {
      clusters.add(new Cluster(a, b));
    }
  }

  protected void addPairwiseToCluster(Collection<Mention> a, Collection<Mention> b) {

    // Technically these will all need to end up in the same cluster

    final Cluster cluster =
        a.stream()
            .map(x -> x.getAnyCluster())
            .filter(Objects::nonNull)
            .findAny()
            .orElseGet(
                () ->
                    b.stream()
                        .map(x -> x.getAnyCluster())
                        .filter(Objects::nonNull)
                        .findAny()
                        .orElseGet(
                            () -> {
                              final Cluster c = new Cluster();
                              clusters.add(c);
                              return c;
                            }));

    cluster.addAll(a);
    cluster.addAll(b);
  }

  protected void addCoveredToCluster(PhraseChunk a, PhraseChunk b) {
    final List<Mention> aMentions = findMentionsExactly(a.getBegin(), a.getEnd());
    final List<Mention> bMentions = findMentionsExactly(b.getBegin(), b.getEnd());

    addPairwiseToCluster(aMentions, bMentions);
  }

  protected List<Mention> findMentionsExactly(int begin, int end) {
    return getMentions()
        .stream()
        .filter(m -> begin == m.getAnnotation().getBegin() && m.getAnnotation().getEnd() == end)
        .collect(Collectors.toList());
  }

  protected List<Mention> findMentionsUnder(int begin, int end) {
    return getMentions()
        .stream()
        .filter(m -> begin >= m.getAnnotation().getBegin() && m.getAnnotation().getEnd() <= end)
        .collect(Collectors.toList());
  }

  protected List<Mention> findMentionAbove(int begin, int end) {
    return getMentions()
        .stream()
        .filter(m -> m.getAnnotation().getBegin() <= begin && end <= m.getAnnotation().getEnd())
        .collect(Collectors.toList());
  }

  protected Set<String> getModifiers(Mention a) {
    // In the paper they say N and J (adjective) but we need cardinal too otherwise "200 people"
    // discards the 200
    // TODO: Modifiers up to head word? See paper
    return a.getWords()
        .stream()
        .filter(
            w ->
                w.getPartOfSpeech().startsWith("N")
                    || w.getPartOfSpeech().startsWith("J")
                    || w.getPartOfSpeech().startsWith("CD"))
        .map(w -> w.getCoveredText().toLowerCase())
        .collect(Collectors.toSet());
  }

  protected static boolean hasHead(Mention m) {
    return !Strings.isNullOrEmpty(m.getHead());
  }
}
