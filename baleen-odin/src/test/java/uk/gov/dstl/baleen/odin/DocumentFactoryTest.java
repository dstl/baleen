// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.odin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.clulab.processors.Document;
import org.clulab.struct.CorefChains;
import org.clulab.struct.CorefMention;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import scala.Option;
import scala.collection.Iterable;
import scala.collection.Iterator;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Sentence;

@RunWith(MockitoJUnitRunner.class)
public class DocumentFactoryTest {

  @Mock SentenceFactory sentenceFactory;

  @Test
  public void canConstruct() throws UIMAException {
    JCas jCas = JCasFactory.createJCas();
    assertNotNull(new DocumentFactory(jCas, sentenceFactory));
  }

  @Test
  public void canCreateDocument() throws UIMAException {
    JCas jCas = JCasFactory.createJCas();
    DocumentFactory documentFactory = new DocumentFactory(jCas, sentenceFactory);

    Document document = documentFactory.create();
    assertNotNull(document);
  }

  @Test
  public void canCreateDocumentWithText() throws UIMAException {
    JCas jCas = JCasFactory.createJCas();
    String text = "test";
    jCas.setDocumentText(text);
    DocumentFactory documentFactory = new DocumentFactory(jCas, sentenceFactory);

    Document document = documentFactory.create();
    assertEquals(text, document.text().get());
  }

  @Test
  public void canCreateDocumentWithSentences() throws UIMAException {
    JCas jCas = JCasFactory.createJCas();
    OdinSentence sentence = mock(OdinSentence.class);
    when(sentenceFactory.create()).thenReturn(ImmutableList.of(sentence));
    DocumentFactory documentFactory = new DocumentFactory(jCas, sentenceFactory);

    Document document = documentFactory.create();
    assertEquals(1, document.sentences().length);
    assertEquals(sentence, document.sentences()[0]);
  }

  @Test
  public void canCreateDocumentWithCoreferences() throws UIMAException {
    JCas jCas = JCasFactory.createJCas();
    jCas.setDocumentText("John Smith was here. Then he went.");

    Sentence s1 = Annotations.createSentence(jCas, 0, 20);
    Sentence s2 = Annotations.createSentence(jCas, 21, 34);
    Person p1 = Annotations.createPerson(jCas, 0, 10, "John Smith");
    Person p2 = Annotations.createPerson(jCas, 26, 28, "he");
    Annotations.createReferenceTarget(jCas, p1, p2);

    OdinSentence sentence1 = mock(OdinSentence.class, "s1");
    OdinSentence sentence2 = mock(OdinSentence.class, "s2");
    CorefMention coref1 = new CorefMention(0, 0, p1.getBegin(), p1.getEnd(), 0);
    CorefMention coref2 = new CorefMention(1, 1, 5, 7, 0);

    when(sentenceFactory.create()).thenReturn(ImmutableList.of(sentence1, sentence2));
    when(sentence1.getBaleenSentence()).thenReturn(s1);
    when(sentence2.getBaleenSentence()).thenReturn(s2);
    when(sentence1.corefMention(p1, 0)).thenReturn(coref1);
    when(sentence2.corefMention(p2, 0)).thenReturn(coref2);

    DocumentFactory documentFactory = new DocumentFactory(jCas, sentenceFactory);

    Document document = documentFactory.create();
    assertEquals(2, document.sentences().length);

    Option<CorefChains> coreferenceChains = document.coreferenceChains();
    assertTrue(coreferenceChains.isDefined());

    CorefChains corefChains = coreferenceChains.get();
    assertFalse(corefChains.isEmpty());
    assertEquals(2, corefChains.rawMentions().size());

    Option<Iterable<CorefMention>> option1 = corefChains.getChain(0, 0);
    assertTrue(option1.isDefined());

    Iterator<CorefMention> iterator = option1.get().iterator();
    assertEquals(coref2, iterator.next());
    assertEquals(coref1, iterator.next());
    assertFalse(iterator.hasNext());
  }

  @Test
  public void ensureSenteceOrderingCorrect() throws UIMAException {
    JCas jCas = JCasFactory.createJCas();
    jCas.setDocumentText("This is sentence one. This is sentence two. This is sentence three.");

    Annotations.createWordTokens(jCas);
    List<Sentence> sentences = Annotations.createSentences(jCas);

    OdinSentence sentence1 = mock(OdinSentence.class, "s1");
    OdinSentence sentence2 = mock(OdinSentence.class, "s2");
    OdinSentence sentence3 = mock(OdinSentence.class, "s3");

    when(sentenceFactory.create()).thenReturn(ImmutableList.of(sentence1, sentence2, sentence3));
    when(sentence1.getBaleenSentence()).thenReturn(sentences.get(0));
    when(sentence2.getBaleenSentence()).thenReturn(sentences.get(1));
    when(sentence3.getBaleenSentence()).thenReturn(sentences.get(2));

    DocumentFactory documentFactory = new DocumentFactory(jCas, sentenceFactory);

    OdinDocument document = documentFactory.create();
    OdinSentence[] odinSentences = (OdinSentence[]) document.sentences();
    assertEquals(3, odinSentences.length);

    assertEquals(sentences.get(0), odinSentences[0].getBaleenSentence());
    assertEquals(sentences.get(1), odinSentences[1].getBaleenSentence());
    assertEquals(sentences.get(2), odinSentences[2].getBaleenSentence());

    assertEquals(document.findSentence(sentences.get(0)), odinSentences[0]);
    assertEquals(document.findSentence(sentences.get(1)), odinSentences[1]);
    assertEquals(document.findSentence(sentences.get(2)), odinSentences[2]);
  }

  @Test(expected = IllegalStateException.class)
  public void ensureDuplicateKeyNotAllowed() throws UIMAException {
    JCas jCas = JCasFactory.createJCas();
    jCas.setDocumentText("This is sentence one. This is sentence two. This is sentence three.");

    List<Sentence> sentences = Annotations.createSentences(jCas);

    OdinSentence sentence1 = mock(OdinSentence.class, "s1");
    OdinSentence sentence2 = mock(OdinSentence.class, "s2");
    OdinSentence sentence3 = mock(OdinSentence.class, "s3");

    when(sentenceFactory.create()).thenReturn(ImmutableList.of(sentence1, sentence2, sentence3));
    when(sentence1.getBaleenSentence()).thenReturn(sentences.get(0));
    // Duplicate keys
    when(sentence2.getBaleenSentence()).thenReturn(sentences.get(1));
    when(sentence3.getBaleenSentence()).thenReturn(sentences.get(1));

    DocumentFactory documentFactory = new DocumentFactory(jCas, sentenceFactory);

    documentFactory.create();
  }
}
