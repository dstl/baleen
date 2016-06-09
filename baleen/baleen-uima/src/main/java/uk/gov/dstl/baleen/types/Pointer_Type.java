
/* First created by JCasGen Tue Apr 12 12:06:19 BST 2016 */
package uk.gov.dstl.baleen.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** A pointer to another annotation in the same document. Designed for general use (eg temporary working inside annotator) rather than having some specific semantic meaning (eg like coreference).
 * Updated by JCasGen Wed Apr 13 13:23:15 BST 2016
 * @generated */
public class Pointer_Type extends BaleenAnnotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Pointer_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Pointer_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Pointer(addr, Pointer_Type.this);
  			   Pointer_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Pointer(addr, Pointer_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Pointer.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.Pointer");
 
  /** @generated */
  final Feature casFeat_target;
  /** @generated */
  final int     casFeatCode_target;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getTarget(int addr) {
        if (featOkTst && casFeat_target == null)
      jcas.throwFeatMissing("target", "uk.gov.dstl.baleen.types.Pointer");
    return ll_cas.ll_getRefValue(addr, casFeatCode_target);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTarget(int addr, int v) {
        if (featOkTst && casFeat_target == null)
      jcas.throwFeatMissing("target", "uk.gov.dstl.baleen.types.Pointer");
    ll_cas.ll_setRefValue(addr, casFeatCode_target, v);}
    
  
 
  /** @generated */
  final Feature casFeat_targetId;
  /** @generated */
  final int     casFeatCode_targetId;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public long getTargetId(int addr) {
        if (featOkTst && casFeat_targetId == null)
      jcas.throwFeatMissing("targetId", "uk.gov.dstl.baleen.types.Pointer");
    return ll_cas.ll_getLongValue(addr, casFeatCode_targetId);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTargetId(int addr, long v) {
        if (featOkTst && casFeat_targetId == null)
      jcas.throwFeatMissing("targetId", "uk.gov.dstl.baleen.types.Pointer");
    ll_cas.ll_setLongValue(addr, casFeatCode_targetId, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Pointer_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_target = jcas.getRequiredFeatureDE(casType, "target", "uk.gov.dstl.baleen.types.BaleenAnnotation", featOkTst);
    casFeatCode_target  = (null == casFeat_target) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_target).getCode();

 
    casFeat_targetId = jcas.getRequiredFeatureDE(casType, "targetId", "uima.cas.Long", featOkTst);
    casFeatCode_targetId  = (null == casFeat_targetId) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_targetId).getCode();

  }
}



    