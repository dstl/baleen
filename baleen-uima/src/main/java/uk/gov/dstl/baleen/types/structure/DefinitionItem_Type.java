// Dstl (c) Crown Copyright 2017

/* First created by JCasGen Thu Dec 22 22:42:17 CET 2016 */
package uk.gov.dstl.baleen.types.structure;

import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

/**
 * An item of an definition list which holds the term (eg an abbreviation). Updated by JCasGen Thu
 * Dec 22 22:42:17 CET 2016
 *
 * @generated
 */
public class DefinitionItem_Type extends Structure_Type {
  /** @generated */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = DefinitionItem.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public static final boolean featOkTst =
      JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.structure.DefinitionItem");

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @generated
   * @param jcas JCas
   * @param casType Type
   */
  public DefinitionItem_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());
  }
}
