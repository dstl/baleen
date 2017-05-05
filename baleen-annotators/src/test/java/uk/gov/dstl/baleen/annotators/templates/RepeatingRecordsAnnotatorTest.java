//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.templates;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.annotators.templates.TemplateRecordConfiguration.Kind;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.structure.Document;
import uk.gov.dstl.baleen.types.structure.Heading;
import uk.gov.dstl.baleen.types.structure.Link;
import uk.gov.dstl.baleen.types.structure.Paragraph;
import uk.gov.dstl.baleen.types.structure.Quotation;
import uk.gov.dstl.baleen.types.structure.Section;
import uk.gov.dstl.baleen.types.structure.Table;
import uk.gov.dstl.baleen.types.structure.TableBody;
import uk.gov.dstl.baleen.types.structure.TableCell;
import uk.gov.dstl.baleen.types.structure.TableRow;
import uk.gov.dstl.baleen.types.templates.TemplateRecord;
import uk.gov.dstl.baleen.types.templates.TemplateField;

public class RepeatingRecordsAnnotatorTest extends AbstractAnnotatorTest {

	protected static final ObjectMapper YAMLMAPPER = new ObjectMapper(new YAMLFactory());

	private static final String QUOTE1 = "quote";
	private static final String QUOTE2 = "better quote";
	private static final String LINK = "link";
	private static final String QUOTE3 = "best quote";
	private static final String R1C1 = "r1c1";
	private static final String R1C2 = "r1c2";
	private static final String R2C1 = "r2c1";
	private static final String R2C2 = "r2c2";
	private static final String ROW1 = R1C1 + " " + R1C2;
	private static final String ROW2 = R2C1 + " " + R2C2;

	private static final String HEADING1 = "h1";
	private static final String PARA1 = "This is para 1";
	private static final String HEADING2 = "h2";
	private static final String PARA2 = "This is para 2";
	private static final String HEADING3 = "h3";
	private static final String PARA3 = "This is para 3";
	private static final String TEXT = String.join("\n", QUOTE1, QUOTE2, LINK, QUOTE1, QUOTE2, LINK, LINK, QUOTE3, ROW1,
			ROW2, HEADING1, PARA1, HEADING2, PARA2, HEADING3, PARA3);

	protected Path tempDirectory;

	private Quotation quotation1;

	private Quotation quotation2;

	private Link link1;

	private Quotation quotation3;

	private Quotation quotation4;

	private Link link2;

	private Link link3;

	private Quotation quotation5;

	private Table table;

	private TableBody tableBody;

	private TableRow tableRow1;

	private TableCell tableCell11;

	private TableCell tableCell12;

	private TableRow tableRow2;

	private TableCell tableCell21;

	private TableCell tableCell22;

	private Section section;

	private Heading heading1;

	private Paragraph paragraph1;

	private Heading heading2;

	private Paragraph paragraph2;

	private Heading heading3;

	private Paragraph paragraph3;

	public RepeatingRecordsAnnotatorTest() {
		super(TemplateAnnotator.class);
	}

	@Before
	public void setup() throws IOException {
		tempDirectory = Files.createTempDirectory(getClass().getSimpleName());
		jCas.setDocumentText(TEXT);
		addAnnotations();
	}

