/* First created by JCasGen Wed Jan 14 12:58:31 GMT 2015 */
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.types.metadata;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.BaleenAnnotation;

/**
 * The published ID of the document, e.g. the Document Reference Updated by JCasGen Tue Apr 12
 * 12:06:57 BST 2016 XML source:
 * H:/git/TextProcessing/core/baleen/baleen-uima/src/main/resources/types/metadata_type_system.xml
 *
 * @generated
 */
public class PublishedId extends BaleenAnnotation {
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(PublishedId.class);
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
  protected PublishedId() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   */
  public PublishedId(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public PublishedId(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public PublishedId(JCas jcas, int begin, int end) {
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
  // * Feature: value

  /**
   * getter for value - gets
   *
   * @generated
   * @return value of the feature
   */
  public String getValue() {
    if (PublishedId_Type.featOkTst && ((PublishedId_Type) jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.metadata.PublishedId");
    return jcasType.ll_cas.ll_getStringValue(addr, ((PublishedId_Type) jcasType).casFeatCode_value);
  }

  /**
   * setter for value - sets
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setValue(String v) {
    if (PublishedId_Type.featOkTst && ((PublishedId_Type) jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.metadata.PublishedId");
    jcasType.ll_cas.ll_setStringValue(addr, ((PublishedId_Type) jcasType).casFeatCode_value, v);
  }

  // *--------------*
  // * Feature: publishedIdType

  /**
   * getter for publishedIdType - gets The type of PublishedID that this particular annotation
   * refers to
   *
   * @generated
   * @return value of the feature
   */
  public String getPublishedIdType() {
    if (PublishedId_Type.featOkTst && ((PublishedId_Type) jcasType).casFeat_publishedIdType == null)
      jcasType.jcas.throwFeatMissing(
          "publishedIdType", "uk.gov.dstl.baleen.types.metadata.PublishedId");
    return jcasType.ll_cas.ll_getStringValue(
        addr, ((PublishedId_Type) jcasType).casFeatCode_publishedIdType);
  }

  /**
   * setter for publishedIdType - sets The type of PublishedID that this particular annotation
   * refers to
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setPublishedIdType(String v) {
    if (PublishedId_Type.featOkTst && ((PublishedId_Type) jcasType).casFeat_publishedIdType == null)
      jcasType.jcas.throwFeatMissing(
          "publishedIdType", "uk.gov.dstl.baleen.types.metadata.PublishedId");
    jcasType.ll_cas.ll_setStringValue(
        addr, ((PublishedId_Type) jcasType).casFeatCode_publishedIdType, v);
  }
}
