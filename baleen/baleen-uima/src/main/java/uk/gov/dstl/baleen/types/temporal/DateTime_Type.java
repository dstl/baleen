
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

/** A combination of a DateType and Time which are part of the same reference, specifying a time on a specific date.
 * Updated by JCasGen Wed Jan 21 11:37:43 GMT 2015
 * @generated */
public class DateTime_Type extends Temporal_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (DateTime_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = DateTime_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new DateTime(addr, DateTime_Type.this);
  			   DateTime_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new DateTime(addr, DateTime_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = DateTime.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.temporal.DateTime");
 
  /** @generated */
  final Feature casFeat_parsedValue;
  /** @generated */
  final int     casFeatCode_parsedValue;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public long getParsedValue(int addr) {
        if (featOkTst && casFeat_parsedValue == null)
      jcas.throwFeatMissing("parsedValue", "uk.gov.dstl.baleen.types.temporal.DateTime");
    return ll_cas.ll_getLongValue(addr, casFeatCode_parsedValue);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setParsedValue(int addr, long v) {
        if (featOkTst && casFeat_parsedValue == null)
      jcas.throwFeatMissing("parsedValue", "uk.gov.dstl.baleen.types.temporal.DateTime");
    ll_cas.ll_setLongValue(addr, casFeatCode_parsedValue, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public DateTime_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_parsedValue = jcas.getRequiredFeatureDE(casType, "parsedValue", "uima.cas.Long", featOkTst);
    casFeatCode_parsedValue  = (null == casFeat_parsedValue) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_parsedValue).getCode();

  }
}



    