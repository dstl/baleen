// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.collector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
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
        "Sir John Major was Prime Minister of the United Kingdom. Major became Prime Minister after Thatcher resigned.");

    List<Sentence> s = Annotations.createSentences(jCas);

    Person j1 = Annotations.createPerson(jCas, 0, 14, "Sir John Major");
    Person j2 = Annotations.createPerson(jCas, 59, 64, "Major");
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
    assertTrue(CollectionUtils.isEqualCollection(s, entityInformation.getSentences()));
  }
}
