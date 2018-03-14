// Dstl (c) Crown Copyright 2017

/* First created by JCasGen Fri Oct 14 12:12:15 BST 2016 */
package uk.gov.dstl.baleen.types.structure;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.TOP_Type;

/**
 * To capture the style applied to a span of text. Updated by JCasGen Thu Dec 22 22:42:18 CET 2016
 * XML source:
 * /Users/chrisflatley/Projects/railroad/baleen/baleen/baleen-uima/src/main/resources/types/structure_type_system.xml
 *
 * @generated
 */
public class Style extends Structure {
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Style.class);
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
  protected Style() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   */
  public Style(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Style(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Style(JCas jcas, int begin, int end) {
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
  // * Feature: font

  /**
   * getter for font - gets The font
   *
   * @generated
   * @return value of the feature
   */
  public String getFont() {
    if (Style_Type.featOkTst && ((Style_Type) jcasType).casFeat_font == null)
      jcasType.jcas.throwFeatMissing("font", "uk.gov.dstl.baleen.types.structure.Style");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Style_Type) jcasType).casFeatCode_font);
  }

  /**
   * setter for font - sets The font
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setFont(String v) {
    if (Style_Type.featOkTst && ((Style_Type) jcasType).casFeat_font == null)
      jcasType.jcas.throwFeatMissing("font", "uk.gov.dstl.baleen.types.structure.Style");
    jcasType.ll_cas.ll_setStringValue(addr, ((Style_Type) jcasType).casFeatCode_font, v);
  }

  // *--------------*
  // * Feature: color

  /**
   * getter for color - gets The color of the text
   *
   * @generated
   * @return value of the feature
   */
  public String getColor() {
    if (Style_Type.featOkTst && ((Style_Type) jcasType).casFeat_color == null)
      jcasType.jcas.throwFeatMissing("color", "uk.gov.dstl.baleen.types.structure.Style");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Style_Type) jcasType).casFeatCode_color);
  }

  /**
   * setter for color - sets The color of the text
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setColor(String v) {
    if (Style_Type.featOkTst && ((Style_Type) jcasType).casFeat_color == null)
      jcasType.jcas.throwFeatMissing("color", "uk.gov.dstl.baleen.types.structure.Style");
    jcasType.ll_cas.ll_setStringValue(addr, ((Style_Type) jcasType).casFeatCode_color, v);
  }

  // *--------------*
  // * Feature: decoration

  /**
   * getter for decoration - gets Decoration applied to the text. For example, [italic, bold,
   * underline, strikethrough, small, superscript, subscript].
   *
   * @generated
   * @return value of the feature
   */
  public StringArray getDecoration() {
    if (Style_Type.featOkTst && ((Style_Type) jcasType).casFeat_decoration == null)
      jcasType.jcas.throwFeatMissing("decoration", "uk.gov.dstl.baleen.types.structure.Style");
    return (StringArray)
        (jcasType.ll_cas.ll_getFSForRef(
            jcasType.ll_cas.ll_getRefValue(addr, ((Style_Type) jcasType).casFeatCode_decoration)));
  }

  /**
   * setter for decoration - sets Decoration applied to the text. For example, [italic, bold,
   * underline, strikethrough, small, superscript, subscript].
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setDecoration(StringArray v) {
    if (Style_Type.featOkTst && ((Style_Type) jcasType).casFeat_decoration == null)
      jcasType.jcas.throwFeatMissing("decoration", "uk.gov.dstl.baleen.types.structure.Style");
    jcasType.ll_cas.ll_setRefValue(
        addr, ((Style_Type) jcasType).casFeatCode_decoration, jcasType.ll_cas.ll_getFSRef(v));
  }

  /**
   * indexed getter for decoration - gets an indexed value - Decoration applied to the text. For
   * example, [italic, bold, underline].
   *
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i
   */
  public String getDecoration(int i) {
    if (Style_Type.featOkTst && ((Style_Type) jcasType).casFeat_decoration == null)
      jcasType.jcas.throwFeatMissing("decoration", "uk.gov.dstl.baleen.types.structure.Style");
    jcasType.jcas.checkArrayBounds(
        jcasType.ll_cas.ll_getRefValue(addr, ((Style_Type) jcasType).casFeatCode_decoration), i);
    return jcasType.ll_cas.ll_getStringArrayValue(
        jcasType.ll_cas.ll_getRefValue(addr, ((Style_Type) jcasType).casFeatCode_decoration), i);
  }

  /**
   * indexed setter for decoration - sets an indexed value - Decoration applied to the text. For
   * example, [italic, bold, underline].
   *
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array
   */
  public void setDecoration(int i, String v) {
    if (Style_Type.featOkTst && ((Style_Type) jcasType).casFeat_decoration == null)
      jcasType.jcas.throwFeatMissing("decoration", "uk.gov.dstl.baleen.types.structure.Style");
    jcasType.jcas.checkArrayBounds(
        jcasType.ll_cas.ll_getRefValue(addr, ((Style_Type) jcasType).casFeatCode_decoration), i);
    jcasType.ll_cas.ll_setStringArrayValue(
        jcasType.ll_cas.ll_getRefValue(addr, ((Style_Type) jcasType).casFeatCode_decoration), i, v);
  }

  // *--------------*
  // * Feature: size

  /**
   * getter for size - gets The size of the text
   *
   * @generated
   * @return value of the feature
   */
  public String getSize() {
    if (Style_Type.featOkTst && ((Style_Type) jcasType).casFeat_size == null)
      jcasType.jcas.throwFeatMissing("size", "uk.gov.dstl.baleen.types.structure.Style");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Style_Type) jcasType).casFeatCode_size);
  }

  /**
   * setter for size - sets The size of the text
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setSize(String v) {
    if (Style_Type.featOkTst && ((Style_Type) jcasType).casFeat_size == null)
      jcasType.jcas.throwFeatMissing("size", "uk.gov.dstl.baleen.types.structure.Style");
    jcasType.ll_cas.ll_setStringValue(addr, ((Style_Type) jcasType).casFeatCode_size, v);
  }
}
