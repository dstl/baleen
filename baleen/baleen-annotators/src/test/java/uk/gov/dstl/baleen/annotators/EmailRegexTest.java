//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.Email;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.types.TestCommsIdentifier;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;

/**
 * 
 */
public class EmailRegexTest extends AbstractAnnotatorTest {
	
	private static final String EMAIL = "email";

	public EmailRegexTest() {
		super(Email.class);
	}
	
	@Test
	public void test() throws Exception{
		jCas.setDocumentText("John Smith has two work e-mail addresses, jsmith@example.com and john.smith@example.com. Joe Bloggs can be reached by e-mail too (j.bloggs@foo.com).");
		processJCas();
		
		assertAnnotations(3, CommsIdentifier.class, 
				new TestCommsIdentifier(0, "jsmith@example.com", EMAIL),
				new TestCommsIdentifier(1, "john.smith@example.com", EMAIL),
				new TestCommsIdentifier(2, "j.bloggs@foo.com", EMAIL));
	}

}
