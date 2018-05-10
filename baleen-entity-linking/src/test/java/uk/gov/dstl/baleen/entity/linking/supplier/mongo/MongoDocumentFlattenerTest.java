// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.supplier.mongo;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

public class MongoDocumentFlattenerTest {

  private Document documentToTest;
  private Map<String, String> flattenedDocument;

  @Before
  public void setup() {
    Document nestedDocument = new Document().append("nestedKey", "nestedValue");
    List<Integer> intList = new ArrayList<>();
    intList.add(10);
    intList.add(20);
    documentToTest =
        new Document()
            .append("key1", "value1")
            .append("num1", 1)
            .append("num2", 2.5)
            .append("char1", "C")
            .append("num3", 3.0F)
            .append("num4", (long) 4)
            .append("bool1", true)
            .append("key2", nestedDocument)
            .append("someArray", intList);

    flattenedDocument = new MongoDocumentFlattener(documentToTest).flatten();
  }

  @Test
  public void testNestedObjectIsFlattened() {
    assertEquals(
        "Nested value should be flattened to key2.nestedKey",
        "nestedValue",
        flattenedDocument.get("key2.nestedKey"));
  }

  @Test
  public void testArrayIsFlattened() {
    assertEquals(
        "someArray should give a value someArray[0] of '10'",
        Integer.toString(10),
        flattenedDocument.get("someArray[0]"));

    assertEquals(
        "someArray should give a value someArray[1] of '20'",
        Integer.toString(20),
        flattenedDocument.get("someArray[1]"));
  }
}
