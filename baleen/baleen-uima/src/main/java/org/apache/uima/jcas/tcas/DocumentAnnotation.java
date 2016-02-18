

/* First created by JCasGen Thu Jan 22 12:33:22 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package org.apache.uima.jcas.tcas;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.StringArray;


import uk.gov.dstl.baleen.core.utils.IdentityUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;



/** Overriding the base DocumentAnntation to add additional features. The JCasGen code generated from this annotation replaces the default type in uima-document-annotation.jar (which should be removed from the classpath).
 * Updated by JCasGen Fri Feb 05 14:54:30 GMT 2016
 * XML source: C:/co/git/CCD-DE/RMR/baleen/baleen/baleen-uima/src/main/resources/types/semantic_type_system.xml
 * @generated */
public class DocumentAnnotation extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(DocumentAnnotation.class);
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
  protected DocumentAnnotation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public DocumentAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public DocumentAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public DocumentAnnotation(JCas jcas, int begin, int end) {
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
  //* Feature: language

  /** getter for language - gets 
   * @generated
   * @return value of the feature 
   */
  public String getLanguage() {
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_language == null)
      jcasType.jcas.throwFeatMissing("language", "uima.tcas.DocumentAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_language);}
    
  /** setter for language - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setLanguage(String v) {
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_language == null)
      jcasType.jcas.throwFeatMissing("language", "uima.tcas.DocumentAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_language, v);}    
   
    
  //*--------------*
  //* Feature: docType

  /** getter for docType - gets The document type
   * @generated
   * @return value of the feature 
   */
  public String getDocType() {
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_docType == null)
      jcasType.jcas.throwFeatMissing("docType", "uima.tcas.DocumentAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_docType);}
    
  /** setter for docType - sets The document type 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDocType(String v) {
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_docType == null)
      jcasType.jcas.throwFeatMissing("docType", "uima.tcas.DocumentAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_docType, v);}    
   
    
  //*--------------*
  //* Feature: sourceUri

  /** getter for sourceUri - gets A URI representing the source of the document
   * @generated
   * @return value of the feature 
   */
  public String getSourceUri() {
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_sourceUri == null)
      jcasType.jcas.throwFeatMissing("sourceUri", "uima.tcas.DocumentAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_sourceUri);}
    
  /** setter for sourceUri - sets A URI representing the source of the document 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSourceUri(String v) {
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_sourceUri == null)
      jcasType.jcas.throwFeatMissing("sourceUri", "uima.tcas.DocumentAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_sourceUri, v);}    
   
    
  //*--------------*
  //* Feature: timestamp

  /** getter for timestamp - gets The time at which the document was processed
   * @generated
   * @return value of the feature 
   */
  public long getTimestamp() {
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_timestamp == null)
      jcasType.jcas.throwFeatMissing("timestamp", "uima.tcas.DocumentAnnotation");
    return jcasType.ll_cas.ll_getLongValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_timestamp);}
    
  /** setter for timestamp - sets The time at which the document was processed 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTimestamp(long v) {
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_timestamp == null)
      jcasType.jcas.throwFeatMissing("timestamp", "uima.tcas.DocumentAnnotation");
    jcasType.ll_cas.ll_setLongValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_timestamp, v);}    
   
    
  //*--------------*
  //* Feature: documentClassification

  /** getter for documentClassification - gets The security classification of the document
   * @generated
   * @return value of the feature 
   */
  public String getDocumentClassification() {
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_documentClassification == null)
      jcasType.jcas.throwFeatMissing("documentClassification", "uima.tcas.DocumentAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_documentClassification);}
    
  /** setter for documentClassification - sets The security classification of the document 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDocumentClassification(String v) {
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_documentClassification == null)
      jcasType.jcas.throwFeatMissing("documentClassification", "uima.tcas.DocumentAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_documentClassification, v);}    
   
    
  //*--------------*
  //* Feature: documentCaveats

  /** getter for documentCaveats - gets An array of string values specifying handling caveats for the document.
   * @generated
   * @return value of the feature 
   */
  public StringArray getDocumentCaveats() {
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_documentCaveats == null)
      jcasType.jcas.throwFeatMissing("documentCaveats", "uima.tcas.DocumentAnnotation");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_documentCaveats)));}
    
  /** setter for documentCaveats - sets An array of string values specifying handling caveats for the document. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDocumentCaveats(StringArray v) {
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_documentCaveats == null)
      jcasType.jcas.throwFeatMissing("documentCaveats", "uima.tcas.DocumentAnnotation");
    jcasType.ll_cas.ll_setRefValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_documentCaveats, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for documentCaveats - gets an indexed value - An array of string values specifying handling caveats for the document.
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getDocumentCaveats(int i) {
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_documentCaveats == null)
      jcasType.jcas.throwFeatMissing("documentCaveats", "uima.tcas.DocumentAnnotation");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_documentCaveats), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_documentCaveats), i);}

  /** indexed setter for documentCaveats - sets an indexed value - An array of string values specifying handling caveats for the document.
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setDocumentCaveats(int i, String v) { 
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_documentCaveats == null)
      jcasType.jcas.throwFeatMissing("documentCaveats", "uima.tcas.DocumentAnnotation");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_documentCaveats), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_documentCaveats), i, v);}
   
    
  //*--------------*
  //* Feature: documentReleasability

  /** getter for documentReleasability - gets Array of country designators to which the document is releasable.
   * @generated
   * @return value of the feature 
   */
  public StringArray getDocumentReleasability() {
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_documentReleasability == null)
      jcasType.jcas.throwFeatMissing("documentReleasability", "uima.tcas.DocumentAnnotation");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_documentReleasability)));}
    
  /** setter for documentReleasability - sets Array of country designators to which the document is releasable. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDocumentReleasability(StringArray v) {
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_documentReleasability == null)
      jcasType.jcas.throwFeatMissing("documentReleasability", "uima.tcas.DocumentAnnotation");
    jcasType.ll_cas.ll_setRefValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_documentReleasability, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for documentReleasability - gets an indexed value - Array of country designators to which the document is releasable.
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getDocumentReleasability(int i) {
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_documentReleasability == null)
      jcasType.jcas.throwFeatMissing("documentReleasability", "uima.tcas.DocumentAnnotation");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_documentReleasability), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_documentReleasability), i);}

  /** indexed setter for documentReleasability - sets an indexed value - Array of country designators to which the document is releasable.
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setDocumentReleasability(int i, String v) { 
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_documentReleasability == null)
      jcasType.jcas.throwFeatMissing("documentReleasability", "uima.tcas.DocumentAnnotation");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_documentReleasability), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_documentReleasability), i, v);}
                        /**
	 * Get hash of current document text
	 */
	public String getHash() {
		try {
			return IdentityUtils.hashStrings(getCAS().getDocumentText());
		} catch (BaleenException e) {
			return "";
		}
	}
  }
 

    