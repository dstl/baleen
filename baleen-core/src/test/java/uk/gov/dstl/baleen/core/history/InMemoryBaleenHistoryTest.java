//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.history;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Maps;

import uk.gov.dstl.baleen.core.history.memory.InMemoryBaleenHistory;

@RunWith(MockitoJUnitRunner.Silent.class)
public class InMemoryBaleenHistoryTest {

	@Mock
	Recordable recordable1;

	@Mock
	Recordable recordable2;

	@Before
	public void setUp() {
		doReturn(1L).when(recordable1).getInternalId();
		doReturn(2L).when(recordable2).getInternalId();
	}

	@Test
	public void testKeySameAndDifferent() throws ResourceInitializationException {
		InMemoryBaleenHistory bh = new InMemoryBaleenHistory();

		bh.initialize(new CustomResourceSpecifier_impl(), Maps.newHashMap());

		DocumentHistory dh1a = bh.getHistory("doc1");

		DocumentHistory dh1b = bh.getHistory("doc1");

		assertSame(dh1a, dh1b);

		DocumentHistory dh2 = bh.getHistory("doc2");

		assertNotSame(dh1a, dh2);
	}

	@Test
	public void testClearOnClose() throws ResourceInitializationException {
		InMemoryBaleenHistory bh = new InMemoryBaleenHistory();

		bh.initialize(new CustomResourceSpecifier_impl(), Maps.newHashMap());

		DocumentHistory dh1 = bh.getHistory("doc1");

		DocumentHistory dh2 = bh.getHistory("doc2");

		dh1.add(HistoryEvents.createAdded(recordable1, null));
		dh1.add(HistoryEvents.createAdded(recordable2, null));

		dh2.add(HistoryEvents.createAdded(recordable1, null));
		dh2.add(HistoryEvents.createAdded(recordable2, null));

		assertEquals(2, dh2.getAllHistory().size());
		assertEquals(1, dh2.getHistory(1).size());

		bh.closeHistory("doc2");

		// If reopens should be new
		DocumentHistory dh2reopen = bh.getHistory("doc2");
		assertNotSame(dh2reopen, dh2);
		assertTrue(dh2reopen.getAllHistory().isEmpty());

		dh1.close();
		assertTrue(dh1.getAllHistory().isEmpty());
		dh1.add( HistoryEvents.createAdded(recordable1, null));
		assertTrue(dh1.getAllHistory().isEmpty());


		DocumentHistory dh1reopen = bh.getHistory("doc1");
		assertTrue(dh1reopen.getAllHistory().isEmpty());


		bh.destroy();
	}

}
