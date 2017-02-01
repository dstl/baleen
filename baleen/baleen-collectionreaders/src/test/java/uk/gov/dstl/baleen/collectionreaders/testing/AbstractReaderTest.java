package uk.gov.dstl.baleen.collectionreaders.testing;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;

import uk.gov.dstl.baleen.uima.BaleenCollectionReader;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;

/**
 * Abstract class for testing collection readers
 */
public class AbstractReaderTest {
	protected JCas jCas;
	private Class<? extends BaleenCollectionReader> readerClass; 

	public AbstractReaderTest(Class<? extends BaleenCollectionReader> readerClass){
		this.readerClass = readerClass;
	}
	
	@Before
	public void beforeTest() throws UIMAException {
		jCas = JCasSingleton.getJCasInstance();
	}
	
	protected BaleenCollectionReader getCollectionReader() throws ResourceInitializationException{
		return (BaleenCollectionReader) CollectionReaderFactory.createReader(readerClass, TypeSystemSingleton.getTypeSystemDescriptionInstance());
	}
	
	protected BaleenCollectionReader getCollectionReader(Object... args) throws ResourceInitializationException{
		return (BaleenCollectionReader) CollectionReaderFactory.createReader(readerClass, TypeSystemSingleton.getTypeSystemDescriptionInstance(), args);
	}
}
