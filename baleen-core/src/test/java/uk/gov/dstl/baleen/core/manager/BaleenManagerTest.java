// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.manager;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.core.manager.BaleenManager.BaleenManagerListener;
import uk.gov.dstl.baleen.core.metrics.MetricsFactory;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/** Basic test of baleen manager, */
public class BaleenManagerTest {

  @Before
  public void before() {
    // We need to clear the registry
    MetricsFactory.getInstance().removeAll();
  }

  @Test
  public void testWithoutConfiguration() throws Exception {
    runAndStop(Optional.empty());
  }

  @Test(expected = RuntimeException.class)
  public void testWithMissingConfiguration() throws BaleenException {
    URL resource = getClass().getResource("missing.yaml");
    runAndStop(Optional.of(new File(resource.getFile())));
  }

  @Test
  public void testWithConfiguration() throws BaleenException {
    URL resource = getClass().getResource("manager.yaml");
    runAndStop(Optional.of(new File(resource.getFile())));
  }

  private void runAndStop(Optional<File> configurationFile) throws BaleenException {
    BaleenManager manager = new BaleenManager(configurationFile);

    try {
      manager.run(
          new BaleenManagerListener() {

            @Override
            public void onStarted(BaleenManager manager) {
              // Do nothing
              assertNotNull(manager.getYaml());
            }
          });
    } finally {
      // Ensure stopped (even on assertion failure)
      manager.stop();
      manager.shutdown();
    }
  }
}
