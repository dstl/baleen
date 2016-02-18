

/* First created by JCasGen Wed Jan 21 11:21:05 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types.common;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.semantic.Entity;


/** Type to annotate references to quantities within text
 * Updated by JCasGen Fri Feb 05 14:49:26 GMT 2016
 * XML source: C:/co/git/CCD-DE/RMR/baleen/baleen/baleen-uima/src/main/resources/types/common_type_system.xml
 * @generated */
public class Quantity extends Entity {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Quantity.class);
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
  protected Quantity() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Quantity(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Quantity(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Quantity(JCas jcas, int begin, int end) {
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
  //* Feature: normalizedUnit

  /** getter for normalizedUnit - gets The unit of the normalized quantity
   * @generated
   * @return value of the feature 
   */
  public String getNormalizedUnit() {
    if (Quantity_Type.featOkTst && ((Quantity_Type)jcasType).casFeat_normalizedUnit == null)
      jcasType.jcas.throwFeatMissing("normalizedUnit", "uk.gov.dstl.baleen.types.common.Quantity");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Quantity_Type)jcasType).casFeatCode_normalizedUnit);}
    
  /** setter for normalizedUnit - sets The unit of the normalized quantity 
   * @generated
   * @param v value to set into the feature 
   */
  public void setNormalizedUnit(String v) {
    if (Quantity_Type.featOkTst && ((Quantity_Type)jcasType).casFeat_normalizedUnit == null)
      jcasType.jcas.throwFeatMissing("normalizedUnit", "uk.gov.dstl.baleen.types.common.Quantity");
    jcasType.ll_cas.ll_setStringValue(addr, ((Quantity_Type)jcasType).casFeatCode_normalizedUnit, v);}    
   
    
  //*--------------*
  //* Feature: normalizedQuantity

  /** getter for normalizedQuantity - gets The normalized quantity
   * @generated
   * @return value of the feature 
   */
  public double getNormalizedQuantity() {
    if (Quantity_Type.featOkTst && ((Quantity_Type)jcasType).casFeat_normalizedQuantity == null)
      jcasType.jcas.throwFeatMissing("normalizedQuantity", "uk.gov.dstl.baleen.types.common.Quantity");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Quantity_Type)jcasType).casFeatCode_normalizedQuantity);}
    
  /** setter for normalizedQuantity - sets The normalized quantity 
   * @generated
   * @param v value to set into the feature 
   */
  public void setNormalizedQuantity(double v) {
    if (Quantity_Type.featOkTst && ((Quantity_Type)jcasType).casFeat_normalizedQuantity == null)
      jcasType.jcas.throwFeatMissing("normalizedQuantity", "uk.gov.dstl.baleen.types.common.Quantity");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Quantity_Type)jcasType).casFeatCode_normalizedQuantity, v);}    
   
    
  //*--------------*
  //* Feature: unit

  /** getter for unit - gets The unit of the raw quantity
   * @generated
   * @return value of the feature 
   */
  public String getUnit() {
    if (Quantity_Type.featOkTst && ((Quantity_Type)jcasType).casFeat_unit == null)
      jcasType.jcas.throwFeatMissing("unit", "uk.gov.dstl.baleen.types.common.Quantity");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Quantity_Type)jcasType).casFeatCode_unit);}
    
  /** setter for unit - sets The unit of the raw quantity 
   * @generated
   * @param v value to set into the feature 
   */
  public void setUnit(String v) {
    if (Quantity_Type.featOkTst && ((Quantity_Type)jcasType).casFeat_unit == null)
      jcasType.jcas.throwFeatMissing("unit", "uk.gov.dstl.baleen.types.common.Quantity");
    jcasType.ll_cas.ll_setStringValue(addr, ((Quantity_Type)jcasType).casFeatCode_unit, v);}    
   
    
  //*--------------*
  //* Feature: quantity

  /** getter for quantity - gets The raw quantity
   * @generated
   * @return value of the feature 
   */
  public double getQuantity() {
    if (Quantity_Type.featOkTst && ((Quantity_Type)jcasType).casFeat_quantity == null)
      jcasType.jcas.throwFeatMissing("quantity", "uk.gov.dstl.baleen.types.common.Quantity");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Quantity_Type)jcasType).casFeatCode_quantity);}
    
  /** setter for quantity - sets The raw quantity 
   * @generated
   * @param v value to set into the feature 
   */
  public void setQuantity(double v) {
    if (Quantity_Type.featOkTst && ((Quantity_Type)jcasType).casFeat_quantity == null)
      jcasType.jcas.throwFeatMissing("quantity", "uk.gov.dstl.baleen.types.common.Quantity");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Quantity_Type)jcasType).casFeatCode_quantity, v);}    
  }

    