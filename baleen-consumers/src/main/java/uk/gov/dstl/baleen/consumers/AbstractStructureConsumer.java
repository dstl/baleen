//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;

import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.uima.BaleenConsumer;
import uk.gov.dstl.baleen.uima.utils.StructureHierarchy;
import uk.gov.dstl.baleen.uima.utils.StructureUtil;
import uk.gov.dstl.baleen.uima.utils.select.ItemHierarchy;

/**
 * Abstract class for common code dealing with consuming Structure.
 */
public abstract class AbstractStructureConsumer extends BaleenConsumer {

	/**
	 * A list of structural types which will be considered during record path
	 * analysis.
	 *
	 * @baleen.config Paragraph,TableCell,ListItem,Aside, ...
	 */
	public static final String PARAM_TYPE_NAMES = "types";

	/** The type names. */
	@ConfigurationParameter(name = PARAM_TYPE_NAMES, mandatory = false)
	private String[] typeNames;

	/** The structural classes. */
	protected Set<Class<? extends Structure>> structuralClasses;

	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		super.doInitialize(aContext);
		structuralClasses = StructureUtil.getStructureClasses(typeNames);
	}

	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		doProcess(StructureHierarchy.build(jCas, structuralClasses));
	}

	/**
	 * Called when consuming the document with the {@link ItemHierarchy} for the
	 * document.
	 * <p>
	 * Implementations should override this method.
	 *
	 * @param structureHierarchy
	 *            the structure hierarchy
	 * @throws AnalysisEngineProcessException
	 */
	protected abstract void doProcess(ItemHierarchy<Structure> structureHierarchy)
			throws AnalysisEngineProcessException;
}
