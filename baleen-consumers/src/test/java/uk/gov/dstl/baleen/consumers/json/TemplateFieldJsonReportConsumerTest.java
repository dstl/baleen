//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.json;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.structure.Paragraph;
import uk.gov.dstl.baleen.types.templates.TemplateField;

public class TemplateFieldJsonReportConsumerTest extends AbstractAnnotatorTest {
	private static final String EXPECTED_OUTPUT_FILE = TemplateFieldJsonReportConsumerTest.class.getSimpleName()
			+ ".json";

	private static final String SOURCEURI = TemplateFieldJsonReportConsumerTest.class.getSimpleName() + ".txt";

	private static final String PARA1 = "The quick brown fox jumped over the lazy dog's back.";

	private static final String PARA2 = "The quick brown cat jumped over the lazy dog's back.";

	private static final String TEXT = String.join("\n", PARA1, PARA2);

	private Path tempDirectory;

	public TemplateFieldJsonReportConsumerTest() {
		super(TemplateFieldJsonReportConsumer.class);
	}

	@Before
	public void setup() throws IOException {
		jCas.setDocumentText(TEXT);
		tempDirectory = Files.createTempDirectory(TemplateFieldJsonReportConsumerTest.class.getSimpleName());
		tempDirectory.toFile().deleteOnExit();

		DocumentAnnotation documentAnnotation = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
		documentAnnotation.setSourceUri(SOURCEURI);

		Paragraph paragraph1 = new Paragraph(jCas);
		paragraph1.setBegin(0);
		paragraph1.setDepth(1);
		paragraph1.setEnd(52);
		paragraph1.addToIndexes();

		Paragraph paragraph2 = new Paragraph(jCas);
		paragraph2.setBegin(53);
		paragraph2.setDepth(1);
		paragraph2.setEnd(105);
		paragraph2.addToIndexes();

		TemplateField field = new TemplateField(jCas);
		field.setBegin(53);
		field.setEnd(105);
		field.addToIndexes();

	}

	@Test
	public void testJson() throws AnalysisEngineProcessException, ResourceInitializationException, IOException {
		processJCas(TemplateFieldJsonReportConsumer.PARAM_OUTPUT_DIRECTORY, tempDirectory.toString());
		Path outputPath = tempDirectory.resolve(EXPECTED_OUTPUT_FILE);
		outputPath.toFile().deleteOnExit();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
		JsonNode tree = objectMapper.readTree(Files.newInputStream(outputPath));

		// simplistic test for any emitted json
		assertNotNull(tree);
		assertTrue(tree.isContainerNode());

		Files.delete(outputPath);
	}

}