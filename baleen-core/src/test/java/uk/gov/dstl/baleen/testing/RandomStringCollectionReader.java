//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.testing;

import java.io.IOException;
import java.util.Random;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;

/**
 * Dummy collection reader that produces produces endless number of documents, with 500ms delay between.
 * 
 * The documents are have an integer number within them.
 * 
 * The purpose of this is to mimick the 'endless' collection readers services in baleen, rather
 * than the finite process N documents in most UIMA implementations. 
 * 
 * 
 */
public class RandomStringCollectionReader extends JCasCollectionReader_ImplBase {
	
	
	private static final int DELAY = 500;
	private Random random;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		random = new Random();
	}

	@Override
	public void getNext(JCas jCas) throws IOException, CollectionException {
		int i = random.nextInt();
		jCas.setDocumentText("The next value is "+i+".");
	}


	@Override
	public Progress[] getProgress() {
		return new Progress[] {  };
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		try {
			Thread.sleep(DELAY);
		} catch(InterruptedException e) {
			// Do nothing
		}
		
		return true;
	}

}
