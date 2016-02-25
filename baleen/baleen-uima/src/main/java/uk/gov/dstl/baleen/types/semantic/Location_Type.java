
/* First created by JCasGen Wed Jan 21 11:22:53 GMT 2015 */
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

/** A reference to a place, country, administrative region or geo-political entity within the source document. This is a general purpose type that is extended in "geo" types.
 * Updated by JCasGen Fri Feb 05 14:54:31 GMT 2016
 * @generated */
public class Location_Type extends Entity_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Location_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Location_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Location(addr, Location_Type.this);
  			   Location_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Location(addr, Location_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Location.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.semantic.Location");
 
  /** @generated */
  final Feature casFeat_geoJson;
  /** @generated */
  final int     casFeatCode_geoJson;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getGeoJson(int addr) {
        if (featOkTst && casFeat_geoJson == null)
      jcas.throwFeatMissing("geoJson", "uk.gov.dstl.baleen.types.semantic.Location");
    return ll_cas.ll_getStringValue(addr, casFeatCode_geoJson);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setGeoJson(int addr, String v) {
        if (featOkTst && casFeat_geoJson == null)
      jcas.throwFeatMissing("geoJson", "uk.gov.dstl.baleen.types.semantic.Location");
    ll_cas.ll_setStringValue(addr, casFeatCode_geoJson, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Location_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_geoJson = jcas.getRequiredFeatureDE(casType, "geoJson", "uima.cas.String", featOkTst);
    casFeatCode_geoJson  = (null == casFeat_geoJson) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_geoJson).getCode();

  }
}



    