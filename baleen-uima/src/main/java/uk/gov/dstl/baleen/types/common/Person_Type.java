/* First created by JCasGen Wed Jan 21 11:21:05 GMT 2015 */
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.types.common;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.semantic.Entity_Type;

/**
 * A Person named entitiy, as defined by an explict name reference within the source document.
 * Updated by JCasGen Tue Mar 27 09:05:45 BST 2018
 *
 * @generated
 */
public class Person_Type extends Entity_Type {
  /** @generated */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = Person.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public static final boolean featOkTst =
      JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.common.Person");

  /** @generated */
  final Feature casFeat_title;
  /** @generated */
  final int casFeatCode_title;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public String getTitle(int addr) {
    if (featOkTst && casFeat_title == null)
      jcas.throwFeatMissing("title", "uk.gov.dstl.baleen.types.common.Person");
    return ll_cas.ll_getStringValue(addr, casFeatCode_title);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setTitle(int addr, String v) {
    if (featOkTst && casFeat_title == null)
      jcas.throwFeatMissing("title", "uk.gov.dstl.baleen.types.common.Person");
    ll_cas.ll_setStringValue(addr, casFeatCode_title, v);
  }

  /** @generated */
  final Feature casFeat_gender;
  /** @generated */
  final int casFeatCode_gender;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public String getGender(int addr) {
    if (featOkTst && casFeat_gender == null)
      jcas.throwFeatMissing("gender", "uk.gov.dstl.baleen.types.common.Person");
    return ll_cas.ll_getStringValue(addr, casFeatCode_gender);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setGender(int addr, String v) {
    if (featOkTst && casFeat_gender == null)
      jcas.throwFeatMissing("gender", "uk.gov.dstl.baleen.types.common.Person");
    ll_cas.ll_setStringValue(addr, casFeatCode_gender, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @generated
   * @param jcas JCas
   * @param casType Type
   */
  public Person_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_title = jcas.getRequiredFeatureDE(casType, "title", "uima.cas.String", featOkTst);
    casFeatCode_title =
        (null == casFeat_title)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_title).getCode();

    casFeat_gender = jcas.getRequiredFeatureDE(casType, "gender", "uima.cas.String", featOkTst);
    casFeatCode_gender =
        (null == casFeat_gender)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_gender).getCode();
  }
}
