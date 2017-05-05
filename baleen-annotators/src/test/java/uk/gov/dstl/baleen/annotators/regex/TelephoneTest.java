//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.Telephone;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.types.TestCommsIdentifier;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;

/** Tests for {@link Telephone}.
 * 
 */
public class TelephoneTest extends AbstractAnnotatorTest{
	
	public TelephoneTest() {
		super(Telephone.class);
	}

	@Test
	public void test() throws Exception{
		
		jCas.setDocumentText("Call Phil on telephone no. (+44)1981 634528. You can call Bob on tel 0800-123-456.");
		processJCas();
		
		assertAnnotations(2, CommsIdentifier.class, 
				new TestCommsIdentifier(0, "(+44)1981 634528", "telephone"),
				new TestCommsIdentifier(1, "0800-123-456", "telephone")
				);
		

	}

}
