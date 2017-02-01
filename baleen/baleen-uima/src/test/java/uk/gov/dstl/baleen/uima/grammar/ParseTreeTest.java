package uk.gov.dstl.baleen.uima.grammar;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class ParseTreeTest {

	private JCas jCas;
	private PhraseChunk sentence;
	private PhraseChunk onetwo;
	private PhraseChunk threefour;
	private final WordToken[] tokens = new WordToken[4];

	@Before
	public void before() throws UIMAException {
		jCas = JCasSingleton.getJCasInstance();
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
		tokens[0].setEnd(0);
		tokens[0].addToIndexes();

		tokens[1] = new WordToken(jCas);
		tokens[1].setBegin(2);
		tokens[1].setEnd(2);
		tokens[1].addToIndexes();

		tokens[2] = new WordToken(jCas);
		tokens[2].setBegin(4);
		tokens[2].setEnd(4);
		tokens[2].addToIndexes();

		tokens[3] = new WordToken(jCas);
		tokens[3].setBegin(6);
		tokens[3].setEnd(text.length());
		tokens[3].addToIndexes();

	}

	int childrenCount = 0;

	@Test
	public void testBuild() {

		final ParseTree pt = ParseTree.build(jCas);

		// Test getParent

		assertEquals(onetwo, pt.getParent(tokens[0]).getChunk());
		assertEquals(onetwo, pt.getParent(tokens[1]).getChunk());
		assertEquals(threefour, pt.getParent(tokens[2]).getChunk());
		assertEquals(threefour, pt.getParent(tokens[3]).getChunk());

		// test child words
		final List<WordToken> words = pt.getChildWords(onetwo, x -> true).collect(Collectors.toList());
		assertEquals(2, words.size());
		assertEquals(tokens[0], words.get(0));
		assertEquals(tokens[1], words.get(1));

		assertEquals(0, pt.getChildWords(sentence, x -> false).count());

		// test traverse

		pt.traverseChildren(l -> childrenCount++);
		assertEquals(3, childrenCount);
	}

}
