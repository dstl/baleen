//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * Writes Records, and the TemplateFields covered by them, to YAML or JSON
 * files.
 * 
 * This consumer takes extracted records and writes them to YAML or JSON in the
 * configured <code>outputDirectory</code>.
 * <p>
 * The output format defaults to YAML, but can be changed to JSON by setting the
 * configuration parameter <code>outputFormat</code> to <code>json</code> (all
 * other values will result in YAML output).
 * </p>
 * 
 * @see AbstractTemplateRecordConsumer
 */
public class FileTemplateRecordConsumer extends AbstractTemplateRecordConsumer {

	/** The Constant PARAM_OUTPUT_DIRECTORY. */
	public static final String PARAM_OUTPUT_DIRECTORY = "outputDirectory";

	/** The output directory. */
	@ConfigurationParameter(name = PARAM_OUTPUT_DIRECTORY, defaultValue = "records")
	private String outputDirectory = "records";

	/** The Constant PARAM_OUTPUT_FORMAT. */
	public static final String PARAM_OUTPUT_FORMAT = "outputFormat";

	/** The output format. */
	@ConfigurationParameter(name = PARAM_OUTPUT_FORMAT, defaultValue = "yaml")
	private String outputFormat = "yaml";

	/** The extension to use for output files */
	private String outputFileExtension;

	/** The object mapper, used for serialising Records */
	private ObjectMapper objectMapper;

	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		super.doInitialize(aContext);
		if ("json".equals(outputFormat)) {
			objectMapper = new ObjectMapper();
			objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			outputFileExtension = "json";
		} else {
			objectMapper = new ObjectMapper(new YAMLFactory());
			outputFileExtension = "yaml";
		}
		objectMapper.setSerializationInclusion(Include.NON_NULL);
	}

	/**
	 * Writes the given records for the document to a new file derived from the
	 * documentSourceName.
	 * 
	 * The file extension for the output format configured (.yaml or .json) is
	 * automatically appended. If the file exists, it is overwritten.
	 * 
	 * @param jcas
	 *            the JCas
	 * @param documentSourceName
	 *            the document source name
	 * @param records
	 *            the records
	 */
	@Override
	protected void writeRecords(JCas jcas, String documentSourceName, Map<String, Collection<ExtractedRecord>> records)
			throws AnalysisEngineProcessException {
		try (Writer w = createOutputWriter(documentSourceName)) {
			objectMapper.writeValue(w, records);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	/**
	 * Creates an output writer for a new file in the configured output
	 * directory, with appropriate name and extension.
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
	private Writer createOutputWriter(final String documentSourceName) throws IOException {
		Path directoryPath = Paths.get(outputDirectory);
		if (!directoryPath.toFile().exists()) {
			Files.createDirectories(directoryPath);
		}
		String baseName = FilenameUtils.getBaseName(documentSourceName);
		Path outputFilePath = directoryPath.resolve(baseName + "." + outputFileExtension);

		if (outputFilePath.toFile().exists()) {
			getMonitor().warn("Overwriting existing output properties file {}", outputFilePath);
		}
		return Files.newBufferedWriter(outputFilePath, StandardCharsets.UTF_8);
	}
}