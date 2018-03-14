// Dstl (c) Crown Copyright 2017

/* First created by JCasGen Tue Apr 18 12:23:04 BST 2017 */
package uk.gov.dstl.baleen.types.templates;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.Base;

/**
 * Beginning / end marker of a record (multiple fields) in a template document, used to create
 * record definitions for subsequent annotation of real documents. Updated by JCasGen Tue Apr 18
 * 12:23:04 BST 2017 XML source:
 * /Users/stuarthendren/git/tenode/baleen/baleen/baleen-uima/src/main/resources/types/template_type_system.xml
 *
 * @generated
 */
public class TemplateRecordDefinition extends Base {
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(TemplateRecordDefinition.class);
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
  protected TemplateRecordDefinition() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   */
  public TemplateRecordDefinition(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public TemplateRecordDefinition(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public TemplateRecordDefinition(JCas jcas, int begin, int end) {
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
   * getter for name - gets The name of the record, eg Address
   *
   * @generated
   * @return value of the feature
   */
  public String getName() {
    if (TemplateRecordDefinition_Type.featOkTst
        && ((TemplateRecordDefinition_Type) jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing(
          "name", "uk.gov.dstl.baleen.types.templates.TemplateRecordDefinition");
    return jcasType.ll_cas.ll_getStringValue(
        addr, ((TemplateRecordDefinition_Type) jcasType).casFeatCode_name);
  }

  /**
   * setter for name - sets The name of the record, eg Address
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setName(String v) {
    if (TemplateRecordDefinition_Type.featOkTst
        && ((TemplateRecordDefinition_Type) jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing(
          "name", "uk.gov.dstl.baleen.types.templates.TemplateRecordDefinition");
    jcasType.ll_cas.ll_setStringValue(
        addr, ((TemplateRecordDefinition_Type) jcasType).casFeatCode_name, v);
  }

  // *--------------*
  // * Feature: repeat

  /**
   * getter for repeat - gets Declare that this record is repeatable in the document. For example, a
   * repeating record spaning a row of a table would create a record for each row in the table.
   *
   * @generated
   * @return value of the feature
   */
  public boolean getRepeat() {
    if (TemplateRecordDefinition_Type.featOkTst
        && ((TemplateRecordDefinition_Type) jcasType).casFeat_repeat == null)
      jcasType.jcas.throwFeatMissing(
          "repeat", "uk.gov.dstl.baleen.types.templates.TemplateRecordDefinition");
    return jcasType.ll_cas.ll_getBooleanValue(
        addr, ((TemplateRecordDefinition_Type) jcasType).casFeatCode_repeat);
  }

  /**
   * setter for repeat - sets Declare that this record is repeatable in the document. For example, a
   * repeating record spaning a row of a table would create a record for each row in the table.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setRepeat(boolean v) {
    if (TemplateRecordDefinition_Type.featOkTst
        && ((TemplateRecordDefinition_Type) jcasType).casFeat_repeat == null)
      jcasType.jcas.throwFeatMissing(
          "repeat", "uk.gov.dstl.baleen.types.templates.TemplateRecordDefinition");
    jcasType.ll_cas.ll_setBooleanValue(
        addr, ((TemplateRecordDefinition_Type) jcasType).casFeatCode_repeat, v);
  }
}
