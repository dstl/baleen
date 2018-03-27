// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.odin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.clulab.struct.CorefMention;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;

@RunWith(MockitoJUnitRunner.class)
public class OdinSentenceTest {

  @Mock Sentence sentence;

  @Mock WordToken w1;

  @Mock WordToken w2;

  @Mock WordToken w3;

  @Mock Entity e1;

  @Test
  public void canConstruct() {
    assertNotNull(
        new OdinSentence(
            0,
            sentence,
            ImmutableList.of(),
            ImmutableList.of(),
            new String[0],
            new int[0],
            new int[0]));
  }

  @Test
  public void canGetConstructs() {
    OdinSentence odinSentence =
        new OdinSentence(
            1,
            sentence,
            ImmutableList.of(w1, w2),
            ImmutableList.of(Optional.of(e1), Optional.empty()),
            new String[] {"Test", "test"},
            new int[] {0, 6},
            new int[] {4, 10});

    assertEquals(1, odinSentence.getSentenceIndex());
    assertEquals(ImmutableList.of(w1, w2), odinSentence.getTokens());
    assertEquals(sentence, odinSentence.getBaleenSentence());
    assertEquals(w1, odinSentence.getToken(0));
    assertEquals(w2, odinSentence.getToken(1));
    assertEquals("Test  test", odinSentence.getSentenceText());
    assertEquals("Test", odinSentence.word(0));
    assertTrue(odinSentence.tags().isEmpty());
  }

  @Test
  public void testToString() {
    OdinSentence odinSentence =
        new OdinSentence(
            1,
            sentence,
            ImmutableList.of(w1, w2),
            ImmutableList.of(Optional.of(e1), Optional.empty()),
            new String[] {"Test", "test"},
            new int[] {0, 6},
            new int[] {4, 10});
    StringBuilder sb =
        new StringBuilder()
            .append("Tokens: Test test")
            .append("\n")
            .append("Start: [0, 6]")
            .append("\n")
            .append("End: [4, 10]")
            .append("\n")
            .append("Test___")
            .append("\n")
            .append("test___");

    assertEquals(sb.toString().trim(), odinSentence.toString().trim());
  }

  @Test
  public void canCreateCoref() {
    when(sentence.getBegin()).thenReturn(50);
    when(e1.getBegin()).thenReturn(55);
    when(e1.getEnd()).thenReturn(61);

    OdinSentence odinSentence =
        new OdinSentence(
            10,
            sentence,
            ImmutableList.of(w1, w2, w3),
            ImmutableList.of(Optional.empty(), Optional.of(e1), Optional.of(e1)),
            new String[] {"Test", "test", "t"},
            new int[] {0, 5, 10},
            new int[] {4, 9, 11});

    CorefMention corefMention = odinSentence.corefMention(e1, 4);
    assertNotNull(corefMention);
    assertEquals(4, corefMention.chainId());
    assertEquals(5, corefMention.startOffset());
    assertEquals(11, corefMention.endOffset());
  }
}
