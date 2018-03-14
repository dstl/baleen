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
 * A field definition in a template document. Updated by JCasGen Tue Apr 18 12:23:04 BST 2017
 *
 * @generated
 */
public class TemplateFieldDefinition_Type extends Base_Type {
  /** @generated */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = TemplateFieldDefinition.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public static final boolean featOkTst =
      JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");

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
      jcas.throwFeatMissing("name", "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");
    return ll_cas.ll_getStringValue(addr, casFeatCode_name);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setName(int addr, String v) {
    if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");
    ll_cas.ll_setStringValue(addr, casFeatCode_name, v);
  }

  /** @generated */
  final Feature casFeat_regex;
  /** @generated */
  final int casFeatCode_regex;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public String getRegex(int addr) {
    if (featOkTst && casFeat_regex == null)
      jcas.throwFeatMissing("regex", "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");
    return ll_cas.ll_getStringValue(addr, casFeatCode_regex);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setRegex(int addr, String v) {
    if (featOkTst && casFeat_regex == null)
      jcas.throwFeatMissing("regex", "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");
    ll_cas.ll_setStringValue(addr, casFeatCode_regex, v);
  }

  /** @generated */
  final Feature casFeat_defaultValue;
  /** @generated */
  final int casFeatCode_defaultValue;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public String getDefaultValue(int addr) {
    if (featOkTst && casFeat_defaultValue == null)
      jcas.throwFeatMissing(
          "defaultValue", "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");
    return ll_cas.ll_getStringValue(addr, casFeatCode_defaultValue);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setDefaultValue(int addr, String v) {
    if (featOkTst && casFeat_defaultValue == null)
      jcas.throwFeatMissing(
          "defaultValue", "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");
    ll_cas.ll_setStringValue(addr, casFeatCode_defaultValue, v);
  }

  /** @generated */
  final Feature casFeat_required;
  /** @generated */
  final int casFeatCode_required;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public boolean getRequired(int addr) {
    if (featOkTst && casFeat_required == null)
      jcas.throwFeatMissing(
          "required", "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_required);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setRequired(int addr, boolean v) {
    if (featOkTst && casFeat_required == null)
      jcas.throwFeatMissing(
          "required", "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_required, v);
  }

  /** @generated */
  final Feature casFeat_repeat;
  /** @generated */
  final int casFeatCode_repeat;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public boolean getRepeat(int addr) {
    if (featOkTst && casFeat_repeat == null)
      jcas.throwFeatMissing("repeat", "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_repeat);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setRepeat(int addr, boolean v) {
    if (featOkTst && casFeat_repeat == null)
      jcas.throwFeatMissing("repeat", "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_repeat, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @generated
   * @param jcas JCas
   * @param casType Type
   */
  public TemplateFieldDefinition_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_name = jcas.getRequiredFeatureDE(casType, "name", "uima.cas.String", featOkTst);
    casFeatCode_name =
        (null == casFeat_name) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl) casFeat_name).getCode();

    casFeat_regex = jcas.getRequiredFeatureDE(casType, "regex", "uima.cas.String", featOkTst);
    casFeatCode_regex =
        (null == casFeat_regex)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_regex).getCode();

    casFeat_defaultValue =
        jcas.getRequiredFeatureDE(casType, "defaultValue", "uima.cas.String", featOkTst);
    casFeatCode_defaultValue =
        (null == casFeat_defaultValue)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_defaultValue).getCode();

    casFeat_required =
        jcas.getRequiredFeatureDE(casType, "required", "uima.cas.Boolean", featOkTst);
    casFeatCode_required =
        (null == casFeat_required)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_required).getCode();

    casFeat_repeat = jcas.getRequiredFeatureDE(casType, "repeat", "uima.cas.Boolean", featOkTst);
    casFeatCode_repeat =
        (null == casFeat_repeat)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_repeat).getCode();
  }
}
