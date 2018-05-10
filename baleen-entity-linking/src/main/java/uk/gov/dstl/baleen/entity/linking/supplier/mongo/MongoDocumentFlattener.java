// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.supplier.mongo;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;

import com.github.wnameless.json.flattener.JsonFlattener;

/**
 * Class to flatten Mongo Document to a map of the form: key.nestedKey.nestedKey2...nestedKeyN =
 * nestedKeyNValue
 */
public class MongoDocumentFlattener {

  private Document document;

  /**
   * The Constructor
   *
   * @param document The Mongo document to be flattened
   */
  public MongoDocumentFlattener(Document document) {
    this.document = document;
  }

  /**
   * Flatten a Mongo Document into a Map of Strings of Key Value pairs
   *
   * @return a Map of key value pairs corresponding to the flattened document
   */
  public Map<String, String> flatten() {
    Map<String, String> flattenedDocument = new HashMap<>();
    Map<String, Object> flatJsonMap = JsonFlattener.flattenAsMap(document.toJson());
    for (Entry<String, Object> entry : flatJsonMap.entrySet()) {
      flattenedDocument.put(entry.getKey(), entry.getValue().toString());
    }
    return flattenedDocument;
  }
}
