
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

/** An temporal interaction of interest, covering political, organisational, miltiary, criminal or social interactions mentioned within the document.
 * Updated by JCasGen Fri Feb 05 14:54:30 GMT 2016
 * @generated */
public class Event_Type extends Entity_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Event_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Event_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Event(addr, Event_Type.this);
  			   Event_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Event(addr, Event_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Event.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.semantic.Event");
 
  /** @generated */
  final Feature casFeat_description;
  /** @generated */
  final int     casFeatCode_description;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDescription(int addr) {
        if (featOkTst && casFeat_description == null)
      jcas.throwFeatMissing("description", "uk.gov.dstl.baleen.types.semantic.Event");
    return ll_cas.ll_getStringValue(addr, casFeatCode_description);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDescription(int addr, String v) {
        if (featOkTst && casFeat_description == null)
      jcas.throwFeatMissing("description", "uk.gov.dstl.baleen.types.semantic.Event");
    ll_cas.ll_setStringValue(addr, casFeatCode_description, v);}
    
  
 
  /** @generated */
  final Feature casFeat_location;
  /** @generated */
  final int     casFeatCode_location;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getLocation(int addr) {
        if (featOkTst && casFeat_location == null)
      jcas.throwFeatMissing("location", "uk.gov.dstl.baleen.types.semantic.Event");
    return ll_cas.ll_getRefValue(addr, casFeatCode_location);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setLocation(int addr, int v) {
        if (featOkTst && casFeat_location == null)
      jcas.throwFeatMissing("location", "uk.gov.dstl.baleen.types.semantic.Event");
    ll_cas.ll_setRefValue(addr, casFeatCode_location, v);}
    
  
 
  /** @generated */
  final Feature casFeat_occurrence;
  /** @generated */
  final int     casFeatCode_occurrence;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getOccurrence(int addr) {
        if (featOkTst && casFeat_occurrence == null)
      jcas.throwFeatMissing("occurrence", "uk.gov.dstl.baleen.types.semantic.Event");
    return ll_cas.ll_getRefValue(addr, casFeatCode_occurrence);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setOccurrence(int addr, int v) {
        if (featOkTst && casFeat_occurrence == null)
      jcas.throwFeatMissing("occurrence", "uk.gov.dstl.baleen.types.semantic.Event");
    ll_cas.ll_setRefValue(addr, casFeatCode_occurrence, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Event_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_description = jcas.getRequiredFeatureDE(casType, "description", "uima.cas.String", featOkTst);
    casFeatCode_description  = (null == casFeat_description) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_description).getCode();

 
    casFeat_location = jcas.getRequiredFeatureDE(casType, "location", "uk.gov.dstl.baleen.types.semantic.Location", featOkTst);
    casFeatCode_location  = (null == casFeat_location) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_location).getCode();

 
    casFeat_occurrence = jcas.getRequiredFeatureDE(casType, "occurrence", "uk.gov.dstl.baleen.types.semantic.Temporal", featOkTst);
    casFeatCode_occurrence  = (null == casFeat_occurrence) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_occurrence).getCode();

  }
}



    