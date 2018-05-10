// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.utils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.gov.dstl.baleen.core.history.DocumentHistory;
import uk.gov.dstl.baleen.core.history.HistoryEvent;
import uk.gov.dstl.baleen.core.history.HistoryEvents;
import uk.gov.dstl.baleen.core.history.Recordable;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.UimaMonitor;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class HistoryConverterTest {

  private HistoryConverter historyConverter;
  private IEntityConverterFields fields;

  @Mock private Entity entity;
  @Mock private DocumentHistory documentHistory;
  @Mock private HistoryEvent historyEvent;
  @Mock private HistoryEvent mergedHistoryEvent;
  @Mock private Recordable recordable;
  @Mock private UimaMonitor monitor;

  private static final String ACTION = "action";
  private static final String EVENT_TYPE = "eventType";
  private static final String REFERRER = "referrer";
  private static final String HISTORY = "history";
  private long currentTimeMillis;

  private LinkedList<Object> historyList;

  @Before
  public void setUp() throws Exception {

    fields = new DefaultFields();
    long internalId = 1L;
    int mergedId = 10;

    currentTimeMillis = System.currentTimeMillis();
    Map<String, String> parameters = new HashMap<>();
    parameters.put("key1", "value1");
    parameters.put(HistoryEvents.PARAM_MERGED_ID, Integer.toString(mergedId));

    when(recordable.getInternalId()).thenReturn(internalId);

    Collection<HistoryEvent> historyEvents = new ArrayList<>();
    when(historyEvent.getAction()).thenReturn(ACTION);
    when(historyEvent.getEventType()).thenReturn(EVENT_TYPE);
    when(historyEvent.getReferrer()).thenReturn(REFERRER);
    when(historyEvent.getParameters()).thenReturn(parameters);
    when(historyEvent.getTimestamp()).thenReturn(currentTimeMillis);
    when(historyEvent.getRecordable()).thenReturn(recordable);
    historyEvents.add(historyEvent);

    when(mergedHistoryEvent.getAction()).thenReturn(ACTION);
    when(mergedHistoryEvent.getEventType()).thenReturn(HistoryEvents.MERGED_TYPE);
    when(mergedHistoryEvent.getReferrer()).thenReturn(REFERRER);
    when(mergedHistoryEvent.getParameters()).thenReturn(parameters);
    when(mergedHistoryEvent.getTimestamp()).thenReturn(currentTimeMillis);
    when(mergedHistoryEvent.getRecordable()).thenReturn(recordable);
    when(mergedHistoryEvent.getParameters(HistoryEvents.PARAM_MERGED_ID))
        .thenReturn(Optional.ofNullable(parameters.get(HistoryEvents.PARAM_MERGED_ID)));
    historyEvents.add(mergedHistoryEvent);

    when(entity.getInternalId()).thenReturn(internalId);
    when(documentHistory.getHistory(internalId)).thenReturn(historyEvents);

    historyConverter = new HistoryConverter(entity, fields, documentHistory, monitor);

    Map<String, Object> historyMap = historyConverter.convert();
    historyList = (LinkedList<Object>) historyMap.get(HISTORY);
  }

  @Test
  public void testHistoryEventIsConvertedToMap() {

    Map<String, Object> nonMergedHistoryMap = (Map<String, Object>) historyList.get(0);
    Map<String, String> nonMergedHistoryParameters =
        (Map<String, String>) nonMergedHistoryMap.get(fields.getHistoryParameters());

    assertEquals(
        "Linked history map should have an action",
        ACTION,
        nonMergedHistoryMap.get(fields.getHistoryAction()));
    assertEquals(
        "Linked history map should have an event type",
        EVENT_TYPE,
        nonMergedHistoryMap.get(fields.getHistoryType()));
    assertEquals(
        "History parameters should have kwy1-value1",
        "value1",
        nonMergedHistoryParameters.get("key1"));
    assertEquals(
        "Linked history map should have referrer",
        REFERRER,
        nonMergedHistoryMap.get(fields.getHistoryReferrer()));
    assertEquals(
        "Linked history map should have timestamp",
        currentTimeMillis,
        nonMergedHistoryMap.get(fields.getHistoryTimestamp()));
  }

  @Test
  public void testMergedHistoryEventIsConverted() {
    Map<String, Object> mergedHistoryMap = (Map<String, Object>) historyList.get(1);

    assertEquals(
        "Merged history event should have a type of " + HistoryEvents.MERGED_TYPE,
        HistoryEvents.MERGED_TYPE,
        mergedHistoryMap.get(fields.getHistoryType()));
  }
}
