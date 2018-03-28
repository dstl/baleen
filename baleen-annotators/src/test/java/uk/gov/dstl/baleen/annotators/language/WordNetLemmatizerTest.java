// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.language;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.resources.SharedWordNetResource;
import uk.gov.dstl.baleen.types.language.WordLemma;
import uk.gov.dstl.baleen.types.language.WordToken;

public class WordNetLemmatizerTest extends AbstractAnnotatorTest {

  private final ExternalResourceDescription wordnetErd;

  public WordNetLemmatizerTest() {
    super(WordNetLemmatizer.class);
    wordnetErd =
        ExternalResourceFactory.createExternalResourceDescription(
            "wordnet", SharedWordNetResource.class);
  }

  @Test
  public void testAddsLemma() throws UIMAException, ResourceInitializationException {
    jCas.setDocumentText("Is this working?");

    final WordToken t = new WordToken(jCas);
    t.setBegin(jCas.getDocumentText().indexOf("working"));
    t.setEnd(t.getBegin() + "working".length());
    t.setPartOfSpeech("VERB");
    t.addToIndexes();

    processJCas("wordnet", wordnetErd);

    final List<WordToken> out = new ArrayList<>(JCasUtil.select(jCas, WordToken.class));
    assertEquals("work", out.get(0).getLemmas(0).getLemmaForm());
  }

  @Test
  public void testAddsLemmaToExistingLemmas()
      throws UIMAException, ResourceInitializationException {
    jCas.setDocumentText("Is this working?");

    final WordToken s = new WordToken(jCas);
    s.setBegin(jCas.getDocumentText().indexOf("working"));
    s.setEnd(s.getBegin() + "working".length());
    s.setPartOfSpeech("VERB");
    s.setLemmas(new FSArray(jCas, 1));
    final WordLemma existingLemma = new WordLemma(jCas);
    existingLemma.setPartOfSpeech("existing");
    existingLemma.setLemmaForm("existing");
    s.setLemmas(0, existingLemma);
    s.addToIndexes();

    processJCas("wordnet", wordnetErd);

    final List<WordToken> out = new ArrayList<>(JCasUtil.select(jCas, WordToken.class));

    assertEquals(existingLemma, out.get(0).getLemmas(0));
    assertEquals("work", out.get(0).getLemmas(1).getLemmaForm());
  }
}
