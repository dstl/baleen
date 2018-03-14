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

import uk.gov.dstl.baleen.types.Base_Type;

/**
 * Grammatical dependencies between wordtokens, as output from a Dependency Grammar Parser Updated
 * by JCasGen Wed Apr 13 13:23:16 BST 2016
 *
 * @generated
 */
public class Dependency_Type extends Base_Type {
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
          if (Dependency_Type.this.useExistingInstance) {
            // Return eq fs instance if already created
            FeatureStructure fs = Dependency_Type.this.jcas.getJfsFromCaddr(addr);
            if (null == fs) {
              fs = new Dependency(addr, Dependency_Type.this);
              Dependency_Type.this.jcas.putJfsFromCaddr(addr, fs);
              return fs;
            }
            return fs;
          } else return new Dependency(addr, Dependency_Type.this);
        }
      };
  /** @generated */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = Dependency.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public static final boolean featOkTst =
      JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.language.Dependency");

  /** @generated */
  final Feature casFeat_governor;
  /** @generated */
  final int casFeatCode_governor;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getGovernor(int addr) {
    if (featOkTst && casFeat_governor == null)
      jcas.throwFeatMissing("governor", "uk.gov.dstl.baleen.types.language.Dependency");
    return ll_cas.ll_getRefValue(addr, casFeatCode_governor);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setGovernor(int addr, int v) {
    if (featOkTst && casFeat_governor == null)
      jcas.throwFeatMissing("governor", "uk.gov.dstl.baleen.types.language.Dependency");
    ll_cas.ll_setRefValue(addr, casFeatCode_governor, v);
  }

  /** @generated */
  final Feature casFeat_dependent;
  /** @generated */
  final int casFeatCode_dependent;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getDependent(int addr) {
    if (featOkTst && casFeat_dependent == null)
      jcas.throwFeatMissing("dependent", "uk.gov.dstl.baleen.types.language.Dependency");
    return ll_cas.ll_getRefValue(addr, casFeatCode_dependent);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setDependent(int addr, int v) {
    if (featOkTst && casFeat_dependent == null)
      jcas.throwFeatMissing("dependent", "uk.gov.dstl.baleen.types.language.Dependency");
    ll_cas.ll_setRefValue(addr, casFeatCode_dependent, v);
  }

  /** @generated */
  final Feature casFeat_dependencyType;
  /** @generated */
  final int casFeatCode_dependencyType;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public String getDependencyType(int addr) {
    if (featOkTst && casFeat_dependencyType == null)
      jcas.throwFeatMissing("dependencyType", "uk.gov.dstl.baleen.types.language.Dependency");
    return ll_cas.ll_getStringValue(addr, casFeatCode_dependencyType);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setDependencyType(int addr, String v) {
    if (featOkTst && casFeat_dependencyType == null)
      jcas.throwFeatMissing("dependencyType", "uk.gov.dstl.baleen.types.language.Dependency");
    ll_cas.ll_setStringValue(addr, casFeatCode_dependencyType, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @generated
   * @param jcas JCas
   * @param casType Type
   */
  public Dependency_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_governor =
        jcas.getRequiredFeatureDE(
            casType, "governor", "uk.gov.dstl.baleen.types.language.WordToken", featOkTst);
    casFeatCode_governor =
        (null == casFeat_governor)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_governor).getCode();

    casFeat_dependent =
        jcas.getRequiredFeatureDE(
            casType, "dependent", "uk.gov.dstl.baleen.types.language.WordToken", featOkTst);
    casFeatCode_dependent =
        (null == casFeat_dependent)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_dependent).getCode();

    casFeat_dependencyType =
        jcas.getRequiredFeatureDE(casType, "dependencyType", "uima.cas.String", featOkTst);
    casFeatCode_dependencyType =
        (null == casFeat_dependencyType)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_dependencyType).getCode();
  }
}
