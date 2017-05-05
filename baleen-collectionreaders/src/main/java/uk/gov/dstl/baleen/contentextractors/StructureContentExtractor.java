//Dstl (c) Crown Copyright 2017
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentextractors;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.uima.UimaContext;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import io.committed.krill.extraction.Extraction;
import io.committed.krill.extraction.exception.ExtractionException;
import io.committed.krill.extraction.tika.TikaFormatExtractor;
import uk.gov.dstl.baleen.common.structure.TextBlocks;
import uk.gov.dstl.baleen.contentextractors.helpers.AbstractContentExtractor;
import uk.gov.dstl.baleen.contentextractors.helpers.DocumentToJCasConverter;
import uk.gov.dstl.baleen.contentmanipulators.helpers.ContentManipulator;
import uk.gov.dstl.baleen.contentmappers.MetaTags;
import uk.gov.dstl.baleen.contentmappers.StructuralAnnotations;
import uk.gov.dstl.baleen.contentmappers.helpers.ContentMapper;
import uk.gov.dstl.baleen.core.utils.BaleenDefaults;
import uk.gov.dstl.baleen.core.utils.BuilderUtils;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.types.structure.Structure;

/**
 * Extracts metadata, structural annotations and text content from the supplied input.
 * 
 * Structural annotations are as defined under the Baleen type system with top level
 * {@link Structure} class.
 * 
 * Structural extraction allows better understanding of the document by downstream annotators which
 * can use the information to segregate the document, rather than treating it as a whole. For
 * example, consider using a regex for each cell in a table which is different to considering the
 * flat text version of the entire table.
 * 
 * The process of structural content extraction is as follows:
 * 
 * <ul>
 * <li>The document is parsed and converted to a rich HTML representation. This is a general 'per
 * document format' conversion.
 * <li>A set of content manipulators act on the HTML which are configured for this Baleen pipeline.
 * These can do anything (add new nodes, remove or amend text, etc). They might be used to clean up
 * the HTML or to remove elements which aren't required by the pipeline.
 * <li>A set of content mappers convert the the HTML nodes into annotations. They may create
 * structural elements, or other types such as metadata or entities. The set of content mappers is
 * configurable per pipeline.
 * <li>The text of the document is extracted. Note that the content mappers can not change the text
 * output, if you wish to change the text output then use a content manipulator.
 * </ul>
 * 
 * Note that content mapper and content manipulators can work in isolation or in coordination. By
 * coordination we mean that a content manipulator might find the most likely title in a document,
 * and mark it via introduction of a new HTML span element with a class title. A special content
 * mapper could then look for this span and add the title as metadata.
 * 
 * To configure content mappers and manipulators, and to use the structural content extractor,
 * define your collection reader as follows.
 * 
 * <pre>
 * collectionreader:
 *   class: FolderReader
 *   contentExtractor: StructureContentExtractor
 *   extractTextBlocks: true
 *   contentManipulators:
 *   - RemoveEmptyText
 *   contentMappers:
 *   - SemanticHtml
 *   - MetaTags
 *   folders:
 *   - ./input
 * </pre>
 * 
 * If you do not include contentManipulators then none will be used. If you omit the contentMappers
 * then the default StructuralAnnotations and MetaTags mapper will be used.
 * 
 * The default value of extractTextBlocks is true. This means that the TextBlocks annotation will be
 * run immediately. If you do not which to run this annotator then set the value to false. Running
 * by default since otherwise the structural annotations extracted here are ignored by the rest of
 * the pipeline. Pipeline developers may wish to disable this so they can configure the TextBlock
 * annotator specifically.
 * 
 * Note that structured extraction will only work (or be beneficial) on certain document types such
 * as DOC, DOCX, PPT/X, XLS/X, PDF and HTML.
 * 
 */
public class StructureContentExtractor extends AbstractContentExtractor {

	public static final String FIELD_CONTENT_MAPPERS = "contentMappers";

	public static final String FIELD_CONTENT_MANIPULATORS = "contentManipulators";

	public static final String FIELD_EXTRACT_TEXT_BLOCKS = "extractTextBlocks";


	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(StructureContentExtractor.class);

	public static final String CORRUPT_FILE_TEXT = "FILE CONTENTS CORRUPT - UNABLE TO PROCESS";

	private static final String METADATA_CONTENT_MANIPULATORS = "baleen:content-manipulators";
	private static final String METADATA_CONTENT_MAPPERS = "baleen:content-mappers";

	private List<String> contentManipulatorClasses;
	private List<String> contentMapperClasses;

	private List<ContentManipulator> manipulators = Collections.emptyList();

	private DocumentToJCasConverter documentConverter;

	private TikaFormatExtractor formatExtractor;

	private TextBlocks textBlocks = null;

	private List<ContentMapper> mappers = Collections.emptyList();

