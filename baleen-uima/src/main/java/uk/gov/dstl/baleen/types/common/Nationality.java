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
 * Nationality denonym (e.g. French, British, Spanish) Updated by JCasGen Wed Apr 17 13:41:42 BST
 * 2019 XML source: types/common_type_system.xml
 *
 * @generated
 */
public class Nationality extends Entity {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.common.Nationality";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Nationality.class);
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

  public static final String _FeatName_countryCode = "countryCode";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_countryCode =
      TypeSystemImpl.createCallSite(Nationality.class, "countryCode");
  private static final MethodHandle _FH_countryCode = _FC_countryCode.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Nationality() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Nationality(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Nationality(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Nationality(JCas jcas, int begin, int end) {
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
  // * Feature: countryCode

  /**
   * getter for countryCode - gets The country code associated with this nationality, e.g. FRA for
   * French
   *
   * @generated
   * @return value of the feature
   */
  public String getCountryCode() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_countryCode));
  }

  /**
   * setter for countryCode - sets The country code associated with this nationality, e.g. FRA for
   * French
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setCountryCode(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_countryCode), v);
  }
}
