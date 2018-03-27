/* First created by JCasGen Tue Feb 03 15:26:49 GMT 2015 */
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.types.common;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.semantic.Entity_Type;

/**
 * User-defined key phrases or domain-specific terms, described by a type property. Updated by
 * JCasGen Wed Apr 13 13:23:15 BST 2016
 *
 * @generated
 */
public class Buzzword_Type extends Entity_Type {
  /** @generated */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = Buzzword.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public static final boolean featOkTst =
      JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.common.Buzzword");

  /** @generated */
  final Feature casFeat_tags;
  /** @generated */
  final int casFeatCode_tags;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getTags(int addr) {
    if (featOkTst && casFeat_tags == null)
      jcas.throwFeatMissing("tags", "uk.gov.dstl.baleen.types.common.Buzzword");
    return ll_cas.ll_getRefValue(addr, casFeatCode_tags);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setTags(int addr, int v) {
    if (featOkTst && casFeat_tags == null)
      jcas.throwFeatMissing("tags", "uk.gov.dstl.baleen.types.common.Buzzword");
    ll_cas.ll_setRefValue(addr, casFeatCode_tags, v);
  }

  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array
   */
  public String getTags(int addr, int i) {
    if (featOkTst && casFeat_tags == null)
      jcas.throwFeatMissing("tags", "uk.gov.dstl.baleen.types.common.Buzzword");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tags), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_tags), i);
    return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tags), i);
  }

  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */
  public void setTags(int addr, int i, String v) {
    if (featOkTst && casFeat_tags == null)
      jcas.throwFeatMissing("tags", "uk.gov.dstl.baleen.types.common.Buzzword");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tags), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_tags), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tags), i, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @generated
   * @param jcas JCas
   * @param casType Type
   */
  public Buzzword_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_tags = jcas.getRequiredFeatureDE(casType, "tags", "uima.cas.StringArray", featOkTst);
    casFeatCode_tags =
        (null == casFeat_tags) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl) casFeat_tags).getCode();
  }
}
