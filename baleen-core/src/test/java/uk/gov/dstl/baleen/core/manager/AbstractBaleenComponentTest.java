// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.manager;

import java.io.IOException;

import org.junit.Test;

import uk.gov.dstl.baleen.core.utils.Configuration;
import uk.gov.dstl.baleen.core.utils.yaml.YamlConfiguration;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/** Tests for {@link AbstractBaleenComponent}. */
public class AbstractBaleenComponentTest {

  private class BCT extends AbstractBaleenComponent {

    @Override
    public void configure(Configuration configuration) throws BaleenException {
      // Do nothing
    }
  }

  @Test
  public void testAbstract() throws BaleenException, IOException {
    BCT bct = new BCT();
    bct.configure(new YamlConfiguration());
    bct.start();
    bct.stop();
  }
}
