
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

/** A communication identifier - including equipent, user, accounts or subscription.  Includes (but not limited to) the following types: emailAddress, IPv4, IPv6, MSISDN, IMEI, IMSI values.
 * Updated by JCasGen Tue Feb 03 15:26:49 GMT 2015
 * @generated */
public class CommsIdentifier_Type extends Entity_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (CommsIdentifier_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = CommsIdentifier_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new CommsIdentifier(addr, CommsIdentifier_Type.this);
  			   CommsIdentifier_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new CommsIdentifier(addr, CommsIdentifier_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = CommsIdentifier.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.common.CommsIdentifier");
 
  /** @generated */
  final Feature casFeat_identifierType;
  /** @generated */
  final int     casFeatCode_identifierType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getIdentifierType(int addr) {
        if (featOkTst && casFeat_identifierType == null)
      jcas.throwFeatMissing("identifierType", "uk.gov.dstl.baleen.types.common.CommsIdentifier");
    return ll_cas.ll_getStringValue(addr, casFeatCode_identifierType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIdentifierType(int addr, String v) {
        if (featOkTst && casFeat_identifierType == null)
      jcas.throwFeatMissing("identifierType", "uk.gov.dstl.baleen.types.common.CommsIdentifier");
    ll_cas.ll_setStringValue(addr, casFeatCode_identifierType, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public CommsIdentifier_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_identifierType = jcas.getRequiredFeatureDE(casType, "identifierType", "uima.cas.String", featOkTst);
    casFeatCode_identifierType  = (null == casFeat_identifierType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_identifierType).getCode();

  }
}



    