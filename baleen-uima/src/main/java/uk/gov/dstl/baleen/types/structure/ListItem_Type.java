//Dstl (c) Crown Copyright 2017

/* First created by JCasGen Thu Dec 01 15:08:29 GMT 2016 */
package uk.gov.dstl.baleen.types.structure;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

/** An item of an ordered or unordered list.
 * Updated by JCasGen Thu Dec 22 22:42:17 CET 2016
 * @generated */
public class ListItem_Type extends Structure_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ListItem.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.structure.ListItem");



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public ListItem_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    