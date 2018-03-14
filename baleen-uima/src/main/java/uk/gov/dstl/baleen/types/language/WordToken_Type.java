/* First created by JCasGen Wed Jan 14 12:58:18 GMT 2015 */
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.types.language;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.Base_Type;

/**
 * The output from some text tokenization process. Updated by JCasGen Wed Apr 13 13:23:16 BST 2016
 *
 * @generated
 */
public class WordToken_Type extends Base_Type {
  /**
   * @generated
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {
    return fsGenerator;
  }
  /** @generated */
  private final FSGenerator fsGenerator =
      new FSGenerator() {
        public FeatureStructure createFS(int addr, CASImpl cas) {
          if (WordToken_Type.this.useExistingInstance) {
            // Return eq fs instance if already created
            FeatureStructure fs = WordToken_Type.this.jcas.getJfsFromCaddr(addr);
            if (null == fs) {
              fs = new WordToken(addr, WordToken_Type.this);
              WordToken_Type.this.jcas.putJfsFromCaddr(addr, fs);
              return fs;
            }
            return fs;
          } else return new WordToken(addr, WordToken_Type.this);
        }
      };
  /** @generated */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = WordToken.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public static final boolean featOkTst =
      JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.language.WordToken");

  /** @generated */
  final Feature casFeat_partOfSpeech;
  /** @generated */
  final int casFeatCode_partOfSpeech;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public String getPartOfSpeech(int addr) {
    if (featOkTst && casFeat_partOfSpeech == null)
      jcas.throwFeatMissing("partOfSpeech", "uk.gov.dstl.baleen.types.language.WordToken");
    return ll_cas.ll_getStringValue(addr, casFeatCode_partOfSpeech);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setPartOfSpeech(int addr, String v) {
    if (featOkTst && casFeat_partOfSpeech == null)
      jcas.throwFeatMissing("partOfSpeech", "uk.gov.dstl.baleen.types.language.WordToken");
    ll_cas.ll_setStringValue(addr, casFeatCode_partOfSpeech, v);
  }

  /** @generated */
  final Feature casFeat_sentenceOrder;
  /** @generated */
  final int casFeatCode_sentenceOrder;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getSentenceOrder(int addr) {
    if (featOkTst && casFeat_sentenceOrder == null)
      jcas.throwFeatMissing("sentenceOrder", "uk.gov.dstl.baleen.types.language.WordToken");
    return ll_cas.ll_getIntValue(addr, casFeatCode_sentenceOrder);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setSentenceOrder(int addr, int v) {
    if (featOkTst && casFeat_sentenceOrder == null)
      jcas.throwFeatMissing("sentenceOrder", "uk.gov.dstl.baleen.types.language.WordToken");
    ll_cas.ll_setIntValue(addr, casFeatCode_sentenceOrder, v);
  }

  /** @generated */
  final Feature casFeat_lemmas;
  /** @generated */
  final int casFeatCode_lemmas;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getLemmas(int addr) {
    if (featOkTst && casFeat_lemmas == null)
      jcas.throwFeatMissing("lemmas", "uk.gov.dstl.baleen.types.language.WordToken");
    return ll_cas.ll_getRefValue(addr, casFeatCode_lemmas);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setLemmas(int addr, int v) {
    if (featOkTst && casFeat_lemmas == null)
      jcas.throwFeatMissing("lemmas", "uk.gov.dstl.baleen.types.language.WordToken");
    ll_cas.ll_setRefValue(addr, casFeatCode_lemmas, v);
  }

  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array
   */
  public int getLemmas(int addr, int i) {
    if (featOkTst && casFeat_lemmas == null)
      jcas.throwFeatMissing("lemmas", "uk.gov.dstl.baleen.types.language.WordToken");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_lemmas), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_lemmas), i);
    return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_lemmas), i);
  }

  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */
  public void setLemmas(int addr, int i, int v) {
    if (featOkTst && casFeat_lemmas == null)
      jcas.throwFeatMissing("lemmas", "uk.gov.dstl.baleen.types.language.WordToken");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_lemmas), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_lemmas), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_lemmas), i, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @generated
   * @param jcas JCas
   * @param casType Type
   */
  public WordToken_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_partOfSpeech =
        jcas.getRequiredFeatureDE(casType, "partOfSpeech", "uima.cas.String", featOkTst);
    casFeatCode_partOfSpeech =
        (null == casFeat_partOfSpeech)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_partOfSpeech).getCode();

    casFeat_sentenceOrder =
        jcas.getRequiredFeatureDE(casType, "sentenceOrder", "uima.cas.Integer", featOkTst);
    casFeatCode_sentenceOrder =
        (null == casFeat_sentenceOrder)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_sentenceOrder).getCode();

    casFeat_lemmas = jcas.getRequiredFeatureDE(casType, "lemmas", "uima.cas.FSArray", featOkTst);
    casFeatCode_lemmas =
        (null == casFeat_lemmas)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_lemmas).getCode();
  }
}
