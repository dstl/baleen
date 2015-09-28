//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.TaskForce;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.TestEntity;
import uk.gov.dstl.baleen.types.common.Organisation;

/**
 * Tests for {@link TaskForce}.
 * 
 * 
 */
public class TaskForceRegexTest extends AbstractAnnotatorTest {

	public TaskForceRegexTest() {
		super(TaskForce.class);
	}

	@Test
	public void test() throws Exception {
		jCas.setDocumentText("Task force 123, TF4-56 and TF 789. But not ATF000 or TF000a.");
		processJCas();
		
		assertAnnotations(3, Organisation.class, 
				new TestEntity<>(0, "Task force 123"),
				new TestEntity<>(1, "TF4-56"),
				new TestEntity<>(2, "TF 789"));
	}

}
