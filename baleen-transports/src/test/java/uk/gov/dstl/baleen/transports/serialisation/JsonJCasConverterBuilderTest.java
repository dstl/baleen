// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.serialisation;

import static org.junit.Assert.assertFalse;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.transports.util.JCasSerializationTester;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.uima.UimaMonitor;

public class JsonJCasConverterBuilderTest {

  @Test
  public void testRountTrip() throws IOException, UIMAException {
    final JsonJCasConverter converter = createBuilder().build();
    JCasSerializationTester testUtil = new JCasSerializationTester();
    final String json = converter.serialise(testUtil.getIn());
    converter.deserialise(testUtil.getOut(), json);
    testUtil.assertCompleteMatch();
  }

  @Test
  public void testWhitelist() throws IOException, UIMAException {

    final JsonJCasConverter converter =
        createBuilder().withWhitelist(ImmutableList.of("Location")).build();

    JCasSerializationTester testUtil = new JCasSerializationTester();

    final String json = converter.serialise(testUtil.getIn());
    converter.deserialise(testUtil.getOut(), json);

    testUtil.assertTopLevel();
    testUtil.assertLocationMatches();
    assertFalse(JCasUtil.exists(testUtil.getOut(), Person.class));
  }

  @Test
  public void testBlacklist() throws IOException, UIMAException {
    final JsonJCasConverter converter =
        createBuilder().withBlacklist(ImmutableList.of("common.Person")).build();

    JCasSerializationTester testUtil = new JCasSerializationTester();

    final String json = converter.serialise(testUtil.getIn());
    converter.deserialise(testUtil.getOut(), json);

    testUtil.assertTopLevel();
    testUtil.assertLocationMatches();
    assertFalse(JCasUtil.exists(testUtil.getOut(), Person.class));
  }

  private JsonJCasConverterBuilder createBuilder() throws UIMAException {
    final UimaMonitor monitor = new UimaMonitor("test", JsonJCasConverter.class);
    return new JsonJCasConverterBuilder(monitor);
  }
}
