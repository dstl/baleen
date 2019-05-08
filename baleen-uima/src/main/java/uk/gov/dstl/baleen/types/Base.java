/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:42:22 BST 2019 */

package uk.gov.dstl.baleen.types;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

/**
 * Base annotation with confidence and annotator properties. Updated by JCasGen Wed Apr 17 13:42:22
 * BST 2019 XML source: types/template_type_system.xml
 *
 * @generated
 */
public class Base extends BaleenAnnotation {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.Base";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Base.class);
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

  /* *******************
   *   Feature Offsets *
   * *******************/

  public static final String _FeatName_confidence = "confidence";
  public static final String _FeatName_referent = "referent";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_confidence =
      TypeSystemImpl.createCallSite(Base.class, "confidence");
  private static final MethodHandle _FH_confidence = _FC_confidence.dynamicInvoker();
  private static final CallSite _FC_referent =
      TypeSystemImpl.createCallSite(Base.class, "referent");
  private static final MethodHandle _FH_referent = _FC_referent.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Base() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Base(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Base(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Base(JCas jcas, int begin, int end) {
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
  // * Feature: confidence

  /**
   * getter for confidence - gets Confidence value between 0 and 1 from annotation processor.
   *
   * @generated
   * @return value of the feature
   */
  public double getConfidence() {
    return _getDoubleValueNc(wrapGetIntCatchException(_FH_confidence));
  }

  /**
   * setter for confidence - sets Confidence value between 0 and 1 from annotation processor.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setConfidence(double v) {
    _setDoubleValueNfc(wrapGetIntCatchException(_FH_confidence), v);
  }

  // *--------------*
  // * Feature: referent

  /**
   * getter for referent - gets Can be used to link a corefence to an entity to another (presuambly
   * more definitive) mention of the same entity elsewhere in the text.
   *
   * @generated
   * @return value of the feature
   */
  public ReferenceTarget getReferent() {
    return (ReferenceTarget) (_getFeatureValueNc(wrapGetIntCatchException(_FH_referent)));
  }

  /**
   * setter for referent - sets Can be used to link a corefence to an entity to another (presuambly
   * more definitive) mention of the same entity elsewhere in the text.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setReferent(ReferenceTarget v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_referent), v);
  }
}
