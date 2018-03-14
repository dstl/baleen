// Dstl (c) Crown Copyright 2017

/* First created by JCasGen Tue Apr 18 12:23:04 BST 2017 */
package uk.gov.dstl.baleen.types.templates;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.Base_Type;

/**
 * A covering annotation that marks the extent of a record within an annotated document. Updated by
 * JCasGen Tue Apr 18 12:23:04 BST 2017
 *
 * @generated
 */
public class TemplateRecord_Type extends Base_Type {
  /** @generated */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = TemplateRecord.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public static final boolean featOkTst =
      JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.templates.TemplateRecord");

  /** @generated */
  final Feature casFeat_name;
  /** @generated */
  final int casFeatCode_name;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public String getName(int addr) {
    if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "uk.gov.dstl.baleen.types.templates.TemplateRecord");
    return ll_cas.ll_getStringValue(addr, casFeatCode_name);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setName(int addr, String v) {
    if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "uk.gov.dstl.baleen.types.templates.TemplateRecord");
    ll_cas.ll_setStringValue(addr, casFeatCode_name, v);
  }

  /** @generated */
  final Feature casFeat_source;
  /** @generated */
  final int casFeatCode_source;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public String getSource(int addr) {
    if (featOkTst && casFeat_source == null)
      jcas.throwFeatMissing("source", "uk.gov.dstl.baleen.types.templates.TemplateRecord");
    return ll_cas.ll_getStringValue(addr, casFeatCode_source);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setSource(int addr, String v) {
    if (featOkTst && casFeat_source == null)
      jcas.throwFeatMissing("source", "uk.gov.dstl.baleen.types.templates.TemplateRecord");
    ll_cas.ll_setStringValue(addr, casFeatCode_source, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @generated
   * @param jcas JCas
   * @param casType Type
   */
  public TemplateRecord_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_name = jcas.getRequiredFeatureDE(casType, "name", "uima.cas.String", featOkTst);
    casFeatCode_name =
        (null == casFeat_name) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl) casFeat_name).getCode();

    casFeat_source = jcas.getRequiredFeatureDE(casType, "source", "uima.cas.String", featOkTst);
    casFeatCode_source =
        (null == casFeat_source)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_source).getCode();
  }
}