	protected void addAnnotations() {
		int cursor = 0;
		int depth = 0;
		Document document = new Document(jCas);
		document.setBegin(cursor);
		document.setDepth(depth);
		document.setEnd(TEXT.length());
		document.addToIndexes();

		quotation1 = new Quotation(jCas);
		quotation1.setBegin(cursor);
		quotation1.setDepth(++depth);
		cursor += QUOTE1.length();
		quotation1.setEnd(cursor);
		quotation1.addToIndexes();

		quotation2 = new Quotation(jCas);
		quotation2.setBegin(++cursor);
		quotation2.setDepth(depth);
		cursor += QUOTE2.length();
		quotation2.setEnd(cursor);
		quotation2.addToIndexes();

		link1 = new Link(jCas);
		link1.setBegin(++cursor);
		link1.setDepth(depth);
		cursor += LINK.length();
		link1.setEnd(cursor);
		link1.addToIndexes();

		quotation3 = new Quotation(jCas);
		quotation3.setBegin(++cursor);
		quotation3.setDepth(++depth);
		cursor += QUOTE1.length();
		quotation3.setEnd(cursor);
		quotation3.addToIndexes();

		quotation4 = new Quotation(jCas);
		quotation4.setBegin(++cursor);
		quotation4.setDepth(depth);
		cursor += QUOTE2.length();
		quotation4.setEnd(cursor);
		quotation4.addToIndexes();

		link2 = new Link(jCas);
		link2.setBegin(++cursor);
		link2.setDepth(depth);
		cursor += LINK.length();
		link2.setEnd(cursor);
		link2.addToIndexes();

		link3 = new Link(jCas);
		link3.setBegin(++cursor);
		link3.setDepth(depth);
		cursor += LINK.length();
		link3.setEnd(cursor);
		link3.addToIndexes();

		quotation5 = new Quotation(jCas);
		quotation5.setBegin(++cursor);
		quotation5.setDepth(++depth);
		cursor += QUOTE3.length();
		quotation5.setEnd(cursor);
		quotation5.addToIndexes();

		table = new Table(jCas);
		table.setBegin(++cursor);
		table.setDepth(depth);

		tableBody = new TableBody(jCas);
		tableBody.setBegin(cursor);
		tableBody.setDepth(++depth);

		tableRow1 = new TableRow(jCas);
		tableRow1.setBegin(cursor);
		tableRow1.setDepth(++depth);

		tableCell11 = new TableCell(jCas);
		tableCell11.setBegin(cursor);
		tableCell11.setDepth(++depth);
		cursor += R1C1.length();
		tableCell11.setEnd(cursor);
		tableCell11.addToIndexes();

		tableCell12 = new TableCell(jCas);
		tableCell12.setBegin(++cursor);
		tableCell12.setDepth(depth);
		cursor += R1C2.length();
		tableCell12.setEnd(cursor);
		tableCell12.addToIndexes();

		tableRow1.setEnd(cursor);
		tableRow1.addToIndexes();

		tableRow2 = new TableRow(jCas);
		tableRow2.setBegin(++cursor);
		tableRow2.setDepth(--depth);

		tableCell21 = new TableCell(jCas);
		tableCell21.setBegin(cursor);
		tableCell21.setDepth(++depth);
		cursor += R2C1.length();
		tableCell21.setEnd(cursor);
		tableCell21.addToIndexes();

		tableCell22 = new TableCell(jCas);
		tableCell22.setBegin(++cursor);
		tableCell22.setDepth(depth);
		cursor += R2C2.length();
		tableCell22.setEnd(cursor);
		tableCell22.addToIndexes();

		tableRow2.setEnd(cursor);
		tableRow2.addToIndexes();

		tableBody.setEnd(cursor);
		tableBody.addToIndexes();
		--depth;

		table.setEnd(cursor);
		table.addToIndexes();
		--depth;

		section = new Section(jCas);
		section.setBegin(++cursor);
		section.setDepth(depth);

		heading1 = new Heading(jCas);
		heading1.setBegin(cursor);
		heading1.setDepth(++depth);
		cursor += HEADING1.length();
		heading1.setEnd(cursor);
		heading1.addToIndexes();

		paragraph1 = new Paragraph(jCas);
		paragraph1.setBegin(++cursor);
		paragraph1.setDepth(depth);
		cursor += PARA1.length();
		paragraph1.setEnd(cursor);
		paragraph1.addToIndexes();

		heading2 = new Heading(jCas);
		heading2.setBegin(++cursor);
		heading2.setDepth(depth);
		cursor += HEADING1.length();
		heading2.setEnd(cursor);
		heading2.addToIndexes();

		paragraph2 = new Paragraph(jCas);
		paragraph2.setBegin(++cursor);
		paragraph2.setDepth(depth);
		cursor += PARA1.length();
		paragraph2.setEnd(cursor);
		paragraph2.addToIndexes();

		heading3 = new Heading(jCas);
		heading3.setBegin(++cursor);
		heading3.setDepth(depth);
		cursor += HEADING1.length();
		heading3.setEnd(cursor);
		heading3.addToIndexes();

		paragraph3 = new Paragraph(jCas);
		paragraph3.setBegin(++cursor);
		paragraph3.setDepth(depth);
		cursor += PARA1.length();
		paragraph3.setEnd(cursor);
		paragraph3.addToIndexes();

		section.setEnd(cursor);
		section.addToIndexes();

	}

