//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.Telephone;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.types.TestCommsIdentifier;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;

/** Tests for {@link Telephone}.
 * 
 */
public class TelephoneRegexTest extends AbstractAnnotatorTest{
	
	public TelephoneRegexTest() {
		super(Telephone.class);
	}

	@Test
	public void test() throws Exception{
		
		jCas.setDocumentText("Call Phil on telephone no. (+44)1981 634528. You can call Bob on tel 0800-123-456.");
		processJCas();
		
		assertAnnotations(2, CommsIdentifier.class, 
				new TestCommsIdentifier(0, "telephone no. (+44)1981 634528", "telephone"),
				new TestCommsIdentifier(1, "tel 0800-123-456", "telephone")
				);
		

	}

}
