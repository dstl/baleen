// Dstl (c) Crown Copyright 2017
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
import uk.gov.dstl.baleen.types.structure.Link;
import uk.gov.dstl.baleen.types.structure.Paragraph;
import uk.gov.dstl.baleen.types.structure.Quotation;
import uk.gov.dstl.baleen.types.structure.Table;
import uk.gov.dstl.baleen.types.structure.TableBody;
import uk.gov.dstl.baleen.types.structure.TableCell;
import uk.gov.dstl.baleen.types.structure.TableRow;
import uk.gov.dstl.baleen.types.templates.TemplateField;
import uk.gov.dstl.baleen.types.templates.TemplateRecord;

public class RepeatingFieldAnnotatorTest extends AbstractAnnotatorTest {

  protected static final ObjectMapper YAMLMAPPER = new ObjectMapper(new YAMLFactory());

  private static final String QUOTE1 = "quote";
  private static final String QUOTE2 = "better quote";
  private static final String LINK = "link";
  private static final String QUOTE3 = "best quote";
  private static final String R1C1P1 = "r1c1p1";
  private static final String R1C1P2 = "r1c1p2";
  private static final String R1C2 = "r1c2";
  private static final String R2C1 = "r2c1";
  private static final String R2C2P1 = "r2c2p1";
  private static final String R2C2P2 = "r2c2p2";
  private static final String ROW1 = R1C1P1 + " " + R1C1P2 + " " + R1C2;
  private static final String ROW2 = R2C1 + " " + R2C2P1 + " " + R2C2P2;

  private static final String TEXT =
      String.join("\n", QUOTE1, QUOTE2, LINK, QUOTE1, QUOTE2, LINK, LINK, QUOTE3, ROW1, ROW2);

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

  private Paragraph paragraph1;

  private Paragraph paragraph2;

  private Paragraph paragraph3;

  private Paragraph paragraph4;

  private Paragraph paragraph5;

  private Paragraph paragraph6;

  public RepeatingFieldAnnotatorTest() {
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

    paragraph1 = new Paragraph(jCas);
    paragraph1.setBegin(cursor);
    paragraph1.setDepth(++depth);
    cursor += R1C1P1.length();
    paragraph1.setEnd(cursor);
    paragraph1.addToIndexes();

    paragraph2 = new Paragraph(jCas);
    paragraph2.setBegin(++cursor);
    paragraph2.setDepth(depth);
    cursor += R1C1P2.length();
    paragraph2.setEnd(cursor);
    paragraph2.addToIndexes();

    --depth;
    tableCell11.setEnd(cursor);
    tableCell11.addToIndexes();

    tableCell12 = new TableCell(jCas);
    tableCell12.setBegin(++cursor);
    tableCell12.setDepth(depth);

    paragraph3 = new Paragraph(jCas);
    paragraph3.setBegin(cursor);
    paragraph3.setDepth(++depth);
    cursor += R1C2.length();
    paragraph3.setEnd(cursor);
    paragraph3.addToIndexes();
    --depth;

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

    paragraph4 = new Paragraph(jCas);
    paragraph4.setBegin(cursor);
    paragraph4.setDepth(++depth);
    cursor += R2C1.length();
    paragraph4.setEnd(cursor);
    paragraph4.addToIndexes();
    --depth;

    tableCell21.setEnd(cursor);
    tableCell21.addToIndexes();

    tableCell22 = new TableCell(jCas);
    tableCell22.setBegin(++cursor);
    tableCell22.setDepth(depth);

    paragraph5 = new Paragraph(jCas);
    paragraph5.setBegin(cursor);
    paragraph5.setDepth(++depth);
    cursor += R2C2P1.length();
    paragraph5.setEnd(cursor);
    paragraph5.addToIndexes();

    paragraph6 = new Paragraph(jCas);
    paragraph6.setBegin(++cursor);
    paragraph6.setDepth(depth);
    cursor += R2C2P2.length();
    paragraph6.setEnd(cursor);
    paragraph6.addToIndexes();

    --depth;
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
  }

  @After
  public void tearDown() throws IOException {
    Files.delete(tempDirectory);
  }

  protected Path writeRecordDefinitions()
      throws IOException, JsonGenerationException, JsonMappingException {
    Path definitionFile =
        Files.createTempFile(
            tempDirectory, AbstractRecordAnnotatorTest.class.getSimpleName(), ".yml");
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
    return recordDefinitionConfigurations;
  }

