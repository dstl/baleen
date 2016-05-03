

/* First created by JCasGen Wed Jan 14 12:58:27 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types.semantic;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.core.history.Recordable;
import uk.gov.dstl.baleen.types.Base;


/** Type to represent named entities - values that are assigned a semantic type.
 * Updated by JCasGen Wed Apr 06 16:49:30 BST 2016
 * XML source: /home/baleen/provide-normalize-cleaners/baleen/baleen/baleen-uima/src/main/resources/types/semantic_type_system.xml
 * @generated */
public class Entity extends Base implements Recordable {
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Entity.class);
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
  protected Entity() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   */
  public Entity(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Entity(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
  */
  public Entity(JCas jcas, int begin, int end) {
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

  /** getter for value - gets A value which reflects the name of the entity. May or may not differ from underlying span from the document.
   * @generated
   * @return value of the feature 
   */
  public String getValue() {
    if (Entity_Type.featOkTst && ((Entity_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.semantic.Entity");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Entity_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets A value which reflects the name of the entity. May or may not differ from underlying span from the document. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue(String v) {
    if (Entity_Type.featOkTst && ((Entity_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.semantic.Entity");
    jcasType.ll_cas.ll_setStringValue(addr, ((Entity_Type)jcasType).casFeatCode_value, v);}    
   
    
  //*--------------*
  //* Feature: referent

  /** getter for referent - gets Can be used to link a corefence to an entity to another (presuambly more definitive) mention of the same entity elsewhere in the text.
   * @generated
   * @return value of the feature 
   */
  public ReferenceTarget getReferent() {
    if (Entity_Type.featOkTst && ((Entity_Type)jcasType).casFeat_referent == null)
      jcasType.jcas.throwFeatMissing("referent", "uk.gov.dstl.baleen.types.semantic.Entity");
    return (ReferenceTarget)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Entity_Type)jcasType).casFeatCode_referent)));}
    
  /** setter for referent - sets Can be used to link a corefence to an entity to another (presuambly more definitive) mention of the same entity elsewhere in the text. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setReferent(ReferenceTarget v) {
    if (Entity_Type.featOkTst && ((Entity_Type)jcasType).casFeat_referent == null)
      jcasType.jcas.throwFeatMissing("referent", "uk.gov.dstl.baleen.types.semantic.Entity");
    jcasType.ll_cas.ll_setRefValue(addr, ((Entity_Type)jcasType).casFeatCode_referent, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: isNormalised

  /** getter for isNormalised - gets Marks the entity value as having been normalised from the original value
   * @generated
   * @return value of the feature 
   */
  public boolean getIsNormalised() {
    if (Entity_Type.featOkTst && ((Entity_Type)jcasType).casFeat_isNormalised == null)
      jcasType.jcas.throwFeatMissing("isNormalised", "uk.gov.dstl.baleen.types.semantic.Entity");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((Entity_Type)jcasType).casFeatCode_isNormalised);}
    
  /** setter for isNormalised - sets Marks the entity value as having been normalised from the original value 
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsNormalised(boolean v) {
    if (Entity_Type.featOkTst && ((Entity_Type)jcasType).casFeat_isNormalised == null)
      jcasType.jcas.throwFeatMissing("isNormalised", "uk.gov.dstl.baleen.types.semantic.Entity");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((Entity_Type)jcasType).casFeatCode_isNormalised, v);}    
   
    
  //*--------------*
  //* Feature: subType

  /** getter for subType - gets String identifying sub type of entity.
   * @generated
   * @return value of the feature 
   */
  public String getSubType() {
    if (Entity_Type.featOkTst && ((Entity_Type)jcasType).casFeat_subType == null)
      jcasType.jcas.throwFeatMissing("subType", "uk.gov.dstl.baleen.types.semantic.Entity");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Entity_Type)jcasType).casFeatCode_subType);}
    
  /** setter for subType - sets String identifying sub type of entity. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSubType(String v) {
    if (Entity_Type.featOkTst && ((Entity_Type)jcasType).casFeat_subType == null)
      jcasType.jcas.throwFeatMissing("subType", "uk.gov.dstl.baleen.types.semantic.Entity");
    jcasType.ll_cas.ll_setStringValue(addr, ((Entity_Type)jcasType).casFeatCode_subType, v);}    
  }

