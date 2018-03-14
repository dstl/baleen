// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils.select;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Token queue tests. */
public class TokenQueueTest {
  @Test
  public void chompBalanced() {
    TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
    String pre = tq.consumeTo("(");
    String guts = tq.chompBalanced('(', ')');
    String remainder = tq.remainder();

    assertEquals(":contains", pre);
    assertEquals("one (two) three", guts);
    assertEquals(" four", remainder);
  }

  @Test
  public void chompEscapedBalanced() {
    TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
    String pre = tq.consumeTo("(");
    String guts = tq.chompBalanced('(', ')');
    String remainder = tq.remainder();

    assertEquals(":contains", pre);
    assertEquals("one (two) \\( \\) \\) three", guts);
    assertEquals("one (two) ( ) ) three", TokenQueue.unescape(guts));
    assertEquals(" four", remainder);
  }

  @Test
  public void chompBalancedMatchesAsMuchAsPossible() {
    TokenQueue tq = new TokenQueue("unbalanced(something(or another)) else");
    tq.consumeTo("(");
    String match = tq.chompBalanced('(', ')');
    assertEquals("something(or another)", match);
  }

  @Test
  public void unescape() {
    assertEquals("one ( ) \\", TokenQueue.unescape("one \\( \\) \\\\"));
  }

  @Test
  public void empty() {
    TokenQueue tq = new TokenQueue("");
    assertFalse(tq.matches("test"));
    assertTrue(tq.matches(""));
    assertFalse(tq.matchesAny("test", "12"));
    assertTrue(tq.matchesAny("test", ""));
    assertFalse(tq.matchesAny('t', 'e', 's', 't'));
    assertFalse(tq.matchesWhitespace());
    assertFalse(tq.matchesWord());
    assertTrue(tq.consumeIdentifier().isEmpty());
    assertTrue(tq.consumeNodeSelector().isEmpty());
  }
}
