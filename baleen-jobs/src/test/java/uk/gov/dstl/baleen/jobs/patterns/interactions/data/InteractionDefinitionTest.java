//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.jobs.patterns.interactions.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import net.sf.extjwnl.data.POS;
import uk.gov.dstl.baleen.jobs.interactions.data.InteractionDefinition;
import uk.gov.dstl.baleen.jobs.interactions.data.Word;

public class InteractionDefinitionTest {

	@Test
	public void test() {
		Word word = new Word("text", POS.NOUN);
		InteractionDefinition id = new InteractionDefinition("type", "subType", word, "source", "target");

		assertEquals("type", id.getType());
		assertEquals("subType", id.getSubType());
		assertSame(word, id.getWord());
		assertEquals("source", id.getSource());
		assertEquals("target", id.getTarget());

		assertEquals("text", id.toString());
	}

	@Test
	public void testHashcode() {
		Word word1 = new Word("text1", POS.NOUN);
		Word word2 = new Word("text2", POS.NOUN);

		InteractionDefinition id1 = new InteractionDefinition("type", "subType", word1, "source", "target");
		InteractionDefinition id1a = new InteractionDefinition("type", "subType", word1, "source", "target");
		
		InteractionDefinition id2 = new InteractionDefinition("type", "subType", word2, "source", "target");

		assertEquals(id1.hashCode(), id1a.hashCode());
		assertNotEquals(id1.hashCode(), id2.hashCode());
	}
	
	@Test
	public void testEquals(){
		Word word1 = new Word("text1", POS.NOUN);		
		InteractionDefinition id1 = new InteractionDefinition("type", "subType", word1, "source", "target");

		assertEquals(id1, id1);
		assertNotEquals(id1, null);
		assertNotEquals(id1, "text1");
		
		
		InteractionDefinition id2 = new InteractionDefinition(null, null, null, null, null);
		assertNotEquals(id1, id2);
		assertNotEquals(id2, id1);
		
		id2 = new InteractionDefinition("type", null, null, null, null);
		assertNotEquals(id1, id2);
		assertNotEquals(id2, id1);
		
		id2 = new InteractionDefinition("type", "subType", null, null, null);
		assertNotEquals(id1, id2);
		assertNotEquals(id2, id1);
		
		id2 = new InteractionDefinition("type", "subType", word1, null, null);
		assertNotEquals(id1, id2);
		assertNotEquals(id2, id1);
		
		id2 = new InteractionDefinition("type", "subType", word1, "source", null);
		assertNotEquals(id1, id2);
		assertNotEquals(id2, id1);
		
		id2 = new InteractionDefinition("type", "subType", word1, "source", "target");
		assertEquals(id1, id2);
		assertEquals(id2, id1);
	}

}