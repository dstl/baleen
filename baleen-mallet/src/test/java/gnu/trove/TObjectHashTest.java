// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class TObjectHashTest {

  @Test
  public void testCreates() {
    TObjectHash<String> tObjectHash = new TObjectHash<>();
    assertNotNull(tObjectHash);
  }
}
