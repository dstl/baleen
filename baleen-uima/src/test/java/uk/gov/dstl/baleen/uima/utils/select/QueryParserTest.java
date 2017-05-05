//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils.select;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class QueryParserTest {
  @Test
  public void testOrGetsCorrectPrecedence() {
    // tests that a selector "a b, c d, e f" evals to (a AND b) OR (c AND d) OR (e AND f)"
    // top level or, three child ands
    Evaluator<Object> eval = QueryParser.parse("a b, c d, e f");
    assertTrue(eval instanceof CombiningEvaluator.Or);
    CombiningEvaluator.Or<Object> or = (CombiningEvaluator.Or<Object>) eval;
    assertEquals(3, or.evaluators.size());
    for (Evaluator<Object> innerEval : or.evaluators) {
      assertTrue(innerEval instanceof CombiningEvaluator.And);
      CombiningEvaluator.And<Object> and = (CombiningEvaluator.And<Object>) innerEval;
      assertEquals(2, and.evaluators.size());
      assertTrue(and.evaluators.get(0) instanceof Evaluator.TypeName);
      assertTrue(and.evaluators.get(1) instanceof StructuralEvaluator.Parent);
    }
  }

  @Test
  public void testParsesMultiCorrectly() {
    Evaluator<Object> eval = QueryParser.parse(".foo > ol, ol > li + li");
    assertTrue(eval instanceof CombiningEvaluator.Or);
    CombiningEvaluator.Or<Object> or = (CombiningEvaluator.Or<Object>) eval;
    assertEquals(2, or.evaluators.size());

    CombiningEvaluator.And<Object> andLeft = (CombiningEvaluator.And<Object>) or.evaluators.get(0);
    CombiningEvaluator.And<Object> andRight = (CombiningEvaluator.And<Object>) or.evaluators.get(1);

    assertEquals("ol :immediateParent(.foo)", andLeft.toString());
    assertEquals(2, andLeft.evaluators.size());
    assertEquals("li :prev(li :immediateParent(ol))", andRight.toString());
    assertEquals(2, andLeft.evaluators.size());
  }

  @Test
  public void testEvaluatorStrings() {
    assertEquals("*", QueryParser.parse("*").toString());
    assertEquals(":empty", QueryParser.parse(":empty").toString());
    assertEquals(":root", QueryParser.parse(":root").toString());
    assertEquals("Type", QueryParser.parse("Type").toString());
    assertEquals(".class", QueryParser.parse(".class").toString());
    assertEquals("[a^=b]", QueryParser.parse("[a^=b]").toString());
    assertEquals("Test [a!=b]", QueryParser.parse("Test[a!=b]").toString());
    assertEquals(".class [c*=d]", QueryParser.parse(".class[c*=d]").toString());
    assertEquals(":matchesOwn(boom)", QueryParser.parse(":matchesOwn(boom)").toString());
    assertEquals(":has(child :immediateParent(Parent))",
        QueryParser.parse(":has(Parent > child)").toString());
    assertEquals(":not(child :parent(Ancestor))",
        QueryParser.parse(":not(Ancestor child)").toString());
    assertEquals("S2 :nth-child(2) :prev(S1)",
        QueryParser.parse("S1 + S2:nth-child(2)").toString());
    assertEquals("S2 [level] :prev*(S1 :nth-of-type(2n+1))",
        QueryParser.parse("S1:nth-of-type(odd) ~ S2[level]").toString());
    assertEquals("S2 [att~=test] :prev*(S1 :contains(test))",
        QueryParser.parse("S1:contains(test) ~ S2[att~=test]").toString());
    assertEquals("S2 :only-child :parent(S1 :containsOwn(t))",
        QueryParser.parse("S1:containsOwn(t) S2:only-child").toString());
    assertEquals("S2 :gt(3) :parent(S1 :matches((?i)t))",
        QueryParser.parse("S1:matches((?i)t) S2:gt(3)").toString());
    assertEquals("S3 [s$=t] :immediateParent(S2 :lt(3) :parent(S1 :eq(1)))",
        QueryParser.parse("S1:eq(1) S2:lt(3) > S3[s$=t]").toString());
  }

  @Test(expected = Selector.SelectorParseException.class)
  public void exceptionOnUncloseAttribute() {
    QueryParser.parse("section > a[href=\"]");
  }

  @Test(expected = Selector.SelectorParseException.class)
  public void testParsesSingleQuoteInContains() {
    QueryParser.parse("p:contains(One \" One)");
  }
}
