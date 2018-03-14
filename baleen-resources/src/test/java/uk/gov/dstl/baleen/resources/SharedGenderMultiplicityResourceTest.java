// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.resources.data.Gender;
import uk.gov.dstl.baleen.resources.data.Multiplicity;

public class SharedGenderMultiplicityResourceTest {
  SharedGenderMultiplicityResource sgmr;

  @Before
  public void beforeTest() throws Exception {
    ExternalResourceDescription erd =
        ExternalResourceFactory.createExternalResourceDescription(
            "genderMultiplicity", SharedGenderMultiplicityResource.class);
    sgmr = new SharedGenderMultiplicityResource();
    sgmr.initialize(erd.getResourceSpecifier(), Collections.emptyMap());
  }

  @After
  public void afterTest() throws Exception {
    sgmr.destroy();
    sgmr = null;
  }

  @Test
  public void testLookupGender() throws Exception {
    assertEquals(Gender.F, sgmr.lookupGender("Alice"));
    assertEquals(Gender.M, sgmr.lookupGender("Brian"));
    assertEquals(Gender.N, sgmr.lookupGender("Car"));
    assertEquals(Gender.UNKNOWN, sgmr.lookupGender("abc123"));
  }

  @Test
  public void testLookupMultiplicity() throws Exception {
    assertEquals(Multiplicity.SINGULAR, sgmr.lookupMultiplicity("chair"));
    assertEquals(Multiplicity.PLURAL, sgmr.lookupMultiplicity("trucks"));
    assertEquals(Multiplicity.UNKNOWN, sgmr.lookupMultiplicity("abc123"));
  }
}
