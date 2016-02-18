
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

/** Specific amount of some current mentioned within the document.
 * Updated by JCasGen Fri Feb 05 14:49:26 GMT 2016
 * @generated */
public class Money_Type extends Entity_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Money_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Money_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Money(addr, Money_Type.this);
  			   Money_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Money(addr, Money_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Money.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.common.Money");
 
  /** @generated */
  final Feature casFeat_amount;
  /** @generated */
  final int     casFeatCode_amount;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getAmount(int addr) {
        if (featOkTst && casFeat_amount == null)
      jcas.throwFeatMissing("amount", "uk.gov.dstl.baleen.types.common.Money");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_amount);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAmount(int addr, double v) {
        if (featOkTst && casFeat_amount == null)
      jcas.throwFeatMissing("amount", "uk.gov.dstl.baleen.types.common.Money");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_amount, v);}
    
  
 
  /** @generated */
  final Feature casFeat_currency;
  /** @generated */
  final int     casFeatCode_currency;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getCurrency(int addr) {
        if (featOkTst && casFeat_currency == null)
      jcas.throwFeatMissing("currency", "uk.gov.dstl.baleen.types.common.Money");
    return ll_cas.ll_getStringValue(addr, casFeatCode_currency);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCurrency(int addr, String v) {
        if (featOkTst && casFeat_currency == null)
      jcas.throwFeatMissing("currency", "uk.gov.dstl.baleen.types.common.Money");
    ll_cas.ll_setStringValue(addr, casFeatCode_currency, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Money_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_amount = jcas.getRequiredFeatureDE(casType, "amount", "uima.cas.Double", featOkTst);
    casFeatCode_amount  = (null == casFeat_amount) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_amount).getCode();

 
    casFeat_currency = jcas.getRequiredFeatureDE(casType, "currency", "uima.cas.String", featOkTst);
    casFeatCode_currency  = (null == casFeat_currency) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_currency).getCode();

  }
}



    