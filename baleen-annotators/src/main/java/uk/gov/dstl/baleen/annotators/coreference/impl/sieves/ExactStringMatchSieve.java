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

/**
 * Coreference based on exact matching.
 *
 * <p>See 3.3.3 Pass 3.
 */
public class ExactStringMatchSieve extends AbstractCoreferenceSieve {
  private static final Set<String> EXCLUDED = new HashSet<>(Arrays.asList("that", "there"));

  /** Constructor for ExactStringMatchSieve */
  public ExactStringMatchSieve(JCas jCas, List<Cluster> clusters, List<Mention> mentions) {
    super(jCas, clusters, mentions);
  }

  @Override
  public void sieve() {
    List<Mention> mentions =
        getMentions(MentionType.ENTITY, MentionType.NP).stream()
            .filter(m -> m.getHead() != null)
            .filter(m -> !EXCLUDED.contains(m.getHead().toLowerCase()))
            .collect(Collectors.toList());

    for (int i = 0; i < mentions.size(); i++) {
      final Mention a = mentions.get(i);
      final String aText = a.getText();

      for (int j = i + 1; j < mentions.size(); j++) {
        final Mention b = mentions.get(j);
        final String bText = b.getText();

        if (aText.equalsIgnoreCase(bText)) {
          addToCluster(a, b);
        }
      }
    }
  }
}
