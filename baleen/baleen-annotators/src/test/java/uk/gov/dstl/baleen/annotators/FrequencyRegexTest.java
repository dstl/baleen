//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.internals.FrequencyRegex;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.TestEntity;
import uk.gov.dstl.baleen.types.common.Frequency;

/**
 * 
 */
public class FrequencyRegexTest extends AbstractAnnotatorTest {

	public FrequencyRegexTest() {
		super(FrequencyRegex.class);
	}

	@Test
	public void test() throws Exception {

		jCas.setDocumentText("James was speaking to Rich on 102 MHz. At the same time, Anthony was speaking to Warren on 45.1 GHz");
		processJCas();

		assertAnnotations(2, Frequency.class, new TestEntity<>(0, "102 MHz"),
				new TestEntity<>(1, "45.1 GHz"));
	}

}
