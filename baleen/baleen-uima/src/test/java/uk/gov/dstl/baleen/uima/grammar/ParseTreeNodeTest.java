package uk.gov.dstl.baleen.uima.grammar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class ParseTreeNodeTest {

	private PhraseChunk sentence;
	private PhraseChunk threefour;
	private PhraseChunk onetwo;
	private final WordToken[] tokens = new WordToken[4];
	private ParseTreeNode root;
	private ParseTreeNode s;
	private ParseTreeNode a;
	private ParseTreeNode b;

	@Before
	public void before() throws UIMAException {
		final JCas jCas = JCasSingleton.getJCasInstance();
		final String text = "1 2 3 4";
		jCas.setDocumentText(text);

		sentence = new PhraseChunk(jCas);
		sentence.setBegin(0);
		sentence.setEnd(text.length());
		sentence.addToIndexes();

		onetwo = new PhraseChunk(jCas);
		onetwo.setBegin(0);
		onetwo.setEnd(2);
		onetwo.addToIndexes();

		threefour = new PhraseChunk(jCas);
		threefour.setBegin(3);
		threefour.setEnd(text.length());
		threefour.addToIndexes();

		tokens[0] = new WordToken(jCas);
		tokens[0].setBegin(0);
		tokens[0].setEnd(1);
		tokens[0].addToIndexes();

		tokens[1] = new WordToken(jCas);
		tokens[1].setBegin(2);
		tokens[1].setEnd(3);
		tokens[1].addToIndexes();

		tokens[2] = new WordToken(jCas);
		tokens[2].setBegin(4);
		tokens[2].setEnd(5);
		tokens[2].addToIndexes();

		tokens[3] = new WordToken(jCas);
		tokens[3].setBegin(6);
		tokens[3].setEnd(text.length());
		tokens[3].addToIndexes();

		root = new ParseTreeNode((PhraseChunk) null);

		s = new ParseTreeNode(sentence);
		s.setParent(root);

		root.addChild(s);

		a = new ParseTreeNode(onetwo);
		a.setParent(s);
		a.addWords(Arrays.asList(tokens[0], tokens[1]));

		b = new ParseTreeNode(threefour);
		b.setParent(s);
		b.addWords(Arrays.asList(tokens[0], tokens[1]));

		s.addChild(a);
		s.addChild(b);
	}

	@Test
	public void testParseTreeNodePhraseChunk() {

		assertTrue(root.isRoot());

		assertFalse(s.isRoot());
		assertTrue(s.hasChildren());
		assertSame(sentence, s.getChunk());
		assertSame(root, s.getParent());
		assertEquals(a, s.getChildren().get(0));
		assertEquals(b, s.getChildren().get(1));

		assertFalse(a.isRoot());
		assertEquals(onetwo, a.getChunk());
		assertEquals(s, a.getParent());
		assertEquals(tokens[0], a.getWords().get(0));
		assertEquals(tokens[1], a.getWords().get(1));

	}

	boolean grandchildrenSeen = false;

	@Test
	public void testTraverseChildren() {

		root.traverseChildren(l -> {
			if (l.get(0).equals(a)) {
				grandchildrenSeen = true;
			}
		});

		assertTrue(grandchildrenSeen);
	}

	boolean grandparentSeen = false;

	@Test
	public void testTraverseParent() {
		a.traverseParent((p, c) -> {
			if (p.equals(root)) {
				grandparentSeen = true;
			}
			return true;
		});

		assertTrue(grandparentSeen);
	}

	@Test
	public void testContainsWord() {
		assertTrue(a.containsWord(w -> w.getCoveredText().equals("1")));
		assertFalse(a.containsWord(w -> w.getCoveredText().equals("4")));

	}

	@Test
	public void testStringAndlog() {
		assertFalse(a.toString().isEmpty());
		// Test no crash
		a.log("\t");
	}

}
