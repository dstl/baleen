//Dstl (c) Crown Copyright 2017

/* First created by JCasGen Thu Dec 22 22:42:17 CET 2016 */
package uk.gov.dstl.baleen.types.structure;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

/** A section which contains additional detail.
 * Updated by JCasGen Thu Dec 22 22:42:17 CET 2016
 * @generated */
public class Details_Type extends Structure_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Details.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.structure.Details");



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Details_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    