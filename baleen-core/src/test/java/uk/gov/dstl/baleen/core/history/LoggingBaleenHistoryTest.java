// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.history;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Maps;

import uk.gov.dstl.baleen.core.history.logging.LoggingBaleenHistory;

@RunWith(MockitoJUnitRunner.Silent.class)
public class LoggingBaleenHistoryTest {

  @Mock private Recordable recordable;

  private HistoryEvent event;

  private String documentId = "fakeId";

  @Before
  public void setUp() {
    doReturn(1L).when(recordable).getInternalId();
    event = HistoryEvents.createAdded(recordable, "referrer");
  }

  @Test
  public void testNoExceptionInNormalUse() throws ResourceInitializationException {
    LoggingBaleenHistory bh = new LoggingBaleenHistory();

    bh.initialize(new CustomResourceSpecifier_impl(), Maps.newHashMap());

    addAtLevel(bh, "info", documentId, event);
    addAtLevel(bh, "errpr", documentId, event);
    addAtLevel(bh, "warn", documentId, event);
    addAtLevel(bh, "trace", documentId, event);
    addAtLevel(bh, "debug", documentId, event);
    addAtLevel(bh, "not valid", documentId, event);
    addAtLevel(bh, null, documentId, event);

    assertEquals("info", bh.getLevel());

    DocumentHistory history = bh.getHistory(documentId);
    assertNotNull(history);

    bh.closeHistory(documentId);

    bh.destroy();

    bh.add(documentId, event);
  }

  private void addAtLevel(
      LoggingBaleenHistory bh, String level, String documentId, HistoryEvent event) {
    bh.setLevel(level);
    bh.add(documentId, event);
  }

  // TODO: Test naming

}
