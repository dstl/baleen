
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

/** Records a relationship between named entities, explicitly mentioned within the source document.
 * Updated by JCasGen Fri Feb 05 14:54:31 GMT 2016
 * @generated */
public class Relation_Type extends Base_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Relation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Relation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Relation(addr, Relation_Type.this);
  			   Relation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Relation(addr, Relation_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Relation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.semantic.Relation");
 
  /** @generated */
  final Feature casFeat_relationshipType;
  /** @generated */
  final int     casFeatCode_relationshipType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getRelationshipType(int addr) {
        if (featOkTst && casFeat_relationshipType == null)
      jcas.throwFeatMissing("relationshipType", "uk.gov.dstl.baleen.types.semantic.Relation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_relationshipType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setRelationshipType(int addr, String v) {
        if (featOkTst && casFeat_relationshipType == null)
      jcas.throwFeatMissing("relationshipType", "uk.gov.dstl.baleen.types.semantic.Relation");
    ll_cas.ll_setStringValue(addr, casFeatCode_relationshipType, v);}
    
  
 
  /** @generated */
  final Feature casFeat_source;
  /** @generated */
  final int     casFeatCode_source;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getSource(int addr) {
        if (featOkTst && casFeat_source == null)
      jcas.throwFeatMissing("source", "uk.gov.dstl.baleen.types.semantic.Relation");
    return ll_cas.ll_getRefValue(addr, casFeatCode_source);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSource(int addr, int v) {
        if (featOkTst && casFeat_source == null)
      jcas.throwFeatMissing("source", "uk.gov.dstl.baleen.types.semantic.Relation");
    ll_cas.ll_setRefValue(addr, casFeatCode_source, v);}
    
  
 
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
      jcas.throwFeatMissing("target", "uk.gov.dstl.baleen.types.semantic.Relation");
    return ll_cas.ll_getRefValue(addr, casFeatCode_target);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTarget(int addr, int v) {
        if (featOkTst && casFeat_target == null)
      jcas.throwFeatMissing("target", "uk.gov.dstl.baleen.types.semantic.Relation");
    ll_cas.ll_setRefValue(addr, casFeatCode_target, v);}
    
  
 
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
      jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.semantic.Relation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_value);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setValue(int addr, String v) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.semantic.Relation");
    ll_cas.ll_setStringValue(addr, casFeatCode_value, v);}
    
  
 
  /** @generated */
  final Feature casFeat_relationSubType;
  /** @generated */
  final int     casFeatCode_relationSubType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getRelationSubType(int addr) {
        if (featOkTst && casFeat_relationSubType == null)
      jcas.throwFeatMissing("relationSubType", "uk.gov.dstl.baleen.types.semantic.Relation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_relationSubType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setRelationSubType(int addr, String v) {
        if (featOkTst && casFeat_relationSubType == null)
      jcas.throwFeatMissing("relationSubType", "uk.gov.dstl.baleen.types.semantic.Relation");
    ll_cas.ll_setStringValue(addr, casFeatCode_relationSubType, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Relation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_relationshipType = jcas.getRequiredFeatureDE(casType, "relationshipType", "uima.cas.String", featOkTst);
    casFeatCode_relationshipType  = (null == casFeat_relationshipType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_relationshipType).getCode();

 
    casFeat_source = jcas.getRequiredFeatureDE(casType, "source", "uk.gov.dstl.baleen.types.semantic.Entity", featOkTst);
    casFeatCode_source  = (null == casFeat_source) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_source).getCode();

 
    casFeat_target = jcas.getRequiredFeatureDE(casType, "target", "uk.gov.dstl.baleen.types.semantic.Entity", featOkTst);
    casFeatCode_target  = (null == casFeat_target) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_target).getCode();

 
    casFeat_value = jcas.getRequiredFeatureDE(casType, "value", "uima.cas.String", featOkTst);
    casFeatCode_value  = (null == casFeat_value) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_value).getCode();

 
    casFeat_relationSubType = jcas.getRequiredFeatureDE(casType, "relationSubType", "uima.cas.String", featOkTst);
    casFeatCode_relationSubType  = (null == casFeat_relationSubType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_relationSubType).getCode();

  }
}



    