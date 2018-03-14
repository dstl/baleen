// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.history.elasticsearch;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import uk.gov.dstl.baleen.core.history.HistoryEvent;

public class ESHistoryTest {

  @Test
  public void test() {

    List<HistoryEvent> e = Lists.newLinkedList();

    ESHistory esHistory = new ESHistory();
    assertNotNull(esHistory.getEvents());

    esHistory.setEvents(e);
    assertSame(e, esHistory.getEvents());

    assertNull(esHistory.getDocumentId());

    esHistory.setDocumentId("2");
    assertEquals("2", esHistory.getDocumentId());

    ESHistory c = new ESHistory("1", e);
    assertEquals("1", c.getDocumentId());
    assertSame(e, c.getEvents());
  }
}
