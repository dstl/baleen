

/* First created by JCasGen Wed Jan 21 11:36:30 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types.temporal;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.semantic.Temporal;


/** A combination of a DateType and Time which are part of the same reference, specifying a time on a specific date.
 * Updated by JCasGen Tue Apr 12 12:07:25 BST 2016
 * XML source: H:/git/TextProcessing/core/baleen/baleen-uima/src/main/resources/types/temporal_type_system.xml
 * @generated */
public class DateTime extends Temporal {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(DateTime.class);
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
  protected DateTime() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public DateTime(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public DateTime(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public DateTime(JCas jcas, int begin, int end) {
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
  //* Feature: parsedValue

  /** getter for parsedValue - gets A Long value representation of a Java Date object, defined from the DateTime value.
   * @generated
   * @return value of the feature 
   */
  public long getParsedValue() {
    if (DateTime_Type.featOkTst && ((DateTime_Type)jcasType).casFeat_parsedValue == null)
      jcasType.jcas.throwFeatMissing("parsedValue", "uk.gov.dstl.baleen.types.temporal.DateTime");
    return jcasType.ll_cas.ll_getLongValue(addr, ((DateTime_Type)jcasType).casFeatCode_parsedValue);}
    
  /** setter for parsedValue - sets A Long value representation of a Java Date object, defined from the DateTime value. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setParsedValue(long v) {
    if (DateTime_Type.featOkTst && ((DateTime_Type)jcasType).casFeat_parsedValue == null)
      jcasType.jcas.throwFeatMissing("parsedValue", "uk.gov.dstl.baleen.types.temporal.DateTime");
    jcasType.ll_cas.ll_setLongValue(addr, ((DateTime_Type)jcasType).casFeatCode_parsedValue, v);}    
  }

    