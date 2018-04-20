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
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

public class ProperNounInformationCollectorTest {

  @Test
  public void testCanCollectInformation() throws UIMAException {
    JCas jCas = JCasFactory.createJCas();

    jCas.setDocumentText(
        "Sir John Major was Prime Minister of the United Kingdom. Major became Prime Minister after Thatcher resigned.");

    List<Sentence> s = Annotations.createSentences(jCas);

    WordToken wt1 = new WordToken(jCas);
    wt1.setBegin(4);
    wt1.setEnd(8);
    wt1.setPartOfSpeech("NNP");
    wt1.addToIndexes(jCas);

    WordToken wt2 = new WordToken(jCas);
    wt2.setBegin(9);
    wt2.setEnd(14);
    wt2.setPartOfSpeech("NNP");
    wt2.addToIndexes(jCas);

    WordToken wt3 = new WordToken(jCas);
    wt3.setBegin(19);
    wt3.setEnd(33);
    wt3.setPartOfSpeech("NN");
    wt3.addToIndexes(jCas);

    WordToken wt4 = new WordToken(jCas);
    wt4.setBegin(59);
    wt4.setEnd(64);
    wt4.setPartOfSpeech("NNP");
    wt4.addToIndexes(jCas);

    Person j1 = Annotations.createPerson(jCas, 0, 14, "Sir John Major");
    Person j2 = Annotations.createPerson(jCas, 19, 33, "Prime Minister");
    Person j3 = Annotations.createPerson(jCas, 59, 64, "Major");
    ReferenceTarget jRT = Annotations.createReferenceTarget(jCas, j1, j2, j3);

    ProperNounInformationCollector collector = new ProperNounInformationCollector();

    Set<EntityInformation<Person>> entityInformations =
        collector.getEntityInformation(jCas, Person.class);

    assertEquals(1, entityInformations.size());
    EntityInformation<Person> entityInformation = entityInformations.iterator().next();

    assertEquals(jRT, entityInformation.getReferenceTarget());
    assertTrue(
        CollectionUtils.isEqualCollection(
            ImmutableSet.of(j1, j3), entityInformation.getMentions()));
    assertTrue(CollectionUtils.isEqualCollection(s, entityInformation.getSentences()));
  }
}
