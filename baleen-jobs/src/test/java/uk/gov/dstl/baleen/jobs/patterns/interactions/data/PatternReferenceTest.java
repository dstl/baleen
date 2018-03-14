// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.jobs.patterns.interactions.data;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;

import net.sf.extjwnl.data.POS;

import org.junit.Test;

import uk.gov.dstl.baleen.jobs.interactions.data.PatternReference;
import uk.gov.dstl.baleen.jobs.interactions.data.Word;

public class PatternReferenceTest {

  @Test
  public void test() {
    final PatternReference p =
        new PatternReference("id", new Word("a", POS.NOUN), new Word("b", POS.NOUN));

    assertEquals("id", p.getId());
    assertEquals("a", p.getTokens().get(0).getLemma());
    assertEquals(POS.NOUN, p.getTokens().get(0).getPos());

    p.setSourceType("st");
    p.setTargetType("tt");

    assertEquals("tt", p.getTargetType());
    assertEquals("st", p.getSourceType());

    final PatternReference p2 = new PatternReference("id", new Word("a", POS.NOUN));

    final HashSet<Word> tokens =
        new HashSet<>(Arrays.asList(new Word("a", POS.NOUN), new Word("b", POS.NOUN)));
    p.calculateTermFrequency(tokens);
    p2.calculateTermFrequency(tokens);

    final double similarity = p.calculateSimilarity(p2);
    assertEquals(0.5, similarity, 0.1);
  }
}
