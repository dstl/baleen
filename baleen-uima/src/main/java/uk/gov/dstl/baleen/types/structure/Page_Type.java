// Dstl (c) Crown Copyright 2017

/* First created by JCasGen Thu Oct 13 13:31:25 BST 2016 */
package uk.gov.dstl.baleen.types.structure;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

/**
 * A page of a Document. Updated by JCasGen Thu Dec 22 22:42:17 CET 2016
 *
 * @generated
 */
public class Page_Type extends Structure_Type {
  /** @generated */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = Page.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public static final boolean featOkTst =
      JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.structure.Page");

  /** @generated */
  final Feature casFeat_PageNumber;
  /** @generated */
  final int casFeatCode_PageNumber;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getPageNumber(int addr) {
    if (featOkTst && casFeat_PageNumber == null)
      jcas.throwFeatMissing("PageNumber", "uk.gov.dstl.baleen.types.structure.Page");
    return ll_cas.ll_getIntValue(addr, casFeatCode_PageNumber);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setPageNumber(int addr, int v) {
    if (featOkTst && casFeat_PageNumber == null)
      jcas.throwFeatMissing("PageNumber", "uk.gov.dstl.baleen.types.structure.Page");
    ll_cas.ll_setIntValue(addr, casFeatCode_PageNumber, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @generated
   * @param jcas JCas
   * @param casType Type
   */
  public Page_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_PageNumber =
        jcas.getRequiredFeatureDE(casType, "PageNumber", "uima.cas.Integer", featOkTst);
    casFeatCode_PageNumber =
        (null == casFeat_PageNumber)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_PageNumber).getCode();
  }
}
