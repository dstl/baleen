package uk.gov.dstl.baleen.schedules;

import java.io.IOException;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Progress;

public class Other extends JCasCollectionReader_ImplBase {

	public static final String PARAM1 = "key";
	@ConfigurationParameter(name = PARAM1, defaultValue = "default")
	private String value;

	public String getValue() {
		return value;
	}

	private boolean run = false;

	@Override
	public Progress[] getProgress() {
		return new Progress[0];
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		if (run) {
			return false;
		} else {
			run = true;
			return true;
		}
	}

	@Override
	public void getNext(JCas jCas) throws IOException, CollectionException {

	}

}
