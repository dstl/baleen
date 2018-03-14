// Dstl (c) Crown Copyright 2017
package org.apache.uima.jcas.tcas;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;

public class DocumentAnnotationTest {
  @Test
  public void docHash() throws Exception {
    JCas jcas = JCasFactory.createJCas(TypeSystemSingleton.getTypeSystemDescriptionInstance());
    jcas.setDocumentText("There is the mention of some entity in this sentence.");

    DocumentAnnotation doc = (DocumentAnnotation) jcas.getDocumentAnnotationFs();
    assertEquals("87cebccde680225b7640878d334b4cbb1c048ba1c8e66763f72cca5396a37807", doc.getHash());
  }
}
