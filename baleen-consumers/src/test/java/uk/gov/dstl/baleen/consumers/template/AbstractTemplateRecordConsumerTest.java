// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.junit.Before;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.consumers.template.ExtractedRecord.Kind;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.templates.TemplateField;
import uk.gov.dstl.baleen.types.templates.TemplateRecord;

public abstract class AbstractTemplateRecordConsumerTest extends AbstractAnnotatorTest {

  protected static final String PARA1 = "The quick brown fox jumped over the lazy dog's back.";

  protected static final String PARA2 = "The quick brown cat jumped over the lazy dog's back.";

  protected static final String PARA3 = "The quick brown rat jumped over the lazy dog's back.";

  protected static final String TEXT = String.join("\n", PARA1, PARA2, PARA3);

  protected final Class<? extends AbstractTemplateRecordConsumer> annotatorClass;

  protected final String sourceUri;

  protected final String sourceName;

  public AbstractTemplateRecordConsumerTest(Class<? extends AbstractTemplateRecordConsumer> clazz) {
    super(clazz);
    this.annotatorClass = clazz;
    this.sourceUri = annotatorClass.getSimpleName() + ".txt";
    this.sourceName = annotatorClass.getSimpleName();
  }

  @Before
  public void beforeAbstractRecordConsumerTest() throws IOException {
    jCas.setDocumentText(TEXT);

    DocumentAnnotation documentAnnotation = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
    documentAnnotation.setSourceUri(sourceUri);

    Metadata author = new Metadata(jCas);
    author.setBegin(0);
    author.setEnd(0);
    author.setKey("author");
    author.setValue("The Author");
    author.addToIndexes();

    Metadata creator1 = new Metadata(jCas);
    creator1.setBegin(0);
    creator1.setEnd(0);
    creator1.setKey("creator");
    creator1.setValue("The Creator");
    creator1.addToIndexes();

    Metadata creator2 = new Metadata(jCas);
    creator2.setBegin(0);
    creator2.setEnd(0);
    creator2.setKey("creator");
    creator2.setValue("Baleen");
    creator2.addToIndexes();

    TemplateRecord record1 = new TemplateRecord(jCas);
    record1.setBegin(0);
    record1.setEnd(52);
    record1.setName("record1");
    record1.setSource(sourceName);
    record1.addToIndexes();

    TemplateField record1Field1 = new TemplateField(jCas);
    record1Field1.setBegin(0);
    record1Field1.setEnd(15);
    record1Field1.setName("record1Field1");
    record1Field1.setSource(sourceName);
    record1Field1.setValue(record1Field1.getCoveredText());
    record1Field1.addToIndexes();

    TemplateField record1Field2 = new TemplateField(jCas);
    record1Field2.setBegin(16);
    record1Field2.setEnd(31);
    record1Field2.setName("record1Field2");
    record1Field2.setSource(sourceName);
    record1Field2.setValue(record1Field2.getCoveredText());
    record1Field2.addToIndexes();

    TemplateRecord record2 = new TemplateRecord(jCas);
    record2.setBegin(53);
    record2.setEnd(105);
    record2.setName("record2");
    record2.setSource(sourceName);
    record2.addToIndexes();

    TemplateField record2Field1 = new TemplateField(jCas);
    record2Field1.setBegin(53);
    record2Field1.setEnd(68);
    record2Field1.setName("record2Field1");
    record2Field1.setSource(sourceName);
    record2Field1.setValue(record2Field1.getCoveredText());
    record2Field1.addToIndexes();

    TemplateField record2Field2 = new TemplateField(jCas);
    record2Field2.setBegin(69);
    record2Field2.setEnd(84);
    record2Field2.setName("record2Field2");
    record2Field2.setSource(sourceName);
    record2Field2.setValue(record2Field2.getCoveredText());
    record2Field2.addToIndexes();

    TemplateField noRecordField1 = new TemplateField(jCas);
    noRecordField1.setBegin(106);
    noRecordField1.setEnd(121);
    noRecordField1.setName("noRecordField1");
    noRecordField1.setSource(sourceName);
    noRecordField1.setValue(noRecordField1.getCoveredText());
    noRecordField1.addToIndexes();

    TemplateField noRecordField2 = new TemplateField(jCas);
    noRecordField2.setBegin(122);
    noRecordField2.setEnd(137);
    noRecordField2.setName("noRecordField2");
    noRecordField2.setSource(sourceName);
    noRecordField2.setValue(noRecordField2.getCoveredText());
    noRecordField2.addToIndexes();
  }

  protected void checkRecords(Map<String, Collection<ExtractedRecord>> recordMap) {
    Collection<ExtractedRecord> records = recordMap.get(annotatorClass.getSimpleName());
    Stream<ExtractedRecord> recordStream =
        records
            .stream()
            .filter(p -> p.getKind().equals(Kind.NAMED) && p.getName().equals("record1"));
    List<ExtractedRecord> collect = recordStream.collect(Collectors.toList());
    ExtractedRecord record1 = collect.get(0);
    assertEquals(Kind.NAMED, record1.getKind());
    assertEquals(2, record1.getFields().size());
    assertEquals("The quick brown", findFieldValue("record1Field1", record1.getFields()));
    assertEquals("fox jumped over", findFieldValue("record1Field2", record1.getFields()));

    ExtractedRecord record2 =
        records
            .stream()
            .filter(p -> p.getKind().equals(Kind.NAMED) && p.getName().equals("record2"))
            .collect(Collectors.toList())
            .get(0);
    assertEquals(Kind.NAMED, record1.getKind());
    assertEquals(2, record2.getFields().size());
    assertEquals("The quick brown", findFieldValue("record2Field1", record2.getFields()));
    assertEquals("cat jumped over", findFieldValue("record2Field2", record2.getFields()));

    ExtractedRecord defaultRecord =
        records
            .stream()
            .filter(p -> p.getKind().equals(Kind.DEFAULT))
            .collect(Collectors.toList())
            .get(0);
    assertEquals(null, defaultRecord.getName());
    assertEquals(2, defaultRecord.getFields().size());
    assertEquals("The quick brown", findFieldValue("noRecordField1", defaultRecord.getFields()));
    assertEquals("rat jumped over", findFieldValue("noRecordField2", defaultRecord.getFields()));
  }

  protected String findFieldValue(String fieldName, Collection<ExtractedField> fields) {
    for (ExtractedField extractedField : fields) {
      if (extractedField.getName().equals(fieldName)) {
        return extractedField.getValue();
      }
    }
    return null;
  }
}
