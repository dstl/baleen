

/* First created by JCasGen Wed Jan 21 11:22:53 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types.semantic;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** An temporal interaction of interest, covering political, organisational, miltiary, criminal or social interactions mentioned within the document.
 * Updated by JCasGen Fri Feb 05 14:54:30 GMT 2016
 * XML source: C:/co/git/CCD-DE/RMR/baleen/baleen/baleen-uima/src/main/resources/types/semantic_type_system.xml
 * @generated */
public class Event extends Entity {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Event.class);
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
  protected Event() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Event(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Event(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Event(JCas jcas, int begin, int end) {
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
  //* Feature: description

  /** getter for description - gets A description of the event
   * @generated
   * @return value of the feature 
   */
  public String getDescription() {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_description == null)
      jcasType.jcas.throwFeatMissing("description", "uk.gov.dstl.baleen.types.semantic.Event");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Event_Type)jcasType).casFeatCode_description);}
    
  /** setter for description - sets A description of the event 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDescription(String v) {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_description == null)
      jcasType.jcas.throwFeatMissing("description", "uk.gov.dstl.baleen.types.semantic.Event");
    jcasType.ll_cas.ll_setStringValue(addr, ((Event_Type)jcasType).casFeatCode_description, v);}    
   
    
  //*--------------*
  //* Feature: location

  /** getter for location - gets The location of the event
   * @generated
   * @return value of the feature 
   */
  public Location getLocation() {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_location == null)
      jcasType.jcas.throwFeatMissing("location", "uk.gov.dstl.baleen.types.semantic.Event");
    return (Location)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type)jcasType).casFeatCode_location)));}
    
  /** setter for location - sets The location of the event 
   * @generated
   * @param v value to set into the feature 
   */
  public void setLocation(Location v) {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_location == null)
      jcasType.jcas.throwFeatMissing("location", "uk.gov.dstl.baleen.types.semantic.Event");
    jcasType.ll_cas.ll_setRefValue(addr, ((Event_Type)jcasType).casFeatCode_location, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: occurrence

  /** getter for occurrence - gets The time-based information relating to the event.
   * @generated
   * @return value of the feature 
   */
  public Temporal getOccurrence() {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_occurrence == null)
      jcasType.jcas.throwFeatMissing("occurrence", "uk.gov.dstl.baleen.types.semantic.Event");
    return (Temporal)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type)jcasType).casFeatCode_occurrence)));}
    
  /** setter for occurrence - sets The time-based information relating to the event. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setOccurrence(Temporal v) {
    if (Event_Type.featOkTst && ((Event_Type)jcasType).casFeat_occurrence == null)
      jcasType.jcas.throwFeatMissing("occurrence", "uk.gov.dstl.baleen.types.semantic.Event");
    jcasType.ll_cas.ll_setRefValue(addr, ((Event_Type)jcasType).casFeatCode_occurrence, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    