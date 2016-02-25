
/* First created by JCasGen Tue Feb 03 15:25:57 GMT 2015 */
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

/** Specific vehicle or vessel
 * Updated by JCasGen Fri Feb 05 14:49:26 GMT 2016
 * @generated */
public class Vehicle_Type extends Entity_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Vehicle_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Vehicle_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Vehicle(addr, Vehicle_Type.this);
  			   Vehicle_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Vehicle(addr, Vehicle_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Vehicle.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.common.Vehicle");
 
  /** @generated */
  final Feature casFeat_vehicleIdentifier;
  /** @generated */
  final int     casFeatCode_vehicleIdentifier;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getVehicleIdentifier(int addr) {
        if (featOkTst && casFeat_vehicleIdentifier == null)
      jcas.throwFeatMissing("vehicleIdentifier", "uk.gov.dstl.baleen.types.common.Vehicle");
    return ll_cas.ll_getStringValue(addr, casFeatCode_vehicleIdentifier);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setVehicleIdentifier(int addr, String v) {
        if (featOkTst && casFeat_vehicleIdentifier == null)
      jcas.throwFeatMissing("vehicleIdentifier", "uk.gov.dstl.baleen.types.common.Vehicle");
    ll_cas.ll_setStringValue(addr, casFeatCode_vehicleIdentifier, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Vehicle_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_vehicleIdentifier = jcas.getRequiredFeatureDE(casType, "vehicleIdentifier", "uima.cas.String", featOkTst);
    casFeatCode_vehicleIdentifier  = (null == casFeat_vehicleIdentifier) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_vehicleIdentifier).getCode();

  }
}



    