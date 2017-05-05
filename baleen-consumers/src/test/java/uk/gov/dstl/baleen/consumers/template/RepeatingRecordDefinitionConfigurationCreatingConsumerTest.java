//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.annotators.templates.TemplateRecordConfiguration;
import uk.gov.dstl.baleen.types.templates.TemplateRecordDefinition;

public class RepeatingRecordDefinitionConfigurationCreatingConsumerTest
		extends AbstractTemplateRecordConfigurationCreatingConsumerTest {

	@Override
	@Before
	public void setup() throws IOException {
		super.setup();

		TemplateRecordDefinition record1 = new TemplateRecordDefinition(jCas);
		record1.setBegin(53);
		record1.setEnd(158);
		record1.setName("record1");
		record1.setRepeat(true);
		record1.addToIndexes();

	}

	@Test
	public void testRecordDefinition() throws AnalysisEngineProcessException, ResourceInitializationException,
			JsonParseException, JsonMappingException, IOException {
		processJCas(TemplateRecordConfigurationCreatingConsumer.PARAM_OUTPUT_DIRECTORY, tempDirectory.toString());
		checkDefinitions();
	}

	private void checkDefinitions() throws IOException, JsonParseException, JsonMappingException {
		Path yamlFile = getDefinitionPath();

		List<TemplateRecordConfiguration> definitions = readDefinitions(yamlFile);

		TemplateRecordConfiguration record = assertNamedRecord(definitions);
		assertTrue(record.isRepeat());

		assertNull(record.getMinimalRepeat());
		assertEquals(ImmutableList.of("Paragraph:nth-of-type(2)", "Paragraph:nth-of-type(3)"),
				record.getCoveredPaths());

		assertDefaultRecord(definitions);

		Files.delete(yamlFile);
	}

}