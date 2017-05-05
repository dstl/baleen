//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.Compiler;
import com.samskivert.mustache.Template;

import uk.gov.dstl.baleen.consumers.template.ExtractedRecord.Kind;
import uk.gov.dstl.baleen.consumers.utils.SingleDocumentConsumerFormat;
import uk.gov.dstl.baleen.types.metadata.Metadata;

/**
 * Abstract base implementation of the Mustache HTML template record consumer.
 * <p>
 * Subclasses should implement {@link #mapFields(Map)} to convert from records
 * to template fields.
 * </p>
 * <p>
 * The mustache context is populated with entries for metadata, content,
 * sources, records, and fields as per
 * {@link #writeRecords(String, JCas, Map, Map)}.
 * </p>
 * <p>
 * Maps exposed in the context can be iterated by using the entrySet property,
 * and then referring to the {{key}} and {{value}} properties, as per JMustache
 * convention (see
 * <a href="https://github.com/samskivert/jmustache/issues/82">JMustache github
 * ticket #82</a> for details) - eg for metadata:
 * </p>
 * 
 * <pre>
 * {{#metadata.entrySet}}
 *     {{key}} = {{value}}
 * {{/metadata.entrySet}}
 * </pre>
 * 
 * An example template that uses all exposed properties in the context may look
 * like:
 * 
 * <pre>
&lt;html&gt;
&lt;body&gt;
	&lt;h1&gt;All metadata&lt;/h1&gt;
	&lt;div&gt;
		&lt;table&gt;
			&lt;tbody&gt;
				{{#metadata.entrySet}}
				&lt;tr&gt;
					&lt;th&gt;{{key}}&lt;/th&gt;
					&lt;td&gt;{{value}}&lt;/td&gt;
				&lt;/tr&gt;
				{{/metadata.entrySet}}
			&lt;/tbody&gt;
		&lt;/table&gt;
	&lt;/div&gt;
	&lt;h1&gt;Single metadata field&lt;/h1&gt;
	&lt;table&gt;
		&lt;tbody&gt;
			&lt;tr&gt;
				&lt;th&gt;Author&lt;/th&gt;
				&lt;td&gt;{{metadata.author}}&lt;/td&gt;
			&lt;/tr&gt;
		&lt;/tbody&gt;
	&lt;/table&gt;
	&lt;div&gt;
		&lt;h1&gt;Content&lt;/h1&gt;
		&lt;pre&gt;
{{content}}
		&lt;/pre&gt;
	&lt;/div&gt;
	&lt;div&gt;
		&lt;h1&gt;Sources&lt;/h1&gt;
		{{#sources.entrySet}}
		&lt;div&gt;
			&lt;h2&gt;{{key}}&lt;/h2&gt;
			{{#value.entrySet}}
			&lt;h3&gt;Record {{key}}&lt;/h3&gt;
			&lt;h4&gt;Fields&lt;/h4&gt;
			&lt;table&gt;
				&lt;tbody&gt;
					{{#value.entrySet}}
					&lt;tr&gt;
						&lt;th&gt;{{key}}&lt;/th&gt;
						&lt;td&gt;{{value}}&lt;/td&gt;
					&lt;/tr&gt;
					{{/value.entrySet}}
				&lt;/tbody&gt;
			&lt;/table&gt;
			{{/value.entrySet}}
		&lt;/div&gt;
		{{/sources.entrySet}}
	&lt;/div&gt;
	&lt;div&gt;
		&lt;h1&gt;Flattened Records&lt;/h1&gt;
		{{#records.entrySet}}
		&lt;div&gt;
			&lt;h2&gt;Record {{key}}&lt;/h2&gt;
			&lt;h3&gt;Fields&lt;/h3&gt;
			&lt;table&gt;
				&lt;tbody&gt;
					{{#value.entrySet}}
					&lt;tr&gt;
						&lt;th&gt;{{key}}&lt;/th&gt;
						&lt;td&gt;{{value}}&lt;/td&gt;
					&lt;/tr&gt;
					{{/value.entrySet}}
				&lt;/tbody&gt;
			&lt;/table&gt;
		&lt;/div&gt;
		{{/records.entrySet}}
	&lt;/div&gt;
	&lt;div&gt;
		&lt;h1&gt;Fields&lt;/h1&gt;
		&lt;div&gt;
			&lt;table&gt;
				&lt;tbody&gt;
					{{#fields.entrySet}}
					&lt;tr&gt;
						&lt;th&gt;{{key}}&lt;/th&gt;
						&lt;td&gt;{{value}}&lt;/td&gt;
					&lt;/tr&gt;
					{{/fields.entrySet}}
				&lt;/tbody&gt;
			&lt;/table&gt;
		&lt;/div&gt;
	&lt;/div&gt;
&lt;/body&gt;
&lt;/html&gt;
 * </pre>
 */
