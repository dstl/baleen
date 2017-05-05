//Dstl (c) Crown Copyright 2017


/* First created by JCasGen Mon Nov 28 18:28:46 GMT 2016 */
package uk.gov.dstl.baleen.types.structure;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** An unordered component of a document such as an unordered list.
 * Updated by JCasGen Thu Dec 22 22:42:18 CET 2016
 * XML source: /Users/chrisflatley/Projects/railroad/baleen/baleen/baleen-uima/src/main/resources/types/structure_type_system.xml
 * @generated */
public class Unordered extends Structure {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Unordered.class);
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
  protected Unordered() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Unordered(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Unordered(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Unordered(JCas jcas, int begin, int end) {
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
  //* Feature: level

  /** getter for level - gets The level of the item.
   * @generated
   * @return value of the feature 
   */
  public int getLevel() {
    if (Unordered_Type.featOkTst && ((Unordered_Type)jcasType).casFeat_level == null)
      jcasType.jcas.throwFeatMissing("level", "uk.gov.dstl.baleen.types.structure.Unordered");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Unordered_Type)jcasType).casFeatCode_level);}
    
  /** setter for level - sets The level of the item. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setLevel(int v) {
    if (Unordered_Type.featOkTst && ((Unordered_Type)jcasType).casFeat_level == null)
      jcasType.jcas.throwFeatMissing("level", "uk.gov.dstl.baleen.types.structure.Unordered");
    jcasType.ll_cas.ll_setIntValue(addr, ((Unordered_Type)jcasType).casFeatCode_level, v);}    
  }

    