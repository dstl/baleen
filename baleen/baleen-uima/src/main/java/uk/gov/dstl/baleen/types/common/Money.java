

/* First created by JCasGen Wed Jan 21 11:21:05 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types.common;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.semantic.Entity;


/** Specific amount of some current mentioned within the document.
 * Updated by JCasGen Fri Feb 05 14:49:26 GMT 2016
 * XML source: C:/co/git/CCD-DE/RMR/baleen/baleen/baleen-uima/src/main/resources/types/common_type_system.xml
 * @generated */
public class Money extends Entity {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Money.class);
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
  protected Money() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Money(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Money(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Money(JCas jcas, int begin, int end) {
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
  //* Feature: amount

  /** getter for amount - gets Numeric value of amount of money mentioned in document.
   * @generated
   * @return value of the feature 
   */
  public double getAmount() {
    if (Money_Type.featOkTst && ((Money_Type)jcasType).casFeat_amount == null)
      jcasType.jcas.throwFeatMissing("amount", "uk.gov.dstl.baleen.types.common.Money");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Money_Type)jcasType).casFeatCode_amount);}
    
  /** setter for amount - sets Numeric value of amount of money mentioned in document. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAmount(double v) {
    if (Money_Type.featOkTst && ((Money_Type)jcasType).casFeat_amount == null)
      jcasType.jcas.throwFeatMissing("amount", "uk.gov.dstl.baleen.types.common.Money");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Money_Type)jcasType).casFeatCode_amount, v);}    
   
    
  //*--------------*
  //* Feature: currency

  /** getter for currency - gets String value of the currency denomination the money amount is specified in.
   * @generated
   * @return value of the feature 
   */
  public String getCurrency() {
    if (Money_Type.featOkTst && ((Money_Type)jcasType).casFeat_currency == null)
      jcasType.jcas.throwFeatMissing("currency", "uk.gov.dstl.baleen.types.common.Money");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Money_Type)jcasType).casFeatCode_currency);}
    
  /** setter for currency - sets String value of the currency denomination the money amount is specified in. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCurrency(String v) {
    if (Money_Type.featOkTst && ((Money_Type)jcasType).casFeat_currency == null)
      jcasType.jcas.throwFeatMissing("currency", "uk.gov.dstl.baleen.types.common.Money");
    jcasType.ll_cas.ll_setStringValue(addr, ((Money_Type)jcasType).casFeatCode_currency, v);}    
  }

    