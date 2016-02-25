
/* First created by JCasGen Thu Jan 22 12:33:22 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package org.apache.uima.jcas.tcas;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** Overriding the base DocumentAnntation to add additional features. The JCasGen code generated from this annotation replaces the default type in uima-document-annotation.jar (which should be removed from the classpath).
 * Updated by JCasGen Fri Feb 05 14:54:30 GMT 2016
 * @generated */
public class DocumentAnnotation_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (DocumentAnnotation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = DocumentAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new DocumentAnnotation(addr, DocumentAnnotation_Type.this);
  			   DocumentAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new DocumentAnnotation(addr, DocumentAnnotation_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = DocumentAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uima.tcas.DocumentAnnotation");
 
  /** @generated */
  final Feature casFeat_language;
  /** @generated */
  final int     casFeatCode_language;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getLanguage(int addr) {
        if (featOkTst && casFeat_language == null)
      jcas.throwFeatMissing("language", "uima.tcas.DocumentAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_language);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setLanguage(int addr, String v) {
        if (featOkTst && casFeat_language == null)
      jcas.throwFeatMissing("language", "uima.tcas.DocumentAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_language, v);}
    
  
 
  /** @generated */
  final Feature casFeat_docType;
  /** @generated */
  final int     casFeatCode_docType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDocType(int addr) {
        if (featOkTst && casFeat_docType == null)
      jcas.throwFeatMissing("docType", "uima.tcas.DocumentAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_docType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDocType(int addr, String v) {
        if (featOkTst && casFeat_docType == null)
      jcas.throwFeatMissing("docType", "uima.tcas.DocumentAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_docType, v);}
    
  
 
  /** @generated */
  final Feature casFeat_sourceUri;
  /** @generated */
  final int     casFeatCode_sourceUri;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSourceUri(int addr) {
        if (featOkTst && casFeat_sourceUri == null)
      jcas.throwFeatMissing("sourceUri", "uima.tcas.DocumentAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_sourceUri);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSourceUri(int addr, String v) {
        if (featOkTst && casFeat_sourceUri == null)
      jcas.throwFeatMissing("sourceUri", "uima.tcas.DocumentAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_sourceUri, v);}
    
  
 
  /** @generated */
  final Feature casFeat_timestamp;
  /** @generated */
  final int     casFeatCode_timestamp;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public long getTimestamp(int addr) {
        if (featOkTst && casFeat_timestamp == null)
      jcas.throwFeatMissing("timestamp", "uima.tcas.DocumentAnnotation");
    return ll_cas.ll_getLongValue(addr, casFeatCode_timestamp);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTimestamp(int addr, long v) {
        if (featOkTst && casFeat_timestamp == null)
      jcas.throwFeatMissing("timestamp", "uima.tcas.DocumentAnnotation");
    ll_cas.ll_setLongValue(addr, casFeatCode_timestamp, v);}
    
  
 
  /** @generated */
  final Feature casFeat_documentClassification;
  /** @generated */
  final int     casFeatCode_documentClassification;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDocumentClassification(int addr) {
        if (featOkTst && casFeat_documentClassification == null)
      jcas.throwFeatMissing("documentClassification", "uima.tcas.DocumentAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_documentClassification);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDocumentClassification(int addr, String v) {
        if (featOkTst && casFeat_documentClassification == null)
      jcas.throwFeatMissing("documentClassification", "uima.tcas.DocumentAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_documentClassification, v);}
    
  
 
  /** @generated */
  final Feature casFeat_documentCaveats;
  /** @generated */
  final int     casFeatCode_documentCaveats;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getDocumentCaveats(int addr) {
        if (featOkTst && casFeat_documentCaveats == null)
      jcas.throwFeatMissing("documentCaveats", "uima.tcas.DocumentAnnotation");
    return ll_cas.ll_getRefValue(addr, casFeatCode_documentCaveats);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDocumentCaveats(int addr, int v) {
        if (featOkTst && casFeat_documentCaveats == null)
      jcas.throwFeatMissing("documentCaveats", "uima.tcas.DocumentAnnotation");
    ll_cas.ll_setRefValue(addr, casFeatCode_documentCaveats, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public String getDocumentCaveats(int addr, int i) {
        if (featOkTst && casFeat_documentCaveats == null)
      jcas.throwFeatMissing("documentCaveats", "uima.tcas.DocumentAnnotation");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_documentCaveats), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_documentCaveats), i);
  return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_documentCaveats), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setDocumentCaveats(int addr, int i, String v) {
        if (featOkTst && casFeat_documentCaveats == null)
      jcas.throwFeatMissing("documentCaveats", "uima.tcas.DocumentAnnotation");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_documentCaveats), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_documentCaveats), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_documentCaveats), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_documentReleasability;
  /** @generated */
  final int     casFeatCode_documentReleasability;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getDocumentReleasability(int addr) {
        if (featOkTst && casFeat_documentReleasability == null)
      jcas.throwFeatMissing("documentReleasability", "uima.tcas.DocumentAnnotation");
    return ll_cas.ll_getRefValue(addr, casFeatCode_documentReleasability);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDocumentReleasability(int addr, int v) {
        if (featOkTst && casFeat_documentReleasability == null)
      jcas.throwFeatMissing("documentReleasability", "uima.tcas.DocumentAnnotation");
    ll_cas.ll_setRefValue(addr, casFeatCode_documentReleasability, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public String getDocumentReleasability(int addr, int i) {
        if (featOkTst && casFeat_documentReleasability == null)
      jcas.throwFeatMissing("documentReleasability", "uima.tcas.DocumentAnnotation");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_documentReleasability), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_documentReleasability), i);
  return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_documentReleasability), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setDocumentReleasability(int addr, int i, String v) {
        if (featOkTst && casFeat_documentReleasability == null)
      jcas.throwFeatMissing("documentReleasability", "uima.tcas.DocumentAnnotation");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_documentReleasability), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_documentReleasability), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_documentReleasability), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public DocumentAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_language = jcas.getRequiredFeatureDE(casType, "language", "uima.cas.String", featOkTst);
    casFeatCode_language  = (null == casFeat_language) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_language).getCode();

 
    casFeat_docType = jcas.getRequiredFeatureDE(casType, "docType", "uima.cas.String", featOkTst);
    casFeatCode_docType  = (null == casFeat_docType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_docType).getCode();

 
    casFeat_sourceUri = jcas.getRequiredFeatureDE(casType, "sourceUri", "uima.cas.String", featOkTst);
    casFeatCode_sourceUri  = (null == casFeat_sourceUri) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_sourceUri).getCode();

 
    casFeat_timestamp = jcas.getRequiredFeatureDE(casType, "timestamp", "uima.cas.Long", featOkTst);
    casFeatCode_timestamp  = (null == casFeat_timestamp) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_timestamp).getCode();

 
    casFeat_documentClassification = jcas.getRequiredFeatureDE(casType, "documentClassification", "uima.cas.String", featOkTst);
    casFeatCode_documentClassification  = (null == casFeat_documentClassification) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_documentClassification).getCode();

 
    casFeat_documentCaveats = jcas.getRequiredFeatureDE(casType, "documentCaveats", "uima.cas.StringArray", featOkTst);
    casFeatCode_documentCaveats  = (null == casFeat_documentCaveats) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_documentCaveats).getCode();

 
    casFeat_documentReleasability = jcas.getRequiredFeatureDE(casType, "documentReleasability", "uima.cas.StringArray", featOkTst);
    casFeatCode_documentReleasability  = (null == casFeat_documentReleasability) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_documentReleasability).getCode();

  }
}



    