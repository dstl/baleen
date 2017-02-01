package uk.gov.dstl.baleen.uima.utils;

import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;

/**
 * Class to hold a singleton instance of the TypeSystem, to save having to generate it repeatedly.
 * 
 * As this is a singleton, it may be shared by different pipelines.
 * However, it is assumed that all pipelines within an instance of Baleen share the same type system,
 * as this is derived from the classpath and not pipeline properties.
 */
public class TypeSystemSingleton {
	private static TypeSystemDescription tsd = null;
	
	private TypeSystemSingleton(){}
	
	/**
	 * Return the singleton TypeSystemDescription instance
	 */
	public static TypeSystemDescription getTypeSystemDescriptionInstance() throws ResourceInitializationException{
		if(tsd == null){
			tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();
		}
		
		return tsd;
	}
}
