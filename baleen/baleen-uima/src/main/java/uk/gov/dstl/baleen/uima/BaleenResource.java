//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.uima;

import java.util.Map;

import org.apache.uima.fit.component.Resource_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import uk.gov.dstl.baleen.uima.utils.UimaUtils;

/** A base class for implementation of shared resources.
 *
 * This provides thin layer over Resource_ImplBase to supplement with Baleen monitoring and support functions.
 *
 * Implementators should override doInitialize and then add any specific functions that their shared object will offer.
 *
 * 
 *
 */
public abstract class BaleenResource extends Resource_ImplBase {
	private UimaMonitor monitor;

	@Override
	public final boolean initialize(ResourceSpecifier specifier, Map<String, Object> additionalParams) throws ResourceInitializationException {
		// This will do initialization of resources,but won't be included in the metrics
		super.initialize(specifier, additionalParams);

		monitor = createMonitor(UimaUtils.getPipelineName(getUimaContext()));
		monitor.startFunction("initialize");

		boolean result = doInitialize(specifier, additionalParams);

		if(!result) {
			monitor.warn("Failed to initialize");
		}

		monitor.finishFunction("initialize");

		return result;
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
	protected boolean doInitialize(ResourceSpecifier specifier, Map<String, Object> additionalParams) throws ResourceInitializationException{
		// Do nothing - this should be overridden in most cases
		return true;
	}



	@Override
	public final void destroy() {
		monitor.startFunction("destroy");
		super.destroy();

		doDestroy();

		monitor.finishFunction("destroy");
	}

	protected void doDestroy() {
		// Do nothing  - override if necessary
	}

	protected UimaMonitor getMonitor() {
		return monitor;
	}
}
