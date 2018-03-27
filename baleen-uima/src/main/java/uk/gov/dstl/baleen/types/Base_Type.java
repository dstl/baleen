/* First created by JCasGen Wed Jan 14 12:58:12 GMT 2015 */
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.types;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

/**
 * Base annotation with confidence and annotator properties. Updated by JCasGen Wed Apr 13 13:23:15
 * BST 2016
 *
 * @generated
 */
public class Base_Type extends BaleenAnnotation_Type {
  /** @generated */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = Base.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public static final boolean featOkTst =
      JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.Base");

  /** @generated */
  final Feature casFeat_confidence;
  /** @generated */
  final int casFeatCode_confidence;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public double getConfidence(int addr) {
    if (featOkTst && casFeat_confidence == null)
      jcas.throwFeatMissing("confidence", "uk.gov.dstl.baleen.types.Base");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_confidence);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setConfidence(int addr, double v) {
    if (featOkTst && casFeat_confidence == null)
      jcas.throwFeatMissing("confidence", "uk.gov.dstl.baleen.types.Base");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_confidence, v);
  }

  /** @generated */
  final Feature casFeat_referent;
  /** @generated */
  final int casFeatCode_referent;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getReferent(int addr) {
    if (featOkTst && casFeat_referent == null)
      jcas.throwFeatMissing("referent", "uk.gov.dstl.baleen.types.Base");
    return ll_cas.ll_getRefValue(addr, casFeatCode_referent);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setReferent(int addr, int v) {
    if (featOkTst && casFeat_referent == null)
      jcas.throwFeatMissing("referent", "uk.gov.dstl.baleen.types.Base");
    ll_cas.ll_setRefValue(addr, casFeatCode_referent, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @generated
   * @param jcas JCas
   * @param casType Type
   */
  public Base_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_confidence =
        jcas.getRequiredFeatureDE(casType, "confidence", "uima.cas.Double", featOkTst);
    casFeatCode_confidence =
        (null == casFeat_confidence)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_confidence).getCode();

    casFeat_referent =
        jcas.getRequiredFeatureDE(
            casType, "referent", "uk.gov.dstl.baleen.types.semantic.ReferenceTarget", featOkTst);
    casFeatCode_referent =
        (null == casFeat_referent)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_referent).getCode();
  }
}
