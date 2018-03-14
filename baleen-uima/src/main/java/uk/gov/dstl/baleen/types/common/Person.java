/* First created by JCasGen Wed Jan 21 11:21:05 GMT 2015 */
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.types.common;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * A Person named entitiy, as defined by an explict name reference within the source document.
 * Updated by JCasGen Wed Apr 13 13:23:16 BST 2016 XML source:
 * H:/git/TextProcessing/core/baleen/baleen-uima/src/main/resources/types/common_type_system.xml
 *
 * @generated
 */
public class Person extends Entity {
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Person.class);
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
  protected Person() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   */
  public Person(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Person(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Person(JCas jcas, int begin, int end) {
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
  // * Feature: title

  /**
   * getter for title - gets A person's title, for example Mr or President
   *
   * @generated
   * @return value of the feature
   */
  public String getTitle() {
    if (Person_Type.featOkTst && ((Person_Type) jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "uk.gov.dstl.baleen.types.common.Person");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Person_Type) jcasType).casFeatCode_title);
  }

  /**
   * setter for title - sets A person's title, for example Mr or President
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setTitle(String v) {
    if (Person_Type.featOkTst && ((Person_Type) jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "uk.gov.dstl.baleen.types.common.Person");
    jcasType.ll_cas.ll_setStringValue(addr, ((Person_Type) jcasType).casFeatCode_title, v);
  }

  // *--------------*
  // * Feature: gender

  /**
   * getter for gender - gets Should be one of the following: MALE FEMALE UNKNOWN
   *
   * <p>If empty or null, it is assumed to be UNKNOWN.
   *
   * @generated
   * @return value of the feature
   */
  public String getGender() {
    if (Person_Type.featOkTst && ((Person_Type) jcasType).casFeat_gender == null)
      jcasType.jcas.throwFeatMissing("gender", "uk.gov.dstl.baleen.types.common.Person");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Person_Type) jcasType).casFeatCode_gender);
  }

  /**
   * setter for gender - sets Should be one of the following: MALE FEMALE UNKNOWN
   *
   * <p>If empty or null, it is assumed to be UNKNOWN.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setGender(String v) {
    if (Person_Type.featOkTst && ((Person_Type) jcasType).casFeat_gender == null)
      jcasType.jcas.throwFeatMissing("gender", "uk.gov.dstl.baleen.types.common.Person");
    jcasType.ll_cas.ll_setStringValue(addr, ((Person_Type) jcasType).casFeatCode_gender, v);
  }
}
