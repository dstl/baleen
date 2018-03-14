// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.coreference.impl.sieves;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.uima.jcas.JCas;

import com.google.common.base.Splitter;

import uk.gov.dstl.baleen.annotators.coreference.impl.data.Cluster;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.Mention;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.MentionType;
import uk.gov.dstl.baleen.resources.utils.StopwordUtils;

/** Head matching sieve that has controllable parameters. */
public class StrictHeadMatchSieve extends AbstractCoreferenceSieve {

  private final boolean compatibleModifiers;
  private final boolean wordInclusion;
  private final Pattern stopwordsPattern;

  private static final Splitter WHITESPACE_SPLITTER =
      Splitter.on(" ").omitEmptyStrings().trimResults();

  /** Constructor for StrictHeadMatchSieve */
  public StrictHeadMatchSieve(
      JCas jCas,
      List<Cluster> clusters,
      List<Mention> mentions,
      boolean compatibleModifiers,
      boolean wordInclusion,
      Collection<String> stopwords) {
    super(jCas, clusters, mentions);

    this.compatibleModifiers = compatibleModifiers;
    this.wordInclusion = wordInclusion;
    this.stopwordsPattern = StopwordUtils.buildStopwordPattern(stopwords, false);
  }

  @Override
  public void sieve() {
    // TODO: We really need to work over clusters for this to make sense!

    List<Mention> mentions = getMentionsWithHead(MentionType.ENTITY, MentionType.NP);

    for (int i = 0; i < mentions.size(); i++) {
      final Mention a = mentions.get(i);

      for (int j = i + 1; j < mentions.size(); j++) {
        final Mention b = mentions.get(j);

        if (shouldAddToCluster(a, b)) addToCluster(a, b);
      }
    }
  }

  private boolean haveSubsetOfSameModifier(Mention a, Mention b) {
    final Set<String> aModifiers = getModifiers(a);
    final Set<String> bModifiers = getModifiers(b);

    // NOTE: This is ordered, a is earlier than b and it is unusal to introduce more information
    // to an entity later in the document
    return !aModifiers.isEmpty() && !bModifiers.isEmpty() && aModifiers.containsAll(bModifiers);
  }

  // TODO: This should at a cluster level
  private boolean hasSubsetOfNonStopWords(Mention a, Mention b) {
    final List<String> aNonStop = getNonStopWords(a);
    final List<String> bNonStop = getNonStopWords(b);

    // TODO: This should not include the head word? See the paper for clarification.

    // NOTE: This is ordered, a is earlier than b and it is unusual to introduce more information
    // to an entity later in the document

    // NOTE: We enforce that the set isn't empty otherwise we aren't really testing anything
    return !aNonStop.isEmpty() && !bNonStop.isEmpty() && aNonStop.containsAll(bNonStop);
  }

  private List<String> getNonStopWords(Mention a) {
    return WHITESPACE_SPLITTER.splitToList(clean(a.getText().toLowerCase()));
  }

  private String clean(String text) {
    return text.replaceAll(stopwordsPattern.pattern(), "");
  }

  private boolean shouldAddToCluster(Mention a, Mention b) {
    String aHead = a.getHead().toLowerCase();
    String bHead = b.getHead().toLowerCase();

    // Entity head match - does one head contain the others
    if (!aHead.contains(bHead) && !bHead.contains(aHead)) {
      return false;
    }

    // Word inclusion - stop words of the mention are in the cluster
    if (wordInclusion && !hasSubsetOfNonStopWords(a, b)) {
      return false;
    }

    // Compatible modifiers only - do the two candidate mentions have the same adject /
    // nouns
    if (compatibleModifiers && !haveSubsetOfSameModifier(a, b)) {
      return false;
    }

    // Not i-within-i
    // NOTE: We just check for overlap here, not if a sub-NP, which is a cheap test and
    // can come first (but not in the cluster based case since, then we need to find the
    // mentions to test first.
    if (a.overlaps(b)) {
      return false;
    }

    return true;
  }
}
