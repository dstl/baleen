
/* First created by JCasGen Wed Jan 14 12:58:31 GMT 2015 */
//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.types.metadata;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.BaleenAnnotation_Type;

/** The protective marking of the text span defined by the begin and end properties.
 * Updated by JCasGen Tue Apr 12 12:06:57 BST 2016
 * @generated */
public class ProtectiveMarking_Type extends BaleenAnnotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ProtectiveMarking_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ProtectiveMarking_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ProtectiveMarking(addr, ProtectiveMarking_Type.this);
  			   ProtectiveMarking_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ProtectiveMarking(addr, ProtectiveMarking_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ProtectiveMarking.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
 
  /** @generated */
  final Feature casFeat_classification;
  /** @generated */
  final int     casFeatCode_classification;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getClassification(int addr) {
        if (featOkTst && casFeat_classification == null)
      jcas.throwFeatMissing("classification", "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    return ll_cas.ll_getStringValue(addr, casFeatCode_classification);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setClassification(int addr, String v) {
        if (featOkTst && casFeat_classification == null)
      jcas.throwFeatMissing("classification", "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    ll_cas.ll_setStringValue(addr, casFeatCode_classification, v);}
    
  
 
  /** @generated */
  final Feature casFeat_caveats;
  /** @generated */
  final int     casFeatCode_caveats;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getCaveats(int addr) {
        if (featOkTst && casFeat_caveats == null)
      jcas.throwFeatMissing("caveats", "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    return ll_cas.ll_getRefValue(addr, casFeatCode_caveats);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCaveats(int addr, int v) {
        if (featOkTst && casFeat_caveats == null)
      jcas.throwFeatMissing("caveats", "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    ll_cas.ll_setRefValue(addr, casFeatCode_caveats, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public String getCaveats(int addr, int i) {
        if (featOkTst && casFeat_caveats == null)
      jcas.throwFeatMissing("caveats", "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_caveats), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_caveats), i);
  return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_caveats), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setCaveats(int addr, int i, String v) {
        if (featOkTst && casFeat_caveats == null)
      jcas.throwFeatMissing("caveats", "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_caveats), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_caveats), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_caveats), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_releasability;
  /** @generated */
  final int     casFeatCode_releasability;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getReleasability(int addr) {
        if (featOkTst && casFeat_releasability == null)
      jcas.throwFeatMissing("releasability", "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    return ll_cas.ll_getRefValue(addr, casFeatCode_releasability);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setReleasability(int addr, int v) {
        if (featOkTst && casFeat_releasability == null)
      jcas.throwFeatMissing("releasability", "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    ll_cas.ll_setRefValue(addr, casFeatCode_releasability, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public String getReleasability(int addr, int i) {
        if (featOkTst && casFeat_releasability == null)
      jcas.throwFeatMissing("releasability", "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_releasability), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_releasability), i);
  return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_releasability), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setReleasability(int addr, int i, String v) {
        if (featOkTst && casFeat_releasability == null)
      jcas.throwFeatMissing("releasability", "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_releasability), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_releasability), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_releasability), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public ProtectiveMarking_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_classification = jcas.getRequiredFeatureDE(casType, "classification", "uima.cas.String", featOkTst);
    casFeatCode_classification  = (null == casFeat_classification) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_classification).getCode();

 
    casFeat_caveats = jcas.getRequiredFeatureDE(casType, "caveats", "uima.cas.StringArray", featOkTst);
    casFeatCode_caveats  = (null == casFeat_caveats) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_caveats).getCode();

 
    casFeat_releasability = jcas.getRequiredFeatureDE(casType, "releasability", "uima.cas.StringArray", featOkTst);
    casFeatCode_releasability  = (null == casFeat_releasability) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_releasability).getCode();

  }
}



    