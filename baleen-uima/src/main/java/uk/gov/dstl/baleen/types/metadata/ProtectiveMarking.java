/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:41:56 BST 2019 */

package uk.gov.dstl.baleen.types.metadata;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.StringArray;

import uk.gov.dstl.baleen.types.BaleenAnnotation;

/**
 * The protective marking of the text span defined by the begin and end properties. Updated by
 * JCasGen Wed Apr 17 13:41:56 BST 2019 XML source: types/metadata_type_system.xml
 *
 * @generated
 */
public class ProtectiveMarking extends BaleenAnnotation {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(ProtectiveMarking.class);
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

  public static final String _FeatName_classification = "classification";
  public static final String _FeatName_caveats = "caveats";
  public static final String _FeatName_releasability = "releasability";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_classification =
      TypeSystemImpl.createCallSite(ProtectiveMarking.class, "classification");
  private static final MethodHandle _FH_classification = _FC_classification.dynamicInvoker();
  private static final CallSite _FC_caveats =
      TypeSystemImpl.createCallSite(ProtectiveMarking.class, "caveats");
  private static final MethodHandle _FH_caveats = _FC_caveats.dynamicInvoker();
  private static final CallSite _FC_releasability =
      TypeSystemImpl.createCallSite(ProtectiveMarking.class, "releasability");
  private static final MethodHandle _FH_releasability = _FC_releasability.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected ProtectiveMarking() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public ProtectiveMarking(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public ProtectiveMarking(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public ProtectiveMarking(JCas jcas, int begin, int end) {
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
  // * Feature: classification

  /**
   * getter for classification - gets The security classification of this protective marking
   *
   * @generated
   * @return value of the feature
   */
  public String getClassification() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_classification));
  }

  /**
   * setter for classification - sets The security classification of this protective marking
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setClassification(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_classification), v);
  }

  // *--------------*
  // * Feature: caveats

  /**
   * getter for caveats - gets An array of string values specifying handling caveats for this
   * protective marking
   *
   * @generated
   * @return value of the feature
   */
  public StringArray getCaveats() {
    return (StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_caveats)));
  }

  /**
   * setter for caveats - sets An array of string values specifying handling caveats for this
   * protective marking
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setCaveats(StringArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_caveats), v);
  }

  /**
   * indexed getter for caveats - gets an indexed value - An array of string values specifying
   * handling caveats for this protective marking
   *
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i
   */
  public String getCaveats(int i) {
    return ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_caveats)))).get(i);
  }

  /**
   * indexed setter for caveats - sets an indexed value - An array of string values specifying
   * handling caveats for this protective marking
   *
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array
   */
  public void setCaveats(int i, String v) {
    ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_caveats)))).set(i, v);
  }

  // *--------------*
  // * Feature: releasability

  /**
   * getter for releasability - gets Array of country designators to which this protective marking
   * is releasable.
   *
   * @generated
   * @return value of the feature
   */
  public StringArray getReleasability() {
    return (StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_releasability)));
  }

  /**
   * setter for releasability - sets Array of country designators to which this protective marking
   * is releasable.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setReleasability(StringArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_releasability), v);
  }

  /**
   * indexed getter for releasability - gets an indexed value - Array of country designators to
   * which this protective marking is releasable.
   *
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i
   */
  public String getReleasability(int i) {
    return ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_releasability)))).get(i);
  }

  /**
   * indexed setter for releasability - sets an indexed value - Array of country designators to
   * which this protective marking is releasable.
   *
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array
   */
  public void setReleasability(int i, String v) {
    ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_releasability)))).set(i, v);
  }
}
