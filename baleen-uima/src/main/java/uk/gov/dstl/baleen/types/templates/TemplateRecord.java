//Dstl (c) Crown Copyright 2017


/* First created by JCasGen Tue Apr 18 12:23:04 BST 2017 */
package uk.gov.dstl.baleen.types.templates;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.Base;


/** A covering annotation that marks the extent of a record within an annotated document.
 * Updated by JCasGen Tue Apr 18 12:23:04 BST 2017
 * XML source: /Users/stuarthendren/git/tenode/baleen/baleen/baleen-uima/src/main/resources/types/template_type_system.xml
 * @generated */
public class TemplateRecord extends Base {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TemplateRecord.class);
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
  protected TemplateRecord() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public TemplateRecord(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public TemplateRecord(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public TemplateRecord(JCas jcas, int begin, int end) {
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
  //* Feature: name

  /** getter for name - gets The name of the record, eg Address
   * @generated
   * @return value of the feature 
   */
  public String getName() {
    if (TemplateRecord_Type.featOkTst && ((TemplateRecord_Type)jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing("name", "uk.gov.dstl.baleen.types.templates.TemplateRecord");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TemplateRecord_Type)jcasType).casFeatCode_name);}
    
  /** setter for name - sets The name of the record, eg Address 
   * @generated
   * @param v value to set into the feature 
   */
  public void setName(String v) {
    if (TemplateRecord_Type.featOkTst && ((TemplateRecord_Type)jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing("name", "uk.gov.dstl.baleen.types.templates.TemplateRecord");
    jcasType.ll_cas.ll_setStringValue(addr, ((TemplateRecord_Type)jcasType).casFeatCode_name, v);}    
   
    
  //*--------------*
  //* Feature: source

  /** getter for source - gets The source of the record definition
   * @generated
   * @return value of the feature 
   */
  public String getSource() {
    if (TemplateRecord_Type.featOkTst && ((TemplateRecord_Type)jcasType).casFeat_source == null)
      jcasType.jcas.throwFeatMissing("source", "uk.gov.dstl.baleen.types.templates.TemplateRecord");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TemplateRecord_Type)jcasType).casFeatCode_source);}
    
  /** setter for source - sets The source of the record definition 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSource(String v) {
    if (TemplateRecord_Type.featOkTst && ((TemplateRecord_Type)jcasType).casFeat_source == null)
      jcasType.jcas.throwFeatMissing("source", "uk.gov.dstl.baleen.types.templates.TemplateRecord");
    jcasType.ll_cas.ll_setStringValue(addr, ((TemplateRecord_Type)jcasType).casFeatCode_source, v);}    
  }

    