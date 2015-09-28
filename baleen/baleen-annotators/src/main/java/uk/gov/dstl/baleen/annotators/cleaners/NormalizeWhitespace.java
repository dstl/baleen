//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Replace blocks of whitespace with a single space (this includes new lines) in the value
 * 
 * 
 */
public class NormalizeWhitespace extends BaleenAnnotator {
	@Override
	public void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		
		FSIterator<Annotation> iter = jCas.getAnnotationIndex(Entity.type).iterator();
		while(iter.hasNext()){
			Entity e = (Entity) iter.next();
			
			if(Strings.isNullOrEmpty(e.getValue())){
				getMonitor().debug("No value set for entity '{}' - skipping", e.getCoveredText());
				continue;
			}
			
			e.setValue(e.getValue().replaceAll("[\n\\h]+", " "));
		}
	}
}
