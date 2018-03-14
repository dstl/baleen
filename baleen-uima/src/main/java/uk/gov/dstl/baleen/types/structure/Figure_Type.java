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
 * A figure (or embedded media). Updated by JCasGen Thu Apr 20 16:06:08 BST 2017
 *
 * @generated
 */
public class Figure_Type extends Structure_Type {
  /** @generated */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = Figure.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public static final boolean featOkTst =
      JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.structure.Figure");

  /** @generated */
  final Feature casFeat_target;
  /** @generated */
  final int casFeatCode_target;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public String getTarget(int addr) {
    if (featOkTst && casFeat_target == null)
      jcas.throwFeatMissing("target", "uk.gov.dstl.baleen.types.structure.Figure");
    return ll_cas.ll_getStringValue(addr, casFeatCode_target);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setTarget(int addr, String v) {
    if (featOkTst && casFeat_target == null)
      jcas.throwFeatMissing("target", "uk.gov.dstl.baleen.types.structure.Figure");
    ll_cas.ll_setStringValue(addr, casFeatCode_target, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @generated
   * @param jcas JCas
   * @param casType Type
   */
  public Figure_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_target = jcas.getRequiredFeatureDE(casType, "target", "uima.cas.String", featOkTst);
    casFeatCode_target =
        (null == casFeat_target)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_target).getCode();
  }
}
