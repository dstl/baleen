// Dstl (c) Crown Copyright 2017

/* First created by JCasGen Tue Apr 18 12:23:04 BST 2017 */
package uk.gov.dstl.baleen.types.templates;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.Base;

/**
 * A field definition in a template document. Updated by JCasGen Tue Apr 18 12:23:04 BST 2017 XML
 * source:
 * /Users/stuarthendren/git/tenode/baleen/baleen/baleen-uima/src/main/resources/types/template_type_system.xml
 *
 * @generated
 */
public class TemplateFieldDefinition extends Base {
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(TemplateFieldDefinition.class);
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int type = typeIndexID;
  /**
   * @generated
   * @return index of the type
   */
  @Override
  public int getTypeIndexID() {
    return typeIndexID;
  }

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected TemplateFieldDefinition() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   */
  public TemplateFieldDefinition(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public TemplateFieldDefinition(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public TemplateFieldDefinition(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }

  /**
   *
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable
   */
  private void readObject() {
    /*default - does nothing empty block */
  }

  // *--------------*
  // * Feature: name

  /**
   * getter for name - gets The name of the identified template field.
   *
   * @generated
   * @return value of the feature
   */
  public String getName() {
    if (TemplateFieldDefinition_Type.featOkTst
        && ((TemplateFieldDefinition_Type) jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing(
          "name", "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");
    return jcasType.ll_cas.ll_getStringValue(
        addr, ((TemplateFieldDefinition_Type) jcasType).casFeatCode_name);
  }

  /**
   * setter for name - sets The name of the identified template field.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setName(String v) {
    if (TemplateFieldDefinition_Type.featOkTst
        && ((TemplateFieldDefinition_Type) jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing(
          "name", "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");
    jcasType.ll_cas.ll_setStringValue(
        addr, ((TemplateFieldDefinition_Type) jcasType).casFeatCode_name, v);
  }

  // *--------------*
  // * Feature: regex

  /**
   * getter for regex - gets An optional regular expresison to extract the field from the structural
   * element.
   *
   * @generated
   * @return value of the feature
   */
  public String getRegex() {
    if (TemplateFieldDefinition_Type.featOkTst
        && ((TemplateFieldDefinition_Type) jcasType).casFeat_regex == null)
      jcasType.jcas.throwFeatMissing(
          "regex", "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");
    return jcasType.ll_cas.ll_getStringValue(
        addr, ((TemplateFieldDefinition_Type) jcasType).casFeatCode_regex);
  }

  /**
   * setter for regex - sets An optional regular expresison to extract the field from the structural
   * element.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setRegex(String v) {
    if (TemplateFieldDefinition_Type.featOkTst
        && ((TemplateFieldDefinition_Type) jcasType).casFeat_regex == null)
      jcasType.jcas.throwFeatMissing(
          "regex", "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");
    jcasType.ll_cas.ll_setStringValue(
        addr, ((TemplateFieldDefinition_Type) jcasType).casFeatCode_regex, v);
  }

  // *--------------*
  // * Feature: defaultValue

  /**
   * getter for defaultValue - gets An optional default value to be used if the field is not matched
   * for the record.
   *
   * @generated
   * @return value of the feature
   */
  public String getDefaultValue() {
    if (TemplateFieldDefinition_Type.featOkTst
        && ((TemplateFieldDefinition_Type) jcasType).casFeat_defaultValue == null)
      jcasType.jcas.throwFeatMissing(
          "defaultValue", "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");
    return jcasType.ll_cas.ll_getStringValue(
        addr, ((TemplateFieldDefinition_Type) jcasType).casFeatCode_defaultValue);
  }

  /**
   * setter for defaultValue - sets An optional default value to be used if the field is not matched
   * for the record.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setDefaultValue(String v) {
    if (TemplateFieldDefinition_Type.featOkTst
        && ((TemplateFieldDefinition_Type) jcasType).casFeat_defaultValue == null)
      jcasType.jcas.throwFeatMissing(
          "defaultValue", "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");
    jcasType.ll_cas.ll_setStringValue(
        addr, ((TemplateFieldDefinition_Type) jcasType).casFeatCode_defaultValue, v);
  }

  // *--------------*
  // * Feature: required

  /**
   * getter for required - gets Set true to declare that the field is required. If a default is set,
   * this is redundant as it will always exist
   *
   * @generated
   * @return value of the feature
   */
  public boolean getRequired() {
    if (TemplateFieldDefinition_Type.featOkTst
        && ((TemplateFieldDefinition_Type) jcasType).casFeat_required == null)
      jcasType.jcas.throwFeatMissing(
          "required", "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");
    return jcasType.ll_cas.ll_getBooleanValue(
        addr, ((TemplateFieldDefinition_Type) jcasType).casFeatCode_required);
  }

  /**
   * setter for required - sets Set true to declare that the field is required. If a default is set,
   * this is redundant as it will always exist
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setRequired(boolean v) {
    if (TemplateFieldDefinition_Type.featOkTst
        && ((TemplateFieldDefinition_Type) jcasType).casFeat_required == null)
      jcasType.jcas.throwFeatMissing(
          "required", "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");
    jcasType.ll_cas.ll_setBooleanValue(
        addr, ((TemplateFieldDefinition_Type) jcasType).casFeatCode_required, v);
  }

  // *--------------*
  // * Feature: repeat

  /**
   * getter for repeat - gets Indicate that this field can be repeated in the document.
   *
   * @generated
   * @return value of the feature
   */
  public boolean getRepeat() {
    if (TemplateFieldDefinition_Type.featOkTst
        && ((TemplateFieldDefinition_Type) jcasType).casFeat_repeat == null)
      jcasType.jcas.throwFeatMissing(
          "repeat", "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");
    return jcasType.ll_cas.ll_getBooleanValue(
        addr, ((TemplateFieldDefinition_Type) jcasType).casFeatCode_repeat);
  }

  /**
   * setter for repeat - sets Indicate that this field can be repeated in the document.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setRepeat(boolean v) {
    if (TemplateFieldDefinition_Type.featOkTst
        && ((TemplateFieldDefinition_Type) jcasType).casFeat_repeat == null)
      jcasType.jcas.throwFeatMissing(
          "repeat", "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition");
    jcasType.ll_cas.ll_setBooleanValue(
        addr, ((TemplateFieldDefinition_Type) jcasType).casFeatCode_repeat, v);
  }
}
