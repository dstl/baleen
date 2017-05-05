

/* First created by JCasGen Tue Feb 03 15:17:25 GMT 2015 */
//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.types.metadata;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.BaleenAnnotation;


/** Metadata associated with the document
 * Updated by JCasGen Tue Apr 12 12:06:57 BST 2016
 * XML source: H:/git/TextProcessing/core/baleen/baleen-uima/src/main/resources/types/metadata_type_system.xml
 * @generated */
public class Metadata extends BaleenAnnotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Metadata.class);
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
  protected Metadata() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Metadata(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Metadata(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Metadata(JCas jcas, int begin, int end) {
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
  //* Feature: key

  /** getter for key - gets The key (name) for the metadata
   * @generated
   * @return value of the feature 
   */
  public String getKey() {
    if (Metadata_Type.featOkTst && ((Metadata_Type)jcasType).casFeat_key == null)
      jcasType.jcas.throwFeatMissing("key", "uk.gov.dstl.baleen.types.metadata.Metadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Metadata_Type)jcasType).casFeatCode_key);}
    
  /** setter for key - sets The key (name) for the metadata 
   * @generated
   * @param v value to set into the feature 
   */
  public void setKey(String v) {
    if (Metadata_Type.featOkTst && ((Metadata_Type)jcasType).casFeat_key == null)
      jcasType.jcas.throwFeatMissing("key", "uk.gov.dstl.baleen.types.metadata.Metadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((Metadata_Type)jcasType).casFeatCode_key, v);}    
   
    
  //*--------------*
  //* Feature: value

  /** getter for value - gets The value of the metadata
   * @generated
   * @return value of the feature 
   */
  public String getValue() {
    if (Metadata_Type.featOkTst && ((Metadata_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.metadata.Metadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Metadata_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets The value of the metadata 
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue(String v) {
    if (Metadata_Type.featOkTst && ((Metadata_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.metadata.Metadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((Metadata_Type)jcasType).casFeatCode_value, v);}    
  }

    