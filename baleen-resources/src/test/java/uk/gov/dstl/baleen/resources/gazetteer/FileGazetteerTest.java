// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources.gazetteer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.SharedFileResource;

public class FileGazetteerTest {
  private static final String HELLO = "hello";
  private static SharedFileResource sfr = new SharedFileResource();
  private static Map<String, Object> config;

  @BeforeClass
  public static void beforeClass() {
    File gazetteerFile = new File(FileGazetteerTest.class.getResource("gazetteer.txt").getPath());

    config = new HashMap<>();
    config.put(FileGazetteer.CONFIG_FILE, gazetteerFile.getPath());
  }

  @Test
  public void testGetValues() throws BaleenException {
    FileGazetteer gaz = new FileGazetteer();
    gaz.init(sfr, config);

    List<String> values = Arrays.asList(gaz.getValues());
    assertEquals(8, values.size());
    assertTrue(values.contains("guten tag"));
    assertTrue(values.contains(HELLO));

    gaz.destroy();
  }

  @Test
  public void testHasValue() throws BaleenException {
    FileGazetteer gaz = new FileGazetteer();
    gaz.init(sfr, config);

    assertTrue(gaz.hasValue("howdy"));
    assertTrue(gaz.hasValue("hej"));
    assertTrue(gaz.hasValue("HEJ"));
    assertFalse(gaz.hasValue("good morning"));

    gaz.destroy();
  }

  @Test
  public void testHasValueCaseSensitive() throws BaleenException {
    FileGazetteer gaz = new FileGazetteer();

    Map<String, Object> csConfig = new HashMap<>();
    csConfig.putAll(config);
    csConfig.put("caseSensitive", true);

    gaz.init(sfr, csConfig);

    assertTrue(gaz.hasValue("hej"));
    assertFalse(gaz.hasValue("HEJ"));

    gaz.destroy();
  }

  @Test
  public void testGetAliases() throws BaleenException {
    FileGazetteer gaz = new FileGazetteer();
    gaz.init(sfr, config);

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
    FileGazetteer gaz = new FileGazetteer();
    gaz.init(sfr, config);

    assertEquals(Collections.emptyMap(), gaz.getAdditionalData(HELLO));

    gaz.destroy();
  }
}
