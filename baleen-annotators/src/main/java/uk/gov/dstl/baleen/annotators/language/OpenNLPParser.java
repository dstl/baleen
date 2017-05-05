//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.language;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.ImmutableSet;

import opennlp.tools.parser.AbstractBottomUpParser;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.util.Span;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.SharedOpenNLPModel;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Perform grammatical parsing with OpenNLP parser.
 * <p>
 * The document content is passed through the OpenNLP parser in order to create a parse tree.
 * <p>
 * It is assumed that first the document has been passed through the OpenNLP pipeline (or similar)
 * so that sentences, POS, etc are extracted into the jCas.
 * <p>
 * <b>Be aware</b> that this annotator will REMOVE any existing PhraseChunks and replace them with its output.
 *
 *
 * @baleen.javadoc
 */
public class OpenNLPParser extends BaleenAnnotator {
	private static final Set<String> PHRASE_TYPES = new HashSet<String>(
			Arrays.asList("ADJP", "ADVP", "FRAG", "INTJ", "LST", "NAC", "NP", "NX", "PP", "PRN", "PRT", "QP", "RRC",
					"UCP", "VP", "WHADJP", "WHAVP", "WHNP", "WHPP", "X"));

	/**
	 * OpenNLP Resource (chunker) - use en-parser-chunking.bin
	 *
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedOpenNLPModel
	 */
	public static final String PARAM_TOKEN = "parserChunking";
	@ExternalResource(key = OpenNLPParser.PARAM_TOKEN)
	private SharedOpenNLPModel parserChunkingModel;

	private Parser parser;

	@Override
	public void doInitialize(final UimaContext aContext) throws ResourceInitializationException {
		try {
			parserChunkingModel.loadModel(ParserModel.class, getClass().getResourceAsStream("en_parser_chunking.bin"));
		} catch (final BaleenException be) {
			getMonitor().error("Unable to load OpenNLP Language Models", be);
			throw new ResourceInitializationException(be);
		}

		try {
			parser = ParserFactory.create((ParserModel) parserChunkingModel.getModel());

		} catch (final Exception e) {
			getMonitor().error("Unable to create OpenNLP parser", e);
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void doProcess(final JCas jCas) throws AnalysisEngineProcessException {
		// For each sentence (in the JCas)e, we recreate the spans from our
		// WordTokens.

		final Map<Sentence, Collection<WordToken>> sentences = JCasUtil.indexCovered(jCas, Sentence.class,
				WordToken.class);

		sentences.entrySet().stream().filter(e -> !e.getValue().isEmpty()).forEach(e -> {

			final Sentence sentence = e.getKey();
			final Collection<WordToken> tokens = e.getValue();

			final Parse parsed = parseSentence(sentence, tokens);

			updatePhraseChunks(jCas, sentence, parsed);

		});
	}

	/**
	 * Update phrase chunks.
	 *
	 * @param jCas
	 *            the j cas
	 * @param sentence
	 *            the sentence
	 * @param parsed
	 *            the parsed
	 */
	private void updatePhraseChunks(final JCas jCas, final Sentence sentence, final Parse parsed) {
		// We remove all the existing PhraseChunks as they are going to be
		// replace with the parsed
		// version
		// TODO: Should we create a new ConstiuentPhraseChunk type in Uima?
		removeFromJCasIndex(JCasUtil.selectCovered(jCas, PhraseChunk.class, sentence));

		addParsedAsAnnotations(jCas, sentence.getBegin(), parsed);

	}

	/**
	 * Adds the parsed as annotations.
	 *
	 * @param jCas
	 *            the j cas
	 * @param offset
	 *            the offset
	 * @param parsed
	 *            the parsed
	 */
	private void addParsedAsAnnotations(final JCas jCas, final int offset, final Parse parsed) {
		final String type = parsed.getType();

		// Ignore non phrase types
		if (OpenNLPParser.PHRASE_TYPES.contains(type)) {
			// Otherwise add new ParseChunks

			final Span span = parsed.getSpan();
			final PhraseChunk phraseChunk = new PhraseChunk(jCas);
			phraseChunk.setBegin(offset + span.getStart());
			phraseChunk.setEnd(offset + span.getEnd());
			phraseChunk.setChunkType(parsed.getType());

			addToJCasIndex(phraseChunk);
		}

		Arrays.stream(parsed.getChildren()).forEach(p -> addParsedAsAnnotations(jCas, offset, p));

	}

	/**
	 * Parses the sentence.
	 *
	 * @param sentence
	 *            the sentence
	 * @param tokens
	 *            the tokens
	 * @return the parses the
	 */
	private Parse parseSentence(final Sentence sentence, final Collection<WordToken> tokens) {
		final String text = sentence.getCoveredText();

		final Parse parse = new Parse(text, new Span(0, text.length()), AbstractBottomUpParser.INC_NODE, 1, 0);

		// Add in the POS
		int index = 0;
		for (final WordToken token : tokens) {
			final Span span = new Span(token.getBegin() - sentence.getBegin(), token.getEnd() - sentence.getBegin());

			parse.insert(new Parse(text, span, AbstractBottomUpParser.TOK_NODE, 0, index));
			index++;
		}

		// Parse the sentence
		return parser.parse(parse);
	}

	@Override
	public void doDestroy() {
		parserChunkingModel = null;
	}
	
	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(ImmutableSet.of(WordToken.class, Sentence.class), ImmutableSet.of(PhraseChunk.class));
	}
}