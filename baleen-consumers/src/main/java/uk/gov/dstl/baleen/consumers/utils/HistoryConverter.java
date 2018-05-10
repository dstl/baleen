// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.utils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.primitives.Ints;

import uk.gov.dstl.baleen.core.history.DocumentHistory;
import uk.gov.dstl.baleen.core.history.HistoryEvent;
import uk.gov.dstl.baleen.core.history.HistoryEvents;
import uk.gov.dstl.baleen.types.Base;
import uk.gov.dstl.baleen.uima.UimaMonitor;

/** Converts a collection of HistoryEvents to a Map */
public class HistoryConverter {

  private final IEntityConverterFields fields;
  private final UimaMonitor monitor;
  private final long entityInternalId;
  private final DocumentHistory documentHistory;

  /**
   * Constructor
   *
   * @param fields The fields to map
   * @param documentHistory The document history
   * @param monitor the UIMAMonitor to log to
   */
  public HistoryConverter(
      Base entity,
      IEntityConverterFields fields,
      DocumentHistory documentHistory,
      UimaMonitor monitor) {

    this.entityInternalId = entity.getInternalId();
    this.fields = fields;
    this.monitor = monitor;
    this.documentHistory = documentHistory;
  }

  /** Add history to the map */
  public Map<String, Object> convert() {
    Map<String, Object> map = new IgnoreEmptyKeyMapDecorator<>(new LinkedHashMap<>());
    List<Object> list = createEventsList();
    map.put(fields.getHistory(), list);
    return map;
  }

  private List<Object> createEventsList() {
    List<Object> list = new LinkedList<>();
    for (HistoryEvent historyEvent : documentHistory.getHistory(entityInternalId)) {
      Map<String, Object> map = createEventMap(historyEvent);
      list.add(map);

      if (isMerged(historyEvent)
          && historyEvent.getParameters(HistoryEvents.PARAM_MERGED_ID).isPresent()) {

        addMergedEventsToList(list, historyEvent);
      }
    }
    return list;
  }

  private Map<String, Object> createEventMap(HistoryEvent historyEvent) {

    Map<String, Object> linkedHistoryMap = new IgnoreEmptyKeyMapDecorator<>(new LinkedHashMap<>());

    if (historyEvent.getRecordable().getInternalId() != entityInternalId) {
      // Only save the internal id as a reference to entities which aren't this one.
      linkedHistoryMap.put(
          fields.getHistoryRecordable(), historyEvent.getRecordable().getInternalId());
    }
    linkedHistoryMap.put(fields.getHistoryAction(), historyEvent.getAction());
    linkedHistoryMap.put(fields.getHistoryType(), historyEvent.getEventType());
    linkedHistoryMap.put(fields.getHistoryParameters(), historyEvent.getParameters());
    linkedHistoryMap.put(fields.getHistoryReferrer(), historyEvent.getReferrer());
    linkedHistoryMap.put(fields.getHistoryTimestamp(), historyEvent.getTimestamp());
    return linkedHistoryMap;
  }

  private void addMergedEventsToList(List<Object> list, HistoryEvent historyEvent) {

    Optional<String> mergedId = historyEvent.getParameters(HistoryEvents.PARAM_MERGED_ID);
    Optional<Integer> id = mergedId.map(Ints::tryParse);
    if (id.isPresent()) {
      Collection<HistoryEvent> mergedEvents = documentHistory.getHistory(id.get());
      if (mergedEvents != null) {
        createEventsList(list, mergedEvents);
      } else {
        monitor.warn("Null history for {}", id.get());
      }
    } else {
      monitor.warn("No merge id for merge history of {}", historyEvent.getRecordable());
    }
  }

  private void createEventsList(List<Object> list, Collection<HistoryEvent> mergedHistoryEvents) {
    for (HistoryEvent mergedHistoryEvent : mergedHistoryEvents) {
      Map<String, Object> map = createEventMap(mergedHistoryEvent);
      list.add(map);
    }
  }

  private boolean isMerged(HistoryEvent historyEvent) {
    return HistoryEvents.MERGED_TYPE.equalsIgnoreCase(historyEvent.getEventType());
  }
}
