// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.collector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.entity.linking.EntityInformation;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

public class JCasInformationCollectorTest {

  @Test
  public void testCanCollectInformation() throws UIMAException {
    JCas jCas = JCasFactory.createJCas();

    jCas.setDocumentText(
        "Sir John Major, KG, CH (born 29 March 1943) is a British politician who was Prime Minister of the United Kingdom and Leader of the Conservative Party from 1990 to 1997. Major became Prime Minister after Thatcher's reluctant resignation in November 1990.");

    Sentence s1 = new Sentence(jCas);
    s1.setBegin(0);
    s1.setEnd(168);
    s1.addToIndexes(jCas);
    Sentence s2 = new Sentence(jCas);
    s2.setBegin(169);
    s2.setEnd(253);
    s2.addToIndexes(jCas);

    Person j1 = Annotations.createPerson(jCas, 0, 14, "Sir John Major");
    Person j2 = Annotations.createPerson(jCas, 169, 174, "Major");
    ReferenceTarget jRT = Annotations.createReferenceTarget(jCas, j1, j2);

    JCasInformationCollector collector = new JCasInformationCollector();

    Set<EntityInformation<Person>> entityInformations =
        collector.getEntityInformation(jCas, Person.class);

    assertEquals(1, entityInformations.size());
    EntityInformation<Person> entityInformation = entityInformations.iterator().next();

    assertEquals(jRT, entityInformation.getReferenceTarget());
    assertTrue(
        CollectionUtils.isEqualCollection(
            ImmutableSet.of(j1, j2), entityInformation.getMentions()));
    assertTrue(
        CollectionUtils.isEqualCollection(
            ImmutableSet.of(s1, s2), entityInformation.getSentences()));
  }
}