	@After
	public void tearDown() throws IOException {
		Files.delete(tempDirectory);
	}

	protected Path writeRecordDefinitions() throws IOException, JsonGenerationException, JsonMappingException {
		Path definitionFile = Files.createTempFile(tempDirectory, AbstractRecordAnnotatorTest.class.getSimpleName(),
				".yml");
		YAMLMAPPER.writeValue(definitionFile.toFile(), createRecordDefinitions());
		return definitionFile;
	}

	private List<TemplateRecordConfiguration> createRecordDefinitions() {
		List<TemplateRecordConfiguration> recordDefinitionConfigurations = new ArrayList<>();
		recordDefinitionConfigurations.add(createRepeatQuoteRecord1());
		recordDefinitionConfigurations.add(createRepeatQuoteRecord2());
		recordDefinitionConfigurations.add(createMissingRepeatQuoteRecord());
		recordDefinitionConfigurations.add(createSingleQuoteRecord());
		recordDefinitionConfigurations.add(createRowRecord());
		recordDefinitionConfigurations.add(createSectionRecord());
		return recordDefinitionConfigurations;
	}

	private TemplateRecordConfiguration createRepeatQuoteRecord1() {
		TemplateRecordConfiguration record = new TemplateRecordConfiguration();
		record.setName("quote1");
		record.setOrder(1);
		record.setPrecedingPath("");
		record.setFollowingPath("Document > Link");
		record.setCoveredPaths(ImmutableList.of("Document > Quotation"));
		record.setMinimalRepeat("Document > Quotation");
		record.setRepeat(true);
		record.setKind(Kind.NAMED);
		List<TemplateFieldConfiguration> fields = ImmutableList
				.of(new TemplateFieldConfiguration("quote", "Document > Quotation:nth-of-type(1)"));
		record.setFieldPaths(fields);
		return record;
	}

	private TemplateRecordConfiguration createRepeatQuoteRecord2() {
		TemplateRecordConfiguration record = new TemplateRecordConfiguration();
		record.setName("quote2");
		record.setOrder(2);
		record.setPrecedingPath("Document > Link");
		record.setFollowingPath("Document > Link:nth-of-type(2)");
		record.setCoveredPaths(ImmutableList.of("Document > Quotation"));
		record.setMinimalRepeat("Document > Quotation");
		record.setRepeat(true);
		record.setKind(Kind.NAMED);
		List<TemplateFieldConfiguration> fields = ImmutableList
				.of(new TemplateFieldConfiguration("quote", "Document > Quotation:nth-of-type(2)"));
		record.setFieldPaths(fields);
		return record;
	}

