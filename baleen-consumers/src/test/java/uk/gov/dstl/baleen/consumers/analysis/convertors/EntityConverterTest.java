// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis.convertors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;

import uk.gov.dstl.baleen.consumers.analysis.converters.EntityConverter;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenEntity;

public class EntityConverterTest {

  final AnalysisMockData data = new AnalysisMockData();

  @Test
  public void test() {
    final EntityConverter converter = new EntityConverter();

    final Map<String, BaleenEntity> entities = converter.convert(data.getMentions());

    assertEquals(4, entities.size());

    // Examine one entity

    // This is also a test really... we look for Jonathon (vs Jon)
    final BaleenEntity e =
        entities.values().stream()
            .filter(p -> p.getValue().equalsIgnoreCase("Jonathon"))
            .findFirst()
            .get();

    assertEquals("Person", e.getType());
    assertEquals(data.getDocumentId(), e.getDocId());
    assertEquals(AnalysisMockData.BALEEN_DOC_ID, e.getBaleenDocId());
    assertEquals(2, e.getMentionIds().size());
    assertNotNull(e.getExternalId());
    assertNotNull(e.getBaleenId());

    assertEquals("male", e.getProperties().get("gender"));
  }
}
