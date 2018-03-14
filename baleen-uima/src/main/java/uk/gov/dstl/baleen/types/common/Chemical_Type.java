// Dstl (c) Crown Copyright 2017

/* First created by JCasGen Tue Apr 04 11:38:06 BST 2017 */
package uk.gov.dstl.baleen.types.common;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.semantic.Entity_Type;

/**
 * A chemical or substance Updated by JCasGen Tue Apr 04 11:38:06 BST 2017
 *
 * @generated
 */
public class Chemical_Type extends Entity_Type {
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
          if (Chemical_Type.this.useExistingInstance) {
            // Return eq fs instance if already created
            FeatureStructure fs = Chemical_Type.this.jcas.getJfsFromCaddr(addr);
            if (null == fs) {
              fs = new Chemical(addr, Chemical_Type.this);
              Chemical_Type.this.jcas.putJfsFromCaddr(addr, fs);
              return fs;
            }
            return fs;
          } else return new Chemical(addr, Chemical_Type.this);
        }
      };
  /** @generated */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = Chemical.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public static final boolean featOkTst =
      JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.common.Chemical");

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @generated
   * @param jcas JCas
   * @param casType Type
   */
  public Chemical_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());
  }
}
