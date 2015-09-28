

/* First created by JCasGen Wed Jan 14 12:58:18 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types.language;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.Base;


/** An annotation that allows a document to be arbitrarily divided up into sections (articles, chapters, comments, ...).
 * Updated by JCasGen Wed Jan 14 12:58:18 GMT 2015
 * XML source: H:/git/core/baleen/baleen-uima/src/main/resources/language_type_system.xml
 * @generated */
public class Section extends Base {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Section.class);
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
  protected Section() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Section(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Section(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Section(JCas jcas, int begin, int end) {
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
  //* Feature: label

  /** getter for label - gets A label that desribes what the section contains.
   * @generated
   * @return value of the feature 
   */
  public String getLabel() {
    if (Section_Type.featOkTst && ((Section_Type)jcasType).casFeat_label == null)
      jcasType.jcas.throwFeatMissing("label", "uk.gov.dstl.baleen.types.language.Section");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Section_Type)jcasType).casFeatCode_label);}
    
  /** setter for label - sets A label that desribes what the section contains. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setLabel(String v) {
    if (Section_Type.featOkTst && ((Section_Type)jcasType).casFeat_label == null)
      jcasType.jcas.throwFeatMissing("label", "uk.gov.dstl.baleen.types.language.Section");
    jcasType.ll_cas.ll_setStringValue(addr, ((Section_Type)jcasType).casFeatCode_label, v);}    
  }

    