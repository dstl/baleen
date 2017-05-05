//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.grammatical;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.grammatical.NPOrganisation;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.TestEntity;
import uk.gov.dstl.baleen.types.common.Organisation;
import uk.gov.dstl.baleen.types.language.PhraseChunk;

/**
 * Tests for {@link NPOrganisation}.
 * 
 * 
 */
public class NPOrganisationTest extends AbstractAnnotatorTest {

	public NPOrganisationTest() {
		super(NPOrganisation.class);
	}

	@Test
	public void test() throws Exception {
		jCas.setDocumentText("The local council was concerned.");

		PhraseChunk pc1 = new PhraseChunk(jCas, 0, 17);
		pc1.setChunkType("NP");
		pc1.addToIndexes();
		
		PhraseChunk pc2 = new PhraseChunk(jCas, 18, 31);
		pc2.setChunkType("VP");
		pc2.addToIndexes();
		
		processJCas();
		
		assertAnnotations(1, Organisation.class, new TestEntity<>(0, "The local council"));
	}
}
