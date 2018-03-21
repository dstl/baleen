// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.gazetteer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.exceptions.BaleenException;

public class ListGazetteerTest {
  private static final String HELLO = "hello";
  private static Map<String, Object> config;

  @BeforeClass
  public static void beforeClass() {

    config = new HashMap<>();
    config.put(
        ListGazetteer.CONFIG_TERMS,
        ImmutableList.<String>of(
            "hello", "hi,howdy,heya", "konnichiwa,konnbanwa", "guten tag", "hej"));
  }

  @Test
  public void testGetValues() throws BaleenException {
    ListGazetteer gaz = new ListGazetteer();
    gaz.init(null, config);

    List<String> values = Arrays.asList(gaz.getValues());
    assertEquals(8, values.size());
    assertTrue(values.contains("guten tag"));
    assertTrue(values.contains(HELLO));

    gaz.destroy();
  }

  @Test
  public void testHasValue() throws BaleenException {
    ListGazetteer gaz = new ListGazetteer();
    gaz.init(null, config);

    assertTrue(gaz.hasValue("howdy"));
    assertTrue(gaz.hasValue("hej"));
    assertTrue(gaz.hasValue("HEJ"));
    assertFalse(gaz.hasValue("good morning"));

    gaz.destroy();
  }

  @Test
  public void testHasValueCaseSensitive() throws BaleenException {
    ListGazetteer gaz = new ListGazetteer();
    config.put("caseSensitive", true);

    gaz.init(null, config);

    assertTrue(gaz.hasValue("hej"));
    assertFalse(gaz.hasValue("HEJ"));

    gaz.destroy();
  }

  @Test
  public void testGetAliases() throws BaleenException {
    ListGazetteer gaz = new ListGazetteer();
    gaz.init(null, config);

    String[] helloAliases = gaz.getAliases(HELLO);
    String[] hiAliases = gaz.getAliases("hi");

    assertEquals(0, helloAliases.length);
    assertEquals(2, hiAliases.length);

    List<String> hiAliasesList = Arrays.asList(hiAliases);
    assertTrue(hiAliasesList.contains("heya"));
    assertTrue(hiAliasesList.contains("howdy"));

    gaz.destroy();
  }

  @Test
  public void testGetAdditionalData() throws BaleenException {
    ListGazetteer gaz = new ListGazetteer();
    gaz.init(null, config);

    assertEquals(Collections.emptyMap(), gaz.getAdditionalData(HELLO));

    gaz.destroy();
  }
}
