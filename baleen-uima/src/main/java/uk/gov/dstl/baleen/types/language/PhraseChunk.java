/* First created by JCasGen Wed Jan 14 12:58:18 GMT 2015 */
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.types.language;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.Base;

/**
 * Annotation to store the result of shallow parsing, which provide noun phrase and verb phrase
 * constituents, rather than just WordTokens. Updated by JCasGen Wed Apr 13 13:23:16 BST 2016 XML
 * source:
 * H:/git/TextProcessing/core/baleen/baleen-uima/src/main/resources/types/common_type_system.xml
 *
 * @generated
 */
public class PhraseChunk extends Base {
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(PhraseChunk.class);
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
  protected PhraseChunk() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   */
  public PhraseChunk(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public PhraseChunk(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public PhraseChunk(JCas jcas, int begin, int end) {
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
  // * Feature: chunkType

  /**
   * getter for chunkType - gets The Penn Treebank constituent annotation
   *
   * @generated
   * @return value of the feature
   */
  public String getChunkType() {
    if (PhraseChunk_Type.featOkTst && ((PhraseChunk_Type) jcasType).casFeat_chunkType == null)
      jcasType.jcas.throwFeatMissing("chunkType", "uk.gov.dstl.baleen.types.language.PhraseChunk");
    return jcasType.ll_cas.ll_getStringValue(
        addr, ((PhraseChunk_Type) jcasType).casFeatCode_chunkType);
  }

  /**
   * setter for chunkType - sets The Penn Treebank constituent annotation
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setChunkType(String v) {
    if (PhraseChunk_Type.featOkTst && ((PhraseChunk_Type) jcasType).casFeat_chunkType == null)
      jcasType.jcas.throwFeatMissing("chunkType", "uk.gov.dstl.baleen.types.language.PhraseChunk");
    jcasType.ll_cas.ll_setStringValue(addr, ((PhraseChunk_Type) jcasType).casFeatCode_chunkType, v);
  }

  // *--------------*
  // * Feature: constituentWords

  /**
   * getter for constituentWords - gets Word tokens which comprise the constituent.
   *
   * @generated
   * @return value of the feature
   */
  public FSArray getConstituentWords() {
    if (PhraseChunk_Type.featOkTst
        && ((PhraseChunk_Type) jcasType).casFeat_constituentWords == null)
      jcasType.jcas.throwFeatMissing(
          "constituentWords", "uk.gov.dstl.baleen.types.language.PhraseChunk");
    return (FSArray)
        (jcasType.ll_cas.ll_getFSForRef(
            jcasType.ll_cas.ll_getRefValue(
                addr, ((PhraseChunk_Type) jcasType).casFeatCode_constituentWords)));
  }

  /**
   * setter for constituentWords - sets Word tokens which comprise the constituent.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setConstituentWords(FSArray v) {
    if (PhraseChunk_Type.featOkTst
        && ((PhraseChunk_Type) jcasType).casFeat_constituentWords == null)
      jcasType.jcas.throwFeatMissing(
          "constituentWords", "uk.gov.dstl.baleen.types.language.PhraseChunk");
    jcasType.ll_cas.ll_setRefValue(
        addr,
        ((PhraseChunk_Type) jcasType).casFeatCode_constituentWords,
        jcasType.ll_cas.ll_getFSRef(v));
  }

  /**
   * indexed getter for constituentWords - gets an indexed value - Word tokens which comprise the
   * constituent.
   *
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i
   */
  public WordToken getConstituentWords(int i) {
    if (PhraseChunk_Type.featOkTst
        && ((PhraseChunk_Type) jcasType).casFeat_constituentWords == null)
      jcasType.jcas.throwFeatMissing(
          "constituentWords", "uk.gov.dstl.baleen.types.language.PhraseChunk");
    jcasType.jcas.checkArrayBounds(
        jcasType.ll_cas.ll_getRefValue(
            addr, ((PhraseChunk_Type) jcasType).casFeatCode_constituentWords),
        i);
    return (WordToken)
        (jcasType.ll_cas.ll_getFSForRef(
            jcasType.ll_cas.ll_getRefArrayValue(
                jcasType.ll_cas.ll_getRefValue(
                    addr, ((PhraseChunk_Type) jcasType).casFeatCode_constituentWords),
                i)));
  }

  /**
   * indexed setter for constituentWords - sets an indexed value - Word tokens which comprise the
   * constituent.
   *
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array
   */
  public void setConstituentWords(int i, WordToken v) {
    if (PhraseChunk_Type.featOkTst
        && ((PhraseChunk_Type) jcasType).casFeat_constituentWords == null)
      jcasType.jcas.throwFeatMissing(
          "constituentWords", "uk.gov.dstl.baleen.types.language.PhraseChunk");
    jcasType.jcas.checkArrayBounds(
        jcasType.ll_cas.ll_getRefValue(
            addr, ((PhraseChunk_Type) jcasType).casFeatCode_constituentWords),
        i);
    jcasType.ll_cas.ll_setRefArrayValue(
        jcasType.ll_cas.ll_getRefValue(
            addr, ((PhraseChunk_Type) jcasType).casFeatCode_constituentWords),
        i,
        jcasType.ll_cas.ll_getFSRef(v));
  }

  // *--------------*
  // * Feature: headWord

  /**
   * getter for headWord - gets The head word of the constituent phrase
   *
   * @generated
   * @return value of the feature
   */
  public WordToken getHeadWord() {
    if (PhraseChunk_Type.featOkTst && ((PhraseChunk_Type) jcasType).casFeat_headWord == null)
      jcasType.jcas.throwFeatMissing("headWord", "uk.gov.dstl.baleen.types.language.PhraseChunk");
    return (WordToken)
        (jcasType.ll_cas.ll_getFSForRef(
            jcasType.ll_cas.ll_getRefValue(
                addr, ((PhraseChunk_Type) jcasType).casFeatCode_headWord)));
  }

  /**
   * setter for headWord - sets The head word of the constituent phrase
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setHeadWord(WordToken v) {
    if (PhraseChunk_Type.featOkTst && ((PhraseChunk_Type) jcasType).casFeat_headWord == null)
      jcasType.jcas.throwFeatMissing("headWord", "uk.gov.dstl.baleen.types.language.PhraseChunk");
    jcasType.ll_cas.ll_setRefValue(
        addr, ((PhraseChunk_Type) jcasType).casFeatCode_headWord, jcasType.ll_cas.ll_getFSRef(v));
  }
}
