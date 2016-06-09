

/* First created by JCasGen Wed Jan 14 12:58:31 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types.metadata;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.BaleenAnnotation;


/** The protective marking of the text span defined by the begin and end properties.
 * Updated by JCasGen Tue Apr 12 12:06:57 BST 2016
 * XML source: H:/git/TextProcessing/core/baleen/baleen-uima/src/main/resources/types/metadata_type_system.xml
 * @generated */
public class ProtectiveMarking extends BaleenAnnotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ProtectiveMarking.class);
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
  protected ProtectiveMarking() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ProtectiveMarking(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ProtectiveMarking(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ProtectiveMarking(JCas jcas, int begin, int end) {
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
  //* Feature: classification

  /** getter for classification - gets The security classification of this protective marking
   * @generated
   * @return value of the feature 
   */
  public String getClassification() {
    if (ProtectiveMarking_Type.featOkTst && ((ProtectiveMarking_Type)jcasType).casFeat_classification == null)
      jcasType.jcas.throwFeatMissing("classification", "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ProtectiveMarking_Type)jcasType).casFeatCode_classification);}
    
  /** setter for classification - sets The security classification of this protective marking 
   * @generated
   * @param v value to set into the feature 
   */
  public void setClassification(String v) {
    if (ProtectiveMarking_Type.featOkTst && ((ProtectiveMarking_Type)jcasType).casFeat_classification == null)
      jcasType.jcas.throwFeatMissing("classification", "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    jcasType.ll_cas.ll_setStringValue(addr, ((ProtectiveMarking_Type)jcasType).casFeatCode_classification, v);}    
   
    
  //*--------------*
  //* Feature: caveats

  /** getter for caveats - gets An array of string values specifying handling caveats for this protective marking
   * @generated
   * @return value of the feature 
   */
  public StringArray getCaveats() {
    if (ProtectiveMarking_Type.featOkTst && ((ProtectiveMarking_Type)jcasType).casFeat_caveats == null)
      jcasType.jcas.throwFeatMissing("caveats", "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ProtectiveMarking_Type)jcasType).casFeatCode_caveats)));}
    
  /** setter for caveats - sets An array of string values specifying handling caveats for this protective marking 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCaveats(StringArray v) {
    if (ProtectiveMarking_Type.featOkTst && ((ProtectiveMarking_Type)jcasType).casFeat_caveats == null)
      jcasType.jcas.throwFeatMissing("caveats", "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    jcasType.ll_cas.ll_setRefValue(addr, ((ProtectiveMarking_Type)jcasType).casFeatCode_caveats, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for caveats - gets an indexed value - An array of string values specifying handling caveats for the document.
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getCaveats(int i) {
    if (ProtectiveMarking_Type.featOkTst && ((ProtectiveMarking_Type)jcasType).casFeat_caveats == null)
      jcasType.jcas.throwFeatMissing("caveats", "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((ProtectiveMarking_Type)jcasType).casFeatCode_caveats), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((ProtectiveMarking_Type)jcasType).casFeatCode_caveats), i);}

  /** indexed setter for caveats - sets an indexed value - An array of string values specifying handling caveats for the document.
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setCaveats(int i, String v) { 
    if (ProtectiveMarking_Type.featOkTst && ((ProtectiveMarking_Type)jcasType).casFeat_caveats == null)
      jcasType.jcas.throwFeatMissing("caveats", "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((ProtectiveMarking_Type)jcasType).casFeatCode_caveats), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((ProtectiveMarking_Type)jcasType).casFeatCode_caveats), i, v);}
   
    
  //*--------------*
  //* Feature: releasability

  /** getter for releasability - gets Array of country designators to which this protective marking is releasable.
   * @generated
   * @return value of the feature 
   */
  public StringArray getReleasability() {
    if (ProtectiveMarking_Type.featOkTst && ((ProtectiveMarking_Type)jcasType).casFeat_releasability == null)
      jcasType.jcas.throwFeatMissing("releasability", "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ProtectiveMarking_Type)jcasType).casFeatCode_releasability)));}
    
  /** setter for releasability - sets Array of country designators to which this protective marking is releasable. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setReleasability(StringArray v) {
    if (ProtectiveMarking_Type.featOkTst && ((ProtectiveMarking_Type)jcasType).casFeat_releasability == null)
      jcasType.jcas.throwFeatMissing("releasability", "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    jcasType.ll_cas.ll_setRefValue(addr, ((ProtectiveMarking_Type)jcasType).casFeatCode_releasability, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for releasability - gets an indexed value - Array of country designators to which the document is releasable.
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getReleasability(int i) {
    if (ProtectiveMarking_Type.featOkTst && ((ProtectiveMarking_Type)jcasType).casFeat_releasability == null)
      jcasType.jcas.throwFeatMissing("releasability", "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((ProtectiveMarking_Type)jcasType).casFeatCode_releasability), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((ProtectiveMarking_Type)jcasType).casFeatCode_releasability), i);}

  /** indexed setter for releasability - sets an indexed value - Array of country designators to which the document is releasable.
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setReleasability(int i, String v) { 
    if (ProtectiveMarking_Type.featOkTst && ((ProtectiveMarking_Type)jcasType).casFeat_releasability == null)
      jcasType.jcas.throwFeatMissing("releasability", "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((ProtectiveMarking_Type)jcasType).casFeatCode_releasability), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((ProtectiveMarking_Type)jcasType).casFeatCode_releasability), i, v);}
  }

    