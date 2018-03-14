// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.history.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.gov.dstl.baleen.core.history.BaleenHistory;
import uk.gov.dstl.baleen.core.history.DocumentHistory;
import uk.gov.dstl.baleen.core.history.HistoryEvent;
import uk.gov.dstl.baleen.core.history.HistoryEvents;
import uk.gov.dstl.baleen.core.history.Recordable;
import uk.gov.dstl.baleen.core.history.RecordableHistoryEvent;
import uk.gov.dstl.baleen.core.history.impl.RecordableImpl;

public class AbstractHistoryTest {

  private static final String TEST_EVENT_TYPE = "testing";
  private Recordable rA = new RecordableImpl(1, "a", 0, 1, "trA");
  private Recordable rB = new RecordableImpl(2, "b", 0, 1, "trB");
  private String ref1 = "ref1";
  private String ref2 = "ref2";

  public AbstractHistoryTest() {
    super();
  }

  public void testGenericHistory(BaleenHistory history) {
    DocumentHistory dh1 = history.getHistory("doc1");
    dh1.add(HistoryEvents.createAdded(rA, ref1));
    dh1.add(HistoryEvents.createAdded(rB, ref1));
    dh1.add(new RecordableHistoryEvent(TEST_EVENT_TYPE, rA, ref1, TEST_EVENT_TYPE));
    dh1.add(HistoryEvents.createMerged(rA, ref2, 3));
    dh1.add(HistoryEvents.createMerged(rB, ref2, rA.getInternalId()));
    dh1.add(HistoryEvents.createRemoved(rA, ref2));

    DocumentHistory dh2 = history.getHistory("doc2");
    dh1.add(HistoryEvents.createAdded(rB, ref1));
    dh2.close();

    Collection<HistoryEvent> allHistory = dh1.getAllHistory();
    Collection<HistoryEvent> aHistory = dh1.getHistory(rA.getInternalId());
    Collection<HistoryEvent> bHistory = dh1.getHistory(rB.getInternalId());

    assertFalse(allHistory.isEmpty());
    assertEquals(allHistory.size(), aHistory.size() + bHistory.size());
    List<HistoryEvent> ahe = new ArrayList<>(aHistory);
    assertEquals(4, aHistory.size());
    assertEquals(HistoryEvents.ADDED_TYPE, ahe.get(0).getEventType());
    assertEquals(TEST_EVENT_TYPE, ahe.get(1).getEventType());
    assertEquals(HistoryEvents.MERGED_TYPE, ahe.get(2).getEventType());
    assertEquals(HistoryEvents.REMOVED_TYPE, ahe.get(3).getEventType());

    dh1.close();

    // Should still be able to get the history after DocumentHistory is closed
    DocumentHistory reopened = history.getHistory("doc1");
    Collection<HistoryEvent> reopenedAHistory = reopened.getAllHistory();

    // We could test that the are the same here (give or take ordering)
    assertEquals(allHistory.size(), reopenedAHistory.size());

    // Close a nonexistent document
    history.closeHistory("missing doc");
  }
}
