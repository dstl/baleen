//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;

import uk.gov.dstl.baleen.collectionreaders.helpers.AbstractStreamCollectionReader;
import uk.gov.dstl.baleen.collectionreaders.helpers.MucEntry;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * Reads the MUC-3 and MUC-4 datasets.
 * <p>
 * The text is all upper case, which Baleen performs poorly on, it also contains metadata (not just
 * the article text). We lower case the extra and remove excess metadata to create the jCas document
 * text.
 *
 * @baleen.javadoc
 */
public class MucReader extends AbstractStreamCollectionReader<MucEntry> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MucReader.class);

	// This should be \n\n\n but the TST-MUC-11 is different!
	private static final Splitter ARTICLE_SPLITTER = Splitter.on(Pattern.compile("\n\n\\s*\n")).trimResults()
			.omitEmptyStrings();

	/**
	 * Location of the directory containing the muc34 files.
	 *
	 * Note that only files which do not begin with <em>key-</em> will be used.
	 *
	 * @baleen.config
	 */
	public static final String KEY_PATH = "path";
	@ConfigurationParameter(name = KEY_PATH, mandatory = true)
	private String mucPath;

	@Override
	protected Stream<MucEntry> initializeStream(UimaContext context) throws BaleenException {
		final File[] files = checkFilesExist();

		return Arrays.stream(files)
				.flatMap(f -> {
					try {
						final byte[] bytes = Files.readAllBytes(f.toPath());
						return StreamSupport.stream(ARTICLE_SPLITTER.split(new String(bytes, "UTF-8")).spliterator(),
								false);
					} catch (final Exception e) {
						LOGGER.warn("Discarding invalid content of {}", f, e);
						return Stream.empty();
					}
				}).map(text -> {

					final int nlIndex = text.indexOf("\n", 1);
					// Strip the first lines up to a the article start (signified by a --)
					final int textIndex = text.indexOf("--");
					if (nlIndex != -1 && textIndex != -1) {
						final String id = text.substring(0, nlIndex);
						final String content = text.substring(textIndex + 2).trim();
						return new MucEntry(id, content);
					} else {
						return null;
					}
				}).filter(Objects::nonNull)
				.map(e -> {
					e.setText(cleanText(e.getText()));
					return e;
				});
	}
	
	/**
	 * Check whether there are files present (which don't start with key-)
	 */
	public File[] checkFilesExist() throws BaleenException{
		final File[] files = new File(mucPath)
				.listFiles(f -> !f.getName().startsWith("key-") && f.isFile());

		if (files == null || files.length == 0) {
			getMonitor().info("No MUC files found is the path correct: {}", mucPath);
			throw new BaleenException("No MUC files found to process");
		}
		
		return files;
	}
	
	private String cleanText(String text){
		String clean = text.replaceAll("\n", " ");

		// Strip out the clarification tags []
		clean = clean.replaceAll("(\\[.*?\\]\\s*)*", "");
		clean = clean.replaceAll("\\s{3,}", " \n\n");
		clean = clean.toLowerCase().trim();
		// Baleen bug? Lower case U.S. breaks the sentence splitter?
		clean = clean.replaceAll(Pattern.quote("u.s."), "us");
		
		return clean;
	}

	@Override
	protected void apply(MucEntry entry, JCas jCas) {
		jCas.setDocumentLanguage("en");
		jCas.setDocumentText(entry.getText());

		getSupport().getDocumentAnnotation(jCas).setSourceUri(entry.getId());
	}

	@Override
	protected void doClose() throws IOException {
		// Do nothing
	}

}