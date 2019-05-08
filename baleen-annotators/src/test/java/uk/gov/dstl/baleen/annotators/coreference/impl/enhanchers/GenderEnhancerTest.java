// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.coreference.impl.enhanchers;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.coreference.impl.data.Mention;
import uk.gov.dstl.baleen.annotators.coreference.impl.enhancers.GenderEnhancer;
import uk.gov.dstl.baleen.resources.SharedGenderMultiplicityResource;
import uk.gov.dstl.baleen.resources.data.Gender;
import uk.gov.dstl.baleen.types.common.Nationality;
import uk.gov.dstl.baleen.types.common.Organisation;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class GenderEnhancerTest {
  static SharedGenderMultiplicityResource genderResource;
  static GenderEnhancer genderEnhancer;

  static JCas jCas;

  @BeforeClass
  public static void setup() throws Exception {
    ExternalResourceDescription erd =
        ExternalResourceFactory.createNamedResourceDescription(
            "gender", SharedGenderMultiplicityResource.class);
    genderResource = new SharedGenderMultiplicityResource();
    genderResource.initialize(erd.getResourceSpecifier(), Collections.emptyMap());

    genderEnhancer = new GenderEnhancer(genderResource);

    jCas = JCasSingleton.getJCasInstance();
  }

  @Test
  public void testGenderFromTitle() {
    assertEquals(Gender.M, GenderEnhancer.getGenderFromTitle("MR"));
    assertEquals(Gender.M, GenderEnhancer.getGenderFromTitle("Sir"));
    assertEquals(Gender.M, GenderEnhancer.getGenderFromTitle("lOrD"));
    assertEquals(Gender.M, GenderEnhancer.getGenderFromTitle("king"));
    assertEquals(Gender.M, GenderEnhancer.getGenderFromTitle("Colonel Sir"));

    assertEquals(Gender.F, GenderEnhancer.getGenderFromTitle("MRS"));
    assertEquals(Gender.F, GenderEnhancer.getGenderFromTitle("Dame"));
    assertEquals(Gender.F, GenderEnhancer.getGenderFromTitle("lAdY"));
    assertEquals(Gender.F, GenderEnhancer.getGenderFromTitle("queen"));
    assertEquals(Gender.F, GenderEnhancer.getGenderFromTitle("Dr Revd Dame"));

    assertEquals(Gender.UNKNOWN, GenderEnhancer.getGenderFromTitle("Dr"));
    assertEquals(Gender.UNKNOWN, GenderEnhancer.getGenderFromTitle(""));
    assertEquals(Gender.UNKNOWN, GenderEnhancer.getGenderFromTitle("Dr Revd"));
    assertEquals(Gender.UNKNOWN, GenderEnhancer.getGenderFromTitle(null));
  }

  @Test
  public void testEnhancePerson() {
    Person p = new Person(jCas);
    p.setValue("Sir Lancelot");
    p.setTitle("Sir");

    Mention m = new Mention(p);

    genderEnhancer.enhance(m);

    assertEquals(Gender.M, m.getGender());
  }

  @Test
  public void testEnhanceNationality() {
    Nationality n = new Nationality(jCas);
    n.setValue("British");

    Mention m = new Mention(n);

    genderEnhancer.enhance(m);

    assertEquals(Gender.UNKNOWN, m.getGender());
  }

  @Test
  public void testEnhanceOrganisation() {
    Organisation o = new Organisation(jCas);
    o.setValue("British Government");

    Mention m = new Mention(o);

    genderEnhancer.enhance(m);

    assertEquals(Gender.N, m.getGender());
  }

  @Test
  public void testEnhancePronoun() {
    jCas.setDocumentText("He went to London");

    WordToken wt = new WordToken(jCas, 0, 2);

    Mention m = new Mention(wt);

    genderEnhancer.enhance(m);

    assertEquals(Gender.M, m.getGender());
  }
}
