//Dstl (c) Crown Copyright 2017


/* First created by JCasGen Thu Oct 13 13:09:13 BST 2016 */
package uk.gov.dstl.baleen.types.structure;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.Base;


/** A base type for all Structure types.
 * Updated by JCasGen Thu Apr 20 16:06:09 BST 2017
 * XML source: /Users/stuarthendren/git/tenode/baleen/baleen/baleen-uima/src/main/resources/types/structure_type_system.xml
 * @generated */
public class Structure extends Base {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Structure.class);
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
  protected Structure() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Structure(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Structure(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Structure(JCas jcas, int begin, int end) {
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
  //* Feature: depth

  /** getter for depth - gets The depth of the structural component.
   * @generated
   * @return value of the feature 
   */
  public int getDepth() {
    if (Structure_Type.featOkTst && ((Structure_Type)jcasType).casFeat_depth == null)
      jcasType.jcas.throwFeatMissing("depth", "uk.gov.dstl.baleen.types.structure.Structure");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Structure_Type)jcasType).casFeatCode_depth);}
    
  /** setter for depth - sets The depth of the structural component. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDepth(int v) {
    if (Structure_Type.featOkTst && ((Structure_Type)jcasType).casFeat_depth == null)
      jcasType.jcas.throwFeatMissing("depth", "uk.gov.dstl.baleen.types.structure.Structure");
    jcasType.ll_cas.ll_setIntValue(addr, ((Structure_Type)jcasType).casFeatCode_depth, v);}    
   
    
  //*--------------*
  //* Feature: elementClass

  /** getter for elementClass - gets A holder for further class information, say a more specific html class or a defined word style.
   * @generated
   * @return value of the feature 
   */
  public String getElementClass() {
    if (Structure_Type.featOkTst && ((Structure_Type)jcasType).casFeat_elementClass == null)
      jcasType.jcas.throwFeatMissing("elementClass", "uk.gov.dstl.baleen.types.structure.Structure");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Structure_Type)jcasType).casFeatCode_elementClass);}
    
  /** setter for elementClass - sets A holder for further class information, say a more specific html class or a defined word style. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setElementClass(String v) {
    if (Structure_Type.featOkTst && ((Structure_Type)jcasType).casFeat_elementClass == null)
      jcasType.jcas.throwFeatMissing("elementClass", "uk.gov.dstl.baleen.types.structure.Structure");
    jcasType.ll_cas.ll_setStringValue(addr, ((Structure_Type)jcasType).casFeatCode_elementClass, v);}    
   
    
  //*--------------*
  //* Feature: elementId

  /** getter for elementId - gets A holder for an id, if defined from the format.
   * @generated
   * @return value of the feature 
   */
  public String getElementId() {
    if (Structure_Type.featOkTst && ((Structure_Type)jcasType).casFeat_elementId == null)
      jcasType.jcas.throwFeatMissing("elementId", "uk.gov.dstl.baleen.types.structure.Structure");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Structure_Type)jcasType).casFeatCode_elementId);}
    
  /** setter for elementId - sets A holder for an id, if defined from the format. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setElementId(String v) {
    if (Structure_Type.featOkTst && ((Structure_Type)jcasType).casFeat_elementId == null)
      jcasType.jcas.throwFeatMissing("elementId", "uk.gov.dstl.baleen.types.structure.Structure");
    jcasType.ll_cas.ll_setStringValue(addr, ((Structure_Type)jcasType).casFeatCode_elementId, v);}    
  }

    