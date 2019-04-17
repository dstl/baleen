/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:42:16 BST 2019 */

package uk.gov.dstl.baleen.types.structure;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.Base;

/**
 * A base type for all Structure types. Updated by JCasGen Wed Apr 17 13:42:16 BST 2019 XML source:
 * types/structure_type_system.xml
 *
 * @generated
 */
public class Structure extends Base {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.structure.Structure";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Structure.class);
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

  public static final String _FeatName_depth = "depth";
  public static final String _FeatName_elementClass = "elementClass";
  public static final String _FeatName_elementId = "elementId";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_depth = TypeSystemImpl.createCallSite(Structure.class, "depth");
  private static final MethodHandle _FH_depth = _FC_depth.dynamicInvoker();
  private static final CallSite _FC_elementClass =
      TypeSystemImpl.createCallSite(Structure.class, "elementClass");
  private static final MethodHandle _FH_elementClass = _FC_elementClass.dynamicInvoker();
  private static final CallSite _FC_elementId =
      TypeSystemImpl.createCallSite(Structure.class, "elementId");
  private static final MethodHandle _FH_elementId = _FC_elementId.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Structure() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Structure(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Structure(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Structure(JCas jcas, int begin, int end) {
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
  // * Feature: depth

  /**
   * getter for depth - gets The depth of the structural component.
   *
   * @generated
   * @return value of the feature
   */
  public int getDepth() {
    return _getIntValueNc(wrapGetIntCatchException(_FH_depth));
  }

  /**
   * setter for depth - sets The depth of the structural component.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setDepth(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_depth), v);
  }

  // *--------------*
  // * Feature: elementClass

  /**
   * getter for elementClass - gets A holder for further class information, say a more specific html
   * class or a defined word style.
   *
   * @generated
   * @return value of the feature
   */
  public String getElementClass() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_elementClass));
  }

  /**
   * setter for elementClass - sets A holder for further class information, say a more specific html
   * class or a defined word style.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setElementClass(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_elementClass), v);
  }

  // *--------------*
  // * Feature: elementId

  /**
   * getter for elementId - gets A holder for an id, if defined from the format.
   *
   * @generated
   * @return value of the feature
   */
  public String getElementId() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_elementId));
  }

  /**
   * setter for elementId - sets A holder for an id, if defined from the format.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setElementId(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_elementId), v);
  }
}
