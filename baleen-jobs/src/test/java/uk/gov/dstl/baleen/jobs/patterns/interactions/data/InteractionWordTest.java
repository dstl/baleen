// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.jobs.patterns.interactions.data;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.sf.extjwnl.data.POS;

import org.junit.Assert;
import org.junit.Test;

import uk.gov.dstl.baleen.jobs.interactions.data.InteractionWord;
import uk.gov.dstl.baleen.jobs.interactions.data.RelationPair;
import uk.gov.dstl.baleen.jobs.interactions.data.Word;

public class InteractionWordTest {

  @Test
  public void testGetWord() {
    final Set<RelationPair> set = new HashSet<>();
    final Word word = new Word("lemma", POS.NOUN);
    final InteractionWord iw = new InteractionWord(word, set);

    assertEquals(word, iw.getWord());
    assertEquals(set, iw.getPairs());
  }

  @Test
  public void testHashCodeAndEquals() {
    final Set<RelationPair> set1 = new HashSet<>();
    final Word word1 = new Word("lemma1", POS.NOUN);
    final Set<RelationPair> set2 = new HashSet<>(Arrays.asList(new RelationPair("s", "t")));
    final Word word2 = new Word("lemma2", POS.NOUN);

    final InteractionWord iw11 = new InteractionWord(word1, set1);
    final InteractionWord iw12 = new InteractionWord(word1, set2);
    final InteractionWord iw21 = new InteractionWord(word2, set1);
    final InteractionWord iw22 = new InteractionWord(word2, set2);

    Assert.assertNotEquals(iw11, iw12);
    Assert.assertNotEquals(iw11, iw21);
    Assert.assertNotEquals(iw11, iw22);

    Assert.assertNotEquals(iw11.hashCode(), iw12.hashCode());
    Assert.assertNotEquals(iw11.hashCode(), iw21.hashCode());
    Assert.assertNotEquals(iw11.hashCode(), iw22.hashCode());
  }
}
