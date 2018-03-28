/* First created by JCasGen Wed Jan 14 12:58:18 GMT 2015 */
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.types.language;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.Base_Type;

/**
 * Annotation to store the result of shallow parsing, which provide noun phrase and verb phrase
 * constituents, rather than just WordTokens. Updated by JCasGen Wed Apr 13 13:23:16 BST 2016
 *
 * @generated
 */
public class PhraseChunk_Type extends Base_Type {
  /** @generated */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = PhraseChunk.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public static final boolean featOkTst =
      JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.language.PhraseChunk");

  /** @generated */
  final Feature casFeat_chunkType;
  /** @generated */
  final int casFeatCode_chunkType;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public String getChunkType(int addr) {
    if (featOkTst && casFeat_chunkType == null)
      jcas.throwFeatMissing("chunkType", "uk.gov.dstl.baleen.types.language.PhraseChunk");
    return ll_cas.ll_getStringValue(addr, casFeatCode_chunkType);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setChunkType(int addr, String v) {
    if (featOkTst && casFeat_chunkType == null)
      jcas.throwFeatMissing("chunkType", "uk.gov.dstl.baleen.types.language.PhraseChunk");
    ll_cas.ll_setStringValue(addr, casFeatCode_chunkType, v);
  }

  /** @generated */
  final Feature casFeat_constituentWords;
  /** @generated */
  final int casFeatCode_constituentWords;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getConstituentWords(int addr) {
    if (featOkTst && casFeat_constituentWords == null)
      jcas.throwFeatMissing("constituentWords", "uk.gov.dstl.baleen.types.language.PhraseChunk");
    return ll_cas.ll_getRefValue(addr, casFeatCode_constituentWords);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setConstituentWords(int addr, int v) {
    if (featOkTst && casFeat_constituentWords == null)
      jcas.throwFeatMissing("constituentWords", "uk.gov.dstl.baleen.types.language.PhraseChunk");
    ll_cas.ll_setRefValue(addr, casFeatCode_constituentWords, v);
  }

  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array
   */
  public int getConstituentWords(int addr, int i) {
    if (featOkTst && casFeat_constituentWords == null)
      jcas.throwFeatMissing("constituentWords", "uk.gov.dstl.baleen.types.language.PhraseChunk");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(
          ll_cas.ll_getRefValue(addr, casFeatCode_constituentWords), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_constituentWords), i);
    return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_constituentWords), i);
  }

  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */
  public void setConstituentWords(int addr, int i, int v) {
    if (featOkTst && casFeat_constituentWords == null)
      jcas.throwFeatMissing("constituentWords", "uk.gov.dstl.baleen.types.language.PhraseChunk");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(
          ll_cas.ll_getRefValue(addr, casFeatCode_constituentWords), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_constituentWords), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_constituentWords), i, v);
  }

  /** @generated */
  final Feature casFeat_headWord;
  /** @generated */
  final int casFeatCode_headWord;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getHeadWord(int addr) {
    if (featOkTst && casFeat_headWord == null)
      jcas.throwFeatMissing("headWord", "uk.gov.dstl.baleen.types.language.PhraseChunk");
    return ll_cas.ll_getRefValue(addr, casFeatCode_headWord);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setHeadWord(int addr, int v) {
    if (featOkTst && casFeat_headWord == null)
      jcas.throwFeatMissing("headWord", "uk.gov.dstl.baleen.types.language.PhraseChunk");
    ll_cas.ll_setRefValue(addr, casFeatCode_headWord, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @generated
   * @param jcas JCas
   * @param casType Type
   */
  public PhraseChunk_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_chunkType =
        jcas.getRequiredFeatureDE(casType, "chunkType", "uima.cas.String", featOkTst);
    casFeatCode_chunkType =
        (null == casFeat_chunkType)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_chunkType).getCode();

    casFeat_constituentWords =
        jcas.getRequiredFeatureDE(casType, "constituentWords", "uima.cas.FSArray", featOkTst);
    casFeatCode_constituentWords =
        (null == casFeat_constituentWords)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_constituentWords).getCode();

    casFeat_headWord =
        jcas.getRequiredFeatureDE(
            casType, "headWord", "uk.gov.dstl.baleen.types.language.WordToken", featOkTst);
    casFeatCode_headWord =
        (null == casFeat_headWord)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_headWord).getCode();
  }
}
