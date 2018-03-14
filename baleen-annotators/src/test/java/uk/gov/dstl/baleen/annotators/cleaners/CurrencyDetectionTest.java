// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.common.Money;

public class CurrencyDetectionTest extends AnnotatorTestBase {

  @Test
  public void testText() throws Exception {
    jCas.setDocumentText("10 USD is the equivalent of GBP 6.60 or 8.80EUR");

    Money usd = new Money(jCas);
    usd.setValue("10 USD");
    usd.addToIndexes();

    Money gbp = new Money(jCas);
    gbp.setValue("GBP 6.60");
    gbp.addToIndexes();

    Money eur = new Money(jCas);
    eur.setValue("8.80EUR");
    eur.addToIndexes();

    AnalysisEngine cleaner = AnalysisEngineFactory.createEngine(CurrencyDetection.class);
    cleaner.process(jCas);

    assertEquals(3, JCasUtil.select(jCas, Money.class).size());
    assertEquals("10 USD", JCasUtil.selectByIndex(jCas, Money.class, 0).getValue());
    assertEquals("USD", JCasUtil.selectByIndex(jCas, Money.class, 0).getCurrency());

    assertEquals("GBP 6.60", JCasUtil.selectByIndex(jCas, Money.class, 1).getValue());
    assertEquals("GBP", JCasUtil.selectByIndex(jCas, Money.class, 1).getCurrency());

    assertEquals("8.80EUR", JCasUtil.selectByIndex(jCas, Money.class, 2).getValue());
    assertEquals("EUR", JCasUtil.selectByIndex(jCas, Money.class, 2).getCurrency());
  }

  @Test
  public void testSymbols() throws Exception {
    jCas.setDocumentText("$10 is the equivalent of £6.60 or €8.80");

    Money usd = new Money(jCas);
    usd.setValue("$10");
    usd.addToIndexes();

    Money gbp = new Money(jCas);
    gbp.setValue("£6.60");
    gbp.addToIndexes();

    Money eur = new Money(jCas);
    eur.setValue("€8.80");
    eur.addToIndexes();

    AnalysisEngine cleaner = AnalysisEngineFactory.createEngine(CurrencyDetection.class);
    cleaner.process(jCas);

    assertEquals(3, JCasUtil.select(jCas, Money.class).size());
    assertEquals("$10", JCasUtil.selectByIndex(jCas, Money.class, 0).getValue());
    assertEquals("USD", JCasUtil.selectByIndex(jCas, Money.class, 0).getCurrency());

    assertEquals("£6.60", JCasUtil.selectByIndex(jCas, Money.class, 1).getValue());
    assertEquals("GBP", JCasUtil.selectByIndex(jCas, Money.class, 1).getCurrency());

    assertEquals("€8.80", JCasUtil.selectByIndex(jCas, Money.class, 2).getValue());
    assertEquals("EUR", JCasUtil.selectByIndex(jCas, Money.class, 2).getCurrency());
  }
}
