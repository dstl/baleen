// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.dstl.baleen.resources.data.ReNounFact.ATTRIBUTE_FIELD;
import static uk.gov.dstl.baleen.resources.data.ReNounFact.OBJECT_FIELD;
import static uk.gov.dstl.baleen.resources.data.ReNounFact.SCORE_FIELD;
import static uk.gov.dstl.baleen.resources.data.ReNounFact.SUBJECT_FIELD;

import org.bson.Document;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import com.mongodb.client.MongoCollection;

public class ReNounFactTest {

  private static final String SENTENCE = "subject attribute object";
  private static final String PATTERN = "pattern";
  private static final String OBJECT = "object";
  private static final String ATTRIBUTE = "attribute";
  private static final String SUBJECT = "subject";

  @Test
  public void testReNounFactBeanMethods() {

    ReNounFact fact1 = new ReNounFact(SUBJECT, ATTRIBUTE, OBJECT);
    ReNounFact fact2 = new ReNounFact(SUBJECT, ATTRIBUTE, OBJECT, PATTERN);
    ReNounFact fact3 = new ReNounFact(SUBJECT, ATTRIBUTE, OBJECT);
    fact3.setScore(1.0);
    ReNounFact fact4 = new ReNounFact("subject1", "attribute1", "object1");
    fact4.setPattern(PATTERN);
    fact4.setSentence(SENTENCE);

    assertFalse(fact1.equals(null));
    assertTrue(fact1.equals(fact1));
    assertTrue(fact1.equals(fact2));
    assertTrue(fact1.equals(fact3));
    assertFalse(fact1.equals(fact4));

    assertEquals(SUBJECT, fact1.getSubject());
    assertEquals(ATTRIBUTE, fact1.getAttribute());
    assertEquals(OBJECT, fact1.getObject());
    assertNull(fact1.getScore());
    assertNull(fact1.getPattern());
    assertEquals(PATTERN, fact2.getPattern());
    assertEquals(SENTENCE, fact4.getSentence());

    assertTrue(fact1.toString().contains(SUBJECT));
    assertTrue(fact1.toString().contains(ATTRIBUTE));
    assertTrue(fact1.toString().contains(OBJECT));
    assertTrue(fact3.toString().contains("1.0"));
    assertTrue(fact2.toString().contains(PATTERN));
    assertTrue(fact4.toString().contains(SENTENCE));
  }

  @Test
  public void testDocumentConstructor() {
    ReNounFact fact =
        new ReNounFact(
            new Document()
                .append(SUBJECT_FIELD, SUBJECT)
                .append(ATTRIBUTE_FIELD, ATTRIBUTE)
                .append(OBJECT_FIELD, OBJECT)
                .append(SCORE_FIELD, 1.0));

    assertEquals(SUBJECT, fact.getSubject());
    assertEquals(ATTRIBUTE, fact.getAttribute());
    assertEquals(OBJECT, fact.getObject());
    assertEquals(1.0, fact.getScore(), 0.0);
    assertNull(fact.getPattern());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testSave() {
    MongoCollection<Document> collection = mock(MongoCollection.class);

    ReNounFact fact1 = new ReNounFact(SUBJECT, ATTRIBUTE, OBJECT);
    ReNounFact fact2 = new ReNounFact(SUBJECT, ATTRIBUTE, OBJECT, PATTERN);
    ReNounFact fact3 = new ReNounFact(SUBJECT, ATTRIBUTE, OBJECT);
    fact3.setScore(1.0);

    fact1.save(collection);
    fact2.save(collection);
    fact3.save(collection);

    verify(collection, times(3)).insertOne(ArgumentMatchers.isA(Document.class));
  }
}
