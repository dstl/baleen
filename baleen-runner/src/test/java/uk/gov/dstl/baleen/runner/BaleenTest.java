// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.runner;

import org.junit.Test;

/** Tests for {@link Baleen}, largely just checking that it doesn't crash! */
public class BaleenTest {

  @Test
  public void testMainDryRun() {
    String[] args = new String[] {"--dry-run"};
    Baleen.main(args);
    // TODO: We should check that this finishes by a second thread!
  }

  @Test
  public void testHelp() {
    String[] args = new String[] {"--help"};
    Baleen.main(args);

    args = new String[] {"-h"};
    Baleen.main(args);
    // TODO: We should check that this finishes by a second thread!
  }

  /** This test is disabled as it does not terminate. */
  // @Test
  public void testMainFull() {

    String[] args = new String[] {"src/test/resources/uk/gov/dstl/baleen/runner/runner.yaml"};
    Baleen.main(args);
  }
}
