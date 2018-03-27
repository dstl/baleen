// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis.convertors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.consumers.analysis.data.AnalysisConstants;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenDocument;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.metadata.PublishedId;

public class DocumentConvertorTest {

  final AnalysisMockData data = new AnalysisMockData();

  @Test
  @SuppressWarnings("unchecked")
  public void test() {
    final DocumentConvertor converter = new DocumentConvertor();

    final BaleenDocument document =
        converter.convert(
            data.getJCas(),
            data.getDocumentId(),
            AnalysisMockData.BALEEN_DOC_ID,
            data.getDocumentAnnotation());

    assertNotNull(document);

    assertEquals(AnalysisMockData.TEXT, document.getContent());
    assertEquals(data.getDocumentId(), document.getExternalId());
    assertEquals(AnalysisMockData.BALEEN_DOC_ID, document.getBaleenId());

    assertEquals(1, document.getMetadata().size());
    assertEquals("key", document.getMetadata().get(0).getKey());
    assertEquals("value", document.getMetadata().get(0).getValue());

    final Map<String, Object> properties = document.getProperties();
    assertEquals(Arrays.asList(AnalysisMockData.CAVEAT), properties.get(AnalysisConstants.CAVEATS));
    assertEquals(
        Arrays.asList(AnalysisMockData.RELEASABILITY),
        properties.get(AnalysisConstants.RELEASABILITY));
    assertEquals(
        new Date(AnalysisMockData.DOC_TIMESTAMP), properties.get(AnalysisConstants.TIMESTAMP));
    assertEquals(AnalysisMockData.DOCTYPE, properties.get(AnalysisConstants.DOCUMENT_TYPE));
    assertEquals(AnalysisMockData.LANGUAGE, properties.get(AnalysisConstants.LANGUAGE));
    assertEquals(AnalysisMockData.SOURCE, properties.get(AnalysisConstants.SOURCE));

    // Published id for title here
    assertEquals(
        AnalysisMockData.PUBLISHED_ID1,
        ((List<uk.gov.dstl.baleen.consumers.analysis.data.BaleenDocument.PublishedId>)
                properties.get(AnalysisConstants.PUBLISHED_IDS))
            .get(0)
            .getId());
  }

  @Test
  public void testTitleFromMetadata() {
    final DocumentConvertor converter = new DocumentConvertor();

    final Metadata titleMetadata = new Metadata(data.getJCas());
    titleMetadata.setKey("title");
    titleMetadata.setValue("metaTitle");
    titleMetadata.addToIndexes();

    final BaleenDocument document =
        converter.convert(
            data.getJCas(),
            data.getDocumentId(),
            AnalysisMockData.BALEEN_DOC_ID,
            data.getDocumentAnnotation());

    assertEquals("metaTitle", document.getProperties().get(AnalysisConstants.DOCUMENT_TITLE));
  }

  @Test
  public void testTitleFromSource() {
    final DocumentConvertor converter = new DocumentConvertor();

    // Remove the publishedId which would be used instead
    JCasUtil.select(data.getJCas(), PublishedId.class).stream().forEach(p -> p.removeFromIndexes());

    data.getDocumentAnnotation().setSourceUri("this/is/the/source/filename");
    final BaleenDocument document =
        converter.convert(
            data.getJCas(),
            data.getDocumentId(),
            AnalysisMockData.BALEEN_DOC_ID,
            data.getDocumentAnnotation());

    assertEquals("filename", document.getProperties().get(AnalysisConstants.DOCUMENT_TITLE));
  }
}
