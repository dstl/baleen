package uk.gov.dstl.baleen.uima.testing;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import uk.gov.dstl.baleen.uima.BaleenTask;
import uk.gov.dstl.baleen.uima.JobSettings;

public class DummyTaskWithParams extends BaleenTask {

	public static final String PARAM_KEY = "key";
	@ConfigurationParameter(name = PARAM_KEY, defaultValue = "key")
	private String key;

	public static final String PARAM_VALUE = "value";
	@ConfigurationParameter(name = PARAM_VALUE, defaultValue = "value")
	private String value;

	@Override
	protected void execute(JobSettings settings) throws AnalysisEngineProcessException {
		settings.set(key, value);
	}

}
