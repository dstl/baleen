

/* First created by JCasGen Wed Jan 21 12:48:50 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types.geo;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.semantic.Location;


/** A well-formed coordinate value - MGRS or WGS84 DD or DMS cooridate system - explictly defined in source document.
 * Updated by JCasGen Fri Feb 05 14:51:56 GMT 2016
 * XML source: C:/co/git/CCD-DE/RMR/baleen/baleen/baleen-uima/src/main/resources/types/geo_type_system.xml
 * @generated */
public class Coordinate extends Location {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Coordinate.class);
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
  protected Coordinate() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Coordinate(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Coordinate(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Coordinate(JCas jcas, int begin, int end) {
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
  //* Feature: coordinateValue

  /** getter for coordinateValue - gets A normalised value for the coordinate.
   * @generated
   * @return value of the feature 
   */
  public String getCoordinateValue() {
    if (Coordinate_Type.featOkTst && ((Coordinate_Type)jcasType).casFeat_coordinateValue == null)
      jcasType.jcas.throwFeatMissing("coordinateValue", "uk.gov.dstl.baleen.types.geo.Coordinate");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Coordinate_Type)jcasType).casFeatCode_coordinateValue);}
    
  /** setter for coordinateValue - sets A normalised value for the coordinate. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCoordinateValue(String v) {
    if (Coordinate_Type.featOkTst && ((Coordinate_Type)jcasType).casFeat_coordinateValue == null)
      jcasType.jcas.throwFeatMissing("coordinateValue", "uk.gov.dstl.baleen.types.geo.Coordinate");
    jcasType.ll_cas.ll_setStringValue(addr, ((Coordinate_Type)jcasType).casFeatCode_coordinateValue, v);}    
  }

    