// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.Collection;
import java.util.Collections;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Money;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Finds Money values without an assigned currency, and tries to determine what currency they are by
 * matching against a list of currency symbols and terms
 */
public class CurrencyDetection extends BaleenAnnotator {

  @Override
  public void doProcess(JCas jCas) throws AnalysisEngineProcessException {
    Collection<Money> money = JCasUtil.select(jCas, Money.class);

    for (Money m : money) {
      if (m.getCurrency() == null || m.getCurrency().isEmpty()) {
        String value = m.getValue().toUpperCase();

        if (value.contains("£") || value.contains("GBP")) {
          m.setCurrency("GBP");
        } else if (value.contains("$") || value.contains("USD")) {
          m.setCurrency("USD");
        } else if (value.contains("€") || value.contains("EUR")) {
          m.setCurrency("EUR");
        }
      }
    }
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(ImmutableSet.of(Money.class), Collections.emptySet());
  }
}
