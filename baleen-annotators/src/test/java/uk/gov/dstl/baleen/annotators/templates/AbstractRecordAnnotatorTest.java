//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.templates;

import static java.util.Collections.singleton;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.structure.Paragraph;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

public abstract class AbstractRecordAnnotatorTest extends AbstractAnnotatorTest {

	protected static final String PARA1 = "The quick brown fox jumped over the lazy dog's back.";

	protected static final String PARA2 = "The quick brown cat jumped over the lazy dog's back.";

	protected static final String PARA3 = "The quick brown rat jumped over the lazy dog's back.";

	protected static final String PARA4 = "The quick brown ant jumped over the lazy dog's back.";

	protected static final String PARA5 = "The quick brown elk jumped over the lazy dog's back.";

	protected static final String TEXT = String.join("\n", PARA1, PARA2, PARA3, PARA4, "", PARA5);

	protected static final ObjectMapper YAMLMAPPER = new ObjectMapper(new YAMLFactory());

	protected Path tempDirectory;

	public AbstractRecordAnnotatorTest(Class<? extends BaleenAnnotator> annotatorClass) {
		super(annotatorClass);
	}

	@Before
	public void setup() throws IOException {
		tempDirectory = Files.createTempDirectory(getClass().getSimpleName());
		jCas.setDocumentText(TEXT);
		addParagraphAnnotations();
	}

	protected void addParagraphAnnotations() {
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

		Paragraph paragraph3 = new Paragraph(jCas);
		paragraph3.setBegin(106);
		paragraph3.setDepth(1);
		paragraph3.setEnd(158);
		paragraph3.addToIndexes();

		Paragraph paragraph4 = new Paragraph(jCas);
		paragraph4.setBegin(159);
		paragraph4.setDepth(1);
		paragraph4.setEnd(211);
		paragraph4.addToIndexes();

		Paragraph paragraph5 = new Paragraph(jCas);
		paragraph5.setBegin(212);
		paragraph5.setDepth(1);
		paragraph5.setEnd(212);
		paragraph5.addToIndexes();

		Paragraph paragraph6 = new Paragraph(jCas);
		paragraph6.setBegin(213);
		paragraph6.setDepth(1);
		paragraph6.setEnd(265);
		paragraph6.addToIndexes();
	}

	@After
	public void tearDown() throws IOException {
		Files.delete(tempDirectory);
	}

	protected Path createRecord(String name, TemplateFieldConfiguration... fields)
			throws IOException, JsonGenerationException, JsonMappingException {
		Path definitionFile = Files.createTempFile(tempDirectory, AbstractRecordAnnotatorTest.class.getSimpleName(),
				".yml");
		String precedingPath = "Paragraph:nth-of-type(1)";
		String followingPath = "Paragraph:nth-of-type(5)";
		TemplateRecordConfiguration recordDefinition = new TemplateRecordConfiguration(name, precedingPath,
				followingPath, Arrays.asList(fields), 0);
		YAMLMAPPER.writeValue(definitionFile.toFile(), singleton(recordDefinition));
		return definitionFile;
	}
}