	private TemplateRecordConfiguration createMissingRepeatQuoteRecord() {
		TemplateRecordConfiguration record = new TemplateRecordConfiguration();
		record.setName("missing");
		record.setOrder(3);
		record.setPrecedingPath("Document > Link:nth-of-type(2)");
		record.setFollowingPath("Document > Link:nth-of-type(3)");
		record.setCoveredPaths(ImmutableList.of("Document > Quotation"));
		record.setMinimalRepeat("Document > Quotation");
		record.setRepeat(true);
		record.setKind(Kind.NAMED);
		List<TemplateFieldConfiguration> fields = ImmutableList
				.of(new TemplateFieldConfiguration("quote", "Document > Quotation:nth-of-type(3)"));
		record.setFieldPaths(fields);
		return record;
	}

	private TemplateRecordConfiguration createSingleQuoteRecord() {
		TemplateRecordConfiguration record = new TemplateRecordConfiguration();
		record.setName("single");
		record.setOrder(4);
		record.setPrecedingPath("Document > Link:nth-of-type(3)");
		record.setFollowingPath("Document > Table");
		record.setRepeat(false);
		record.setKind(Kind.NAMED);
		List<TemplateFieldConfiguration> fields = ImmutableList
				.of(new TemplateFieldConfiguration("quote", "Document > Quotation:nth-of-type(4)"));
		record.setFieldPaths(fields);
		return record;
	}

	private TemplateRecordConfiguration createRowRecord() {
		TemplateRecordConfiguration record = new TemplateRecordConfiguration();
		record.setName("row");
		record.setOrder(5);
		record.setPrecedingPath("Document > Quotation:nth-of-type(4)");
		record.setFollowingPath("Document > Section");
		record.setCoveredPaths(ImmutableList.of("Document > Table"));
		record.setMinimalRepeat("Document > Table > TableBody > TableRow");
		record.setRepeat(true);
		record.setKind(Kind.NAMED);
		List<TemplateFieldConfiguration> fields = ImmutableList.of(
				new TemplateFieldConfiguration("cell1",
						"Document > Table > TableBody > TableRow > TableCell:nth-of-type(1)"),
				new TemplateFieldConfiguration("cell2",
						"Document > Table > TableBody > TableRow > TableCell:nth-of-type(2)"));
		record.setFieldPaths(fields);
		return record;
	}

	private TemplateRecordConfiguration createSectionRecord() {
		TemplateRecordConfiguration record = new TemplateRecordConfiguration();
		record.setName("section");
		record.setOrder(6);
		record.setPrecedingPath("Document > Table > TableBody > TableRow > TableCell:nth-of-type(2)");
		record.setFollowingPath("");
		record.setCoveredPaths(ImmutableList.of("Document > Section > Heading", "Document > Section > Paragraph"));
		record.setMinimalRepeat("");
		record.setRepeat(true);
		record.setKind(Kind.NAMED);
		List<TemplateFieldConfiguration> fields = ImmutableList.of(
				new TemplateFieldConfiguration("heading", "Document > Section > Heading"),
				new TemplateFieldConfiguration("para", "Document > Section > Paragraph"));
		record.setFieldPaths(fields);
		return record;
	}

	@Test
	public void testRepeatingRecords()
			throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

