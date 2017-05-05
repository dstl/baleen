//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.templates;

import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.types.templates.TemplateRecord;
import uk.gov.dstl.baleen.types.templates.TemplateField;

public class TemplateAnnotatorTest extends AbstractRecordAnnotatorTest {

	public TemplateAnnotatorTest() {
		super(TemplateAnnotator.class);
	}

	@Test
	public void testCreateFieldAnnotationsFromSelectorFile()
			throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

		Path definitionFile = createGoodRecordDefinition();
		try {
			processJCas(TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY, tempDirectory.toString());

			assertRecordCoversParas2to4();

			TemplateField field1 = JCasUtil.selectSingle(jCas, TemplateField.class);
			assertEquals(53, field1.getBegin());
			assertEquals(105, field1.getEnd());
			assertEquals(PARA2, field1.getCoveredText());
			assertEquals(PARA2, field1.getValue());

		} finally {
			Files.delete(definitionFile);
		}
	}

	@Test(expected = ResourceInitializationException.class)
	public void testCanNotInitializeWithIncorrectStructureType()
			throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

		processJCas(TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY, tempDirectory.toString(),
				TemplateAnnotator.PARAM_TYPE_NAMES, new String[] { "NotAStructure" });
	}

	@Test
	public void testCreateRecordWhenNoFollowingPath()
			throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

		Path definitionFile = createNoFollowingRecordDefinition();
		try {
			processJCas(TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY, tempDirectory.toString(),
					TemplateAnnotator.PARAM_TYPE_NAMES, new String[] { "Paragraph" });

			TemplateRecord record = JCasUtil.selectSingle(jCas, TemplateRecord.class);
			assertEquals(212, record.getBegin());
			assertEquals(265, record.getEnd());
			assertEquals("\n" + PARA5, record.getCoveredText());

			TemplateField field1 = JCasUtil.selectSingle(jCas, TemplateField.class);
			assertEquals(213, field1.getBegin());
			assertEquals(265, field1.getEnd());
			assertEquals(PARA5, field1.getCoveredText());
			assertEquals(PARA5, field1.getValue());

		} finally {
			Files.delete(definitionFile);
		}
	}

	@Test
	public void testCreateDefaultFieldAnnotationsWithDefault()
			throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

		Path definitionFile = createGoodRecordDefinitionWithDefaultAndMissing();
		try {
			processJCas(TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY, tempDirectory.toString());

			TemplateField field1 = JCasUtil.selectSingle(jCas, TemplateField.class);
			assertEquals(212, field1.getBegin());
			assertEquals(212, field1.getEnd());
			assertEquals("", field1.getCoveredText());
			assertEquals("default value", field1.getValue());

		} finally {
			Files.delete(definitionFile);
		}
	}

	@Test
	public void testCreateDefaultFieldAnnotationsMising()
			throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

		Path definitionFile = createGoodRecordDefinitionRequiredMissing();
		try {
			processJCas(TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY, tempDirectory.toString());

			assertEquals(0, JCasUtil.select(jCas, TemplateField.class).size());
		} finally {
			Files.delete(definitionFile);
		}
	}

	@Test
	public void testCreateDefaultFieldAnnotationsMisingWithDefaultValue()
			throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

		Path definitionFile = createGoodRecordDefinitionWithDefaultAndMissing();
		try {
			processJCas(TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY, tempDirectory.toString());

			TemplateField field1 = JCasUtil.selectSingle(jCas, TemplateField.class);
			assertEquals(212, field1.getBegin());
			assertEquals(212, field1.getEnd());
			assertEquals("", field1.getCoveredText());
			assertEquals("default value", field1.getValue());

		} finally {
			Files.delete(definitionFile);
		}
	}

	@Test
	public void testCreateFieldAnnotationsFromSelectorFileWithRegex()
			throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

		Path definitionFile = createGoodRecordDefinitionWithRegex();
		try {
			processJCas(TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY, tempDirectory.toString());

			assertRecordCoversParas2to4();

			TemplateField field1 = JCasUtil.selectSingle(jCas, TemplateField.class);
			assertEquals(69, field1.getBegin());
			assertEquals(72, field1.getEnd());
			assertEquals("cat", field1.getCoveredText());
			assertEquals("cat", field1.getValue());

		} finally {
			Files.delete(definitionFile);
		}
	}

	@Test
	public void testCreateFieldAnnotationsFromSelectorFileWithRegexRequired()
			throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

		Path definitionFile = createGoodRecordDefinitionWithRegexRequired();
		try {
			processJCas(TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY, tempDirectory.toString());

			assertRecordCoversParas2to4();

			TemplateField field1 = JCasUtil.selectSingle(jCas, TemplateField.class);
			assertEquals(122, field1.getBegin());
			assertEquals(125, field1.getEnd());
			assertEquals("rat", field1.getCoveredText());
			assertEquals("rat", field1.getValue());

		} finally {
			Files.delete(definitionFile);
		}
	}

	@Test
	public void testCreateFieldAnnotationsFromSelectorFileWithRegexDefaultNotNeeded()
			throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

		Path definitionFile = createGoodRecordDefinitionWithRegexDefaultNotNeeded();
		try {
			processJCas(TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY, tempDirectory.toString());

			assertRecordCoversParas2to4();

			TemplateField field1 = JCasUtil.selectSingle(jCas, TemplateField.class);
			assertEquals(179, field1.getBegin());
			assertEquals(185, field1.getEnd());
			assertEquals("jumped", field1.getCoveredText());
			assertEquals("jumped", field1.getValue());

		} finally {
			Files.delete(definitionFile);
		}
	}

	@Test
	public void testCreateFieldAnnotationsFromSelectorFileWithRegexDefaultUsed()
			throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

		Path definitionFile = createGoodRecordDefinitionWithRegexDefaultNeeded();
		try {
			processJCas(TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY, tempDirectory.toString());

			assertRecordCoversParas2to4();

			TemplateField field1 = JCasUtil.selectSingle(jCas, TemplateField.class);
			assertEquals(159, field1.getBegin());
			assertEquals(159, field1.getEnd());
			assertEquals("", field1.getCoveredText());
			assertEquals("horse", field1.getValue());

		} finally {
			Files.delete(definitionFile);
		}
	}

	@Test
	public void testCreateFieldAnnotationsFromSelectorFileWithRegexMissingRequired()
			throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

		Path definitionFile = createGoodRecordDefinitionWithRegexRequiredAndMissing();
		try {
			processJCas(TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY, tempDirectory.toString());
			assertFalse(JCasUtil.exists(jCas, TemplateField.class));
			assertTrue(JCasUtil.exists(jCas, TemplateRecord.class));
		} finally {
			Files.delete(definitionFile);
		}
	}

	@Test
	public void testMultipleElementsSelectedForField()
			throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

		Path definitionFile = createBadRecordDefinition();

		try {
			processJCas(TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY, tempDirectory.toString());
			assertRecordCoversParas2to4();
			assertFalse(JCasUtil.exists(jCas, TemplateField.class));
		} finally {
			Files.delete(definitionFile);
		}
	}

	protected TemplateRecord assertRecordCoversParas2to4() {
		TemplateRecord record = JCasUtil.selectSingle(jCas, TemplateRecord.class);
		assertEquals(52, record.getBegin());
		assertEquals(212, record.getEnd());
		assertEquals(String.join("\n", "", PARA2, PARA3, PARA4, ""), record.getCoveredText());
		return record;
	}

	@Test
	public void testDefaultRecord()
			throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

		Path definitionFile = createDefaultRecordDefinition();

		try {
			processJCas(TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY, tempDirectory.toString());

			assertEquals(0, JCasUtil.select(jCas, TemplateRecord.class).size());

			TemplateField field1 = JCasUtil.selectSingle(jCas, TemplateField.class);
			assertEquals(53, field1.getBegin());
			assertEquals(105, field1.getEnd());
			assertEquals(PARA2, field1.getCoveredText());
			assertEquals(PARA2, field1.getValue());

		} finally {
			Files.delete(definitionFile);
		}
	}

	@Test
	public void testNoFieldsInRecord()
			throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

		Path definitionFile = createNoFieldsRecordDefinition();

		try {
			processJCas(TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY, tempDirectory.toString());

			TemplateRecord record = JCasUtil.selectSingle(jCas, TemplateRecord.class);
			assertEquals(158, record.getBegin());
			assertEquals(212, record.getEnd());
			assertEquals(String.join("\n", "", PARA4, ""), record.getCoveredText());

			Collection<TemplateField> fields = JCasUtil.select(jCas, TemplateField.class);
			assertEquals(0, fields.size());

			assertFalse(JCasUtil.contains(jCas, record, TemplateField.class));

		} finally {
			Files.delete(definitionFile);
		}
	}

	private Path createGoodRecordDefinition() throws IOException {
		return createRecord("test", new TemplateFieldConfiguration("field", "Paragraph:nth-of-type(2)"));
	}

	private Path createGoodRecordDefinitionRequiredMissing() throws IOException {
		List<TemplateFieldConfiguration> fields = new ArrayList<>();
		TemplateFieldConfiguration field = new TemplateFieldConfiguration("field", "Paragraph:nth-of-type(5)");
		field.setRequired(true);
		fields.add(field);
		Path definitionFile = Files.createTempFile(tempDirectory, AbstractRecordAnnotatorTest.class.getSimpleName(),
				".yml");
		TemplateRecordConfiguration recordDefinition = new TemplateRecordConfiguration(fields, 0);
		YAMLMAPPER.writeValue(definitionFile.toFile(), singleton(recordDefinition));
		return definitionFile;
	}

	private Path createGoodRecordDefinitionWithDefaultAndMissing() throws IOException {
		TemplateFieldConfiguration fieldDefinitionConfiguration = new TemplateFieldConfiguration("field",
				"Paragraph:nth-of-type(5)");
		fieldDefinitionConfiguration.setDefaultValue("default value");

		List<TemplateFieldConfiguration> fields = new ArrayList<>();
		fields.add(fieldDefinitionConfiguration);
		Path definitionFile = Files.createTempFile(tempDirectory, AbstractRecordAnnotatorTest.class.getSimpleName(),
				".yml");
		TemplateRecordConfiguration recordDefinition = new TemplateRecordConfiguration(fields, 0);
		YAMLMAPPER.writeValue(definitionFile.toFile(), singleton(recordDefinition));
		return definitionFile;
	}

	private Path createGoodRecordDefinitionWithRegex() throws IOException {
		TemplateFieldConfiguration fieldDefinitionConfiguration = new TemplateFieldConfiguration("field",
				"Paragraph:nth-of-type(2)");
		fieldDefinitionConfiguration.setRegex("(?<=brown )(.*)(?= jumped)");
		return createRecord("test", fieldDefinitionConfiguration);
	}

	private Path createGoodRecordDefinitionWithRegexRequired() throws IOException {
		TemplateFieldConfiguration fieldDefinitionConfiguration = new TemplateFieldConfiguration("field",
				"Paragraph:nth-of-type(3)");
		fieldDefinitionConfiguration.setRegex("(?<=brown )(.*)(?= jumped)");
		fieldDefinitionConfiguration.setRequired(true);
		return createRecord("test", fieldDefinitionConfiguration);
	}

	private Path createGoodRecordDefinitionWithRegexRequiredAndMissing() throws IOException {
		TemplateFieldConfiguration fieldDefinitionConfiguration = new TemplateFieldConfiguration("field",
				"Paragraph:nth-of-type(3)");
		fieldDefinitionConfiguration.setRegex("(?<=white )(.*)(?= jumped)");
		fieldDefinitionConfiguration.setRequired(true);
		return createRecord("test", fieldDefinitionConfiguration);
	}

	private Path createGoodRecordDefinitionWithRegexDefaultNotNeeded() throws IOException {
		TemplateFieldConfiguration fieldDefinitionConfiguration = new TemplateFieldConfiguration("field",
				"Paragraph:nth-of-type(4)");
		fieldDefinitionConfiguration.setRegex("(?<=ant )(.*)(?= over)");
		fieldDefinitionConfiguration.setDefaultValue("crawled");
		return createRecord("test", fieldDefinitionConfiguration);
	}

	private Path createGoodRecordDefinitionWithRegexDefaultNeeded() throws IOException {
		TemplateFieldConfiguration fieldDefinitionConfiguration = new TemplateFieldConfiguration("field",
				"Paragraph:nth-of-type(4)");
		fieldDefinitionConfiguration.setRegex("(?<=white )(.*)(?= jumped)");
		fieldDefinitionConfiguration.setDefaultValue("horse");
		return createRecord("test", fieldDefinitionConfiguration);
	}

	private Path createBadRecordDefinition() throws IOException {
		return createRecord("test", new TemplateFieldConfiguration("field", "Table"));
	}

	private Path createDefaultRecordDefinition() throws IOException {
		List<TemplateFieldConfiguration> fields = new ArrayList<>();
		fields.add(new TemplateFieldConfiguration("field", "Paragraph:nth-of-type(2)"));
		Path definitionFile = Files.createTempFile(tempDirectory, AbstractRecordAnnotatorTest.class.getSimpleName(),
				".yml");
		TemplateRecordConfiguration recordDefinition = new TemplateRecordConfiguration(fields, 0);
		YAMLMAPPER.writeValue(definitionFile.toFile(), singleton(recordDefinition));
		return definitionFile;
	}

	private Path createNoFieldsRecordDefinition() throws IOException {
		List<TemplateFieldConfiguration> fields = new ArrayList<>();
		Path definitionFile = Files.createTempFile(tempDirectory, AbstractRecordAnnotatorTest.class.getSimpleName(),
				".yml");
		String precedingPath = "Paragraph:nth-of-type(3)";
		String followingPath = "Paragraph:nth-of-type(5)";
		TemplateRecordConfiguration recordDefinition = new TemplateRecordConfiguration("test", precedingPath,
				followingPath, fields, 0);
		YAMLMAPPER.writeValue(definitionFile.toFile(), singleton(recordDefinition));
		return definitionFile;
	}

	private Path createNoFollowingRecordDefinition() throws IOException {
		List<TemplateFieldConfiguration> fields = new ArrayList<>();
		fields.add(new TemplateFieldConfiguration("field", "Paragraph:nth-of-type(6)"));
		Path definitionFile = Files.createTempFile(tempDirectory, AbstractRecordAnnotatorTest.class.getSimpleName(),
				".yml");
		String precedingPath = "Paragraph:nth-of-type(5)";
		String followingPath = "";
		TemplateRecordConfiguration recordDefinition = new TemplateRecordConfiguration("test", precedingPath,
				followingPath, fields, 0);
		YAMLMAPPER.writeValue(definitionFile.toFile(), singleton(recordDefinition));
		return definitionFile;
	}

}