

/* First created by JCasGen Wed Jan 14 12:58:18 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types.language;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.Base;


/** Specifies a lemma form for a word token
 * Updated by JCasGen Wed Jan 14 12:58:18 GMT 2015
 * XML source: H:/git/core/baleen/baleen-uima/src/main/resources/language_type_system.xml
 * @generated */
public class WordLemma extends Base {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(WordLemma.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected WordLemma() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public WordLemma(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public WordLemma(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public WordLemma(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: partOfSpeech

  /** getter for partOfSpeech - gets The part of speech (POS) tag for this lemma. Usually a Penn Treebank tag.
   * @generated
   * @return value of the feature 
   */
  public String getPartOfSpeech() {
    if (WordLemma_Type.featOkTst && ((WordLemma_Type)jcasType).casFeat_partOfSpeech == null)
      jcasType.jcas.throwFeatMissing("partOfSpeech", "uk.gov.dstl.baleen.types.language.WordLemma");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WordLemma_Type)jcasType).casFeatCode_partOfSpeech);}
    
  /** setter for partOfSpeech - sets The part of speech (POS) tag for this lemma. Usually a Penn Treebank tag. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPartOfSpeech(String v) {
    if (WordLemma_Type.featOkTst && ((WordLemma_Type)jcasType).casFeat_partOfSpeech == null)
      jcasType.jcas.throwFeatMissing("partOfSpeech", "uk.gov.dstl.baleen.types.language.WordLemma");
    jcasType.ll_cas.ll_setStringValue(addr, ((WordLemma_Type)jcasType).casFeatCode_partOfSpeech, v);}    
   
    
  //*--------------*
  //* Feature: lemmaForm

  /** getter for lemmaForm - gets The normal form for this lemma.
   * @generated
   * @return value of the feature 
   */
  public String getLemmaForm() {
    if (WordLemma_Type.featOkTst && ((WordLemma_Type)jcasType).casFeat_lemmaForm == null)
      jcasType.jcas.throwFeatMissing("lemmaForm", "uk.gov.dstl.baleen.types.language.WordLemma");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WordLemma_Type)jcasType).casFeatCode_lemmaForm);}
    
  /** setter for lemmaForm - sets The normal form for this lemma. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setLemmaForm(String v) {
    if (WordLemma_Type.featOkTst && ((WordLemma_Type)jcasType).casFeat_lemmaForm == null)
      jcasType.jcas.throwFeatMissing("lemmaForm", "uk.gov.dstl.baleen.types.language.WordLemma");
    jcasType.ll_cas.ll_setStringValue(addr, ((WordLemma_Type)jcasType).casFeatCode_lemmaForm, v);}    
  }

    