//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.testing;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;


public class JCasSingleton {
	private static JCas jCas = null;
	
	private JCasSingleton(){}
	
	public static JCas getJCasInstance() throws UIMAException{
		if(jCas == null){
			jCas = JCasFactory.createJCas(TypeSystemSingleton.getTypeSystemDescriptionInstance());
		}else{
			jCas.reset();
		}
			
		return jCas;
	}
}