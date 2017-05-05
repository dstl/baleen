//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.jobs;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.UUID;

import org.junit.Test;

public class BaleenJobTest {
	@Test
	public void test(){
		String randomString = UUID.randomUUID().toString();
		
		BaleenJob job = new BaleenJob("Test", randomString, null, Collections.emptyList());
		
		assertEquals(randomString, job.orderedYaml());
		assertEquals("job", job.getType());
	}
}