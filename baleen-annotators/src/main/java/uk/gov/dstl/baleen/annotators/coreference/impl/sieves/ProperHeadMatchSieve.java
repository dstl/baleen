// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.coreference.impl.sieves;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.uima.jcas.JCas;

import com.google.common.primitives.Doubles;

import uk.gov.dstl.baleen.annotators.coreference.impl.data.Cluster;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.Mention;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.MentionType;

/** Sieve based on exact matching of the head word. */
public class ProperHeadMatchSieve extends AbstractCoreferenceSieve {
  private static final Pattern NUMBER =
      Pattern.compile("-?\\d+(,\\d+)*(\\.\\d+)?[k|m|b]?", Pattern.CASE_INSENSITIVE);

  private final Set<String> spatialModifiers =
      new HashSet<String>(
          Arrays.asList(
              "northern",
              "southern",
              "western",
              "eastern",
              "south",
              "east",
              "north",
              "west",
              "central",
              "upper",
              "lower",
              "middle",
              "inner",
              "outer"));

  /** Constructor for ProperHeadMatchSieve */
  public ProperHeadMatchSieve(JCas jCas, List<Cluster> clusters, List<Mention> mentions) {
    super(jCas, clusters, mentions);
  }

  @Override
  public void sieve() {
    // Note: Head must be proper nouns, but ours are by construction
    List<Mention> mentions = getMentionsWithHead(MentionType.ENTITY, MentionType.NP);

    for (int i = 0; i < mentions.size(); i++) {
      final Mention a = mentions.get(i);

      String aHead = a.getHead().toLowerCase();

      for (int j = i + 1; j < mentions.size(); j++) {
        final Mention b = mentions.get(j);

        String bHead = b.getHead().toLowerCase();

        if (aHead.equals(bHead) && shouldAddMentionsToCluster(a, b)) {
          addToCluster(a, b);
        }
      }
    }
  }

  private boolean hasSameModifiers(Mention a, Mention b) {
    // TODO: The paper says location named entities, other proper nouns or other spatial
    // modifiers but since locations should be other proper nouns we ignore that clause. We
    // could look for Locations covered by the annotation.

    final Set<String> aModifiers = getSpatialAndPNModifier(a);
    final Set<String> bModifiers = getSpatialAndPNModifier(b);

    return aModifiers.size() == bModifiers.size() && aModifiers.containsAll(bModifiers);
  }

  private Set<String> getSpatialAndPNModifier(Mention a) {
    return a.getWords()
        .stream()
        .filter(
            w ->
                w.getPartOfSpeech().startsWith("NP")
                    || spatialModifiers.contains(w.getCoveredText()))
        .map(w -> w.getCoveredText().toLowerCase())
        .collect(Collectors.toSet());
  }

  // Asymetric
  private List<Double> extractNumbers(String text) {
    final List<Double> list = new LinkedList<>();
    final Matcher matcher = NUMBER.matcher(text);
    while (matcher.find()) {
      final Double d = Doubles.tryParse(matcher.group().replaceAll(",", ""));
      if (d != null) {
        list.add(d);
      }
    }
    return list;
  }

  // Asymetric
  private boolean hasSameNumbers(Collection<Double> aNumbers, Collection<Double> bNumbers) {

    for (final double b : bNumbers) {
      boolean found = false;
      for (final double a : aNumbers) {
        // 'Fuzzy match' the numbers
        if (Math.abs(a - b) < 0.01 * Math.max(Math.abs(a), Math.abs(a))) {
          found = true;
          break;
        }
      }

      if (!found) {
        return false;
      }
    }

    return true;
  }

  private boolean shouldAddMentionsToCluster(Mention a, Mention b) {
    // Not i-within-i
    if (a.overlaps(b)) {
      return false;
    }

    // No modifier
    if (!hasSameModifiers(a, b)) {
      return false;
    }

    // No numerical mismatches
    final List<Double> aNumbers = extractNumbers(a.getText());
    final List<Double> bNumbers = extractNumbers(b.getText());

    if (!hasSameNumbers(aNumbers, bNumbers)) {
      return false;
    }

    return true;
  }
}
