//Dstl (c) Crown Copyright 2017

/* First created by JCasGen Tue Apr 18 12:23:04 BST 2017 */
package uk.gov.dstl.baleen.types.templates;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import uk.gov.dstl.baleen.types.Base_Type;

/** Beginning / end marker of a record (multiple fields) in a template document, used to create record definitions for subsequent annotation of real documents.
 * Updated by JCasGen Tue Apr 18 12:23:04 BST 2017
 * @generated */
public class TemplateRecordDefinition_Type extends Base_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TemplateRecordDefinition.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.templates.TemplateRecordDefinition");
 
  /** @generated */
  final Feature casFeat_name;
  /** @generated */
  final int     casFeatCode_name;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getName(int addr) {
        if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "uk.gov.dstl.baleen.types.templates.TemplateRecordDefinition");
    return ll_cas.ll_getStringValue(addr, casFeatCode_name);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setName(int addr, String v) {
        if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "uk.gov.dstl.baleen.types.templates.TemplateRecordDefinition");
    ll_cas.ll_setStringValue(addr, casFeatCode_name, v);}
    
  
 
  /** @generated */
  final Feature casFeat_repeat;
  /** @generated */
  final int     casFeatCode_repeat;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getRepeat(int addr) {
        if (featOkTst && casFeat_repeat == null)
      jcas.throwFeatMissing("repeat", "uk.gov.dstl.baleen.types.templates.TemplateRecordDefinition");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_repeat);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setRepeat(int addr, boolean v) {
        if (featOkTst && casFeat_repeat == null)
      jcas.throwFeatMissing("repeat", "uk.gov.dstl.baleen.types.templates.TemplateRecordDefinition");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_repeat, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public TemplateRecordDefinition_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_name = jcas.getRequiredFeatureDE(casType, "name", "uima.cas.String", featOkTst);
    casFeatCode_name  = (null == casFeat_name) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_name).getCode();

 
    casFeat_repeat = jcas.getRequiredFeatureDE(casType, "repeat", "uima.cas.Boolean", featOkTst);
    casFeatCode_repeat  = (null == casFeat_repeat) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_repeat).getCode();

  }
}



    