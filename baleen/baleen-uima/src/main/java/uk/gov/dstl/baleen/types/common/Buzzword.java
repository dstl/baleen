

/* First created by JCasGen Tue Feb 03 15:26:49 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types.common;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.semantic.Entity;
import org.apache.uima.jcas.cas.StringArray;


/** User-defined key phrases or domain-specific terms, described by a type property.
 * Updated by JCasGen Fri Feb 05 14:49:26 GMT 2016
 * XML source: C:/co/git/CCD-DE/RMR/baleen/baleen/baleen-uima/src/main/resources/types/common_type_system.xml
 * @generated */
public class Buzzword extends Entity {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Buzzword.class);
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
  protected Buzzword() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Buzzword(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Buzzword(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Buzzword(JCas jcas, int begin, int end) {
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
  //* Feature: tags

  /** getter for tags - gets A list of types that are associated with a given BuzzWord value.
   * @generated
   * @return value of the feature 
   */
  public StringArray getTags() {
    if (Buzzword_Type.featOkTst && ((Buzzword_Type)jcasType).casFeat_tags == null)
      jcasType.jcas.throwFeatMissing("tags", "uk.gov.dstl.baleen.types.common.Buzzword");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Buzzword_Type)jcasType).casFeatCode_tags)));}
    
  /** setter for tags - sets A list of types that are associated with a given BuzzWord value. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTags(StringArray v) {
    if (Buzzword_Type.featOkTst && ((Buzzword_Type)jcasType).casFeat_tags == null)
      jcasType.jcas.throwFeatMissing("tags", "uk.gov.dstl.baleen.types.common.Buzzword");
    jcasType.ll_cas.ll_setRefValue(addr, ((Buzzword_Type)jcasType).casFeatCode_tags, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for tags - gets an indexed value - A list of types that are associated with a given BuzzWord value.
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getTags(int i) {
    if (Buzzword_Type.featOkTst && ((Buzzword_Type)jcasType).casFeat_tags == null)
      jcasType.jcas.throwFeatMissing("tags", "uk.gov.dstl.baleen.types.common.Buzzword");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Buzzword_Type)jcasType).casFeatCode_tags), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Buzzword_Type)jcasType).casFeatCode_tags), i);}

  /** indexed setter for tags - sets an indexed value - A list of types that are associated with a given BuzzWord value.
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setTags(int i, String v) { 
    if (Buzzword_Type.featOkTst && ((Buzzword_Type)jcasType).casFeat_tags == null)
      jcasType.jcas.throwFeatMissing("tags", "uk.gov.dstl.baleen.types.common.Buzzword");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Buzzword_Type)jcasType).casFeatCode_tags), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Buzzword_Type)jcasType).casFeatCode_tags), i, v);}
  }

    