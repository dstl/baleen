//Dstl (c) Crown Copyright 2017


/* First created by JCasGen Tue Apr 12 12:06:25 BST 2016 */
package uk.gov.dstl.baleen.types.language;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.BaleenAnnotation;


/** A word which acts as a relation in a sentence (eg 'saw' in the 'John saw the car').
 * Updated by JCasGen Wed Apr 13 13:23:16 BST 2016
 * XML source: H:/git/TextProcessing/core/baleen/baleen-uima/src/main/resources/types/common_type_system.xml
 * @generated */
public class Interaction extends BaleenAnnotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Interaction.class);
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
  protected Interaction() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Interaction(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Interaction(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Interaction(JCas jcas, int begin, int end) {
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
  //* Feature: value

  /** getter for value - gets The value of the relationship, typically the spanning text within the sentence or the .
   * @generated
   * @return value of the feature 
   */
  public String getValue() {
    if (Interaction_Type.featOkTst && ((Interaction_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.language.Interaction");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Interaction_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets The value of the relationship, typically the spanning text within the sentence or the . 
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue(String v) {
    if (Interaction_Type.featOkTst && ((Interaction_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.language.Interaction");
    jcasType.ll_cas.ll_setStringValue(addr, ((Interaction_Type)jcasType).casFeatCode_value, v);}    
   
    
  //*--------------*
  //* Feature: relationshipType

  /** getter for relationshipType - gets Denotes the semantic type of the relationship between entities.
   * @generated
   * @return value of the feature 
   */
  public String getRelationshipType() {
    if (Interaction_Type.featOkTst && ((Interaction_Type)jcasType).casFeat_relationshipType == null)
      jcasType.jcas.throwFeatMissing("relationshipType", "uk.gov.dstl.baleen.types.language.Interaction");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Interaction_Type)jcasType).casFeatCode_relationshipType);}
    
  /** setter for relationshipType - sets Denotes the semantic type of the relationship between entities. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setRelationshipType(String v) {
    if (Interaction_Type.featOkTst && ((Interaction_Type)jcasType).casFeat_relationshipType == null)
      jcasType.jcas.throwFeatMissing("relationshipType", "uk.gov.dstl.baleen.types.language.Interaction");
    jcasType.ll_cas.ll_setStringValue(addr, ((Interaction_Type)jcasType).casFeatCode_relationshipType, v);}    
   
    
  //*--------------*
  //* Feature: relationSubType

  /** getter for relationSubType - gets Used to record any sub-type information for the relation, for example the sub-relations defined within the ACE dataset.
   * @generated
   * @return value of the feature 
   */
  public String getRelationSubType() {
    if (Interaction_Type.featOkTst && ((Interaction_Type)jcasType).casFeat_relationSubType == null)
      jcasType.jcas.throwFeatMissing("relationSubType", "uk.gov.dstl.baleen.types.language.Interaction");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Interaction_Type)jcasType).casFeatCode_relationSubType);}
    
  /** setter for relationSubType - sets Used to record any sub-type information for the relation, for example the sub-relations defined within the ACE dataset. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setRelationSubType(String v) {
    if (Interaction_Type.featOkTst && ((Interaction_Type)jcasType).casFeat_relationSubType == null)
      jcasType.jcas.throwFeatMissing("relationSubType", "uk.gov.dstl.baleen.types.language.Interaction");
    jcasType.ll_cas.ll_setStringValue(addr, ((Interaction_Type)jcasType).casFeatCode_relationSubType, v);}    
  }

    