public abstract class AbstractMustacheHtmlTemplateRecordConsumer extends AbstractTemplateRecordConsumer {

	/** The Constant PARAM_OUTPUT_DIRECTORY. */
	public static final String PARAM_OUTPUT_DIRECTORY = "outputDirectory";

	/**
	 * The output directory.
	 *
	 * @baleen.config generatedDocuments
	 */
	@ConfigurationParameter(name = PARAM_OUTPUT_DIRECTORY, defaultValue = "generatedDocuments")
	private String outputDirectory = "generatedDocuments";

	/**
	 * Compile template.
	 * 
	 * @return
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected static Template compileTemplate(Path templateFilepath) throws IOException {
		Compiler compiler = Mustache.compiler();
		String templateHtml = new String(Files.readAllBytes(templateFilepath), StandardCharsets.UTF_8);
		return compiler.compile(templateHtml);
	}

	@Override
	protected void writeRecords(JCas jCas, String documentSourceName, Map<String, Collection<ExtractedRecord>> records)
			throws AnalysisEngineProcessException {
		Collection<Metadata> metadata = JCasUtil.select(jCas, Metadata.class);
		Map<String, Object> metadataMap = SingleDocumentConsumerFormat.createMetadataMap(metadata);
		Map<String, ?> fields = mapFields(records);

		Map<String, Object> mustacheContext = new HashMap<>(fields);
		mustacheContext.put("metadata", metadataMap);
		mustacheContext.put("content", jCas.getDocumentText());

		writeRecords(documentSourceName, jCas, records, mustacheContext);
	}

	/**
	 * Write records using the given mustache template.
	 * 
	 * The JCas and records are given for reference; subclasses should generally
	 * use the mustache context, which contains entries for:
	 * 
	 * <dl>
	 * <dt>metadata</dt>
	 * <dd>Metadata annotations, collected with
	 * {@link SingleDocumentConsumerFormat#createMetadataMap(Collection)}.
	 * Values for duplicate metadata keys are collected as a list.</dd>
	 * <dt>content</dt>
	 * <dd>The document content, as set on the JCas.</dd>
	 * <dt>sources</dt>
	 * <dd>A map of the records per source. If source names are duplicated then
	 * the records from the last entry will overwrite previous entries.</dd>
	 * <dt>records</dt>
	 * <dd>A flattened map of the records found. If record names are duplicated
	 * between sources then the records from the last entry will overwrite
	 * previous entries.</dd>
	 * <dt>fields</dt>
	 * <dd>A flattened map of all the fields found. If record names or sources
	 * are duplicated then the fields from the last entry will overwrite
	 * previous entries.</dd>
	 * </dl>
	 *
	 * @param documentSourceName
	 *            the document source name
	 * @param records
	 *            the records
	 * @param mustacheContext
	 *            the mustache context
	 */
	protected abstract void writeRecords(String documentSourceName, JCas jCas,
			Map<String, Collection<ExtractedRecord>> records, Map<String, Object> mustacheContext);

	/**
	 * Map records to a moustache field name and value.
	 * 
	 * In trivial cases the field value may be a String, but in others it could
	 * be a list so the template can iterate the values.
	 *
	 * @param metadataMap
	 *            the map of metadata key/value pairs (values can be a String,
	 *            or a list of Strings if there are multiple values - see
	 *            {@link SingleDocumentConsumerFormat#createMetadataMap(Collection)}
	 * @param records
	 *            the records
	 * @return the map of field name to value
	 */
	private Map<String, ?> mapFields(Map<String, Collection<ExtractedRecord>> records) {
		Map<String, Object> context = new HashMap<>();

		Map<String, String> flattenedFields = getFlattenedFields(records);
		context.put("fields", flattenedFields);

		Map<String, Map<String, String>> flattenedRecords = getFlattenedRecords(records);
		context.put("records", flattenedRecords);

		Map<String, Map<String, Map<String, String>>> sourceRecords = getSourceRecords(records);
		context.put("sources", sourceRecords);

		return context;
	}

