//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.language.Text;
import uk.gov.dstl.baleen.uima.data.TextBlock;

public class BaleenTextAwareAnnotatorTest {

	private JCas noTextJCas;
	private JCas textJCas;

	@Before
	public void before() throws UIMAException {
		noTextJCas = JCasFactory.createJCas();
		noTextJCas.setDocumentText("This is text and more text");
		textJCas = JCasFactory.createJCas();
		textJCas.setDocumentText("This is text and more text");
		final Text text = new Text(textJCas, 8, 12);
		text.addToIndexes();
		final Text moreText = new Text(textJCas, 17, 26);
		moreText.addToIndexes();
	}

	@Test
	public void testProcessTextBlockIsCalledForEntireDoc() throws AnalysisEngineProcessException {
		final FakeTextAwareAnnotator annotator = createAnnotator(false);
		annotator.doProcess(noTextJCas);
		assertEquals(1, annotator.getCount());
	}

	@Test
	public void testProcessTextBlockIsCalledForTextBlocks() throws AnalysisEngineProcessException {
		final FakeTextAwareAnnotator annotator = createAnnotator(false);
		annotator.doProcess(textJCas);
		assertEquals(2, annotator.getCount());
	}


	@Test
	public void testGetTextInBlocksForTextBlocks() {
		final FakeTextAwareAnnotator annotator = createAnnotator(false);
		final String s = annotator.getTextInTextBlocks(textJCas);
		assertEquals("text\n\nmore text", s);
	}

	@Test
	public void testGetTextInBlocksForEntireDoc() {
		final FakeTextAwareAnnotator annotator = createAnnotator(false);
		final String s = annotator.getTextInTextBlocks(noTextJCas);
		assertEquals("This is text and more text", s);
	}

	@Test
	public void testGetTextInBlocksWhenHasBlocksButWholeDocParamIsSet() {
		final FakeTextAwareAnnotator annotator = createAnnotator(true);
		final String s = annotator.getTextInTextBlocks(textJCas);
		assertEquals("This is text and more text", s);
	}

	@Test
	public void testGetTextBlockForTextBlocks() {
		final FakeTextAwareAnnotator annotator = createAnnotator(false);
		final List<TextBlock> list = annotator.getTextBlocks(textJCas);
		assertEquals(2, list.size());
		assertEquals("text", list.get(0).getCoveredText());
		assertEquals("more text", list.get(1).getCoveredText());

	}

	@Test
	public void testGetTextBlockForEntireDoc() {
		final FakeTextAwareAnnotator annotator = createAnnotator(false);
		final List<TextBlock> list = annotator.getTextBlocks(noTextJCas);
		assertEquals(1, list.size());
		assertEquals("This is text and more text", list.get(0).getCoveredText());
	}

	@Test
	public void testGetTextBlockWhenHasBlocksButWholeDocParamIsSet() {
		final FakeTextAwareAnnotator annotator = createAnnotator(true);
		final List<TextBlock> list = annotator.getTextBlocks(textJCas);
		assertEquals(1, list.size());
		assertEquals("This is text and more text", list.get(0).getCoveredText());
	}

	public FakeTextAwareAnnotator createAnnotator(final boolean wholeDoc) {
		return new FakeTextAwareAnnotator(wholeDoc);
	}

	public static class FakeTextAwareAnnotator extends BaleenTextAwareAnnotator {
		private final Set<TextBlock> blocksSeen = new HashSet<>();

		public FakeTextAwareAnnotator(final boolean wholeDoc) {
			setWholeDocumentAsText(wholeDoc);
		}

		@Override
		public void doProcess(final JCas jCas) throws AnalysisEngineProcessException {
			// make this public basically so we can avoid all the other process stuff.
			super.doProcess(jCas);
		}

		@Override
		protected void doProcessTextBlock(final TextBlock block) throws AnalysisEngineProcessException {
			blocksSeen.add(block);
		}

		@Override
		public List<TextBlock> getTextBlocks(final JCas jCas) {
			return super.getTextBlocks(jCas);
		}

		@Override
		public String getTextInTextBlocks(final JCas jCas) {
			return super.getTextInTextBlocks(jCas);
		}

		public int getCount() {
			return blocksSeen.size();
		}
		
		@Override
		public AnalysisEngineAction getAction() {
			return new AnalysisEngineAction(Collections.emptySet(), Collections.emptySet());
		}
	}
}