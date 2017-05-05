//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.cas.FSArray;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

import uk.gov.dstl.baleen.core.history.DocumentHistory;
import uk.gov.dstl.baleen.core.history.HistoryEvent;
import uk.gov.dstl.baleen.core.history.HistoryEvents;
import uk.gov.dstl.baleen.types.Base;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.UimaMonitor;
import uk.gov.dstl.baleen.uima.utils.FeatureUtils;

/**
 * Converts from an Entity or Relation into a Map representation, adding history if required.
 */
public class EntityRelationConverter {

	private final boolean outputHistory;
	private final DocumentHistory documentHistory;
	private final Set<String> stopFeatures;
	private final UimaMonitor monitor;
	private final IEntityConverterFields fields;

	private static final String FIELD_VALUE = "value";

	private final ObjectMapper mapper = new ObjectMapper();

	private final MapLikeType mapLikeType = TypeFactory
			.defaultInstance()
			.constructMapLikeType(Map.class, String.class, Object.class);

	/**
	 * New instance.
	 *
	 * @param monitor
	 *            the monitor to log to
	 * @param outputHistory
	 *            true if should output history
	 * @param documentHistory
	 *            the history of the document
	 * @param stopFeatures
	 *            features which should be excluded from the serialisation
	 * @param fields
	 *            fields to map properties to
	 */
	public EntityRelationConverter(UimaMonitor monitor, boolean outputHistory, DocumentHistory documentHistory,
			Set<String> stopFeatures, IEntityConverterFields fields) {
		this.monitor = monitor;
		this.outputHistory = outputHistory;
		this.documentHistory = documentHistory;
		this.stopFeatures = stopFeatures;
		this.fields = fields;
	}

	private UimaMonitor getMonitor() {
		return monitor;
	}

	/**
	 * Convert from an entity to a map.
	 *
	 * @param entity
	 *            the entity to convert
	 * @return a map containing the entity's fields (and history is required)
	 */
	public Map<String, Object> convertEntity(Entity entity) {
		Map<String, Object> map = Maps.newHashMap();

		convertFeatures(map, entity);

		if (outputHistory && documentHistory != null) {
			Collection<HistoryEvent> events = documentHistory.getHistory(entity.getInternalId());
			convertHistory(map, events, entity.getInternalId());
		}
		putIfExists(map, fields.getExternalId(), entity.getExternalId());

		return map;
	}

	/**
	 * Convert from a relation to a map.
	 *
	 * @param relation
	 *            the relation to convert
	 * @return a map containing the relation's fields (and history is required)
	 */
	public Map<String, Object> convertRelation(Relation relation) {
		Map<String, Object> map = Maps.newHashMap();

		convertFeatures(map, relation);

		if (outputHistory && documentHistory != null) {
			Collection<HistoryEvent> events = documentHistory.getHistory(relation.getInternalId());
			convertHistory(map, events, relation.getInternalId());
		}
		putIfExists(map, fields.getExternalId(), relation.getExternalId());

		return map;
	}

	/**
	 * Convert from an event to a map.
	 *
	 * @param event
	 *            the relation to convert
	 * @return a map containing the relation's fields (and history is required)
	 */
	public Map<String, Object> convertEvent(Event event) {
		Map<String, Object> map = Maps.newHashMap();

		convertFeatures(map, event);

		if (outputHistory && documentHistory != null) {
			Collection<HistoryEvent> events = documentHistory.getHistory(event.getInternalId());
			convertHistory(map, events, event.getInternalId());
		}
		putIfExists(map, fields.getExternalId(), event.getExternalId());

		return map;
	}

	private void convertFeatures(Map<String, Object> map, Base base) {
		for (Feature f : base.getType().getFeatures()) {
			if (stopFeatures.contains(f.getName())) {
				continue;
			}

			try {
				convertFeature(map, base, f);
			} catch (Exception e) {
				getMonitor().warn(
						"Couldn't output {} to map. Type '{}' isn't supported.", f.getName(),
						f.getRange().getShortName(), e);
			}
		}
		map.put("type", base.getType().getShortName());
		if (map.get(FIELD_VALUE) == null || Strings.isNullOrEmpty(map.get(FIELD_VALUE).toString())) {
			map.put(FIELD_VALUE, base.getCoveredText());
		}
	}

