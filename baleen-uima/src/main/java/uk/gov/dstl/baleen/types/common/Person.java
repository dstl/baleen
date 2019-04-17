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
 * A Person named entitiy, as defined by an explict name reference within the source document.
 * Updated by JCasGen Wed Apr 17 13:41:42 BST 2019 XML source: types/common_type_system.xml
 *
 * @generated
 */
public class Person extends Entity {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.common.Person";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Person.class);
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

  public static final String _FeatName_title = "title";
  public static final String _FeatName_gender = "gender";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_title = TypeSystemImpl.createCallSite(Person.class, "title");
  private static final MethodHandle _FH_title = _FC_title.dynamicInvoker();
  private static final CallSite _FC_gender = TypeSystemImpl.createCallSite(Person.class, "gender");
  private static final MethodHandle _FH_gender = _FC_gender.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Person() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Person(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Person(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Person(JCas jcas, int begin, int end) {
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
  // * Feature: title

  /**
   * getter for title - gets A person's title, for example Mr or President
   *
   * @generated
   * @return value of the feature
   */
  public String getTitle() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_title));
  }

  /**
   * setter for title - sets A person's title, for example Mr or President
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setTitle(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_title), v);
  }

  // *--------------*
  // * Feature: gender

  /**
   * getter for gender - gets Should be one of the following: MALE FEMALE UNKNOWN
   *
   * <p>If empty or null, it is assumed to be UNKNOWN.
   *
   * @generated
   * @return value of the feature
   */
  public String getGender() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_gender));
  }

  /**
   * setter for gender - sets Should be one of the following: MALE FEMALE UNKNOWN
   *
   * <p>If empty or null, it is assumed to be UNKNOWN.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setGender(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_gender), v);
  }
}
