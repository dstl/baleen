/* First created by JCasGen Wed Jan 14 12:58:31 GMT 2015 */
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.types.metadata;

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
 * The published ID of the document, e.g. the Document Reference Updated by JCasGen Tue Apr 12
 * 12:06:57 BST 2016
 *
 * @generated
 */
public class PublishedId_Type extends BaleenAnnotation_Type {
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
          if (PublishedId_Type.this.useExistingInstance) {
            // Return eq fs instance if already created
            FeatureStructure fs = PublishedId_Type.this.jcas.getJfsFromCaddr(addr);
            if (null == fs) {
              fs = new PublishedId(addr, PublishedId_Type.this);
              PublishedId_Type.this.jcas.putJfsFromCaddr(addr, fs);
              return fs;
            }
            return fs;
          } else return new PublishedId(addr, PublishedId_Type.this);
        }
      };
  /** @generated */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = PublishedId.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public static final boolean featOkTst =
      JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.metadata.PublishedId");

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
      jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.metadata.PublishedId");
    return ll_cas.ll_getStringValue(addr, casFeatCode_value);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setValue(int addr, String v) {
    if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.metadata.PublishedId");
    ll_cas.ll_setStringValue(addr, casFeatCode_value, v);
  }

  /** @generated */
  final Feature casFeat_publishedIdType;
  /** @generated */
  final int casFeatCode_publishedIdType;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public String getPublishedIdType(int addr) {
    if (featOkTst && casFeat_publishedIdType == null)
      jcas.throwFeatMissing("publishedIdType", "uk.gov.dstl.baleen.types.metadata.PublishedId");
    return ll_cas.ll_getStringValue(addr, casFeatCode_publishedIdType);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setPublishedIdType(int addr, String v) {
    if (featOkTst && casFeat_publishedIdType == null)
      jcas.throwFeatMissing("publishedIdType", "uk.gov.dstl.baleen.types.metadata.PublishedId");
    ll_cas.ll_setStringValue(addr, casFeatCode_publishedIdType, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @generated
   * @param jcas JCas
   * @param casType Type
   */
  public PublishedId_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_value = jcas.getRequiredFeatureDE(casType, "value", "uima.cas.String", featOkTst);
    casFeatCode_value =
        (null == casFeat_value)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_value).getCode();

    casFeat_publishedIdType =
        jcas.getRequiredFeatureDE(casType, "publishedIdType", "uima.cas.String", featOkTst);
    casFeatCode_publishedIdType =
        (null == casFeat_publishedIdType)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_publishedIdType).getCode();
  }
}
