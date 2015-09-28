//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.Callsign;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.TestEntity;
import uk.gov.dstl.baleen.types.common.Person;

/** Test {@link Callsign}.
 * 
 */
public class CallsignRegexTest extends AbstractAnnotatorTest {

	public CallsignRegexTest() {
		super(Callsign.class);
	}

	@Test
	public void test() throws Exception{
	
		jCas.setDocumentText("Bob (C\\S ECHO BRAVO) reported a contact at 0900. Alice (C/S FOXTROT) responded.");
		processJCas();

		assertAnnotations(2, Person.class, 
				new TestEntity<>(0, "C\\S ECHO BRAVO", "C\\S ECHO BRAVO"),
				new TestEntity<>(1, "C/S FOXTROT", "C/S FOXTROT")				
		);
	}

	@Test
	public void testAllCaps() throws Exception{
		jCas.setDocumentText("BOB (C\\S ECHO BRAVO) REPORTED A CONTACT AT 0900. ALICE (C/S FOXTROT) RESPONDED.");
		processJCas();
		
		assertAnnotations(0, Person.class);
	}
}
