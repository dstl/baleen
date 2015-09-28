//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.history.mongo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.history.HistoryEvent;
import uk.gov.dstl.baleen.core.history.Recordable;
import uk.gov.dstl.baleen.core.history.RecordableHistoryEvent;
import uk.gov.dstl.baleen.core.history.impl.RecordableImpl;
import uk.gov.dstl.baleen.history.helpers.AbstractDocumentHistory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Stores history in a Mongo collection.
 *
 * Note that this implementation is 'live', when you add an event it is pushed to
 * the database. When you get events it performs a query. Thus no data is
 * retained in memory. This is particularly important for getHistory since you
 * will not want to repeatedly call get() in a loop.
 *
 * Each document through Baleen is stored as its own Mongo document, in a
 * structure of
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
 *
 * 
 *
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

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MongoDocumentHistory.class);

	private static final String EVENT_PARAMETERS = "params";

	private final DBCollection collection;

	/**
	 * New instance, should only be called via MongoHistory.
	 *
	 * @param history
	 * @param collection
	 * @param documentId
	 */
	public MongoDocumentHistory(MongoHistory history, DBCollection collection,
			String documentId) {
		super(history, documentId);
		this.collection = collection;
	}

	@Override
	public void add(HistoryEvent event) {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder
				.start()
				.push("$push")
				.add("entities." + event.getRecordable().getInternalId(),
						convert(event)).pop();

		collection.update(new BasicDBObject(DOC_ID, getDocumentId()),
				builder.get(), true, false);
	}

	@Override
	public Collection<HistoryEvent> getAllHistory() {
		return convert(collection.findOne(new BasicDBObject(DOC_ID,
				getDocumentId())));
	}

	@Override
	public Collection<HistoryEvent> getHistory(long recordableId) {
		// Get the document, but only for specific entity
		return convert(collection.findOne(new BasicDBObject(DOC_ID,
				getDocumentId()), new BasicDBObject(ENTITIES + "."
				+ recordableId, 1)));

	}

	private DBObject convert(HistoryEvent event) {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start()
				.push(RECORDABLE)
					.add(RECORDABLE_TEXT, event.getRecordable().getCoveredText())
					.add(RECORDABLE_END, event.getRecordable().getEnd())
					.add(RECORDABLE_BEGIN, event.getRecordable().getBegin())
					.add(RECORDABLE_TYPE, event.getRecordable().getTypeName())
				.pop()
				.add(EVENT_ACTION, event.getAction())
				.add(EVENT_TYPE, event.getEventType())
				.add(EVENT_REFERRER, event.getReferrer())
				.add(EVENT_TIMESTAMP, event.getTimestamp())
				.add(EVENT_PARAMETERS, event.getParameters());

		return builder.get();
	}

	private Collection<HistoryEvent> convert(DBObject dbo) {
		if (dbo == null ||dbo.get(ENTITIES)== null|| !(dbo.get(ENTITIES) instanceof BasicDBObject)) {
			LOGGER.warn("Invalid history document");
			return Collections.emptyList();
		}
		BasicDBObject entities = (BasicDBObject) dbo.get(ENTITIES);

		List<HistoryEvent> history = Lists.newLinkedList();
		for (String entityId : entities.keySet()) {
			if (entities.get(entityId) != null && entities.get(entityId) instanceof BasicDBList) {

				BasicDBList list = (BasicDBList) entities.get(entityId);
				convertForEntity(history, entityId, list);
			}
		}
		return history;
	}

	private void convertForEntity(List<HistoryEvent> history, String entityId,
			BasicDBList list) {
		for (Object o : list) {
			if(o instanceof DBObject) {
				HistoryEvent he = convert(entityId, (DBObject) o);
				if (he != null) {
					history.add(he);
				}
			}
		}
	}

	private HistoryEvent convert(String entityId, DBObject o) {
		try {
			long id = Long.parseLong(entityId);
			Recordable recordable = convertToRecordable(id,
					(DBObject) o.get(RECORDABLE));

			String eventType = (String) o.get(EVENT_TYPE);
			String referrer = (String) o.get(EVENT_REFERRER);
			String action = (String) o.get(EVENT_ACTION);
			long timestamp = (long) o.get(EVENT_TIMESTAMP);

			Map<String,String> params = convertToStringMap(o.get(EVENT_PARAMETERS));


			return new RecordableHistoryEvent(eventType, timestamp, recordable, referrer, action, params);
		} catch (Exception e) {
			LOGGER.warn("Unable to deserialise history event", e);
			return null;
		}

	}

	private Map<String, String> convertToStringMap(Object object) {
		if(object == null || !(object instanceof BasicDBObject)) {
			return ImmutableMap.of();
		}
		BasicDBObject dbo = (BasicDBObject)object;

		Builder<String, String> builder = ImmutableMap.builder();
		for(Entry<String,Object> e : dbo.entrySet()) {
			if(e.getValue() instanceof String) {
				builder.put(e.getKey(), (String)e.getValue());
			} else {
				builder.put(e.getKey(), e.getValue().toString());
			}
		}

		return builder.build();
	}

	private Recordable convertToRecordable(long id, DBObject o) {
		return new RecordableImpl(id, (String) o.get(RECORDABLE_TEXT),
				(int) o.get(RECORDABLE_BEGIN), (int) o.get(RECORDABLE_END),
				(String) o.get(RECORDABLE_TYPE));

	}

}
