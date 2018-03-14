// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.patterns.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PatternExtractTest {

  private PatternExtract pe;

  @Mock private Entity from;

  @Mock private Entity to;

  @Mock private WordToken token;

  @Before
  public void before() {
    pe = new PatternExtract(from, to, 0, 10);

    Mockito.when(token.getPartOfSpeech()).thenReturn("NN");
    Mockito.when(token.getCoveredText()).thenReturn("token");
  }

  @Test
  public void testFields() {
    Assert.assertSame(from, pe.getFrom());
    Assert.assertSame(to, pe.getTo());
    Assert.assertSame(0, pe.getStart());
    Assert.assertSame(10, pe.getEnd());
  }

  @Test
  public void testFromNew() {
    Assert.assertTrue(pe.isEmpty());

    Assert.assertNull(pe.getWordTokens());

    Assert.assertEquals("", pe.getText());
  }

  @Test
  public void testSetWordTokens() {
    final List<WordToken> list = new ArrayList<>();
    pe.setWordTokens(list);
    Assert.assertSame(list, pe.getWordTokens());
  }

  @Test
  public void testContains() {
    pe.setWordTokens(Collections.singletonList(token));
    Assert.assertTrue(pe.contains("this is sample text", "is"));
    Assert.assertFalse(pe.contains("this is sample text", "text"));
    Assert.assertTrue(pe.contains("this is sample text", "text", "this"));
  }

  @Test
  public void testGetText() {
    pe.setWordTokens(Collections.singletonList(token));
    Assert.assertEquals("token", pe.getText());

    pe.setWordTokens(Arrays.asList(token, token));
    Assert.assertEquals("token token", pe.getText());
  }

  @Test
  public void testIsEmpty() {
    pe.setWordTokens(Collections.singletonList(token));
    Assert.assertFalse(pe.isEmpty());

    pe.setWordTokens(Collections.emptyList());
    Assert.assertTrue(pe.isEmpty());
  }
}
