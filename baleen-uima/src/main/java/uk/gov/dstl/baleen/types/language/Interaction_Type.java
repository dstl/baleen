// Dstl (c) Crown Copyright 2017

/* First created by JCasGen Tue Apr 12 12:06:25 BST 2016 */
package uk.gov.dstl.baleen.types.language;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.BaleenAnnotation_Type;

/**
 * A word which acts as a relation in a sentence (eg 'saw' in the 'John saw the car'). Updated by
 * JCasGen Wed Apr 13 13:23:16 BST 2016
 *
 * @generated
 */
public class Interaction_Type extends BaleenAnnotation_Type {
  /**
   * @generated
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {
    return fsGenerator;
  }
  /** @generated */
  private final FSGenerator fsGenerator =
      new FSGenerator() {
        public FeatureStructure createFS(int addr, CASImpl cas) {
          if (Interaction_Type.this.useExistingInstance) {
            // Return eq fs instance if already created
            FeatureStructure fs = Interaction_Type.this.jcas.getJfsFromCaddr(addr);
            if (null == fs) {
              fs = new Interaction(addr, Interaction_Type.this);
              Interaction_Type.this.jcas.putJfsFromCaddr(addr, fs);
              return fs;
            }
            return fs;
          } else return new Interaction(addr, Interaction_Type.this);
        }
      };
  /** @generated */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = Interaction.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public static final boolean featOkTst =
      JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.language.Interaction");

  /** @generated */
  final Feature casFeat_value;
  /** @generated */
  final int casFeatCode_value;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public String getValue(int addr) {
    if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.language.Interaction");
    return ll_cas.ll_getStringValue(addr, casFeatCode_value);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setValue(int addr, String v) {
    if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.language.Interaction");
    ll_cas.ll_setStringValue(addr, casFeatCode_value, v);
  }

  /** @generated */
  final Feature casFeat_relationshipType;
  /** @generated */
  final int casFeatCode_relationshipType;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public String getRelationshipType(int addr) {
    if (featOkTst && casFeat_relationshipType == null)
      jcas.throwFeatMissing("relationshipType", "uk.gov.dstl.baleen.types.language.Interaction");
    return ll_cas.ll_getStringValue(addr, casFeatCode_relationshipType);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setRelationshipType(int addr, String v) {
    if (featOkTst && casFeat_relationshipType == null)
      jcas.throwFeatMissing("relationshipType", "uk.gov.dstl.baleen.types.language.Interaction");
    ll_cas.ll_setStringValue(addr, casFeatCode_relationshipType, v);
  }

  /** @generated */
  final Feature casFeat_relationSubType;
  /** @generated */
  final int casFeatCode_relationSubType;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public String getRelationSubType(int addr) {
    if (featOkTst && casFeat_relationSubType == null)
      jcas.throwFeatMissing("relationSubType", "uk.gov.dstl.baleen.types.language.Interaction");
    return ll_cas.ll_getStringValue(addr, casFeatCode_relationSubType);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setRelationSubType(int addr, String v) {
    if (featOkTst && casFeat_relationSubType == null)
      jcas.throwFeatMissing("relationSubType", "uk.gov.dstl.baleen.types.language.Interaction");
    ll_cas.ll_setStringValue(addr, casFeatCode_relationSubType, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @generated
   * @param jcas JCas
   * @param casType Type
   */
  public Interaction_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_value = jcas.getRequiredFeatureDE(casType, "value", "uima.cas.String", featOkTst);
    casFeatCode_value =
        (null == casFeat_value)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_value).getCode();

    casFeat_relationshipType =
        jcas.getRequiredFeatureDE(casType, "relationshipType", "uima.cas.String", featOkTst);
    casFeatCode_relationshipType =
        (null == casFeat_relationshipType)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_relationshipType).getCode();

    casFeat_relationSubType =
        jcas.getRequiredFeatureDE(casType, "relationSubType", "uima.cas.String", featOkTst);
    casFeatCode_relationSubType =
        (null == casFeat_relationSubType)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_relationSubType).getCode();
  }
}
