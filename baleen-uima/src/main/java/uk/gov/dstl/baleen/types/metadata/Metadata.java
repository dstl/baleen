/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:41:56 BST 2019 */

package uk.gov.dstl.baleen.types.metadata;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.BaleenAnnotation;

/**
 * Metadata associated with the document Updated by JCasGen Wed Apr 17 13:41:56 BST 2019 XML source:
 * types/metadata_type_system.xml
 *
 * @generated
 */
public class Metadata extends BaleenAnnotation {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.metadata.Metadata";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Metadata.class);
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

  public static final String _FeatName_key = "key";
  public static final String _FeatName_value = "value";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_key = TypeSystemImpl.createCallSite(Metadata.class, "key");
  private static final MethodHandle _FH_key = _FC_key.dynamicInvoker();
  private static final CallSite _FC_value = TypeSystemImpl.createCallSite(Metadata.class, "value");
  private static final MethodHandle _FH_value = _FC_value.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Metadata() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Metadata(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Metadata(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Metadata(JCas jcas, int begin, int end) {
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
  // * Feature: key

  /**
   * getter for key - gets The key (name) for the metadata
   *
   * @generated
   * @return value of the feature
   */
  public String getKey() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_key));
  }

  /**
   * setter for key - sets The key (name) for the metadata
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setKey(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_key), v);
  }

  // *--------------*
  // * Feature: value

  /**
   * getter for value - gets The value of the metadata
   *
   * @generated
   * @return value of the feature
   */
  public String getValue() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_value));
  }

  /**
   * setter for value - sets The value of the metadata
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setValue(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_value), v);
  }
}
