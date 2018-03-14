// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.testing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

/** Dummy annotator that annotates numbers */
public class DummyAnnotator2 extends JCasAnnotator_ImplBase {

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // Do nothing
    Pattern p = Pattern.compile("\\d+");
    Matcher m = p.matcher(aJCas.getDocumentText());
    while (m.find()) {
      Annotation a = new Annotation(aJCas);
      a.setBegin(m.start());
      a.setEnd(m.end());

      a.addToIndexes();
    }
  }
}
