// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.serialisation;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.transports.util.JCasSerializationTester;
import uk.gov.dstl.baleen.types.BaleenAnnotation;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.uima.UimaMonitor;

public class JsonJCasConverterTest {

  @Test
  public void testRountTrip() throws IOException, UIMAException {

    final JsonJCasConverter converter = createConverter();

    JCasSerializationTester testUtil = new JCasSerializationTester();

    final String json = converter.serialise(testUtil.getIn());
    converter.deserialise(testUtil.getOut(), json);

    testUtil.assertCompleteMatch();
  }

  @Test
  public void testWhitelist() throws IOException, UIMAException {

    List<Class<? extends BaleenAnnotation>> whiteList =
        ImmutableList.<Class<? extends BaleenAnnotation>>of(Location.class);

    final JsonJCasConverter converter = createConverter(whiteList, Collections.emptyList());

    JCasSerializationTester testUtil = new JCasSerializationTester();

    final String json = converter.serialise(testUtil.getIn());
    converter.deserialise(testUtil.getOut(), json);

    testUtil.assertTopLevel();
    testUtil.assertLocationMatches();
    assertFalse(JCasUtil.exists(testUtil.getOut(), Person.class));
  }

  @Test
  public void testBlacklist() throws IOException, UIMAException {

    List<Class<? extends BaleenAnnotation>> blackList =
        ImmutableList.<Class<? extends BaleenAnnotation>>of(Person.class);

    final JsonJCasConverter converter = createConverter(Collections.emptyList(), blackList);

    JCasSerializationTester testUtil = new JCasSerializationTester();

    final String json = converter.serialise(testUtil.getIn());
    converter.deserialise(testUtil.getOut(), json);

    testUtil.assertTopLevel();
    testUtil.assertLocationMatches();
    assertFalse(JCasUtil.exists(testUtil.getOut(), Person.class));
  }

  @Test
  public void testSerializeWhitelist() throws IOException, UIMAException {

    List<Class<? extends BaleenAnnotation>> whiteList =
        ImmutableList.<Class<? extends BaleenAnnotation>>of(Location.class);

    final JsonJCasConverter serializer = createConverter(whiteList, Collections.emptyList());
    final JsonJCasConverter deserializer = createConverter();

    JCasSerializationTester testUtil = new JCasSerializationTester();

    final String json = serializer.serialise(testUtil.getIn());
    deserializer.deserialise(testUtil.getOut(), json);

    testUtil.assertTopLevel();
    testUtil.assertLocationMatches();
    assertFalse(JCasUtil.exists(testUtil.getOut(), Person.class));
  }

  @Test
  public void testSerializeBlacklist() throws IOException, UIMAException {

    List<Class<? extends BaleenAnnotation>> blackList =
        ImmutableList.<Class<? extends BaleenAnnotation>>of(Person.class);

    final JsonJCasConverter serializer = createConverter(Collections.emptyList(), blackList);
    final JsonJCasConverter deserializer = createConverter();

    JCasSerializationTester testUtil = new JCasSerializationTester();

    final String json = serializer.serialise(testUtil.getIn());
    deserializer.deserialise(testUtil.getOut(), json);

    testUtil.assertTopLevel();
    testUtil.assertLocationMatches();
    assertFalse(JCasUtil.exists(testUtil.getOut(), Person.class));
  }

  @Test
  public void testDeserializeWhitelist() throws IOException, UIMAException {

    List<Class<? extends BaleenAnnotation>> whiteList =
        ImmutableList.<Class<? extends BaleenAnnotation>>of(Location.class);

    final JsonJCasConverter serializer = createConverter();
    final JsonJCasConverter deserializer = createConverter(whiteList, Collections.emptyList());

    JCasSerializationTester testUtil = new JCasSerializationTester();

    final String json = serializer.serialise(testUtil.getIn());
    deserializer.deserialise(testUtil.getOut(), json);

    testUtil.assertTopLevel();
    testUtil.assertLocationMatches();
    assertFalse(JCasUtil.exists(testUtil.getOut(), Person.class));
  }

  @Test
  public void testDeserializeBlacklist() throws IOException, UIMAException {

    List<Class<? extends BaleenAnnotation>> blackList =
        ImmutableList.<Class<? extends BaleenAnnotation>>of(Person.class);

    final JsonJCasConverter serializer = createConverter();
    final JsonJCasConverter deserializer = createConverter(Collections.emptyList(), blackList);

    JCasSerializationTester testUtil = new JCasSerializationTester();

    final String json = serializer.serialise(testUtil.getIn());
    deserializer.deserialise(testUtil.getOut(), json);

    testUtil.assertTopLevel();
    testUtil.assertLocationMatches();
    assertFalse(JCasUtil.exists(testUtil.getOut(), Person.class));
  }

  private JsonJCasConverter createConverter() {
    return createConverter(Collections.emptyList(), Collections.emptyList());
  }

  private JsonJCasConverter createConverter(
      List<Class<? extends BaleenAnnotation>> whiteList,
      List<Class<? extends BaleenAnnotation>> blackList) {
    final UimaMonitor monitor = new UimaMonitor("test", JsonJCasConverter.class);
    return new JsonJCasConverter(monitor, whiteList, blackList);
  }
}
