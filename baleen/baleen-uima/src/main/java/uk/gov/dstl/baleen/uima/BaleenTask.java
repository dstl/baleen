package uk.gov.dstl.baleen.uima;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.uima.utils.UimaUtils;

/**
 * A discrete task within Baleen which forms part of a job.
 *
 * Tasks are effectively annotators, and as such can have configuration paramters and access shared
 * resources.Tasks can override the usual doInitialise and doDestroy functions can have access to a
 * Uima monitor for logging. They do not have access to history (as they function outside of
 * document processing).
 *
 * Task implementations should override execute and perform their logic. They may access / change
 * the settings available in the JobSettings they are provided.
 *
 * @baleen.javadoc
 *
 */
public abstract class BaleenTask extends JCasAnnotator_ImplBase {

	/** The monitor. */
	private UimaMonitor monitor;

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		// This will do initialization of resources,
		// but won't be included in the metrics
		super.initialize(context);

		final String pipelineName = UimaUtils.getPipelineName(context);
		monitor = createMonitor(pipelineName);

		monitor.startFunction("initialize");

		doInitialize(context);

		monitor.finishFunction("initialize");
	}

	/**
	 * Creates the monitor.
	 *
	 * @param pipelineName
	 *            the pipeline name
	 * @return the uima monitor
	 */
	protected UimaMonitor createMonitor(final String pipelineName) {
		return new UimaMonitor(pipelineName, this.getClass());
	}

	/**
	 * Called when the analysis engine is being initialized. Any required resources, for example,
	 * should be opened at this point.
	 *
	 * @param aContext
	 *            UimaContext object passed by the Collection Processing Engine
	 * @throws ResourceInitializationException
	 *             the resource initialization exception
	 */
	public void doInitialize(final UimaContext aContext) throws ResourceInitializationException {
		// Do nothing - this should be overridden in most cases
	}

	@Override
	public final void process(final JCas aJCas) throws AnalysisEngineProcessException {
		monitor.startFunction("execute");

		execute(new JobSettings(aJCas));

		monitor.finishFunction("execute");
		monitor.persistCounts();
	}

	/**
	 * Called when the task should run.
	 *
	 * @param settings
	 *            the job settings to run with
	 * @throws AnalysisEngineProcessException
	 *             the analysis engine process exception
	 */
	protected abstract void execute(JobSettings settings) throws AnalysisEngineProcessException;

	@Override
	public void destroy() {
		monitor.startFunction("destroy");

		doDestroy();

		monitor.finishFunction("destroy");
	}

	/**
	 * Called when the analysis engine has finished and is closing down. Any open resources, for
	 * example, should be closed at this point.
	 */
	protected void doDestroy() {
		// Do nothing - this should be overridden in most cases
	}

	/**
	 * Get the UIMA monitor for this annotator.
	 *
	 * @return the uima monitor
	 */
	protected UimaMonitor getMonitor() {
		return monitor;
	}

}
