// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.coreference;

import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.entity.linking.EntityInformation;
import uk.gov.dstl.baleen.entity.linking.util.MockCandidateRanker;
import uk.gov.dstl.baleen.entity.linking.util.MockCandidateSupplier;
import uk.gov.dstl.baleen.entity.linking.util.MockInformationCollector;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

@RunWith(MockitoJUnitRunner.class)
public class EntityLinkingAnnotatorTest extends AbstractAnnotatorTest {

  @Mock private EntityInformation<Person> entityInformation;

  private static final String PERSON = "uk.gov.dstl.baleen.types.common.Person";

  public EntityLinkingAnnotatorTest() {
    super(EntityLinkingAnnotator.class);
  }

  private static final String DOCUMENT = "Jon knows Chris, Chris knows Steve";

  private ExternalResourceDescription stopwords;

  @Before
  public void setup() throws ResourceInitializationException {

    stopwords =
        ExternalResourceFactory.createNamedResourceDescription(
            EntityLinkingAnnotator.KEY_STOPWORDS, SharedStopwordResource.class);

    Set<EntityInformation<Person>> entityInformationSet = new HashSet<>();
    entityInformationSet.add(entityInformation);
  }

  @Test
  public void test() throws AnalysisEngineProcessException, ResourceInitializationException {

    Person jon = Annotations.createPerson(jCas, 0, 3, "Jon");
    Person chris1 = Annotations.createPerson(jCas, 9, 14, "Chris");
    Person chris2 = Annotations.createPerson(jCas, 16, 21, "Chris");
    Person steve =
        Annotations.createPerson(jCas, DOCUMENT.length() - 1 - 5, DOCUMENT.length() - 1, "Steve");

    Annotations.createReferenceTarget(jCas, jon);
    Annotations.createReferenceTarget(jCas, chris1, chris2);
    Annotations.createReferenceTarget(jCas, steve);

    // @formatter:off
    processJCas(
        EntityLinkingAnnotator.KEY_STOPWORDS, stopwords,
        EntityLinkingAnnotator.PARAM_ENTITY_TYPE, PERSON,
        EntityLinkingAnnotator.PARAM_INFORMATION_COLLECTOR,
            MockInformationCollector.class.getName(),
        EntityLinkingAnnotator.PARAM_CANDIDATE_SUPPLIER, MockCandidateSupplier.class.getName(),
        EntityLinkingAnnotator.PARAM_CANDIDATE_RANKER, MockCandidateRanker.class.getName());
    // @formatter:on

    String linking =
        jCas.getAnnotationIndex(ReferenceTarget.class).iterator(false).get().getLinking();

    assertNotNull(linking);
  }

  @Test(expected = ResourceInitializationException.class)
  public void testResourceInitializationExceptionIsThrownIfClassIsNotFound()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    // @formatter:off
    processJCas(
        EntityLinkingAnnotator.KEY_STOPWORDS,
        stopwords,
        EntityLinkingAnnotator.PARAM_ENTITY_TYPE,
        PERSON,
        EntityLinkingAnnotator.PARAM_INFORMATION_COLLECTOR,
        "Invalid Information Collector name",
        EntityLinkingAnnotator.PARAM_CANDIDATE_SUPPLIER,
        MockCandidateSupplier.class.getName(),
        EntityLinkingAnnotator.PARAM_CANDIDATE_RANKER,
        MockCandidateRanker.class.getName());
    // @formatter:on
  }
}
