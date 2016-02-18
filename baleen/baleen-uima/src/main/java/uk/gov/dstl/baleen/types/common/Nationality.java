

/* First created by JCasGen Wed Jan 21 11:21:05 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types.common;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.semantic.Entity;


/** Nationality denonym (e.g. French, British, Spanish)
 * Updated by JCasGen Fri Feb 05 14:49:26 GMT 2016
 * XML source: C:/co/git/CCD-DE/RMR/baleen/baleen/baleen-uima/src/main/resources/types/common_type_system.xml
 * @generated */
public class Nationality extends Entity {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Nationality.class);
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
  protected Nationality() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Nationality(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Nationality(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Nationality(JCas jcas, int begin, int end) {
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
  //* Feature: countryCode

  /** getter for countryCode - gets The country code associated with this nationality, e.g. FRA for French
   * @generated
   * @return value of the feature 
   */
  public String getCountryCode() {
    if (Nationality_Type.featOkTst && ((Nationality_Type)jcasType).casFeat_countryCode == null)
      jcasType.jcas.throwFeatMissing("countryCode", "uk.gov.dstl.baleen.types.common.Nationality");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Nationality_Type)jcasType).casFeatCode_countryCode);}
    
  /** setter for countryCode - sets The country code associated with this nationality, e.g. FRA for French 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCountryCode(String v) {
    if (Nationality_Type.featOkTst && ((Nationality_Type)jcasType).casFeat_countryCode == null)
      jcasType.jcas.throwFeatMissing("countryCode", "uk.gov.dstl.baleen.types.common.Nationality");
    jcasType.ll_cas.ll_setStringValue(addr, ((Nationality_Type)jcasType).casFeatCode_countryCode, v);}    
  }

    