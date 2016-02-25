
/* First created by JCasGen Wed Jan 21 11:21:05 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types.common;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import uk.gov.dstl.baleen.types.semantic.Entity_Type;

/** Type to annotate references to quantities within text
 * Updated by JCasGen Fri Feb 05 14:49:26 GMT 2016
 * @generated */
public class Quantity_Type extends Entity_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Quantity_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Quantity_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Quantity(addr, Quantity_Type.this);
  			   Quantity_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Quantity(addr, Quantity_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Quantity.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.common.Quantity");
 
  /** @generated */
  final Feature casFeat_normalizedUnit;
  /** @generated */
  final int     casFeatCode_normalizedUnit;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getNormalizedUnit(int addr) {
        if (featOkTst && casFeat_normalizedUnit == null)
      jcas.throwFeatMissing("normalizedUnit", "uk.gov.dstl.baleen.types.common.Quantity");
    return ll_cas.ll_getStringValue(addr, casFeatCode_normalizedUnit);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNormalizedUnit(int addr, String v) {
        if (featOkTst && casFeat_normalizedUnit == null)
      jcas.throwFeatMissing("normalizedUnit", "uk.gov.dstl.baleen.types.common.Quantity");
    ll_cas.ll_setStringValue(addr, casFeatCode_normalizedUnit, v);}
    
  
 
  /** @generated */
  final Feature casFeat_normalizedQuantity;
  /** @generated */
  final int     casFeatCode_normalizedQuantity;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getNormalizedQuantity(int addr) {
        if (featOkTst && casFeat_normalizedQuantity == null)
      jcas.throwFeatMissing("normalizedQuantity", "uk.gov.dstl.baleen.types.common.Quantity");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_normalizedQuantity);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNormalizedQuantity(int addr, double v) {
        if (featOkTst && casFeat_normalizedQuantity == null)
      jcas.throwFeatMissing("normalizedQuantity", "uk.gov.dstl.baleen.types.common.Quantity");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_normalizedQuantity, v);}
    
  
 
  /** @generated */
  final Feature casFeat_unit;
  /** @generated */
  final int     casFeatCode_unit;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getUnit(int addr) {
        if (featOkTst && casFeat_unit == null)
      jcas.throwFeatMissing("unit", "uk.gov.dstl.baleen.types.common.Quantity");
    return ll_cas.ll_getStringValue(addr, casFeatCode_unit);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setUnit(int addr, String v) {
        if (featOkTst && casFeat_unit == null)
      jcas.throwFeatMissing("unit", "uk.gov.dstl.baleen.types.common.Quantity");
    ll_cas.ll_setStringValue(addr, casFeatCode_unit, v);}
    
  
 
  /** @generated */
  final Feature casFeat_quantity;
  /** @generated */
  final int     casFeatCode_quantity;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getQuantity(int addr) {
        if (featOkTst && casFeat_quantity == null)
      jcas.throwFeatMissing("quantity", "uk.gov.dstl.baleen.types.common.Quantity");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_quantity);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setQuantity(int addr, double v) {
        if (featOkTst && casFeat_quantity == null)
      jcas.throwFeatMissing("quantity", "uk.gov.dstl.baleen.types.common.Quantity");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_quantity, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Quantity_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_normalizedUnit = jcas.getRequiredFeatureDE(casType, "normalizedUnit", "uima.cas.String", featOkTst);
    casFeatCode_normalizedUnit  = (null == casFeat_normalizedUnit) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_normalizedUnit).getCode();

 
    casFeat_normalizedQuantity = jcas.getRequiredFeatureDE(casType, "normalizedQuantity", "uima.cas.Double", featOkTst);
    casFeatCode_normalizedQuantity  = (null == casFeat_normalizedQuantity) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_normalizedQuantity).getCode();

 
    casFeat_unit = jcas.getRequiredFeatureDE(casType, "unit", "uima.cas.String", featOkTst);
    casFeatCode_unit  = (null == casFeat_unit) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_unit).getCode();

 
    casFeat_quantity = jcas.getRequiredFeatureDE(casType, "quantity", "uima.cas.Double", featOkTst);
    casFeatCode_quantity  = (null == casFeat_quantity) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_quantity).getCode();

  }
}



    