

/* First created by JCasGen Wed Jan 21 11:22:53 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types.semantic;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** A reference to a place, country, administrative region or geo-political entity within the source document. This is a general purpose type that is extended in "geo" types.
 * Updated by JCasGen Fri Feb 05 14:54:30 GMT 2016
 * XML source: C:/co/git/CCD-DE/RMR/baleen/baleen/baleen-uima/src/main/resources/types/semantic_type_system.xml
 * @generated */
public class Location extends Entity {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Location.class);
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
  protected Location() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Location(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Location(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Location(JCas jcas, int begin, int end) {
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
  //* Feature: geoJson

  /** getter for geoJson - gets A strnig representation of geoJson format represention of geographic information associated with the location, including where possible coordinate(s).
   * @generated
   * @return value of the feature 
   */
  public String getGeoJson() {
    if (Location_Type.featOkTst && ((Location_Type)jcasType).casFeat_geoJson == null)
      jcasType.jcas.throwFeatMissing("geoJson", "uk.gov.dstl.baleen.types.semantic.Location");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Location_Type)jcasType).casFeatCode_geoJson);}
    
  /** setter for geoJson - sets A strnig representation of geoJson format represention of geographic information associated with the location, including where possible coordinate(s). 
   * @generated
   * @param v value to set into the feature 
   */
  public void setGeoJson(String v) {
    if (Location_Type.featOkTst && ((Location_Type)jcasType).casFeat_geoJson == null)
      jcasType.jcas.throwFeatMissing("geoJson", "uk.gov.dstl.baleen.types.semantic.Location");
    jcasType.ll_cas.ll_setStringValue(addr, ((Location_Type)jcasType).casFeatCode_geoJson, v);}    
  }

    