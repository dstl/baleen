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
 * An ordered component of a document such as a list. Updated by JCasGen Wed Apr 17 13:42:16 BST
 * 2019 XML source: types/structure_type_system.xml
 *
 * @generated
 */
public class Ordered extends Structure {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.structure.Ordered";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Ordered.class);
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

  public static final String _FeatName_level = "level";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_level = TypeSystemImpl.createCallSite(Ordered.class, "level");
  private static final MethodHandle _FH_level = _FC_level.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Ordered() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Ordered(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Ordered(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Ordered(JCas jcas, int begin, int end) {
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
  // * Feature: level

  /**
   * getter for level - gets The level of the item.
   *
   * @generated
   * @return value of the feature
   */
  public int getLevel() {
    return _getIntValueNc(wrapGetIntCatchException(_FH_level));
  }

  /**
   * setter for level - sets The level of the item.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setLevel(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_level), v);
  }
}
