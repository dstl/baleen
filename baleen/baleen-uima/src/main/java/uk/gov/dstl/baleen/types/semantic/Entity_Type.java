
/* First created by JCasGen Wed Jan 14 12:58:27 GMT 2015 */
//Dstl (c) Crown Copyright 2015
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
import uk.gov.dstl.baleen.types.Base_Type;

/** Type to represent named entities - values that are assigned a semantic type.
 * Updated by JCasGen Wed Apr 06 16:49:30 BST 2016
 * @generated */
public class Entity_Type extends Base_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Entity_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Entity_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Entity(addr, Entity_Type.this);
  			   Entity_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Entity(addr, Entity_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Entity.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.semantic.Entity");
 
  /** @generated */
  final Feature casFeat_value;
  /** @generated */
  final int     casFeatCode_value;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getValue(int addr) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.semantic.Entity");
    return ll_cas.ll_getStringValue(addr, casFeatCode_value);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setValue(int addr, String v) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.semantic.Entity");
    ll_cas.ll_setStringValue(addr, casFeatCode_value, v);}
    
  
 
  /** @generated */
  final Feature casFeat_referent;
  /** @generated */
  final int     casFeatCode_referent;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getReferent(int addr) {
        if (featOkTst && casFeat_referent == null)
      jcas.throwFeatMissing("referent", "uk.gov.dstl.baleen.types.semantic.Entity");
    return ll_cas.ll_getRefValue(addr, casFeatCode_referent);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setReferent(int addr, int v) {
        if (featOkTst && casFeat_referent == null)
      jcas.throwFeatMissing("referent", "uk.gov.dstl.baleen.types.semantic.Entity");
    ll_cas.ll_setRefValue(addr, casFeatCode_referent, v);}
    
  
 
  /** @generated */
  final Feature casFeat_isNormalised;
  /** @generated */
  final int     casFeatCode_isNormalised;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getIsNormalised(int addr) {
        if (featOkTst && casFeat_isNormalised == null)
      jcas.throwFeatMissing("isNormalised", "uk.gov.dstl.baleen.types.semantic.Entity");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_isNormalised);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIsNormalised(int addr, boolean v) {
        if (featOkTst && casFeat_isNormalised == null)
      jcas.throwFeatMissing("isNormalised", "uk.gov.dstl.baleen.types.semantic.Entity");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_isNormalised, v);}
    
  
 
  /** @generated */
  final Feature casFeat_subType;
  /** @generated */
  final int     casFeatCode_subType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSubType(int addr) {
        if (featOkTst && casFeat_subType == null)
      jcas.throwFeatMissing("subType", "uk.gov.dstl.baleen.types.semantic.Entity");
    return ll_cas.ll_getStringValue(addr, casFeatCode_subType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSubType(int addr, String v) {
        if (featOkTst && casFeat_subType == null)
      jcas.throwFeatMissing("subType", "uk.gov.dstl.baleen.types.semantic.Entity");
    ll_cas.ll_setStringValue(addr, casFeatCode_subType, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Entity_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_value = jcas.getRequiredFeatureDE(casType, "value", "uima.cas.String", featOkTst);
    casFeatCode_value  = (null == casFeat_value) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_value).getCode();

 
    casFeat_referent = jcas.getRequiredFeatureDE(casType, "referent", "uk.gov.dstl.baleen.types.semantic.ReferenceTarget", featOkTst);
    casFeatCode_referent  = (null == casFeat_referent) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_referent).getCode();

 
    casFeat_isNormalised = jcas.getRequiredFeatureDE(casType, "isNormalised", "uima.cas.Boolean", featOkTst);
    casFeatCode_isNormalised  = (null == casFeat_isNormalised) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_isNormalised).getCode();

 
    casFeat_subType = jcas.getRequiredFeatureDE(casType, "subType", "uima.cas.String", featOkTst);
    casFeatCode_subType  = (null == casFeat_subType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_subType).getCode();

  }
}



    