	@Override
	public void doInitialize(UimaContext context, Map<String, Object> params) throws ResourceInitializationException {
		super.doInitialize(context, params);

		Object manipulatorConfig = params.get(FIELD_CONTENT_MANIPULATORS);
		if (manipulatorConfig != null && manipulatorConfig instanceof String[]) {
			try {
				manipulators = createContentProcessor(ContentManipulator.class, BaleenDefaults.DEFAULT_CONTENT_MANIPULATOR_PACKAGE, (String[]) manipulatorConfig);

				// Initialise the manipulators
				initialiseManipulators(context);
			} catch (InvalidParameterException e) {
				throw new ResourceInitializationException(e);
			}
		}


		Object mapperConfig = params.get(FIELD_CONTENT_MAPPERS);
		if (mapperConfig != null && mapperConfig instanceof String[]) {
			try {
				mappers = createContentProcessor(ContentMapper.class, BaleenDefaults.DEFAULT_CONTENT_MAPPER_PACKAGE, (String[]) mapperConfig);

				// Initialise the mappers
				initialiseMappers(context);
			} catch (InvalidParameterException e) {
				throw new ResourceInitializationException(e);
			}
		} else {
			// Defaults to extraction of the Structural Annotations only
			mappers = Lists.newArrayList(new StructuralAnnotations(), new MetaTags());
		}


		contentManipulatorClasses =
				manipulators.stream().map(m -> m.getClass().getName()).collect(Collectors.toList());
		contentMapperClasses =
				mappers.stream().map(m -> m.getClass().getName()).collect(Collectors.toList());

		documentConverter = new DocumentToJCasConverter(mappers);
		formatExtractor = new TikaFormatExtractor();

		// Run the text block annotator after the configuration

		Object extractTextBlockConfig = params.get(FIELD_EXTRACT_TEXT_BLOCKS);
		boolean runTextBlocks = true;
		if (extractTextBlockConfig != null) {
			if (extractTextBlockConfig instanceof String) {
				if ("false".equalsIgnoreCase((String) extractTextBlockConfig)
						|| "no".equalsIgnoreCase((String) extractTextBlockConfig)) {
					runTextBlocks = false;
				}
			} else if (extractTextBlockConfig instanceof Boolean) {
				runTextBlocks = (Boolean) extractTextBlockConfig;

			}
		}

		if (runTextBlocks) {
			textBlocks = new TextBlocks();
			textBlocks.initialize(context);
		}
	}
	
	private void initialiseMappers(UimaContext context){
		mappers.forEach(m -> {
			try {
				m.initialize(context);
			} catch (Exception e) {
				getMonitor().warn("Unable to initialse content mapper: {}", e);
			}
		});
	}
	
	private void initialiseManipulators(UimaContext context){
		manipulators.forEach(m -> {
			try {
				m.initialize(context);
			} catch (Exception e) {
				getMonitor().warn("Unable to initialse content manipulator: {}", e);
			}
		});
	}

	/**
	 * Creates the content processor (ie a mapper or a manipulator).
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz (of T)
	 * @param defaultPackage the default package to look in
	 * @param context the context
	 * @param classes the classes
	 * @return the list
	 * @throws InvalidParameterException the invalid parameter exception
	 */
	// Note this is checked by clazz isInstance
	@SuppressWarnings("unchecked")
	private <T> List<T> createContentProcessor(Class<T> clazz, String defaultPackage, String[] classes) throws InvalidParameterException {
		List<T> list = new ArrayList<>();
		for (String c : classes) {
			try {
				Object instance = BuilderUtils.getClassFromString(c, defaultPackage).newInstance();

				if (clazz.isInstance(instance)) {
					list.add((T) instance);
				} else {
					LOGGER.warn(String.format("Unable to create, as %s is not of  type %s", c, clazz.getName()));
				}

			} catch (InstantiationException | IllegalAccessException e) {
				LOGGER.info("Could not find or instantiate  " + c, e);
			}
		}
		return list;
	}

	@Override
	public void doProcessStream(InputStream stream, String source, JCas jCas) throws IOException {

		try {
			Extraction extraction = extract(stream, source);

			Document document = Jsoup.parse(extraction.getHtml());


			// Add information on content mappers and content manipulators to the metadata
			contentManipulatorClasses.forEach(
					c -> document.head().appendElement("meta").attr("name", METADATA_CONTENT_MANIPULATORS)
					.attr("content", c));
			contentMapperClasses
			.forEach(c -> document.head().appendElement("meta").attr("name", METADATA_CONTENT_MAPPERS)
					.attr("content", c));

			for (ContentManipulator manipulator : manipulators) {
				manipulator.manipulate(document);
			}

			documentConverter.apply(document, jCas);

			super.doProcessStream(stream, source, jCas);

			// Run the text block extraction (if configured)
			if (textBlocks != null) {
				textBlocks.process(jCas);
			}

		} catch (Exception e) {
			getMonitor().warn("Couldn't extract structure from document '{}'", source, e);
			setCorrupt(jCas);
		}
	}

	/**
	 * Perform actual extraction.
	 * 
	 * THis is a separate function to allow it to be overridden during testing (or by other
	 * implementations).
	 *
	 * @param stream the stream
	 * @param source the source
	 * @return the extraction
	 * @throws ExtractionException the extraction exception
	 */
	protected Extraction extract(InputStream stream, String source) throws ExtractionException {
		return formatExtractor.parse(stream, source);
	}

	/**
	 * Mark a document as corrupt.
	 *
	 * @param jCas the jCas
	 */
	private void setCorrupt(JCas jCas) {
		if (Strings.isNullOrEmpty(jCas.getDocumentText())) {
			jCas.setDocumentText(CORRUPT_FILE_TEXT);
		}
	}

	@Override
	public void doDestroy() {
		if (textBlocks != null) {
			textBlocks.destroy();
			textBlocks = null;
		}

		// Destroy all the content mapper and manipulators
		manipulators.forEach(ContentManipulator::destroy);
		mappers.forEach(ContentMapper::destroy);

		super.doDestroy();
	}

}