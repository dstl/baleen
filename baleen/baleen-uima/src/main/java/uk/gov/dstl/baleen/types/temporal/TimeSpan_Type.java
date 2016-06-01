
/* First created by JCasGen Wed Jan 21 11:36:30 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types.temporal;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import uk.gov.dstl.baleen.types.semantic.Temporal_Type;

/** An entity representing a time span
 * Updated by JCasGen Tue Apr 12 12:07:25 BST 2016
 * @generated */
public class TimeSpan_Type extends Temporal_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (TimeSpan_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = TimeSpan_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new TimeSpan(addr, TimeSpan_Type.this);
  			   TimeSpan_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new TimeSpan(addr, TimeSpan_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TimeSpan.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.temporal.TimeSpan");
 
  /** @generated */
  final Feature casFeat_spanStart;
  /** @generated */
  final int     casFeatCode_spanStart;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public long getSpanStart(int addr) {
        if (featOkTst && casFeat_spanStart == null)
      jcas.throwFeatMissing("spanStart", "uk.gov.dstl.baleen.types.temporal.TimeSpan");
    return ll_cas.ll_getLongValue(addr, casFeatCode_spanStart);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSpanStart(int addr, long v) {
        if (featOkTst && casFeat_spanStart == null)
      jcas.throwFeatMissing("spanStart", "uk.gov.dstl.baleen.types.temporal.TimeSpan");
    ll_cas.ll_setLongValue(addr, casFeatCode_spanStart, v);}
    
  
 
  /** @generated */
  final Feature casFeat_spanStop;
  /** @generated */
  final int     casFeatCode_spanStop;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public long getSpanStop(int addr) {
        if (featOkTst && casFeat_spanStop == null)
      jcas.throwFeatMissing("spanStop", "uk.gov.dstl.baleen.types.temporal.TimeSpan");
    return ll_cas.ll_getLongValue(addr, casFeatCode_spanStop);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSpanStop(int addr, long v) {
        if (featOkTst && casFeat_spanStop == null)
      jcas.throwFeatMissing("spanStop", "uk.gov.dstl.baleen.types.temporal.TimeSpan");
    ll_cas.ll_setLongValue(addr, casFeatCode_spanStop, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public TimeSpan_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_spanStart = jcas.getRequiredFeatureDE(casType, "spanStart", "uima.cas.Long", featOkTst);
    casFeatCode_spanStart  = (null == casFeat_spanStart) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_spanStart).getCode();

 
    casFeat_spanStop = jcas.getRequiredFeatureDE(casType, "spanStop", "uima.cas.Long", featOkTst);
    casFeatCode_spanStop  = (null == casFeat_spanStop) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_spanStop).getCode();

  }
}



    