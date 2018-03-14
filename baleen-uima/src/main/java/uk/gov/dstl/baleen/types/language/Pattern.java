// Dstl (c) Crown Copyright 2017

/* First created by JCasGen Tue Apr 12 12:06:25 BST 2016 */
package uk.gov.dstl.baleen.types.language;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.Base;

/**
 * The text pattern between two annotations (usually entities) which has been processed to be more
 * meaningful than simply the covered text between them Updated by JCasGen Wed Apr 13 13:23:16 BST
 * 2016 XML source:
 * H:/git/TextProcessing/core/baleen/baleen-uima/src/main/resources/types/common_type_system.xml
 *
 * @generated
 */
public class Pattern extends Base {
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Pattern.class);
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
  protected Pattern() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   */
  public Pattern(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Pattern(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Pattern(JCas jcas, int begin, int end) {
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
  // * Feature: source

  /**
   * getter for source - gets The source entity (first entity in the sentence)
   *
   * @generated
   * @return value of the feature
   */
  public Base getSource() {
    if (Pattern_Type.featOkTst && ((Pattern_Type) jcasType).casFeat_source == null)
      jcasType.jcas.throwFeatMissing("source", "uk.gov.dstl.baleen.types.language.Pattern");
    return (Base)
        (jcasType.ll_cas.ll_getFSForRef(
            jcasType.ll_cas.ll_getRefValue(addr, ((Pattern_Type) jcasType).casFeatCode_source)));
  }

  /**
   * setter for source - sets The source entity (first entity in the sentence)
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setSource(Base v) {
    if (Pattern_Type.featOkTst && ((Pattern_Type) jcasType).casFeat_source == null)
      jcasType.jcas.throwFeatMissing("source", "uk.gov.dstl.baleen.types.language.Pattern");
    jcasType.ll_cas.ll_setRefValue(
        addr, ((Pattern_Type) jcasType).casFeatCode_source, jcasType.ll_cas.ll_getFSRef(v));
  }

  // *--------------*
  // * Feature: target

  /**
   * getter for target - gets The target entity (last entity in the sentence)
   *
   * @generated
   * @return value of the feature
   */
  public Base getTarget() {
    if (Pattern_Type.featOkTst && ((Pattern_Type) jcasType).casFeat_target == null)
      jcasType.jcas.throwFeatMissing("target", "uk.gov.dstl.baleen.types.language.Pattern");
    return (Base)
        (jcasType.ll_cas.ll_getFSForRef(
            jcasType.ll_cas.ll_getRefValue(addr, ((Pattern_Type) jcasType).casFeatCode_target)));
  }

  /**
   * setter for target - sets The target entity (last entity in the sentence)
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setTarget(Base v) {
    if (Pattern_Type.featOkTst && ((Pattern_Type) jcasType).casFeat_target == null)
      jcasType.jcas.throwFeatMissing("target", "uk.gov.dstl.baleen.types.language.Pattern");
    jcasType.ll_cas.ll_setRefValue(
        addr, ((Pattern_Type) jcasType).casFeatCode_target, jcasType.ll_cas.ll_getFSRef(v));
  }

  // *--------------*
  // * Feature: words

  /**
   * getter for words - gets The collection of word tokens which form this pattern.
   *
   * @generated
   * @return value of the feature
   */
  public FSArray getWords() {
    if (Pattern_Type.featOkTst && ((Pattern_Type) jcasType).casFeat_words == null)
      jcasType.jcas.throwFeatMissing("words", "uk.gov.dstl.baleen.types.language.Pattern");
    return (FSArray)
        (jcasType.ll_cas.ll_getFSForRef(
            jcasType.ll_cas.ll_getRefValue(addr, ((Pattern_Type) jcasType).casFeatCode_words)));
  }

  /**
   * setter for words - sets The collection of word tokens which form this pattern.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setWords(FSArray v) {
    if (Pattern_Type.featOkTst && ((Pattern_Type) jcasType).casFeat_words == null)
      jcasType.jcas.throwFeatMissing("words", "uk.gov.dstl.baleen.types.language.Pattern");
    jcasType.ll_cas.ll_setRefValue(
        addr, ((Pattern_Type) jcasType).casFeatCode_words, jcasType.ll_cas.ll_getFSRef(v));
  }

  /**
   * indexed getter for words - gets an indexed value - The collection of word tokens which form
   * this pattern.
   *
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i
   */
  public WordToken getWords(int i) {
    if (Pattern_Type.featOkTst && ((Pattern_Type) jcasType).casFeat_words == null)
      jcasType.jcas.throwFeatMissing("words", "uk.gov.dstl.baleen.types.language.Pattern");
    jcasType.jcas.checkArrayBounds(
        jcasType.ll_cas.ll_getRefValue(addr, ((Pattern_Type) jcasType).casFeatCode_words), i);
    return (WordToken)
        (jcasType.ll_cas.ll_getFSForRef(
            jcasType.ll_cas.ll_getRefArrayValue(
                jcasType.ll_cas.ll_getRefValue(addr, ((Pattern_Type) jcasType).casFeatCode_words),
                i)));
  }

  /**
   * indexed setter for words - sets an indexed value - The collection of word tokens which form
   * this pattern.
   *
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array
   */
  public void setWords(int i, WordToken v) {
    if (Pattern_Type.featOkTst && ((Pattern_Type) jcasType).casFeat_words == null)
      jcasType.jcas.throwFeatMissing("words", "uk.gov.dstl.baleen.types.language.Pattern");
    jcasType.jcas.checkArrayBounds(
        jcasType.ll_cas.ll_getRefValue(addr, ((Pattern_Type) jcasType).casFeatCode_words), i);
    jcasType.ll_cas.ll_setRefArrayValue(
        jcasType.ll_cas.ll_getRefValue(addr, ((Pattern_Type) jcasType).casFeatCode_words),
        i,
        jcasType.ll_cas.ll_getFSRef(v));
  }
}
