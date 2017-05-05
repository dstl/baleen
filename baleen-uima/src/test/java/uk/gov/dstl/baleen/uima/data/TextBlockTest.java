//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.types.common.Buzzword;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Text;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class TextBlockTest {

	private static final String DOC_TEXT = "The prefix. This is the text block. The postfix.";
	private static final String TEXT_TEXT = "This is the text block.";
	private JCas jCas;
	private Text text;

	@Before
	public void before() throws UIMAException {
		jCas = JCasSingleton.getJCasInstance();
		jCas.setDocumentText(DOC_TEXT);

		text = new Text(jCas, 12, 12 + TEXT_TEXT.length());

		final Buzzword b =
				new Buzzword(jCas, DOC_TEXT.indexOf("text"), DOC_TEXT.indexOf("text") + "text".length());
		b.addToIndexes();
		final Buzzword postfix =
				new Buzzword(jCas, DOC_TEXT.indexOf("prefix"),
						DOC_TEXT.indexOf("prefix") + "prefix".length());
		postfix.addToIndexes();
		final Buzzword prefix =
				new Buzzword(jCas, DOC_TEXT.indexOf("postfix"),
						DOC_TEXT.indexOf("postfix") + "postfix".length());
		prefix.addToIndexes();

	}

	@Test
	public void testIsolatedTextBlock() {
		final TextBlock block = new TextBlock(jCas, text);

		assertFalse(block.isWholeDocument());
		assertEquals(12, block.getBegin());
		assertEquals(TEXT_TEXT, block.getCoveredText());
		assertEquals(DOC_TEXT, block.getDocumentText());
		assertEquals(12 + TEXT_TEXT.length(), block.getEnd());
		assertSame(jCas, block.getJCas());
		assertSame(text, block.getText());

		final Entity annotation = block.newAnnotation(Entity.class, 3, 7);
		assertNotNull(annotation);
		assertEquals(12 + 3, annotation.getBegin());
		assertEquals(12 + 7, annotation.getEnd());

		final Person p = new Person(jCas);
		block.setBeginAndEnd(p, 2, 10);
		assertEquals(12 + 2, p.getBegin());
		assertEquals(12 + 10, p.getEnd());

		assertEquals(13, block.toDocumentOffset(1));
		assertEquals(1, block.toBlockOffset(13));
		assertEquals(1, block.select(Buzzword.class).size());
		assertEquals("text", block.select(Buzzword.class).iterator().next().getCoveredText());
		
		try{
			block.toBlockOffset(10);
			fail("Expected exception not thrown");
		}catch(IllegalArgumentException e){}
		
		try{
			block.toBlockOffset(block.getEnd() + 5);
			fail("Expected exception not thrown");
		}catch(IllegalArgumentException e){}
	}

	@Test
	public void testWholeDocument() {
		final TextBlock block = new TextBlock(jCas);
		assertTrue(block.isWholeDocument());
		assertEquals(0, block.getBegin());
		assertEquals(DOC_TEXT, block.getCoveredText());
		assertEquals(DOC_TEXT, block.getDocumentText());
		assertEquals(DOC_TEXT.length(), block.getEnd());
		assertSame(jCas, block.getJCas());
		assertNull(block.getText());

		final Entity annotation = block.newAnnotation(Entity.class, 3, 7);
		assertNotNull(annotation);
		assertEquals(3, annotation.getBegin());
		assertEquals(7, annotation.getEnd());

		final Person p = new Person(jCas);
		block.setBeginAndEnd(p, 2, 10);
		assertEquals(2, p.getBegin());
		assertEquals(10, p.getEnd());

		assertEquals(1, block.toDocumentOffset(1));
		assertEquals(1, block.toBlockOffset(1));

		assertEquals(3, block.select(Buzzword.class).size());
	}

}