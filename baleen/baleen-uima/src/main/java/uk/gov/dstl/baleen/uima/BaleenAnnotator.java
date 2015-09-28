//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.uima;

import java.util.Collection;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.core.history.BaleenHistory;
import uk.gov.dstl.baleen.cpe.CpeBuilder;
import uk.gov.dstl.baleen.uima.utils.UimaUtils;

/**
 * This class provides basic functionality for an annotator, such as metrics and
 * logging, so that we don't need to put it into every annotator manually. All
 * annotators in Baleen should inherit from this class and use any utility
 * methods it provides as required to ensure we standardise logging and metrics
 * as much as possible.
 *
 * 
 */
public abstract class BaleenAnnotator extends JCasAnnotator_ImplBase {
	private UimaMonitor monitor;
	private UimaSupport support;

	/**
	 * Baleen History resource
	 * 
	 * @baleen.resource uk.gov.dstl.baleen.core.history.BaleenHistory
	 */
	public static final String KEY_HISTORY = CpeBuilder.BALEEN_HISTORY;
	@ExternalResource(key = KEY_HISTORY, mandatory = false)
	private BaleenHistory history;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		// This will do initialization of resources,
		// but won't be included in the metrics
		super.initialize(context);

		String pipelineName = UimaUtils.getPipelineName(context);
		monitor = createMonitor(pipelineName);
		support = createSupport(pipelineName, context);

		monitor.startFunction("initialize");

		doInitialize(context);

		monitor.finishFunction("initialize");
	}

	protected UimaSupport createSupport(String pipelineName, UimaContext context) {
		return new UimaSupport(pipelineName, this.getClass(), history, monitor, UimaUtils.isMergeDistinctEntities(context));
	}

	protected UimaMonitor createMonitor(String pipelineName) {
		return new UimaMonitor(pipelineName, this.getClass());
	}

	/**
	 * Called when the analysis engine is being initialized. Any required
	 * resources, for example, should be opened at this point.
	 *
	 * @param aContext
	 *            UimaContext object passed by the Collection Processing Engine
	 */
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException{
		// Do nothing - this should be overridden in most cases
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		monitor.startFunction("process");

		doProcess(aJCas);

		monitor.finishFunction("process");
		monitor.persistCounts();
	}

	/**
	 * Called when UIMA wants the annotator to process a document. The passed
	 * JCas object contains information about the document and any existing
	 * annotations.
	 *
	 * @param jCas
	 *            JCas object to process
	 * @throws AnalysisEngineProcessException
	 */
	protected abstract void doProcess(JCas jCas) throws AnalysisEngineProcessException;

	@Override
	public void destroy() {
		monitor.startFunction("destroy");

		doDestroy();

		monitor.finishFunction("destroy");
	}

	/**
	 * Called when the analysis engine has finished and is closing down. Any
	 * open resources, for example, should be closed at this point.
	 */
	protected void doDestroy(){
		//Do nothing - this should be overridden in most cases
	}

	/** Get the UIMA monitor for this annotator.
	 * @return the uima monitor
	 */
	protected UimaMonitor getMonitor() {
		return monitor;
	}


	/** Get the UIMA support for this annotator.
	 * @return the uima support
	 */
	protected UimaSupport getSupport() {
		return support;
	}

	// Direct access to common support functions

	/**
	 * Add an annotation to the JCas index, notifying UimaMonitor of the fact we
	 * have done so
	 *
	 * @param annot
	 *            Annotation(s) to add
	 */
	protected void addToJCasIndex(Annotation... annotations) {
		getSupport().add(annotations);
	}

	/**
	 * Add an annotation to the JCas index, notifying UimaMonitor of the fact we
	 * have done so
	 *
	 * @param annot
	 *            Annotation(s) to add
	 */
	protected void addToJCasIndex(Collection<? extends Annotation> annotations) {
		getSupport().add(annotations);
	}

	/**
	 * Remove an annotation to the JCas index, notifying UimaMonitor of the fact
	 * we have done so
	 *
	 * @param annot
	 *            Annotation(s) to remove
	 */
	protected void removeFromJCasIndex(Collection<? extends Annotation> annotations) {
		getSupport().remove(annotations);
	}

	/**
	 * Remove an annotation to the JCas index, notifying UimaMonitor of the fact
	 * we have done so
	 *
	 * @param annot
	 *            Annotation(s) to remove
	 */
	protected void removeFromJCasIndex(Annotation... annotations) {
		getSupport().remove(annotations);
	}

	/**
	 * Add a new annotation, which is merged from the old annotations, removing the old annotations.
	 *
	 * @param newAnnotation
	 *            The annotation which is to be added to the document as the merged result of the old annotations
	 * @param annotations
	 *            Annotation(s) which have been merged and should be removed
	 */
	protected void mergeWithNew(Annotation newAnnotation, Annotation... annotations) {
		getSupport().mergeWithNew(newAnnotation, annotations);
	}

	/**
	 * Add a new annotation, which is merged from the old annotations, removing the old annotations.
	 *
	 * @param newAnnotation
	 *            The annotation which is to be added to the document as the merged result of the old annotations
	 * @param annotations
	 *            Annotation(s) which have been merged and should be removed
	 */
	protected void mergeWithNew(Annotation newAnnotation, Collection<? extends Annotation> annotations) {
		getSupport().mergeWithNew(newAnnotation, annotations);
	}

	/**
	 * Merge an existing annotation with old annotations, removing the old annotations.
	 *
	 * @param existingAnnotation
	 *            The annotation which exists and is to be left in the document (merged)
	 * @param annotations
	 *            Annotation(s) which have been merged wiht existingAnnotation and then removed
	 */
	protected void mergeWithExisting(Annotation existingAnnotation, Annotation... annotations) {
		getSupport().mergeWithExisting(existingAnnotation, annotations);
	}

	/**
	 * Add a new annotation, which is merged from the old annotations, removing the old annotations.
	 *
	 * @param existingAnnotation
	 *            The annotation which exists and is to be left in the document (merged)
	 * @param annotations
	 *            Annotation(s) which have been merged wiht existingAnnotation and then removed
	 */
	protected void mergeWithExisting(Annotation existingAnnotation, Collection<? extends Annotation> annotations) {
		getSupport().mergeWithExisting(existingAnnotation, annotations);
	}

	/**
	 * Return the document annotation.
	 *
	 * @param jCas
	 * @return the document annotation
	 */
	protected DocumentAnnotation getDocumentAnnotation(JCas jCas){
		return getSupport().getDocumentAnnotation(jCas);
	}
}
