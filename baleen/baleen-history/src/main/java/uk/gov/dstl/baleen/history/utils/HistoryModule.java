//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.history.utils;

import uk.gov.dstl.baleen.core.history.HistoryEvent;
import uk.gov.dstl.baleen.core.history.Recordable;
import uk.gov.dstl.baleen.core.history.impl.HistoryEventImpl;
import uk.gov.dstl.baleen.core.history.impl.RecordableImpl;

import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * A Jackson module which map History interfaces to implementations.
 *
 * 
 *
 */
public class HistoryModule extends SimpleModule {
	private static final long serialVersionUID = 1L;

	/**
	 * New instance.
	 *
	 */
	public HistoryModule() {
		addAbstractTypeMapping(Recordable.class, RecordableImpl.class);
		addAbstractTypeMapping(HistoryEvent.class, HistoryEventImpl.class);
	}

}