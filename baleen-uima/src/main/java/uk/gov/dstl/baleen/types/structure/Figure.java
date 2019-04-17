/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:42:16 BST 2019 */

package uk.gov.dstl.baleen.types.structure;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

/**
 * A figure (or embedded media). Updated by JCasGen Wed Apr 17 13:42:16 BST 2019 XML source:
 * types/structure_type_system.xml
 *
 * @generated
 */
public class Figure extends Structure {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.structure.Figure";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Figure.class);
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

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_target = TypeSystemImpl.createCallSite(Figure.class, "target");
  private static final MethodHandle _FH_target = _FC_target.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Figure() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Figure(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Figure(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Figure(JCas jcas, int begin, int end) {
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
   * getter for target - gets The target of the media if available. (eg the src of an img tag).
   *
   * @generated
   * @return value of the feature
   */
  public String getTarget() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_target));
  }

  /**
   * setter for target - sets The target of the media if available. (eg the src of an img tag).
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setTarget(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_target), v);
  }
}