  private TemplateRecordConfiguration createRepeatQuoteRecord1() {
    TemplateRecordConfiguration record = new TemplateRecordConfiguration();
    record.setName("quote1");
    record.setOrder(1);
    record.setPrecedingPath("");
    record.setFollowingPath("Document > Link");
    record.setKind(Kind.NAMED);
    TemplateFieldConfiguration field =
        new TemplateFieldConfiguration("quote", "Document > Quotation:nth-of-type(1)");
    field.setRepeat(true);
    record.setFieldPaths(ImmutableList.of(field));
    return record;
  }

  private TemplateRecordConfiguration createRepeatQuoteRecord2() {
    TemplateRecordConfiguration record = new TemplateRecordConfiguration();
    record.setName("quote2");
    record.setOrder(2);
    record.setPrecedingPath("Document > Link");
    record.setFollowingPath("Document > Link:nth-of-type(2)");
    record.setKind(Kind.NAMED);
    TemplateFieldConfiguration field =
        new TemplateFieldConfiguration("quote", "Document > Quotation:nth-of-type(2)");
    field.setRepeat(true);
    record.setFieldPaths(ImmutableList.of(field));
    return record;
  }

  private TemplateRecordConfiguration createMissingRepeatQuoteRecord() {
    TemplateRecordConfiguration record = new TemplateRecordConfiguration();
    record.setName("missing");
    record.setOrder(3);
    record.setPrecedingPath("Document > Link:nth-of-type(2)");
    record.setFollowingPath("Document > Link:nth-of-type(3)");
    record.setKind(Kind.NAMED);
    TemplateFieldConfiguration field =
        new TemplateFieldConfiguration("quote", "Document > Quotation:nth-of-type(3)");
    field.setRepeat(true);
    record.setFieldPaths(ImmutableList.of(field));
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
    List<TemplateFieldConfiguration> fields =
        ImmutableList.of(
            new TemplateFieldConfiguration("quote", "Document > Quotation:nth-of-type(4)"));
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
    TemplateFieldConfiguration cell1 =
        new TemplateFieldConfiguration(
            "cell1",
            "Document > Table > TableBody > TableRow > TableCell:nth-of-type(1) > Paragraph");
    cell1.setRepeat(true);
    TemplateFieldConfiguration cell2 =
        new TemplateFieldConfiguration(
            "cell2",
            "Document > Table > TableBody > TableRow > TableCell:nth-of-type(2)> Paragraph");
    cell2.setRepeat(true);
    record.setFieldPaths(ImmutableList.of(cell1, cell2));
    return record;
  }

  @Test
  public void testCreateRepeatingFields()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

