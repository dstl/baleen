// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.history.mongo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;

import uk.gov.dstl.baleen.core.history.HistoryEvent;
import uk.gov.dstl.baleen.core.history.Recordable;
import uk.gov.dstl.baleen.core.history.RecordableHistoryEvent;
import uk.gov.dstl.baleen.core.history.impl.RecordableImpl;
import uk.gov.dstl.baleen.history.helpers.AbstractDocumentHistory;

/**
 * Stores history in a Mongo collection.
 *
 * <p>Note that this implementation is 'live', when you add an event it is pushed to the database.
 * When you get events it performs a query. Thus no data is retained in memory. This is particularly
 * important for getHistory since you will not want to repeatedly call get() in a loop.
 *
 * <p>Each document through Baleen is stored as its own Mongo document, in a structure of
 *
 * <pre>
 * {
 *  docId: "document id"
 *  entities: {
 *   "internalid": [
 *   	{ history event }
 *   ],
 *
 *  }
 *
 * }
 * </pre>
 */
public class MongoDocumentHistory extends AbstractDocumentHistory<MongoHistory> {

  private static final String EVENT_TYPE = "type";
  private static final String EVENT_ACTION = "msg";
  private static final String EVENT_REFERRER = "ref";
  private static final String EVENT_TIMESTAMP = "timestamp";

  private static final String RECORDABLE = "rec";
  private static final String RECORDABLE_TEXT = "text";
  private static final String RECORDABLE_BEGIN = "begin";
  private static final String RECORDABLE_END = "end";
  private static final String RECORDABLE_TYPE = "type";

  private static final String DOC_ID = "docId";
  private static final String ENTITIES = "entities";

  private static final Logger LOGGER = LoggerFactory.getLogger(MongoDocumentHistory.class);

  private static final String EVENT_PARAMETERS = "params";

  private final MongoCollection<Document> collection;

  /**
   * New instance, should only be called via MongoHistory.
   *
   * @param history
   * @param collection
   * @param documentId
   */
  public MongoDocumentHistory(
      MongoHistory history, MongoCollection<Document> collection, String documentId) {
    super(history, documentId);
    this.collection = collection;
  }

  @Override
  public void add(HistoryEvent event) {
    Document insert =
        new Document(
            "$push",
            new Document("entities." + event.getRecordable().getInternalId(), convert(event)));

    collection.updateOne(
        new Document(DOC_ID, getDocumentId()), insert, new UpdateOptions().upsert(true));
  }

  @Override
  public Collection<HistoryEvent> getAllHistory() {
    return convert(collection.find(new Document(DOC_ID, getDocumentId())).first());
  }

  @Override
  public Collection<HistoryEvent> getHistory(long recordableId) {
    // Get the document, but only for specific entity
    return convert(
        collection
            .find(new Document(DOC_ID, getDocumentId()))
            .projection(new Document(ENTITIES + "." + recordableId, 1))
            .first());
  }

  private Document convert(HistoryEvent event) {
    return new Document(
            RECORDABLE,
            new Document()
                .append(RECORDABLE_TEXT, event.getRecordable().getCoveredText())
                .append(RECORDABLE_END, event.getRecordable().getEnd())
                .append(RECORDABLE_BEGIN, event.getRecordable().getBegin())
                .append(RECORDABLE_TYPE, event.getRecordable().getTypeName()))
        .append(EVENT_ACTION, event.getAction())
        .append(EVENT_TYPE, event.getEventType())
        .append(EVENT_REFERRER, event.getReferrer())
        .append(EVENT_TIMESTAMP, event.getTimestamp())
        .append(EVENT_PARAMETERS, event.getParameters());
  }

  private Collection<HistoryEvent> convert(Document doc) {
    if (doc == null || doc.get(ENTITIES) == null || !(doc.get(ENTITIES) instanceof Document)) {
      LOGGER.warn("Invalid history document");
      return Collections.emptyList();
    }
    Document entities = (Document) doc.get(ENTITIES);

    List<HistoryEvent> history = Lists.newLinkedList();
    for (Entry<String, Object> entry : entities.entrySet()) {
      if (entry.getValue() instanceof List) {
        convertForEntity(history, entry.getKey(), (List<?>) entry.getValue());
      }
    }
    return history;
  }

  private void convertForEntity(List<HistoryEvent> history, String entityId, List<?> list) {
    for (Object o : list) {
      if (o instanceof Document) {
        HistoryEvent he = convert(entityId, (Document) o);
        if (he != null) {
          history.add(he);
        }
      }
    }
  }

  private HistoryEvent convert(String entityId, Document o) {
    try {
      long id = Long.parseLong(entityId);
      Recordable recordable = convertToRecordable(id, (Document) o.get(RECORDABLE));

      String eventType = (String) o.get(EVENT_TYPE);
      String referrer = (String) o.get(EVENT_REFERRER);
      String action = (String) o.get(EVENT_ACTION);
      long timestamp = (long) o.get(EVENT_TIMESTAMP);

      Map<String, String> params = convertToStringMap(o.get(EVENT_PARAMETERS));

      return new RecordableHistoryEvent(eventType, timestamp, recordable, referrer, action, params);
    } catch (Exception e) {
      LOGGER.warn("Unable to deserialise history event", e);
      return null;
    }
  }

  private Map<String, String> convertToStringMap(Object object) {
    if (object == null || !(object instanceof Document)) {
      return ImmutableMap.of();
    }
    Document dbo = (Document) object;

    Builder<String, String> builder = ImmutableMap.builder();
    for (Entry<String, Object> e : dbo.entrySet()) {
      if (e.getValue() instanceof String) {
        builder.put(e.getKey(), (String) e.getValue());
      } else {
        builder.put(e.getKey(), e.getValue().toString());
      }
    }

    return builder.build();
  }

  private Recordable convertToRecordable(long id, Document o) {
    return new RecordableImpl(
        id,
        (String) o.get(RECORDABLE_TEXT),
        (int) o.get(RECORDABLE_BEGIN),
        (int) o.get(RECORDABLE_END),
        (String) o.get(RECORDABLE_TYPE));
  }
}
