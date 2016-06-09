

/* First created by JCasGen Wed Jan 21 11:36:30 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types.temporal;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.semantic.Temporal;


/** An entity representing a time span
 * Updated by JCasGen Tue Apr 12 12:07:25 BST 2016
 * XML source: H:/git/TextProcessing/core/baleen/baleen-uima/src/main/resources/types/temporal_type_system.xml
 * @generated */
public class TimeSpan extends Temporal {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TimeSpan.class);
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
  protected TimeSpan() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public TimeSpan(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public TimeSpan(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public TimeSpan(JCas jcas, int begin, int end) {
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
  //* Feature: spanStart

  /** getter for spanStart - gets The start of the time span, as a timestamp
   * @generated
   * @return value of the feature 
   */
  public long getSpanStart() {
    if (TimeSpan_Type.featOkTst && ((TimeSpan_Type)jcasType).casFeat_spanStart == null)
      jcasType.jcas.throwFeatMissing("spanStart", "uk.gov.dstl.baleen.types.temporal.TimeSpan");
    return jcasType.ll_cas.ll_getLongValue(addr, ((TimeSpan_Type)jcasType).casFeatCode_spanStart);}
    
  /** setter for spanStart - sets The start of the time span, as a timestamp 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSpanStart(long v) {
    if (TimeSpan_Type.featOkTst && ((TimeSpan_Type)jcasType).casFeat_spanStart == null)
      jcasType.jcas.throwFeatMissing("spanStart", "uk.gov.dstl.baleen.types.temporal.TimeSpan");
    jcasType.ll_cas.ll_setLongValue(addr, ((TimeSpan_Type)jcasType).casFeatCode_spanStart, v);}    
   
    
  //*--------------*
  //* Feature: spanStop

  /** getter for spanStop - gets The end of the time span, as a timestamp
   * @generated
   * @return value of the feature 
   */
  public long getSpanStop() {
    if (TimeSpan_Type.featOkTst && ((TimeSpan_Type)jcasType).casFeat_spanStop == null)
      jcasType.jcas.throwFeatMissing("spanStop", "uk.gov.dstl.baleen.types.temporal.TimeSpan");
    return jcasType.ll_cas.ll_getLongValue(addr, ((TimeSpan_Type)jcasType).casFeatCode_spanStop);}
    
  /** setter for spanStop - sets The end of the time span, as a timestamp 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSpanStop(long v) {
    if (TimeSpan_Type.featOkTst && ((TimeSpan_Type)jcasType).casFeat_spanStop == null)
      jcasType.jcas.throwFeatMissing("spanStop", "uk.gov.dstl.baleen.types.temporal.TimeSpan");
    jcasType.ll_cas.ll_setLongValue(addr, ((TimeSpan_Type)jcasType).casFeatCode_spanStop, v);}    
  }

    