// Dstl (c) Crown Copyright 2017

/* First created by JCasGen Tue Apr 12 12:06:25 BST 2016 */
package uk.gov.dstl.baleen.types.language;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.Base_Type;

/**
 * The text pattern between two annotations (usually entities) which has been processed to be more
 * meaningful than simply the covered text between them Updated by JCasGen Wed Apr 13 13:23:16 BST
 * 2016
 *
 * @generated
 */
public class Pattern_Type extends Base_Type {
  /** @generated */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = Pattern.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public static final boolean featOkTst =
      JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.language.Pattern");

  /** @generated */
  final Feature casFeat_source;
  /** @generated */
  final int casFeatCode_source;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getSource(int addr) {
    if (featOkTst && casFeat_source == null)
      jcas.throwFeatMissing("source", "uk.gov.dstl.baleen.types.language.Pattern");
    return ll_cas.ll_getRefValue(addr, casFeatCode_source);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setSource(int addr, int v) {
    if (featOkTst && casFeat_source == null)
      jcas.throwFeatMissing("source", "uk.gov.dstl.baleen.types.language.Pattern");
    ll_cas.ll_setRefValue(addr, casFeatCode_source, v);
  }

  /** @generated */
  final Feature casFeat_target;
  /** @generated */
  final int casFeatCode_target;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getTarget(int addr) {
    if (featOkTst && casFeat_target == null)
      jcas.throwFeatMissing("target", "uk.gov.dstl.baleen.types.language.Pattern");
    return ll_cas.ll_getRefValue(addr, casFeatCode_target);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setTarget(int addr, int v) {
    if (featOkTst && casFeat_target == null)
      jcas.throwFeatMissing("target", "uk.gov.dstl.baleen.types.language.Pattern");
    ll_cas.ll_setRefValue(addr, casFeatCode_target, v);
  }

  /** @generated */
  final Feature casFeat_words;
  /** @generated */
  final int casFeatCode_words;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getWords(int addr) {
    if (featOkTst && casFeat_words == null)
      jcas.throwFeatMissing("words", "uk.gov.dstl.baleen.types.language.Pattern");
    return ll_cas.ll_getRefValue(addr, casFeatCode_words);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setWords(int addr, int v) {
    if (featOkTst && casFeat_words == null)
      jcas.throwFeatMissing("words", "uk.gov.dstl.baleen.types.language.Pattern");
    ll_cas.ll_setRefValue(addr, casFeatCode_words, v);
  }

  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array
   */
  public int getWords(int addr, int i) {
    if (featOkTst && casFeat_words == null)
      jcas.throwFeatMissing("words", "uk.gov.dstl.baleen.types.language.Pattern");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_words), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_words), i);
    return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_words), i);
  }

  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */
  public void setWords(int addr, int i, int v) {
    if (featOkTst && casFeat_words == null)
      jcas.throwFeatMissing("words", "uk.gov.dstl.baleen.types.language.Pattern");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_words), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_words), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_words), i, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @generated
   * @param jcas JCas
   * @param casType Type
   */
  public Pattern_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_source =
        jcas.getRequiredFeatureDE(casType, "source", "uk.gov.dstl.baleen.types.Base", featOkTst);
    casFeatCode_source =
        (null == casFeat_source)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_source).getCode();

    casFeat_target =
        jcas.getRequiredFeatureDE(casType, "target", "uk.gov.dstl.baleen.types.Base", featOkTst);
    casFeatCode_target =
        (null == casFeat_target)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_target).getCode();

    casFeat_words = jcas.getRequiredFeatureDE(casType, "words", "uima.cas.FSArray", featOkTst);
    casFeatCode_words =
        (null == casFeat_words)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_words).getCode();
  }
}
