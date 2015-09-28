//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.temporal.DateTime;
import uk.gov.dstl.baleen.types.temporal.DateType;
import uk.gov.dstl.baleen.types.temporal.Time;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Remove DateType and Time entities that are nested within a DateTime entity
 * 
 * <p>All DateTime entities are looped through, and should any dates or times that are entirely within the DateTime entity be found, they are removed.
 * The comparison is done purely on start and end positions, and ignores other information within the entity.</p>
 * 
 * 
 */
public class RemoveNestedDateTimes extends BaleenAnnotator {
	
	@Override
	public void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		
		int removed = 0;
		
		Map<DateTime, Collection<Time>> timeCovered = JCasUtil.indexCovered(jCas, DateTime.class, Time.class);
		for(Entry<DateTime,Collection<Time>> e : timeCovered.entrySet()) {
			mergeWithExisting(e.getKey(), e.getValue());
			removed += e.getValue().size();
		}
		
		
		Map<DateTime, Collection<DateType>> dateCovered = JCasUtil.indexCovered(jCas, DateTime.class, DateType.class);
		
		for(Entry<DateTime,Collection<DateType>> e : dateCovered.entrySet()) {
			mergeWithExisting(e.getKey(), e.getValue());
			removed += e.getValue().size();
		}
		
		getMonitor().debug("Removed {} entities",removed);

	}
}
