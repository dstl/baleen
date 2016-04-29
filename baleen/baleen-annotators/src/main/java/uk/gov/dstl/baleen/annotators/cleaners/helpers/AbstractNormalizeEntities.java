package uk.gov.dstl.baleen.annotators.cleaners.helpers;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

import com.google.common.base.Strings;

/**
 * A class for containing the generic functionality shared by all normalizing
 * cleaners. Both methods are intended to be overridden with operations specific
 * to the entities handled by a particular child cleaner.
 * 
 * @baleen.javadoc
 */
public abstract class AbstractNormalizeEntities extends BaleenAnnotator {

	public void doProcess(JCas jCas) throws AnalysisEngineProcessException {

		FSIterator<Annotation> iter = jCas.getAnnotationIndex(Entity.type).iterator();

		while (iter.hasNext()) {
			Entity e = (Entity) iter.next();

			if (Strings.isNullOrEmpty(e.getValue())) {
				getMonitor().debug("No value set for entity '{}' - skipping", e.getCoveredText());
				continue;
			}

			if (this.shouldNormalize(e)) {
				String normalized = this.normalize(e);
				if (!normalized.equals(e.getValue())) {
					e.setValue(normalized);
					e.setIsNormalised(true);
				}
			}

		}
	}
	
	/**
	 * The shouldNormalize method is used first to identify entities of the type
	 * the cleaner is supposed to operate on.
	 */
	protected abstract boolean shouldNormalize(Entity e);

	/**
	 * Overridden with the specific operations required to calculate the normalized
	 * value of the entity. If it is not possible to normalize this method should return
	 * the original value of the entity.
	 */
	protected abstract String normalize(Entity e);
}
