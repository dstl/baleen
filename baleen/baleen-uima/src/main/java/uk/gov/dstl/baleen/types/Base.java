

/* First created by JCasGen Wed Jan 14 12:58:12 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

/** Base annotation with confidence and annotator properties.
 * Updated by JCasGen Fri Feb 05 14:54:30 GMT 2016
 * XML source: C:/co/git/CCD-DE/RMR/baleen/baleen/baleen-uima/src/main/resources/types/semantic_type_system.xml
 * @generated */
public class Base extends BaleenAnnotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Base.class);
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
  protected Base() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Base(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Base(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Base(JCas jcas, int begin, int end) {
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
  //* Feature: confidence

  /** getter for confidence - gets Confidence value between 0 and 1 from annotation processor.
   * @generated
   * @return value of the feature 
   */
  public double getConfidence() {
    if (Base_Type.featOkTst && ((Base_Type)jcasType).casFeat_confidence == null)
      jcasType.jcas.throwFeatMissing("confidence", "uk.gov.dstl.baleen.types.Base");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Base_Type)jcasType).casFeatCode_confidence);}
    
  /** setter for confidence - sets Confidence value between 0 and 1 from annotation processor. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setConfidence(double v) {
    if (Base_Type.featOkTst && ((Base_Type)jcasType).casFeat_confidence == null)
      jcasType.jcas.throwFeatMissing("confidence", "uk.gov.dstl.baleen.types.Base");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Base_Type)jcasType).casFeatCode_confidence, v);}    
  }

    