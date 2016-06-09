

/* First created by JCasGen Tue Apr 12 12:06:19 BST 2016 */
package uk.gov.dstl.baleen.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** A pointer to another annotation in the same document. Designed for general use (eg temporary working inside annotator) rather than having some specific semantic meaning (eg like coreference).
 * Updated by JCasGen Wed Apr 13 13:23:15 BST 2016
 * XML source: H:/git/TextProcessing/core/baleen/baleen-uima/src/main/resources/types/common_type_system.xml
 * @generated */
public class Pointer extends BaleenAnnotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Pointer.class);
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
  protected Pointer() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Pointer(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Pointer(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Pointer(JCas jcas, int begin, int end) {
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

  /** getter for target - gets At target annotation to which this pointer refers.
   * @generated
   * @return value of the feature 
   */
  public BaleenAnnotation getTarget() {
    if (Pointer_Type.featOkTst && ((Pointer_Type)jcasType).casFeat_target == null)
      jcasType.jcas.throwFeatMissing("target", "uk.gov.dstl.baleen.types.Pointer");
    return (BaleenAnnotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Pointer_Type)jcasType).casFeatCode_target)));}
    
  /** setter for target - sets At target annotation to which this pointer refers. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTarget(BaleenAnnotation v) {
    if (Pointer_Type.featOkTst && ((Pointer_Type)jcasType).casFeat_target == null)
      jcasType.jcas.throwFeatMissing("target", "uk.gov.dstl.baleen.types.Pointer");
    jcasType.ll_cas.ll_setRefValue(addr, ((Pointer_Type)jcasType).casFeatCode_target, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: targetId

  /** getter for targetId - gets Used to reference a (Baleen) annotation by its internal id. Useful for if you are pointing to a different jCas (in which case you can't use target)
   * @generated
   * @return value of the feature 
   */
  public long getTargetId() {
    if (Pointer_Type.featOkTst && ((Pointer_Type)jcasType).casFeat_targetId == null)
      jcasType.jcas.throwFeatMissing("targetId", "uk.gov.dstl.baleen.types.Pointer");
    return jcasType.ll_cas.ll_getLongValue(addr, ((Pointer_Type)jcasType).casFeatCode_targetId);}
    
  /** setter for targetId - sets Used to reference a (Baleen) annotation by its internal id. Useful for if you are pointing to a different jCas (in which case you can't use target) 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTargetId(long v) {
    if (Pointer_Type.featOkTst && ((Pointer_Type)jcasType).casFeat_targetId == null)
      jcasType.jcas.throwFeatMissing("targetId", "uk.gov.dstl.baleen.types.Pointer");
    jcasType.ll_cas.ll_setLongValue(addr, ((Pointer_Type)jcasType).casFeatCode_targetId, v);}    
  }

    