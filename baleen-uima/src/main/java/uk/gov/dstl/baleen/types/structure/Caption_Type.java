//Dstl (c) Crown Copyright 2017

/* First created by JCasGen Thu Oct 13 13:31:25 BST 2016 */
package uk.gov.dstl.baleen.types.structure;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** A caption of a Figure.
 * Updated by JCasGen Thu Dec 22 22:42:17 CET 2016
 * @generated */
public class Caption_Type extends Structure_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Caption.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.structure.Caption");



  /** @generated */
  final Feature casFeat_figure;
  /** @generated */
  final int     casFeatCode_figure;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getFigure(int addr) {
        if (featOkTst && casFeat_figure == null)
      jcas.throwFeatMissing("figure", "uk.gov.dstl.baleen.types.structure.Caption");
    return ll_cas.ll_getRefValue(addr, casFeatCode_figure);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setFigure(int addr, int v) {
        if (featOkTst && casFeat_figure == null)
      jcas.throwFeatMissing("figure", "uk.gov.dstl.baleen.types.structure.Caption");
    ll_cas.ll_setRefValue(addr, casFeatCode_figure, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Caption_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_figure = jcas.getRequiredFeatureDE(casType, "figure", "uk.gov.dstl.baleen.types.structure.Figure", featOkTst);
    casFeatCode_figure  = (null == casFeat_figure) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_figure).getCode();

  }
}



    