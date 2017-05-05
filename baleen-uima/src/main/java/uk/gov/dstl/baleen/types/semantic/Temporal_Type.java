
/* First created by JCasGen Wed Jan 21 11:20:35 GMT 2015 */
//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.types.semantic;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** Type to record all temporal references in the text and, where possible, information about them (e.g. timestamp). This includes all times, dates, datetimes, periods, etc.
 * Updated by JCasGen Thu Oct 06 15:46:19 BST 2016
 * @generated */
public class Temporal_Type extends Entity_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Temporal_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Temporal_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Temporal(addr, Temporal_Type.this);
  			   Temporal_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Temporal(addr, Temporal_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Temporal.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.semantic.Temporal");



  /** @generated */
  final Feature casFeat_timestampStart;
  /** @generated */
  final int     casFeatCode_timestampStart;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public long getTimestampStart(int addr) {
        if (featOkTst && casFeat_timestampStart == null)
      jcas.throwFeatMissing("timestampStart", "uk.gov.dstl.baleen.types.semantic.Temporal");
    return ll_cas.ll_getLongValue(addr, casFeatCode_timestampStart);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTimestampStart(int addr, long v) {
        if (featOkTst && casFeat_timestampStart == null)
      jcas.throwFeatMissing("timestampStart", "uk.gov.dstl.baleen.types.semantic.Temporal");
    ll_cas.ll_setLongValue(addr, casFeatCode_timestampStart, v);}
    
  
 
  /** @generated */
  final Feature casFeat_timestampStop;
  /** @generated */
  final int     casFeatCode_timestampStop;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public long getTimestampStop(int addr) {
        if (featOkTst && casFeat_timestampStop == null)
      jcas.throwFeatMissing("timestampStop", "uk.gov.dstl.baleen.types.semantic.Temporal");
    return ll_cas.ll_getLongValue(addr, casFeatCode_timestampStop);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTimestampStop(int addr, long v) {
        if (featOkTst && casFeat_timestampStop == null)
      jcas.throwFeatMissing("timestampStop", "uk.gov.dstl.baleen.types.semantic.Temporal");
    ll_cas.ll_setLongValue(addr, casFeatCode_timestampStop, v);}
    
  
 
  /** @generated */
  final Feature casFeat_scope;
  /** @generated */
  final int     casFeatCode_scope;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getScope(int addr) {
        if (featOkTst && casFeat_scope == null)
      jcas.throwFeatMissing("scope", "uk.gov.dstl.baleen.types.semantic.Temporal");
    return ll_cas.ll_getStringValue(addr, casFeatCode_scope);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setScope(int addr, String v) {
        if (featOkTst && casFeat_scope == null)
      jcas.throwFeatMissing("scope", "uk.gov.dstl.baleen.types.semantic.Temporal");
    ll_cas.ll_setStringValue(addr, casFeatCode_scope, v);}
    
  
 
  /** @generated */
  final Feature casFeat_temporalType;
  /** @generated */
  final int     casFeatCode_temporalType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTemporalType(int addr) {
        if (featOkTst && casFeat_temporalType == null)
      jcas.throwFeatMissing("temporalType", "uk.gov.dstl.baleen.types.semantic.Temporal");
    return ll_cas.ll_getStringValue(addr, casFeatCode_temporalType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTemporalType(int addr, String v) {
        if (featOkTst && casFeat_temporalType == null)
      jcas.throwFeatMissing("temporalType", "uk.gov.dstl.baleen.types.semantic.Temporal");
    ll_cas.ll_setStringValue(addr, casFeatCode_temporalType, v);}
    
  
 
  /** @generated */
  final Feature casFeat_precision;
  /** @generated */
  final int     casFeatCode_precision;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPrecision(int addr) {
        if (featOkTst && casFeat_precision == null)
      jcas.throwFeatMissing("precision", "uk.gov.dstl.baleen.types.semantic.Temporal");
    return ll_cas.ll_getStringValue(addr, casFeatCode_precision);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPrecision(int addr, String v) {
        if (featOkTst && casFeat_precision == null)
      jcas.throwFeatMissing("precision", "uk.gov.dstl.baleen.types.semantic.Temporal");
    ll_cas.ll_setStringValue(addr, casFeatCode_precision, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Temporal_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_timestampStart = jcas.getRequiredFeatureDE(casType, "timestampStart", "uima.cas.Long", featOkTst);
    casFeatCode_timestampStart  = (null == casFeat_timestampStart) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_timestampStart).getCode();

 
    casFeat_timestampStop = jcas.getRequiredFeatureDE(casType, "timestampStop", "uima.cas.Long", featOkTst);
    casFeatCode_timestampStop  = (null == casFeat_timestampStop) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_timestampStop).getCode();

 
    casFeat_scope = jcas.getRequiredFeatureDE(casType, "scope", "uima.cas.String", featOkTst);
    casFeatCode_scope  = (null == casFeat_scope) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_scope).getCode();

 
    casFeat_temporalType = jcas.getRequiredFeatureDE(casType, "temporalType", "uima.cas.String", featOkTst);
    casFeatCode_temporalType  = (null == casFeat_temporalType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_temporalType).getCode();

 
    casFeat_precision = jcas.getRequiredFeatureDE(casType, "precision", "uima.cas.String", featOkTst);
    casFeatCode_precision  = (null == casFeat_precision) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_precision).getCode();

  }
}



    