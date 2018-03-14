/* First created by JCasGen Wed Jan 14 12:58:18 GMT 2015 */
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.types.language;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.Base;

/**
 * The output from some text tokenization process. Updated by JCasGen Wed Apr 13 13:23:16 BST 2016
 * XML source:
 * H:/git/TextProcessing/core/baleen/baleen-uima/src/main/resources/types/common_type_system.xml
 *
 * @generated
 */
public class WordToken extends Base {
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(WordToken.class);
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int type = typeIndexID;
  /**
   * @generated
   * @return index of the type
   */
  @Override
  public int getTypeIndexID() {
    return typeIndexID;
  }

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected WordToken() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   */
  public WordToken(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public WordToken(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public WordToken(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }

  /**
   *
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable
   */
  private void readObject() {
    /*default - does nothing empty block */
  }

  // *--------------*
  // * Feature: partOfSpeech

  /**
   * getter for partOfSpeech - gets The part of speech (POS) tag. Usually a Penn Treebank tag.
   *
   * @generated
   * @return value of the feature
   */
  public String getPartOfSpeech() {
    if (WordToken_Type.featOkTst && ((WordToken_Type) jcasType).casFeat_partOfSpeech == null)
      jcasType.jcas.throwFeatMissing("partOfSpeech", "uk.gov.dstl.baleen.types.language.WordToken");
    return jcasType.ll_cas.ll_getStringValue(
        addr, ((WordToken_Type) jcasType).casFeatCode_partOfSpeech);
  }

  /**
   * setter for partOfSpeech - sets The part of speech (POS) tag. Usually a Penn Treebank tag.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setPartOfSpeech(String v) {
    if (WordToken_Type.featOkTst && ((WordToken_Type) jcasType).casFeat_partOfSpeech == null)
      jcasType.jcas.throwFeatMissing("partOfSpeech", "uk.gov.dstl.baleen.types.language.WordToken");
    jcasType.ll_cas.ll_setStringValue(
        addr, ((WordToken_Type) jcasType).casFeatCode_partOfSpeech, v);
  }

  // *--------------*
  // * Feature: sentenceOrder

  /**
   * getter for sentenceOrder - gets If not null, this should be the index position of the word
   * token within parent sentence.
   *
   * @generated
   * @return value of the feature
   */
  public int getSentenceOrder() {
    if (WordToken_Type.featOkTst && ((WordToken_Type) jcasType).casFeat_sentenceOrder == null)
      jcasType.jcas.throwFeatMissing(
          "sentenceOrder", "uk.gov.dstl.baleen.types.language.WordToken");
    return jcasType.ll_cas.ll_getIntValue(
        addr, ((WordToken_Type) jcasType).casFeatCode_sentenceOrder);
  }

  /**
   * setter for sentenceOrder - sets If not null, this should be the index position of the word
   * token within parent sentence.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setSentenceOrder(int v) {
    if (WordToken_Type.featOkTst && ((WordToken_Type) jcasType).casFeat_sentenceOrder == null)
      jcasType.jcas.throwFeatMissing(
          "sentenceOrder", "uk.gov.dstl.baleen.types.language.WordToken");
    jcasType.ll_cas.ll_setIntValue(addr, ((WordToken_Type) jcasType).casFeatCode_sentenceOrder, v);
  }

  // *--------------*
  // * Feature: lemmas

  /**
   * getter for lemmas - gets A list of alternative lemmas for this word token.
   *
   * @generated
   * @return value of the feature
   */
  public FSArray getLemmas() {
    if (WordToken_Type.featOkTst && ((WordToken_Type) jcasType).casFeat_lemmas == null)
      jcasType.jcas.throwFeatMissing("lemmas", "uk.gov.dstl.baleen.types.language.WordToken");
    return (FSArray)
        (jcasType.ll_cas.ll_getFSForRef(
            jcasType.ll_cas.ll_getRefValue(addr, ((WordToken_Type) jcasType).casFeatCode_lemmas)));
  }

  /**
   * setter for lemmas - sets A list of alternative lemmas for this word token.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setLemmas(FSArray v) {
    if (WordToken_Type.featOkTst && ((WordToken_Type) jcasType).casFeat_lemmas == null)
      jcasType.jcas.throwFeatMissing("lemmas", "uk.gov.dstl.baleen.types.language.WordToken");
    jcasType.ll_cas.ll_setRefValue(
        addr, ((WordToken_Type) jcasType).casFeatCode_lemmas, jcasType.ll_cas.ll_getFSRef(v));
  }

  /**
   * indexed getter for lemmas - gets an indexed value - A list of alternative lemmas for this word
   * token.
   *
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i
   */
  public WordLemma getLemmas(int i) {
    if (WordToken_Type.featOkTst && ((WordToken_Type) jcasType).casFeat_lemmas == null)
      jcasType.jcas.throwFeatMissing("lemmas", "uk.gov.dstl.baleen.types.language.WordToken");
    jcasType.jcas.checkArrayBounds(
        jcasType.ll_cas.ll_getRefValue(addr, ((WordToken_Type) jcasType).casFeatCode_lemmas), i);
    return (WordLemma)
        (jcasType.ll_cas.ll_getFSForRef(
            jcasType.ll_cas.ll_getRefArrayValue(
                jcasType.ll_cas.ll_getRefValue(
                    addr, ((WordToken_Type) jcasType).casFeatCode_lemmas),
                i)));
  }

  /**
   * indexed setter for lemmas - sets an indexed value - A list of alternative lemmas for this word
   * token.
   *
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array
   */
  public void setLemmas(int i, WordLemma v) {
    if (WordToken_Type.featOkTst && ((WordToken_Type) jcasType).casFeat_lemmas == null)
      jcasType.jcas.throwFeatMissing("lemmas", "uk.gov.dstl.baleen.types.language.WordToken");
    jcasType.jcas.checkArrayBounds(
        jcasType.ll_cas.ll_getRefValue(addr, ((WordToken_Type) jcasType).casFeatCode_lemmas), i);
    jcasType.ll_cas.ll_setRefArrayValue(
        jcasType.ll_cas.ll_getRefValue(addr, ((WordToken_Type) jcasType).casFeatCode_lemmas),
        i,
        jcasType.ll_cas.ll_getFSRef(v));
  }
}
