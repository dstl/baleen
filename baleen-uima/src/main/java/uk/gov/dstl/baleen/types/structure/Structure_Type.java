// Dstl (c) Crown Copyright 2017

/* First created by JCasGen Thu Oct 13 13:09:13 BST 2016 */
package uk.gov.dstl.baleen.types.structure;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.Base_Type;

/**
 * A base type for all Structure types. Updated by JCasGen Thu Apr 20 16:06:09 BST 2017
 *
 * @generated
 */
public class Structure_Type extends Base_Type {
  /** @generated */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = Structure.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public static final boolean featOkTst =
      JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.structure.Structure");

  /** @generated */
  final Feature casFeat_depth;
  /** @generated */
  final int casFeatCode_depth;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getDepth(int addr) {
    if (featOkTst && casFeat_depth == null)
      jcas.throwFeatMissing("depth", "uk.gov.dstl.baleen.types.structure.Structure");
    return ll_cas.ll_getIntValue(addr, casFeatCode_depth);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setDepth(int addr, int v) {
    if (featOkTst && casFeat_depth == null)
      jcas.throwFeatMissing("depth", "uk.gov.dstl.baleen.types.structure.Structure");
    ll_cas.ll_setIntValue(addr, casFeatCode_depth, v);
  }

  /** @generated */
  final Feature casFeat_elementClass;
  /** @generated */
  final int casFeatCode_elementClass;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public String getElementClass(int addr) {
    if (featOkTst && casFeat_elementClass == null)
      jcas.throwFeatMissing("elementClass", "uk.gov.dstl.baleen.types.structure.Structure");
    return ll_cas.ll_getStringValue(addr, casFeatCode_elementClass);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setElementClass(int addr, String v) {
    if (featOkTst && casFeat_elementClass == null)
      jcas.throwFeatMissing("elementClass", "uk.gov.dstl.baleen.types.structure.Structure");
    ll_cas.ll_setStringValue(addr, casFeatCode_elementClass, v);
  }

  /** @generated */
  final Feature casFeat_elementId;
  /** @generated */
  final int casFeatCode_elementId;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public String getElementId(int addr) {
    if (featOkTst && casFeat_elementId == null)
      jcas.throwFeatMissing("elementId", "uk.gov.dstl.baleen.types.structure.Structure");
    return ll_cas.ll_getStringValue(addr, casFeatCode_elementId);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setElementId(int addr, String v) {
    if (featOkTst && casFeat_elementId == null)
      jcas.throwFeatMissing("elementId", "uk.gov.dstl.baleen.types.structure.Structure");
    ll_cas.ll_setStringValue(addr, casFeatCode_elementId, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @generated
   * @param jcas JCas
   * @param casType Type
   */
  public Structure_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_depth = jcas.getRequiredFeatureDE(casType, "depth", "uima.cas.Integer", featOkTst);
    casFeatCode_depth =
        (null == casFeat_depth)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_depth).getCode();

    casFeat_elementClass =
        jcas.getRequiredFeatureDE(casType, "elementClass", "uima.cas.String", featOkTst);
    casFeatCode_elementClass =
        (null == casFeat_elementClass)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_elementClass).getCode();

    casFeat_elementId =
        jcas.getRequiredFeatureDE(casType, "elementId", "uima.cas.String", featOkTst);
    casFeatCode_elementId =
        (null == casFeat_elementId)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_elementId).getCode();
  }
}
