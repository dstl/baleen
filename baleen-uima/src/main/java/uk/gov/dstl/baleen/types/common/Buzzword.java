/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:41:42 BST 2019 */

package uk.gov.dstl.baleen.types.common;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.StringArray;

import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * User-defined key phrases or domain-specific terms, described by a type property. Updated by
 * JCasGen Wed Apr 17 13:41:42 BST 2019 XML source: types/common_type_system.xml
 *
 * @generated
 */
public class Buzzword extends Entity {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.common.Buzzword";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Buzzword.class);
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

  public static final String _FeatName_tags = "tags";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_tags = TypeSystemImpl.createCallSite(Buzzword.class, "tags");
  private static final MethodHandle _FH_tags = _FC_tags.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Buzzword() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Buzzword(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Buzzword(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Buzzword(JCas jcas, int begin, int end) {
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
  // * Feature: tags

  /**
   * getter for tags - gets A list of types that are associated with a given BuzzWord value.
   *
   * @generated
   * @return value of the feature
   */
  public StringArray getTags() {
    return (StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_tags)));
  }

  /**
   * setter for tags - sets A list of types that are associated with a given BuzzWord value.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setTags(StringArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_tags), v);
  }

  /**
   * indexed getter for tags - gets an indexed value - A list of types that are associated with a
   * given BuzzWord value.
   *
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i
   */
  public String getTags(int i) {
    return ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_tags)))).get(i);
  }

  /**
   * indexed setter for tags - sets an indexed value - A list of types that are associated with a
   * given BuzzWord value.
   *
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array
   */
  public void setTags(int i, String v) {
    ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_tags)))).set(i, v);
  }
}
