

/* First created by JCasGen Tue Feb 03 15:25:57 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types.common;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.semantic.Entity;


/** Specific vehicle or vessel
 * Updated by JCasGen Fri Feb 05 14:49:26 GMT 2016
 * XML source: C:/co/git/CCD-DE/RMR/baleen/baleen/baleen-uima/src/main/resources/types/common_type_system.xml
 * @generated */
public class Vehicle extends Entity {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Vehicle.class);
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
  protected Vehicle() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Vehicle(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Vehicle(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Vehicle(JCas jcas, int begin, int end) {
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
  //* Feature: vehicleIdentifier

  /** getter for vehicleIdentifier - gets An identifying name or number for the vehicle
   * @generated
   * @return value of the feature 
   */
  public String getVehicleIdentifier() {
    if (Vehicle_Type.featOkTst && ((Vehicle_Type)jcasType).casFeat_vehicleIdentifier == null)
      jcasType.jcas.throwFeatMissing("vehicleIdentifier", "uk.gov.dstl.baleen.types.common.Vehicle");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Vehicle_Type)jcasType).casFeatCode_vehicleIdentifier);}
    
  /** setter for vehicleIdentifier - sets An identifying name or number for the vehicle 
   * @generated
   * @param v value to set into the feature 
   */
  public void setVehicleIdentifier(String v) {
    if (Vehicle_Type.featOkTst && ((Vehicle_Type)jcasType).casFeat_vehicleIdentifier == null)
      jcasType.jcas.throwFeatMissing("vehicleIdentifier", "uk.gov.dstl.baleen.types.common.Vehicle");
    jcasType.ll_cas.ll_setStringValue(addr, ((Vehicle_Type)jcasType).casFeatCode_vehicleIdentifier, v);}    
  }

    