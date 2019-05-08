/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:42:22 BST 2019 */

package uk.gov.dstl.baleen.types.semantic;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.BaleenAnnotation;

/**
 * A target type for the referent property, such that entities pointing to the same target are
 * assumed to be coreferences. The target can therefore be thought of as a super-entity, though it
 * has no properties or value of it's own. The span of this entity is taken to be the scope in which
 * this reference target is valid. Updated by JCasGen Wed Apr 17 13:42:22 BST 2019 XML source:
 * types/template_type_system.xml
 *
 * @generated
 */
public class ReferenceTarget extends BaleenAnnotation {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.semantic.ReferenceTarget";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(ReferenceTarget.class);
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

  public static final String _FeatName_linking = "linking";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_linking =
      TypeSystemImpl.createCallSite(ReferenceTarget.class, "linking");
  private static final MethodHandle _FH_linking = _FC_linking.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected ReferenceTarget() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public ReferenceTarget(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public ReferenceTarget(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public ReferenceTarget(JCas jcas, int begin, int end) {
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
  // * Feature: linking

  /**
   * getter for linking - gets A property to define an external linking that may describe this group
   * of mentions. For example, a set of mentions of a person could be linked to a database entry or
   * webpage for the person.
   *
   * @generated
   * @return value of the feature
   */
  public String getLinking() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_linking));
  }

  /**
   * setter for linking - sets A property to define an external linking that may describe this group
   * of mentions. For example, a set of mentions of a person could be linked to a database entry or
   * webpage for the person.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setLinking(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_linking), v);
  }
}