    Path definitionFile = writeRecordDefinitions();
    try {
      processJCas(TemplateAnnotator.PARAM_RECORD_DEFINITIONS_DIRECTORY, tempDirectory.toString());

      List<TemplateRecord> records = new ArrayList<>(JCasUtil.select(jCas, TemplateRecord.class));
      assertEquals(6, records.size());

      TemplateRecord r1 = records.get(0);
      assertEquals("quote1", r1.getName());
      assertEquals(0, r1.getBegin());
      assertEquals(link1.getBegin(), r1.getEnd());

      List<TemplateField> fields = JCasUtil.selectCovered(TemplateField.class, r1);
      assertEquals(2, fields.size());

      TemplateField field = fields.get(0);
      assertEquals(0, field.getBegin());
      assertEquals("quote", field.getName());
      assertEquals(QUOTE1.length(), field.getEnd());
      assertEquals(QUOTE1, field.getCoveredText());
      assertEquals(QUOTE1, field.getValue());

      field = fields.get(1);
      assertEquals("quote", field.getName());
      assertEquals(quotation2.getBegin(), field.getBegin());
      assertEquals(field.getBegin() + QUOTE2.length(), field.getEnd());
      assertEquals(QUOTE2, field.getCoveredText());
      assertEquals(QUOTE2, field.getValue());

      TemplateRecord r2 = records.get(1);
      assertEquals("quote2", r2.getName());
      assertEquals(link1.getEnd(), r2.getBegin());
      assertEquals(link2.getBegin(), r2.getEnd());

      fields = JCasUtil.selectCovered(TemplateField.class, r2);
      assertEquals(2, fields.size());

      field = fields.get(0);
      assertEquals(quotation3.getBegin(), field.getBegin());
      assertEquals("quote", field.getName());
      assertEquals(field.getBegin() + QUOTE1.length(), field.getEnd());
      assertEquals(QUOTE1, field.getCoveredText());
      assertEquals(QUOTE1, field.getValue());

      field = fields.get(1);
      assertEquals("quote", field.getName());
      assertEquals(quotation4.getBegin(), field.getBegin());
      assertEquals(field.getBegin() + QUOTE2.length(), field.getEnd());
      assertEquals(QUOTE2, field.getCoveredText());
      assertEquals(QUOTE2, field.getValue());

      TemplateRecord r3 = records.get(2);
      assertEquals("missing", r3.getName());
      assertEquals(link2.getEnd(), r3.getBegin());
      assertEquals(link3.getBegin(), r3.getEnd());

      fields = JCasUtil.selectCovered(TemplateField.class, r3);
      assertEquals(0, fields.size());

      TemplateRecord r4 = records.get(3);
      assertEquals("single", r4.getName());
      assertEquals(link3.getEnd(), r4.getBegin());
      assertEquals(table.getBegin(), r4.getEnd());

      fields = JCasUtil.selectCovered(TemplateField.class, r4);
      assertEquals(1, fields.size());
      field = fields.get(0);
      assertEquals("quote", field.getName());
      assertEquals(quotation5.getBegin(), field.getBegin());
      assertEquals(field.getBegin() + QUOTE3.length(), field.getEnd());
      assertEquals(QUOTE3, field.getCoveredText());
      assertEquals(QUOTE3, field.getValue());

      TemplateRecord r5 = records.get(4);
      assertEquals(1, fields.size());
      field = fields.get(0);
      assertEquals("row", r5.getName());
      assertEquals(quotation5.getEnd(), r5.getBegin());
      assertEquals(tableRow1.getEnd(), r5.getEnd());

      fields = JCasUtil.selectCovered(TemplateField.class, r5);
      assertEquals(3, fields.size());
      TemplateField cell111 = fields.get(0);
      assertEquals("cell1", cell111.getName());
      assertEquals(paragraph1.getBegin(), cell111.getBegin());
      assertEquals(paragraph1.getEnd(), cell111.getEnd());
      assertEquals(R1C1P1, cell111.getCoveredText());
      assertEquals(R1C1P1, cell111.getValue());
      TemplateField cell112 = fields.get(1);
      assertEquals("cell1", cell112.getName());
      assertEquals(paragraph2.getBegin(), cell112.getBegin());
      assertEquals(paragraph2.getEnd(), cell112.getEnd());
      assertEquals(R1C1P2, cell112.getCoveredText());
      assertEquals(R1C1P2, cell112.getValue());
      TemplateField cell12 = fields.get(2);
      assertEquals("cell2", cell12.getName());
      assertEquals(tableCell12.getBegin(), cell12.getBegin());
      assertEquals(tableCell12.getEnd(), cell12.getEnd());
      assertEquals(R1C2, cell12.getCoveredText());
      assertEquals(R1C2, cell12.getValue());

      TemplateRecord r6 = records.get(5);
      assertEquals("row", r6.getName());
      assertEquals(tableRow1.getEnd(), r6.getBegin());
      assertEquals(tableRow2.getEnd(), r6.getEnd());

      fields = JCasUtil.selectCovered(TemplateField.class, r6);
      assertEquals(3, fields.size());
      TemplateField cell21 = fields.get(0);
      assertEquals("cell1", cell21.getName());
      assertEquals(tableCell21.getBegin(), cell21.getBegin());
      assertEquals(tableCell21.getEnd(), cell21.getEnd());
      assertEquals(R2C1, cell21.getCoveredText());
      assertEquals(R2C1, cell21.getValue());
      TemplateField cell221 = fields.get(1);
      assertEquals("cell2", cell221.getName());
      assertEquals(paragraph5.getBegin(), cell221.getBegin());
      assertEquals(paragraph5.getEnd(), cell221.getEnd());
      assertEquals(R2C2P1, cell221.getCoveredText());
      assertEquals(R2C2P1, cell221.getValue());
      TemplateField cell222 = fields.get(2);
      assertEquals("cell2", cell222.getName());
      assertEquals(paragraph6.getBegin(), cell222.getBegin());
      assertEquals(paragraph6.getEnd(), cell222.getEnd());
      assertEquals(R2C2P2, cell222.getCoveredText());
      assertEquals(R2C2P2, cell222.getValue());

    } finally {
      Files.delete(definitionFile);
    }
  }
}