	/**
	 * Gets the flattened fields.
	 *
	 * Returns a Map of fieldName to fieldValue. Duplicate fields between
	 * records (and records in sources) are flattened - which record / source
	 * wins is undefined.
	 * 
	 * @param records
	 *            the records
	 * @return the flattened fields
	 */
	private static Map<String, String> getFlattenedFields(Map<String, Collection<ExtractedRecord>> records) {
		Map<String, String> fieldMap = new HashMap<>();
		for (Entry<String, Collection<ExtractedRecord>> entry : records.entrySet()) {
			Collection<ExtractedRecord> sourceRecords = entry.getValue();
			for (ExtractedRecord extractedRecord : sourceRecords) {
				Collection<ExtractedField> fields = extractedRecord.getFields();
				fields.forEach(field -> fieldMap.put(field.getName(), field.getValue()));
			}
		}
		return fieldMap;
	}

	/**
	 * Gets the flattened records.
	 * 
	 * Returns a Map of recordName to fields, and fields is a map of fieldName
	 * to fieldValue. Duplicate recordNames between sources are flattened -
	 * which source record wins is undefined.
	 *
	 * @param records
	 *            the records
	 * @return the flattened records
	 */
	private static Map<String, Map<String, String>> getFlattenedRecords(
			Map<String, Collection<ExtractedRecord>> records) {
		Map<String, Map<String, String>> recordMap = new HashMap<>();
		for (Entry<String, Collection<ExtractedRecord>> entry : records.entrySet()) {
			Collection<ExtractedRecord> sourceRecords = entry.getValue();
			for (ExtractedRecord extractedRecord : sourceRecords) {
				if (extractedRecord.getKind() == Kind.DEFAULT) {
					continue;
				}
				Collection<ExtractedField> fields = extractedRecord.getFields();
				Map<String, String> fieldMap = new HashMap<>();
				String name = extractedRecord.getName();
				fields.forEach(field -> fieldMap.put(field.getName(), field.getValue()));
				if (fieldMap.size() > 0) {
					recordMap.put(name, fieldMap);
				}
			}
		}
		return recordMap;
	}

	/**
	 * Gets the source records, as a nested map of maps of maps.
	 * 
	 * Returns a Map of sourceName -> records, where records is a map of
	 * recordName to fields, and fields is a map of fieldName to fieldValue.
	 *
	 * @param records
	 *            the records
	 * @return the source records
	 */
	private static Map<String, Map<String, Map<String, String>>> getSourceRecords(
			Map<String, Collection<ExtractedRecord>> records) {
		Map<String, Map<String, Map<String, String>>> sourceMap = new HashMap<>();
		for (Entry<String, Collection<ExtractedRecord>> entry : records.entrySet()) {
			String sourceName = entry.getKey();
			Map<String, Map<String, String>> recordsMap = new HashMap<>();
			Collection<ExtractedRecord> sourceRecords = entry.getValue();
			for (ExtractedRecord extractedRecord : sourceRecords) {
				if (extractedRecord.getKind() == Kind.DEFAULT) {
					continue;
				}
				Collection<ExtractedField> fields = extractedRecord.getFields();
				Map<String, String> recordFields = new HashMap<>();
				recordsMap.put(extractedRecord.getName(), recordFields);
				fields.forEach(field -> recordFields.put(field.getName(), field.getValue()));
			}
			if (recordsMap.size() > 0) {
				sourceMap.put(sourceName, recordsMap);
			}
		}
		return sourceMap;
	}

	/**
	 * Creates an output writer for a new file in the configured output
	 * directory, with appropriate name and ".html" extension.
	 * <p>
	 * Note: this overwrites existing files (warning if it does so).
	 * </p>
	 * 
	 * @param documentSourceName
	 *            the document source name
	 * @return the writer
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected Writer createOutputWriter(final String documentSourceName, String... parts) throws IOException {
		Path directoryPath = Paths.get(outputDirectory);
		if (!directoryPath.toFile().exists()) {
			Files.createDirectories(directoryPath);
		}
		String baseName = FilenameUtils.getBaseName(documentSourceName);
		String filename = baseName + ((parts != null && parts.length > 0) ? "-" + String.join("-", parts) : "")
				+ ".html";
		Path outputFilePath = directoryPath.resolve(filename);

		if (outputFilePath.toFile().exists()) {
			getMonitor().warn("Overwriting existing output properties file {}", outputFilePath);
		}
		return Files.newBufferedWriter(outputFilePath, StandardCharsets.UTF_8);
	}
}