// Dstl (c) Crown Copyright 2017

/* First created by JCasGen Mon Nov 28 18:28:46 GMT 2016 */
package uk.gov.dstl.baleen.types.structure;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

/**
 * An unordered component of a document such as an unordered list. Updated by JCasGen Thu Dec 22
 * 22:42:18 CET 2016
 *
 * @generated
 */
public class Unordered_Type extends Structure_Type {
  /** @generated */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = Unordered.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public static final boolean featOkTst =
      JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.structure.Unordered");

  /** @generated */
  final Feature casFeat_level;
  /** @generated */
  final int casFeatCode_level;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getLevel(int addr) {
    if (featOkTst && casFeat_level == null)
      jcas.throwFeatMissing("level", "uk.gov.dstl.baleen.types.structure.Unordered");
    return ll_cas.ll_getIntValue(addr, casFeatCode_level);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setLevel(int addr, int v) {
    if (featOkTst && casFeat_level == null)
      jcas.throwFeatMissing("level", "uk.gov.dstl.baleen.types.structure.Unordered");
    ll_cas.ll_setIntValue(addr, casFeatCode_level, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @generated
   * @param jcas JCas
   * @param casType Type
   */
  public Unordered_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_level = jcas.getRequiredFeatureDE(casType, "level", "uima.cas.Integer", featOkTst);
    casFeatCode_level =
        (null == casFeat_level)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_level).getCode();
  }
}
