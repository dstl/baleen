/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:42:22 BST 2019 */

package uk.gov.dstl.baleen.types;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

/**
 * A pointer to another annotation in the same document. Designed for general use (eg temporary
 * working inside annotator) rather than having some specific semantic meaning (eg like
 * coreference). Updated by JCasGen Wed Apr 17 13:42:22 BST 2019 XML source:
 * types/template_type_system.xml
 *
 * @generated
 */
public class Pointer extends BaleenAnnotation {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.Pointer";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Pointer.class);
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

  public static final String _FeatName_target = "target";
  public static final String _FeatName_targetId = "targetId";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_target = TypeSystemImpl.createCallSite(Pointer.class, "target");
  private static final MethodHandle _FH_target = _FC_target.dynamicInvoker();
  private static final CallSite _FC_targetId =
      TypeSystemImpl.createCallSite(Pointer.class, "targetId");
  private static final MethodHandle _FH_targetId = _FC_targetId.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Pointer() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Pointer(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Pointer(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Pointer(JCas jcas, int begin, int end) {
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
  // * Feature: target

  /**
   * getter for target - gets At target annotation to which this pointer refers.
   *
   * @generated
   * @return value of the feature
   */
  public BaleenAnnotation getTarget() {
    return (BaleenAnnotation) (_getFeatureValueNc(wrapGetIntCatchException(_FH_target)));
  }

  /**
   * setter for target - sets At target annotation to which this pointer refers.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setTarget(BaleenAnnotation v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_target), v);
  }

  // *--------------*
  // * Feature: targetId

  /**
   * getter for targetId - gets Used to reference a (Baleen) annotation by its internal id. Useful
   * for if you are pointing to a different jCas (in which case you can't use target)
   *
   * @generated
   * @return value of the feature
   */
  public long getTargetId() {
    return _getLongValueNc(wrapGetIntCatchException(_FH_targetId));
  }

  /**
   * setter for targetId - sets Used to reference a (Baleen) annotation by its internal id. Useful
   * for if you are pointing to a different jCas (in which case you can't use target)
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setTargetId(long v) {
    _setLongValueNfc(wrapGetIntCatchException(_FH_targetId), v);
  }
}
