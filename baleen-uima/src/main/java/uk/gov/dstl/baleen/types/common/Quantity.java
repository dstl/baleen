/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:41:42 BST 2019 */

package uk.gov.dstl.baleen.types.common;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * Type to annotate references to quantities within text Updated by JCasGen Wed Apr 17 13:41:42 BST
 * 2019 XML source: types/common_type_system.xml
 *
 * @generated
 */
public class Quantity extends Entity {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.common.Quantity";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Quantity.class);
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

  public static final String _FeatName_normalizedUnit = "normalizedUnit";
  public static final String _FeatName_normalizedQuantity = "normalizedQuantity";
  public static final String _FeatName_unit = "unit";
  public static final String _FeatName_quantity = "quantity";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_normalizedUnit =
      TypeSystemImpl.createCallSite(Quantity.class, "normalizedUnit");
  private static final MethodHandle _FH_normalizedUnit = _FC_normalizedUnit.dynamicInvoker();
  private static final CallSite _FC_normalizedQuantity =
      TypeSystemImpl.createCallSite(Quantity.class, "normalizedQuantity");
  private static final MethodHandle _FH_normalizedQuantity =
      _FC_normalizedQuantity.dynamicInvoker();
  private static final CallSite _FC_unit = TypeSystemImpl.createCallSite(Quantity.class, "unit");
  private static final MethodHandle _FH_unit = _FC_unit.dynamicInvoker();
  private static final CallSite _FC_quantity =
      TypeSystemImpl.createCallSite(Quantity.class, "quantity");
  private static final MethodHandle _FH_quantity = _FC_quantity.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Quantity() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Quantity(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Quantity(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Quantity(JCas jcas, int begin, int end) {
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
  // * Feature: normalizedUnit

  /**
   * getter for normalizedUnit - gets The unit of the normalized quantity
   *
   * @generated
   * @return value of the feature
   */
  public String getNormalizedUnit() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_normalizedUnit));
  }

  /**
   * setter for normalizedUnit - sets The unit of the normalized quantity
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setNormalizedUnit(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_normalizedUnit), v);
  }

  // *--------------*
  // * Feature: normalizedQuantity

  /**
   * getter for normalizedQuantity - gets The normalized quantity
   *
   * @generated
   * @return value of the feature
   */
  public double getNormalizedQuantity() {
    return _getDoubleValueNc(wrapGetIntCatchException(_FH_normalizedQuantity));
  }

  /**
   * setter for normalizedQuantity - sets The normalized quantity
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setNormalizedQuantity(double v) {
    _setDoubleValueNfc(wrapGetIntCatchException(_FH_normalizedQuantity), v);
  }

  // *--------------*
  // * Feature: unit

  /**
   * getter for unit - gets The unit of the raw quantity
   *
   * @generated
   * @return value of the feature
   */
  public String getUnit() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_unit));
  }

  /**
   * setter for unit - sets The unit of the raw quantity
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setUnit(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_unit), v);
  }

  // *--------------*
  // * Feature: quantity

  /**
   * getter for quantity - gets The raw quantity
   *
   * @generated
   * @return value of the feature
   */
  public double getQuantity() {
    return _getDoubleValueNc(wrapGetIntCatchException(_FH_quantity));
  }

  /**
   * setter for quantity - sets The raw quantity
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setQuantity(double v) {
    _setDoubleValueNfc(wrapGetIntCatchException(_FH_quantity), v);
  }
}
