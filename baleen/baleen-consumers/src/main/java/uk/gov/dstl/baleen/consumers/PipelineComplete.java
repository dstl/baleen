package uk.gov.dstl.baleen.consumers;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import uk.gov.dstl.baleen.resources.SharedDocumentCheckerResource;
import uk.gov.dstl.baleen.resources.SharedDocumentStatusResource;
import uk.gov.dstl.baleen.uima.BaleenConsumer;

/**
 * This should be the *last* consumer in the pipeline, and is used to indicate when
 * a document has traversed the complete pipeline.
 * @author cd1
 *
 */
public class PipelineComplete extends BaleenConsumer {

	/**
	 * Document status resource
	 * 
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedDocumentStatusResource
	 */
	public static final String KEY_DOC_STATUS = "documentstatus";
	@ExternalResource(key = KEY_DOC_STATUS)
	private SharedDocumentStatusResource docStatus;

	/**
	 * Document checker resource
	 * 
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedDocumentStatusResource
	 */
	public static final String KEY_DOC_CHECKER = "documentchecker";
	@ExternalResource(key = KEY_DOC_CHECKER)
	private SharedDocumentCheckerResource docChecker;

	/**
	 * Should a hash of the content be used to generate the ID?
	 * If false, then a hash of the Source URI is used instead.
	 *
	 * @baleen.config true
	 */
	public static final String PARAM_STORE_DOC_DETAILS = "storeDocDetails";
	@ConfigurationParameter(name = PARAM_STORE_DOC_DETAILS, defaultValue = "true")
	boolean storeDocDetails = true;
	

	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		DocumentAnnotation da = getSupport().getDocumentAnnotation(jCas);
		String uri=da.getSourceUri();
		if (storeDocDetails) {
			docStatus.persistDocumentDetails(uri);
		}
	}
}