	private void convertFeature(Map<String, Object> map, Base base, Feature f) {
		if (f.getRange().isPrimitive()) {
			if ("geoJson".equals(f.getShortName())) {
				getMonitor().trace("Feature is GeoJSON - parsing to a database object");
				putGeoJson(map, base.getFeatureValueAsString(f));
			} else {
				getMonitor().trace("Converting primitive feature to an object");
				map.put(ConsumerUtils.toCamelCase(f.getShortName()), FeatureUtils.featureToObject(f, base));
			}
		} else if (f.getRange().isArray() && f.getRange().getComponentType() != null
				&& f.getRange().getComponentType().isPrimitive()) {
			getMonitor().trace("Converting primitive feature to an array");
			map.put(ConsumerUtils.toCamelCase(f.getShortName()), FeatureUtils.featureToList(f, base));
		} else {
			getMonitor().trace("Feature is not a primitive type - will try to treat the feature as an entity");
			if (f.getRange().isArray()) {
				getMonitor().trace("Feature is an array - attempting converstion to an array of entities");
				FSArray fArr = (FSArray) base.getFeatureValue(f);
				if (fArr != null) {
					map.put(ConsumerUtils.toCamelCase(f.getShortName()), getEntityIds(fArr));
				}
			} else {
				getMonitor().trace("Feature is singular - attempting conversion to a single entity");
				FeatureStructure ent = base.getFeatureValue(f);
				if (ent == null) {
					// Ignore null entities
				} else if (ent instanceof Entity) {
					map.put(ConsumerUtils.toCamelCase(f.getShortName()), ((Entity) ent).getExternalId());
				} else {
					getMonitor().trace("Unable to persist feature {}", f.getShortName());
				}

			}

		}
	}

	private void putGeoJson(Map<String, Object> map, String geojson) {
		try {
			if (!Strings.isNullOrEmpty(geojson)) {
				putIfExists(map, fields.getGeoJSON(), mapper.readValue(geojson, mapLikeType));
			}
		} catch (IOException e) {
			getMonitor().warn("Unable to persist geoJson", e);
		}
	}

	private List<String> getEntityIds(FSArray entityArray) {
		List<String> entities = new ArrayList<>();

		for (int x = 0; x < entityArray.size(); x++) {

			FeatureStructure featureStructure = entityArray.get(x);
			if (featureStructure instanceof Entity) {
				Entity ent = (Entity) featureStructure;
				entities.add(ent.getExternalId());
			}
		}

		return entities;
	}

	private void convertHistory(Map<String, Object> map, Collection<HistoryEvent> events, long entityInternalId) {
		List<Object> list = new LinkedList<Object>();
		saveEvents(list, events, entityInternalId);
		putIfExists(map, fields.getHistory(), list);
	}

	private void saveEvents(List<Object> list, Collection<HistoryEvent> events, long entityInternalId) {
		for (HistoryEvent event : events) {
			saveEvent(list, event, entityInternalId);
		}
	}

	private void saveEvent(List<Object> list, HistoryEvent event, long entityInternalId) {
		Map<String, Object> e = new LinkedHashMap<String, Object>();

		if (event.getRecordable().getInternalId() != entityInternalId) {
			// Only save the internal id as a reference to entities which aren't this one.
			putIfExists(e, fields.getHistoryRecordable(), event.getRecordable().getInternalId());
		}
		putIfExists(e, fields.getHistoryAction(), event.getAction());
		putIfExists(e, fields.getHistoryType(), event.getEventType());
		putIfExists(e, fields.getHistoryParameters(), event.getParameters());
		putIfExists(e, fields.getHistoryReferrer(), event.getReferrer());
		putIfExists(e, fields.getHistoryTimestamp(), event.getTimestamp());

		list.add(e);

		if (HistoryEvents.MERGED_TYPE.equalsIgnoreCase(event.getEventType()) && event.getParameters() != null
				&& event.getParameters(HistoryEvents.PARAM_MERGED_ID).isPresent()) {
			Optional<String> mergedId = event.getParameters(HistoryEvents.PARAM_MERGED_ID);
			Integer id = Ints.tryParse(mergedId.get());
			if (id != null) {
				Collection<HistoryEvent> mergedEvents = documentHistory.getHistory(id);
				if (mergedEvents != null) {
					saveEvents(list, mergedEvents, entityInternalId);
				} else {
					getMonitor().warn("Null history for {}", id);
				}
			} else {
				getMonitor().warn("No merge id for merge history of {}", event.getRecordable());
			}
		}
	}

	private void putIfExists(Map<String, Object> map, String key, Object value) {
		if (!Strings.isNullOrEmpty(key)) {
			map.put(key, value);
		}
	}

}
