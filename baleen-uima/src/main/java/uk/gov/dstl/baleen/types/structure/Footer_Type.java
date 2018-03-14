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
 * A Footer of a Document. Updated by JCasGen Thu Dec 22 22:42:17 CET 2016
 *
 * @generated
 */
public class Footer_Type extends Structure_Type {
  /** @generated */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = Footer.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public static final boolean featOkTst =
      JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.structure.Footer");

  /** @generated */
  final Feature casFeat_page;
  /** @generated */
  final int casFeatCode_page;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getPage(int addr) {
    if (featOkTst && casFeat_page == null)
      jcas.throwFeatMissing("page", "uk.gov.dstl.baleen.types.structure.Footer");
    return ll_cas.ll_getRefValue(addr, casFeatCode_page);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setPage(int addr, int v) {
    if (featOkTst && casFeat_page == null)
      jcas.throwFeatMissing("page", "uk.gov.dstl.baleen.types.structure.Footer");
    ll_cas.ll_setRefValue(addr, casFeatCode_page, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @generated
   * @param jcas JCas
   * @param casType Type
   */
  public Footer_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_page =
        jcas.getRequiredFeatureDE(
            casType, "page", "uk.gov.dstl.baleen.types.structure.Page", featOkTst);
    casFeatCode_page =
        (null == casFeat_page) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl) casFeat_page).getCode();
  }
}
