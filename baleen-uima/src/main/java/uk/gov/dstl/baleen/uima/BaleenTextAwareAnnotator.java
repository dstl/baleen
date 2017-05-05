//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.base.Joiner;

import uk.gov.dstl.baleen.types.language.Text;
import uk.gov.dstl.baleen.uima.data.TextBlock;

/**
 * A helper class to deal with Text annotations within documents.
 * 
 * For annotators which wish to work on each text annotation (text area) in order to process
 * independently the important content of the document, this annotator and the TextBlock class
 * provide a simplified approach.
 * 
 * Implementations may choose to override the standard doProcess method, and then use the helper
 * methods (getTextInTextBlocks, getTextBlocks) or they may simply use the doProcessTextBlock in
 * order to iterate through each block in turn.
 * 
 * If no text areas are present then the annotator defaults to the entire document (effectively
 * providing backwards compatibility). Even if text annotations are present the pipeline
 * configuration can still decide to use the whole document through the wholeDocument parameter.
 * 
 * Note the value of getTextBlocks is not just a list of Text annotations, but an abstraction
 * called @link {@link TextBlock} which provides helper fucntions for managing the difference
 * between whole document and partial text annotations, and the implication of this for annotation
 * offsets.
 * 
 * For clarity on annotation offset: UIMA requires an annotation's begin and end offset to be relative
 * to the document text. If you are working with text areas then the covered text is a subset of the
 * entire document. Thus you need to convert any offsets within the subset of text to document text
 * offset before creating annotations. TextBlock helps with this.
 * 
 * @baleen.javadoc
 *
 */
public abstract class BaleenTextAwareAnnotator extends BaleenAnnotator {

	public static final String TEXT_BLOCK_SEPARATOR = "\n\n";
	private static final Joiner TEXT_BLOCK_JOINER = Joiner.on(TEXT_BLOCK_SEPARATOR);


	/**
	 * If true, then whole document will be treated as a single text block
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_WHOLE_DOCUMENT = "wholeDocument";
	@ConfigurationParameter(name = PARAM_WHOLE_DOCUMENT, defaultValue = "false")
	private boolean wholeDocumentAsText;

	@Override
	protected void doProcess(final JCas jCas) throws AnalysisEngineProcessException {
		final List<TextBlock> blocks = getTextBlocks(jCas);

		for (final TextBlock b : blocks) {
			doProcessTextBlock(b);
		}
	}

	/**
	 * Process a text block.
	 * 
	 * This is only called if doProcess is not overridden by a child class.
	 * 
	 * The default implementation will do nothing (so children do not need to call super).
	 *
	 * @param block the block to process
	 * @throws AnalysisEngineProcessException the analysis engine process exception
	 */
	protected void doProcessTextBlock(final TextBlock block) throws AnalysisEngineProcessException {
		// Do nothing
	}

	/**
	 * Get the text areas within the document.
	 * 
	 * Provide a list of text blocks, representing the text annotations within the document.
	 * 
	 * @param jCas
	 * @return list of text boxes (non-null, maybe a singleton though)
	 */
	protected List<TextBlock> getTextBlocks(final JCas jCas) {
		if (!wholeDocumentAsText) {
			final Collection<Text> collection = JCasUtil.select(jCas, Text.class);

			// If there are no text blocks, then treat do the whole document looking for something.
			// This is effectively legacy compatibility, they will have no structural or text annotations
			// therefore this preserves the functionality of existing pipelines.
			// TODO: Perhaps this should be configurable as a parameter?

			if (!collection.isEmpty()) {
				return JCasUtil.select(jCas, Text.class).stream()
						.map(t -> new TextBlock(jCas, t))
						.collect(Collectors.toList());
			}
		}


		// Doesn't matter what we have here we create a new Text
		return Collections.singletonList(new TextBlock(jCas));

	}

	/**
	 * Gets the text in all text blocks.
	 * 
	 * This will be separated by the constant TEXT_BLOCK_SEPARATOR (but that same pattern may occur
	 * naturally in the document).
	 * 
	 * Note that offsets within this make no sense for creation of annotations. If you wish to create
	 * annotations you should use either TextBlock (which have relative offsets) or the Document text
	 * which has an absolute offset.
	 * 
	 * This is really for annotations which need to read the text, but not create annotations based on
	 * it. For example keyword extraction.
	 *
	 * @param jCas the jcas
	 * @return the combined text in text blocks
	 */
	protected String getTextInTextBlocks(final JCas jCas) {
		final List<TextBlock> blocks = getTextBlocks(jCas);

		if (blocks.isEmpty()) {
			// If it's empty save ourselves work
			return "";
		} else if (blocks.size() == 1 && blocks.get(0).isWholeDocument()) {
			// If the text block is the document, then save creating new large strings
			return jCas.getDocumentText();
		} else {
			return TEXT_BLOCK_JOINER.join(blocks.stream().map(TextBlock::getCoveredText).iterator());
		}
	}

	/**
	 * Allow child class to specifically override the wholeDocument parameter.
	 * 
	 * Suggest to call in doInitialise (after call to super.doIniitalise). This allows a child to
	 * enforce the type of processing if only one type is sensible (typically forcing whole document
	 * mode).
	 *
	 * @param wholeDocument the new whole document as text
	 */
	protected void setWholeDocumentAsText(final boolean wholeDocument) {
		this.wholeDocumentAsText = wholeDocument;
	}

	/**
	 * Checks if is whole document mode enabled
	 *
	 * @return true, if enabled
	 */
	protected boolean isWholeDocumentAsText() {
		return wholeDocumentAsText;
	}
}