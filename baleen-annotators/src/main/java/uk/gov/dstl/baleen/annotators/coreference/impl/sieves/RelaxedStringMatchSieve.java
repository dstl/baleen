// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.coreference.impl.sieves;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.coreference.impl.data.Cluster;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.Mention;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.MentionType;

/** String matching which is more easily satisfied than exact matching. */
public class RelaxedStringMatchSieve extends AbstractCoreferenceSieve {

  private static final Set<String> EXCLUDED = new HashSet<>(Arrays.asList("that", "there"));

  /** Constructor for RelaxedStringMatchSieve */
  public RelaxedStringMatchSieve(JCas jCas, List<Cluster> clusters, List<Mention> mentions) {
    super(jCas, clusters, mentions);
  }

  @Override
  public void sieve() {
    // Text says nominal mention, we assume that to be mean Entity
    List<Mention> mentions =
        getMentionsWithHead(MentionType.ENTITY).stream()
            .filter(m -> !EXCLUDED.contains(m.getHead().toLowerCase()))
            .collect(Collectors.toList());

    for (int i = 0; i < mentions.size(); i++) {
      final Mention a = mentions.get(i);

      for (int j = i + 1; j < mentions.size(); j++) {
        final Mention b = mentions.get(j);

        if (a.getHead().equalsIgnoreCase(b.getHead())) {
          addToCluster(a, b);
        }
      }
    }
  }
}
