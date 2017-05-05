//Dstl (c) Crown Copyright 2017

/* First created by JCasGen Fri Oct 14 12:12:15 BST 2016 */
package uk.gov.dstl.baleen.types.structure;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** To capture the style applied to a span of text.
 * Updated by JCasGen Thu Dec 22 22:42:18 CET 2016
 * @generated */
public class Style_Type extends Structure_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Style.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.structure.Style");
 
  /** @generated */
  final Feature casFeat_font;
  /** @generated */
  final int     casFeatCode_font;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getFont(int addr) {
        if (featOkTst && casFeat_font == null)
      jcas.throwFeatMissing("font", "uk.gov.dstl.baleen.types.structure.Style");
    return ll_cas.ll_getStringValue(addr, casFeatCode_font);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setFont(int addr, String v) {
        if (featOkTst && casFeat_font == null)
      jcas.throwFeatMissing("font", "uk.gov.dstl.baleen.types.structure.Style");
    ll_cas.ll_setStringValue(addr, casFeatCode_font, v);}
    
  
 
  /** @generated */
  final Feature casFeat_color;
  /** @generated */
  final int     casFeatCode_color;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getColor(int addr) {
        if (featOkTst && casFeat_color == null)
      jcas.throwFeatMissing("color", "uk.gov.dstl.baleen.types.structure.Style");
    return ll_cas.ll_getStringValue(addr, casFeatCode_color);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setColor(int addr, String v) {
        if (featOkTst && casFeat_color == null)
      jcas.throwFeatMissing("color", "uk.gov.dstl.baleen.types.structure.Style");
    ll_cas.ll_setStringValue(addr, casFeatCode_color, v);}
    
  
 
  /** @generated */
  final Feature casFeat_decoration;
  /** @generated */
  final int     casFeatCode_decoration;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getDecoration(int addr) {
        if (featOkTst && casFeat_decoration == null)
      jcas.throwFeatMissing("decoration", "uk.gov.dstl.baleen.types.structure.Style");
    return ll_cas.ll_getRefValue(addr, casFeatCode_decoration);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDecoration(int addr, int v) {
        if (featOkTst && casFeat_decoration == null)
      jcas.throwFeatMissing("decoration", "uk.gov.dstl.baleen.types.structure.Style");
    ll_cas.ll_setRefValue(addr, casFeatCode_decoration, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public String getDecoration(int addr, int i) {
        if (featOkTst && casFeat_decoration == null)
      jcas.throwFeatMissing("decoration", "uk.gov.dstl.baleen.types.structure.Style");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_decoration), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_decoration), i);
  return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_decoration), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setDecoration(int addr, int i, String v) {
        if (featOkTst && casFeat_decoration == null)
      jcas.throwFeatMissing("decoration", "uk.gov.dstl.baleen.types.structure.Style");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_decoration), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_decoration), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_decoration), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_size;
  /** @generated */
  final int     casFeatCode_size;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSize(int addr) {
        if (featOkTst && casFeat_size == null)
      jcas.throwFeatMissing("size", "uk.gov.dstl.baleen.types.structure.Style");
    return ll_cas.ll_getStringValue(addr, casFeatCode_size);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSize(int addr, String v) {
        if (featOkTst && casFeat_size == null)
      jcas.throwFeatMissing("size", "uk.gov.dstl.baleen.types.structure.Style");
    ll_cas.ll_setStringValue(addr, casFeatCode_size, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Style_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_font = jcas.getRequiredFeatureDE(casType, "font", "uima.cas.String", featOkTst);
    casFeatCode_font  = (null == casFeat_font) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_font).getCode();

 
    casFeat_color = jcas.getRequiredFeatureDE(casType, "color", "uima.cas.String", featOkTst);
    casFeatCode_color  = (null == casFeat_color) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_color).getCode();

 
    casFeat_decoration = jcas.getRequiredFeatureDE(casType, "decoration", "uima.cas.StringArray", featOkTst);
    casFeatCode_decoration  = (null == casFeat_decoration) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_decoration).getCode();

 
    casFeat_size = jcas.getRequiredFeatureDE(casType, "size", "uima.cas.String", featOkTst);
    casFeatCode_size  = (null == casFeat_size) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_size).getCode();

  }
}



    