		Path definitionFile = writeRecordDefinitions();
		try {
			processJCas(TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY, tempDirectory.toString());

			List<TemplateRecord> records = new ArrayList<>(JCasUtil.select(jCas, TemplateRecord.class));
			assertEquals(10, records.size());

			TemplateRecord r1 = records.get(0);
			assertEquals("quote1", r1.getName());
			assertEquals(0, r1.getBegin());
			assertEquals(quotation1.getEnd(), r1.getEnd());

			List<TemplateField> fields = JCasUtil.selectCovered(TemplateField.class, r1);
			assertEquals(1, fields.size());

			TemplateField field = fields.get(0);
			assertEquals(0, field.getBegin());
			assertEquals("quote", field.getName());
			assertEquals(QUOTE1.length(), field.getEnd());
			assertEquals(QUOTE1, field.getCoveredText());
			assertEquals(QUOTE1, field.getValue());

			TemplateRecord r2 = records.get(1);
			assertEquals("quote1", r2.getName());
			assertEquals(quotation1.getEnd(), r2.getBegin());
			assertEquals(quotation2.getEnd(), r2.getEnd());

			fields = JCasUtil.selectCovered(TemplateField.class, r2);
			assertEquals(1, fields.size());
			field = fields.get(0);
			assertEquals("quote", field.getName());
			assertEquals(quotation2.getBegin(), field.getBegin());
			assertEquals(field.getBegin() + QUOTE2.length(), field.getEnd());
			assertEquals(QUOTE2, field.getCoveredText());
			assertEquals(QUOTE2, field.getValue());

			TemplateRecord r3 = records.get(2);
			assertEquals("quote2", r3.getName());
			assertEquals(link1.getEnd(), r3.getBegin());
			assertEquals(quotation3.getEnd(), r3.getEnd());

			fields = JCasUtil.selectCovered(TemplateField.class, r3);
			assertEquals(1, fields.size());

			field = fields.get(0);
			assertEquals(quotation3.getBegin(), field.getBegin());
			assertEquals("quote", field.getName());
			assertEquals(field.getBegin() + QUOTE1.length(), field.getEnd());
			assertEquals(QUOTE1, field.getCoveredText());
			assertEquals(QUOTE1, field.getValue());

			TemplateRecord r4 = records.get(3);
			assertEquals("quote2", r4.getName());
			assertEquals(quotation3.getEnd(), r4.getBegin());
			assertEquals(quotation4.getEnd(), r4.getEnd());

			fields = JCasUtil.selectCovered(TemplateField.class, r4);
			assertEquals(1, fields.size());
			field = fields.get(0);
			assertEquals("quote", field.getName());
			assertEquals(quotation4.getBegin(), field.getBegin());
			assertEquals(field.getBegin() + QUOTE2.length(), field.getEnd());
			assertEquals(QUOTE2, field.getCoveredText());
			assertEquals(QUOTE2, field.getValue());

			TemplateRecord r5 = records.get(4);
			assertEquals("single", r5.getName());
			assertEquals(link3.getEnd(), r5.getBegin());
			assertEquals(table.getBegin(), r5.getEnd());

			fields = JCasUtil.selectCovered(TemplateField.class, r5);
			assertEquals(1, fields.size());
			field = fields.get(0);
			assertEquals("quote", field.getName());
			assertEquals(quotation5.getBegin(), field.getBegin());
			assertEquals(field.getBegin() + QUOTE3.length(), field.getEnd());
			assertEquals(QUOTE3, field.getCoveredText());
			assertEquals(QUOTE3, field.getValue());

			TemplateRecord r6 = records.get(5);
			assertEquals(1, fields.size());
			field = fields.get(0);
			assertEquals("row", r6.getName());
			assertEquals(quotation5.getEnd(), r6.getBegin());
			assertEquals(tableRow1.getEnd(), r6.getEnd());

			fields = JCasUtil.selectCovered(TemplateField.class, r6);
			assertEquals(2, fields.size());
			TemplateField cell11 = fields.get(0);
			assertEquals("cell1", cell11.getName());
			assertEquals(tableCell11.getBegin(), cell11.getBegin());
			assertEquals(tableCell11.getEnd(), cell11.getEnd());
			assertEquals(R1C1, cell11.getCoveredText());
			assertEquals(R1C1, cell11.getValue());
			TemplateField cell12 = fields.get(1);
			assertEquals("cell2", cell12.getName());
			assertEquals(tableCell12.getBegin(), cell12.getBegin());
			assertEquals(tableCell12.getEnd(), cell12.getEnd());
			assertEquals(R1C2, cell12.getCoveredText());
			assertEquals(R1C2, cell12.getValue());

			TemplateRecord r7 = records.get(6);
			assertEquals("row", r7.getName());
			assertEquals(tableRow1.getEnd(), r7.getBegin());
			assertEquals(tableRow2.getEnd(), r7.getEnd());

			fields = JCasUtil.selectCovered(TemplateField.class, r7);
			assertEquals(2, fields.size());
			TemplateField cell21 = fields.get(0);
			assertEquals("cell1", cell21.getName());
			assertEquals(tableCell21.getBegin(), cell21.getBegin());
			assertEquals(tableCell21.getEnd(), cell21.getEnd());
			assertEquals(R2C1, cell21.getCoveredText());
			assertEquals(R2C1, cell21.getValue());
			TemplateField cell22 = fields.get(1);
			assertEquals("cell2", cell22.getName());
			assertEquals(tableCell22.getBegin(), cell22.getBegin());
			assertEquals(tableCell22.getEnd(), cell22.getEnd());
			assertEquals(R2C2, cell22.getCoveredText());
			assertEquals(R2C2, cell22.getValue());

			TemplateRecord r8 = records.get(7);
			assertEquals("section", r8.getName());
			assertEquals(tableRow2.getEnd(), r8.getBegin());
			assertEquals(paragraph1.getEnd(), r8.getEnd());

			fields = JCasUtil.selectCovered(TemplateField.class, r8);
			assertEquals(2, fields.size());
			TemplateField heading = fields.get(0);
			assertEquals("heading", heading.getName());
			assertEquals(heading1.getBegin(), heading.getBegin());
			assertEquals(heading.getBegin() + HEADING1.length(), heading.getEnd());
			assertEquals(HEADING1, heading.getCoveredText());
			assertEquals(HEADING1, heading.getValue());
			TemplateField para = fields.get(1);
			assertEquals("para", para.getName());
			assertEquals(paragraph1.getBegin(), para.getBegin());
			assertEquals(para.getBegin() + PARA1.length(), para.getEnd());
			assertEquals(PARA1, para.getCoveredText());
			assertEquals(PARA1, para.getValue());

			TemplateRecord r9 = records.get(8);
			assertEquals("section", r9.getName());
			assertEquals(paragraph1.getEnd(), r9.getBegin());
			assertEquals(paragraph2.getEnd(), r9.getEnd());

			fields = JCasUtil.selectCovered(TemplateField.class, r9);
			assertEquals(2, fields.size());
			heading = fields.get(0);
			assertEquals("heading", heading.getName());
			assertEquals(heading2.getBegin(), heading.getBegin());
			assertEquals(heading.getBegin() + HEADING2.length(), heading.getEnd());
			assertEquals(HEADING2, heading.getCoveredText());
			assertEquals(HEADING2, heading.getValue());
			para = fields.get(1);
			assertEquals("para", para.getName());
			assertEquals(paragraph2.getBegin(), para.getBegin());
			assertEquals(para.getBegin() + PARA2.length(), para.getEnd());
			assertEquals(PARA2, para.getCoveredText());
			assertEquals(PARA2, para.getValue());

			TemplateRecord r10 = records.get(9);
			assertEquals("section", r10.getName());
			assertEquals(paragraph2.getEnd(), r10.getBegin());
			assertEquals(paragraph3.getEnd(), r10.getEnd());

			fields = JCasUtil.selectCovered(TemplateField.class, r10);
			assertEquals(2, fields.size());
			heading = fields.get(0);
			assertEquals("heading", heading.getName());
			assertEquals(heading3.getBegin(), heading.getBegin());
			assertEquals(heading.getBegin() + HEADING3.length(), heading.getEnd());
			assertEquals(HEADING3, heading.getCoveredText());
			assertEquals(HEADING3, heading.getValue());
			para = fields.get(1);
			assertEquals("para", para.getName());
			assertEquals(paragraph3.getBegin(), para.getBegin());
			assertEquals(para.getBegin() + PARA3.length(), para.getEnd());
			assertEquals(PARA3, para.getCoveredText());
			assertEquals(PARA3, para.getValue());

		} finally {
			Files.delete(definitionFile);
		}
	}

}