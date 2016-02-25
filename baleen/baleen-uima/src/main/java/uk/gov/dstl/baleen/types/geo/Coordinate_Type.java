
/* First created by JCasGen Wed Jan 21 12:48:50 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types.geo;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import uk.gov.dstl.baleen.types.semantic.Location_Type;

/** A well-formed coordinate value - MGRS or WGS84 DD or DMS cooridate system - explictly defined in source document.
 * Updated by JCasGen Fri Feb 05 14:51:56 GMT 2016
 * @generated */
public class Coordinate_Type extends Location_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Coordinate_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Coordinate_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Coordinate(addr, Coordinate_Type.this);
  			   Coordinate_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Coordinate(addr, Coordinate_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Coordinate.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.geo.Coordinate");
 
  /** @generated */
  final Feature casFeat_coordinateValue;
  /** @generated */
  final int     casFeatCode_coordinateValue;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getCoordinateValue(int addr) {
        if (featOkTst && casFeat_coordinateValue == null)
      jcas.throwFeatMissing("coordinateValue", "uk.gov.dstl.baleen.types.geo.Coordinate");
    return ll_cas.ll_getStringValue(addr, casFeatCode_coordinateValue);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCoordinateValue(int addr, String v) {
        if (featOkTst && casFeat_coordinateValue == null)
      jcas.throwFeatMissing("coordinateValue", "uk.gov.dstl.baleen.types.geo.Coordinate");
    ll_cas.ll_setStringValue(addr, casFeatCode_coordinateValue, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Coordinate_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_coordinateValue = jcas.getRequiredFeatureDE(casType, "coordinateValue", "uima.cas.String", featOkTst);
    casFeatCode_coordinateValue  = (null == casFeat_coordinateValue) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_coordinateValue).getCode();

  }
}



    