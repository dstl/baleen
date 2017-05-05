//Dstl (c) Crown Copyright 2017


/* First created by JCasGen Thu Oct 13 13:31:25 BST 2016 */
package uk.gov.dstl.baleen.types.structure;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** A figure (or embedded media).
 * Updated by JCasGen Thu Apr 20 16:06:08 BST 2017
 * XML source: /Users/stuarthendren/git/tenode/baleen/baleen/baleen-uima/src/main/resources/types/structure_type_system.xml
 * @generated */
public class Figure extends Structure {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Figure.class);
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
  protected Figure() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Figure(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Figure(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Figure(JCas jcas, int begin, int end) {
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
  //* Feature: target

  /** getter for target - gets The target of the media if available. (eg the src of an img tag).
   * @generated
   * @return value of the feature 
   */
  public String getTarget() {
    if (Figure_Type.featOkTst && ((Figure_Type)jcasType).casFeat_target == null)
      jcasType.jcas.throwFeatMissing("target", "uk.gov.dstl.baleen.types.structure.Figure");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Figure_Type)jcasType).casFeatCode_target);}
    
  /** setter for target - sets The target of the media if available. (eg the src of an img tag). 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTarget(String v) {
    if (Figure_Type.featOkTst && ((Figure_Type)jcasType).casFeat_target == null)
      jcasType.jcas.throwFeatMissing("target", "uk.gov.dstl.baleen.types.structure.Figure");
    jcasType.ll_cas.ll_setStringValue(addr, ((Figure_Type)jcasType).casFeatCode_target, v);}    
  }

    