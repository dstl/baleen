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
 * Specific amount of some current mentioned within the document. Updated by JCasGen Wed Apr 17
 * 13:41:42 BST 2019 XML source: types/common_type_system.xml
 *
 * @generated
 */
public class Money extends Entity {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.common.Money";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Money.class);
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

  public static final String _FeatName_amount = "amount";
  public static final String _FeatName_currency = "currency";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_amount = TypeSystemImpl.createCallSite(Money.class, "amount");
  private static final MethodHandle _FH_amount = _FC_amount.dynamicInvoker();
  private static final CallSite _FC_currency =
      TypeSystemImpl.createCallSite(Money.class, "currency");
  private static final MethodHandle _FH_currency = _FC_currency.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Money() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Money(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Money(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Money(JCas jcas, int begin, int end) {
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
  // * Feature: amount

  /**
   * getter for amount - gets Numeric value of amount of money mentioned in document.
   *
   * @generated
   * @return value of the feature
   */
  public double getAmount() {
    return _getDoubleValueNc(wrapGetIntCatchException(_FH_amount));
  }

  /**
   * setter for amount - sets Numeric value of amount of money mentioned in document.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setAmount(double v) {
    _setDoubleValueNfc(wrapGetIntCatchException(_FH_amount), v);
  }

  // *--------------*
  // * Feature: currency

  /**
   * getter for currency - gets String value of the currency denomination the money amount is
   * specified in.
   *
   * @generated
   * @return value of the feature
   */
  public String getCurrency() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_currency));
  }

  /**
   * setter for currency - sets String value of the currency denomination the money amount is
   * specified in.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setCurrency(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_currency), v);
  }
}
