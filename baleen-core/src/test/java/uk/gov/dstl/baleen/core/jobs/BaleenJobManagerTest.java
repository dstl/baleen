//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.jobs;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BaleenJobManagerTest {

	@Test
	public void test() throws Exception {
		BaleenJobManager manager = new BaleenJobManager();
		
		assertEquals("job", manager.getType());
	}

}
