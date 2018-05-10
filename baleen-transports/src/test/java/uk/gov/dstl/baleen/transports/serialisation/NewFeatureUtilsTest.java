// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.serialisation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.Feature;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import uk.gov.dstl.baleen.types.common.Buzzword;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;

@RunWith(MockitoJUnitRunner.class)
public class NewFeatureUtilsTest {

  private static TypeSystemDescription typeSystemDescription;
  private static JCas jCas;

  @BeforeClass
  public static void setupClass() throws UIMAException {
    typeSystemDescription = TypeSystemSingleton.getTypeSystemDescriptionInstance();
    jCas = JCasFactory.createJCas(typeSystemDescription);
  }

  @Before
  public void setup() {
    jCas.reset();
  }

  @Test
  public void testString() {
    Entity e = new Entity(jCas);
    e.setValue("entity");
    e.addToIndexes();
    Feature feature = e.getType().getFeatureByBaseName("value");
    NewFeatureUtils.setPrimitive(e, feature, "newValue");
    assertEquals("newValue", e.getStringValue(feature));
  }

  @Test
  public void testInteger() {
    Entity e = new Entity(jCas);
    e.setBegin(0);
    e.addToIndexes();
    Feature feature = e.getType().getFeatureByBaseName("begin");
    NewFeatureUtils.setPrimitive(e, feature, 1);
    assertEquals(1, e.getIntValue(feature));
  }

  @Test
  public void testDouble() {
    Entity e = new Entity(jCas);
    e.setConfidence(0.5);
    e.addToIndexes();
    Feature feature = e.getType().getFeatureByBaseName("confidence");
    NewFeatureUtils.setPrimitive(e, feature, 1.0);
    assertEquals(1.0, e.getDoubleValue(feature), 0.0);
  }

  @Test
  public void testLong() {
    Entity e = new Entity(jCas);
    e.setInternalId(12345);
    e.addToIndexes();
    Feature feature = e.getType().getFeatureByBaseName("internalId");
    NewFeatureUtils.setPrimitive(e, feature, 54321);
    assertEquals(54321, e.getLongValue(feature));
  }

  @Test
  public void testFloat() {
    Entity e = new Entity(jCas);
    e.setIsNormalised(false);
    e.addToIndexes();
    Feature feature = e.getType().getFeatureByBaseName("isNormalised");
    NewFeatureUtils.setPrimitive(e, feature, true);
    assertTrue(e.getBooleanValue(feature));
  }

  @Test
  public void testStringArray() {
    Buzzword bw = new Buzzword(jCas);
    StringArray tags = new StringArray(jCas, 2);
    tags.set(0, "tag1");
    tags.set(1, "tag2");
    bw.setTags(tags);
    bw.addToIndexes();

    Feature f = bw.getType().getFeatureByBaseName("tags");
    StringArray newTags = new StringArray(jCas, 2);
    newTags.set(0, "first");
    newTags.set(1, "second");
    NewFeatureUtils.setPrimitiveArray(jCas, bw, f, Arrays.asList(newTags.toArray()));

    assertEquals("first", bw.getTags(0));
    assertEquals("second", bw.getTags(1));
  }
}
