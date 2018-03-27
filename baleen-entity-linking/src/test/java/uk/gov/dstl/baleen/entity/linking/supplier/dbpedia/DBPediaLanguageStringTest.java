// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.supplier.dbpedia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class DBPediaLanguageStringTest {

  @Test
  public void canConstrust() {
    assertNotNull(new DBPediaLanguageString("test@en"));
  }

  @Test
  public void canGetLanguage() {
    assertEquals("@en", new DBPediaLanguageString("test@en").language());
  }

  @Test
  public void canGetRaw() {
    assertEquals("test", new DBPediaLanguageString("test@en").raw());
  }

  @Test
  public void canGetMissingLanguage() {
    assertEquals("", new DBPediaLanguageString("test").language());
  }

  @Test
  public void canGetMissingRaw() {
    assertEquals("test", new DBPediaLanguageString("test").raw());
  }

  @Test
  public void canGetOriginal() {
    assertEquals("test@en", new DBPediaLanguageString("test@en").toString());
  }
}
