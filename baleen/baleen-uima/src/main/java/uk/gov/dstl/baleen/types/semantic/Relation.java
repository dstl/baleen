

/* First created by JCasGen Wed Jan 14 12:58:27 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types.semantic;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.Base;


/** Records a relationship between named entities, explicitly mentioned within the source document.
 * Updated by JCasGen Fri Feb 05 14:54:31 GMT 2016
 * XML source: C:/co/git/CCD-DE/RMR/baleen/baleen/baleen-uima/src/main/resources/types/semantic_type_system.xml
 * @generated */
public class Relation extends Base {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Relation.class);
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
  protected Relation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Relation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Relation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Relation(JCas jcas, int begin, int end) {
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
  //* Feature: relationshipType

  /** getter for relationshipType - gets Denotes the semantic type of the relationship between entities. 

Currently based on the ACE2002 top-level relationships: {"AT" , "NEAR" , "PART" , "ROLE" , "SOCIAL"}
Additional relationship types that have been added: {"QUANTITY", "ALIAS"}
   * @generated
   * @return value of the feature 
   */
  public String getRelationshipType() {
    if (Relation_Type.featOkTst && ((Relation_Type)jcasType).casFeat_relationshipType == null)
      jcasType.jcas.throwFeatMissing("relationshipType", "uk.gov.dstl.baleen.types.semantic.Relation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Relation_Type)jcasType).casFeatCode_relationshipType);}
    
  /** setter for relationshipType - sets Denotes the semantic type of the relationship between entities. 

Currently based on the ACE2002 top-level relationships: {"AT" , "NEAR" , "PART" , "ROLE" , "SOCIAL"}
Additional relationship types that have been added: {"QUANTITY", "ALIAS"} 
   * @generated
   * @param v value to set into the feature 
   */
  public void setRelationshipType(String v) {
    if (Relation_Type.featOkTst && ((Relation_Type)jcasType).casFeat_relationshipType == null)
      jcasType.jcas.throwFeatMissing("relationshipType", "uk.gov.dstl.baleen.types.semantic.Relation");
    jcasType.ll_cas.ll_setStringValue(addr, ((Relation_Type)jcasType).casFeatCode_relationshipType, v);}    
   
    
  //*--------------*
  //* Feature: source

  /** getter for source - gets The source of the relationship (subject)
   * @generated
   * @return value of the feature 
   */
  public Entity getSource() {
    if (Relation_Type.featOkTst && ((Relation_Type)jcasType).casFeat_source == null)
      jcasType.jcas.throwFeatMissing("source", "uk.gov.dstl.baleen.types.semantic.Relation");
    return (Entity)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Relation_Type)jcasType).casFeatCode_source)));}
    
  /** setter for source - sets The source of the relationship (subject) 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSource(Entity v) {
    if (Relation_Type.featOkTst && ((Relation_Type)jcasType).casFeat_source == null)
      jcasType.jcas.throwFeatMissing("source", "uk.gov.dstl.baleen.types.semantic.Relation");
    jcasType.ll_cas.ll_setRefValue(addr, ((Relation_Type)jcasType).casFeatCode_source, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: target

  /** getter for target - gets The target of the relationship (object)
   * @generated
   * @return value of the feature 
   */
  public Entity getTarget() {
    if (Relation_Type.featOkTst && ((Relation_Type)jcasType).casFeat_target == null)
      jcasType.jcas.throwFeatMissing("target", "uk.gov.dstl.baleen.types.semantic.Relation");
    return (Entity)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Relation_Type)jcasType).casFeatCode_target)));}
    
  /** setter for target - sets The target of the relationship (object) 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTarget(Entity v) {
    if (Relation_Type.featOkTst && ((Relation_Type)jcasType).casFeat_target == null)
      jcasType.jcas.throwFeatMissing("target", "uk.gov.dstl.baleen.types.semantic.Relation");
    jcasType.ll_cas.ll_setRefValue(addr, ((Relation_Type)jcasType).casFeatCode_target, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: value

  /** getter for value - gets The value of the relationship, typically the spanning text within the sentence
   * @generated
   * @return value of the feature 
   */
  public String getValue() {
    if (Relation_Type.featOkTst && ((Relation_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.semantic.Relation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Relation_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets The value of the relationship, typically the spanning text within the sentence 
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue(String v) {
    if (Relation_Type.featOkTst && ((Relation_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.semantic.Relation");
    jcasType.ll_cas.ll_setStringValue(addr, ((Relation_Type)jcasType).casFeatCode_value, v);}    
   
    
  //*--------------*
  //* Feature: relationSubType

  /** getter for relationSubType - gets Used to record any sub-type information for the relation, for example the sub-relations defined within the ACE dataset.
   * @generated
   * @return value of the feature 
   */
  public String getRelationSubType() {
    if (Relation_Type.featOkTst && ((Relation_Type)jcasType).casFeat_relationSubType == null)
      jcasType.jcas.throwFeatMissing("relationSubType", "uk.gov.dstl.baleen.types.semantic.Relation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Relation_Type)jcasType).casFeatCode_relationSubType);}
    
  /** setter for relationSubType - sets Used to record any sub-type information for the relation, for example the sub-relations defined within the ACE dataset. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setRelationSubType(String v) {
    if (Relation_Type.featOkTst && ((Relation_Type)jcasType).casFeat_relationSubType == null)
      jcasType.jcas.throwFeatMissing("relationSubType", "uk.gov.dstl.baleen.types.semantic.Relation");
    jcasType.ll_cas.ll_setStringValue(addr, ((Relation_Type)jcasType).casFeatCode_relationSubType, v);}    
  }

    