// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis.convertors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;

import uk.gov.dstl.baleen.consumers.analysis.data.BaleenRelation;

public class RelationConvertorTest {

  final AnalysisMockData data = new AnalysisMockData();

  @Test
  public void testConverter() {
    final RelationConvertor converter =
        new RelationConvertor(data.getMonitor(), data.getIdGenerator(), data.getErc());

    final Map<String, BaleenRelation> relations =
        converter.convert(
            data.getJCas(),
            data.getDocumentId(),
            AnalysisMockData.BALEEN_DOC_ID,
            data.getMentions());

    assertEquals(2, relations.size());

    final BaleenRelation r1 =
        relations.values().stream().filter(p -> p.getValue().equals("r1Value")).findFirst().get();

    assertNotNull(r1.getExternalId());
    assertNotNull(r1.getBaleenId());
    assertEquals(data.getDocumentId(), r1.getDocId());
    assertEquals(AnalysisMockData.BALEEN_DOC_ID, r1.getBaleenDocId());
    assertEquals("r1Type", r1.getType());
    assertEquals("r1SubType", r1.getSubType());
    assertNotNull(r1.getSource());
    assertNotNull(r1.getTarget());
  }
}
