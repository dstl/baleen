package uk.gov.dstl.baleen.schedules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.schedules.Once;

public class OnceTest extends AbstractSchedulerTest<Once> {

	public OnceTest() {
		super(Once.class);
	}

	@Test
	public void test() throws CollectionException, IOException, ResourceInitializationException {
		Once once = create();

		assertTrue(once.hasNext());

		assertFalse(once.hasNext());
		assertFalse(once.hasNext());
	}

}
