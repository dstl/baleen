// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.uima.UimaSupport;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class ConsumerUtilsTest {
  @Test
  public void testCamelCase() {
    assertEquals("helloWorld", ConsumerUtils.toCamelCase("HelloWorld"));
  }

  @Test
  public void testExternalId() throws UIMAException {
    JCas jCas = JCasSingleton.getJCasInstance();
    jCas.setDocumentText("Hello World");
    DocumentAnnotation da = UimaSupport.getDocumentAnnotation(jCas);

    assertEquals(
        "a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e",
        ConsumerUtils.getExternalId(da, true));

    da.setSourceUri("http://www.example.com/test.html");
    assertEquals(
        "b2e870534ee6fc1abc14feac22dcfd0b268460ac4205d9c3f68a000aab685f4f",
        ConsumerUtils.getExternalId(da, false));
  }

  @Test
  public void testEntityExternalId() throws UIMAException, BaleenException {
    JCas jCas = JCasSingleton.getJCasInstance();
    jCas.setDocumentText("Hello World");

    Person p1 = new Person(jCas);
    p1.setGender("female");
    p1.setValue("Jane Doe");
    p1.addToIndexes(jCas);

    Person p2 = new Person(jCas);
    p2.setGender("female");
    p2.setValue("J. Doe");
    p2.addToIndexes(jCas);

    assertEquals(
        ConsumerUtils.getExternalId(ImmutableSet.of(p1, p2)),
        ConsumerUtils.getExternalId(ImmutableSet.of(p1, p2)));
    assertEquals(
        "d3c514ea1fb3367430959255917ee4de12468004897d683d60114b475d37264a",
        ConsumerUtils.getExternalId(ImmutableSet.of(p1, p2)));

    assertNotEquals(
        ConsumerUtils.getExternalId(ImmutableSet.of(p1)),
        ConsumerUtils.getExternalId(ImmutableSet.of(p1, p2)));
  }
}
