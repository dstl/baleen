//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.internals.DateTimeRegex;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.temporal.DateTime;

/**
 * 
 */
public class DateTimeRegexTest extends AbstractAnnotatorTest{

	public DateTimeRegexTest() {
		super(DateTimeRegex.class);
	}
	
	@Test
	public void test0() throws Exception{
		jCas.setDocumentText("On 6 May 2014 1245Z, James wrote an annotator. On 30 Apr 2014 (16:00Z), James was in London.");
		processJCas();
		
		assertAnnotations(2, DateTime.class, 
				new TestDateTime(0, "6 May 2014 1245Z", 1399380300000L),
				new TestDateTime(1, "30 Apr 2014 (16:00Z)", 1398873600000L)
		);
	
	}

	@Test
	public void test1() throws Exception{
		jCas.setDocumentText("On 6 May 2014 124530Z, James wrote an annotator. On 30 Apr 2014 (16:00:20Z), James was in London.");
		processJCas();
		
		
		assertAnnotations(2, DateTime.class, 
				new TestDateTime(0, "6 May 2014 124530Z", 1399380330000L),
				new TestDateTime(1, "30 Apr 2014 (16:00:20Z)", 1398873620000L)
		);
		
	}
	
	@Test
	public void test2() throws Exception{
		jCas.setDocumentText("On 2014-05-06 12:45:30, James wrote an annotator. On 2014-04-30 (16:00:20), James was in London.");
		processJCas();
		
		
		assertAnnotations(2, DateTime.class, 
				new TestDateTime(0, "2014-05-06 12:45:30", 1399380330000L),
				new TestDateTime(1, "2014-04-30 (16:00:20)", 1398873620000L)
		);
	}
}
