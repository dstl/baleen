//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.Date;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.TestEntity;
import uk.gov.dstl.baleen.types.temporal.DateType;

/**
 * 
 */
public class DateRegexTest extends AbstractAnnotatorTest{

	public DateRegexTest() {
		super(Date.class);
	}
	
	@Test
	public void test() throws Exception{
	
		jCas.setDocumentText("Today is Monday 25th February 2013.");
		processJCas();
		
		assertAnnotations(1, DateType.class,
			new TestEntity<>(0, "Monday 25th February 2013")
		);
		
	}

}
