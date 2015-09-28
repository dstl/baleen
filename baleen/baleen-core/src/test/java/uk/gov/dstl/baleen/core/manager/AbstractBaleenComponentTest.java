//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.manager;

import org.junit.Test;

import uk.gov.dstl.baleen.core.utils.YamlConfiguration;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * Tests for {@link AbstractBaleenComponent}.
 * 
 * 
 *
 */
public class AbstractBaleenComponentTest {

	private class BCT extends AbstractBaleenComponent {

		@Override
		public void configure(YamlConfiguration configuration) throws BaleenException {
			// Do nothing
		}

	}

	@Test
	public void testAbstract() throws BaleenException {
		BCT bct = new BCT();
		bct.configure(new YamlConfiguration());
		bct.start();
		bct.stop();
	}
}
