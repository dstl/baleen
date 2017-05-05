//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.testing;

import java.util.Optional;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;

import uk.gov.dstl.baleen.uima.BaleenTask;
import uk.gov.dstl.baleen.uima.JobSettings;

public class CounterTask extends BaleenTask {

	private static int executedCount = 0;
	private static Optional<String> lastSettings;

	@Override
	protected void execute(JobSettings settings) throws AnalysisEngineProcessException {
		executedCount++;

		// NOTE: You should never hold onto this , not in a static or anything like that but its
		// useful for our test.
		lastSettings = settings.get("key");
	}

	public static int getExecutedCount() {
		return executedCount;
	}

	public static void reset() {
		lastSettings = null;
		executedCount = 0;
	}

	public static Optional<String> getLastSettingsForKey() {
		return lastSettings;
	}

}