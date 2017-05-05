//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.BritishArmyUnits;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.TestEntity;
import uk.gov.dstl.baleen.types.common.Organisation;


/** Tests for {@link BritishArmyUnits}.
 * 
 * 
 */
public class BritishArmyUnitsTest extends AbstractAnnotatorTest {

	public BritishArmyUnitsTest() {
		super(BritishArmyUnits.class);
	}

	@Test
	public void testMerge() throws Exception {
		jCas.setDocumentText("1 Pl, A Coy have reported suspicious activitiy whilst patrolling near CP A. 1 Pl did not investigate further.");
		processJCas();
		
		assertAnnotations(2, Organisation.class, 
				new TestEntity<>(0, "1 Pl, A Coy"),
				new TestEntity<>(1, "1 Pl"));
	}

	@Test
	public void testSingle() throws Exception {
		jCas.setDocumentText("C Coy - Generally quiet but a number of ptls reported prob dickers monitoring their activities.");
		processJCas();
		
		assertAnnotations(1, Organisation.class, 
				new TestEntity<>(0, "C Coy"));
	}

}
