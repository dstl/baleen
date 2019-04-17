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
 * The published ID of the document, e.g. the Document Reference Updated by JCasGen Wed Apr 17
 * 13:41:56 BST 2019 XML source: types/metadata_type_system.xml
 *
 * @generated
 */
public class PublishedId extends BaleenAnnotation {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.metadata.PublishedId";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(PublishedId.class);
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

  public static final String _FeatName_value = "value";
  public static final String _FeatName_publishedIdType = "publishedIdType";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_value =
      TypeSystemImpl.createCallSite(PublishedId.class, "value");
  private static final MethodHandle _FH_value = _FC_value.dynamicInvoker();
  private static final CallSite _FC_publishedIdType =
      TypeSystemImpl.createCallSite(PublishedId.class, "publishedIdType");
  private static final MethodHandle _FH_publishedIdType = _FC_publishedIdType.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected PublishedId() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public PublishedId(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public PublishedId(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public PublishedId(JCas jcas, int begin, int end) {
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
  // * Feature: value

  /**
   * getter for value - gets
   *
   * @generated
   * @return value of the feature
   */
  public String getValue() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_value));
  }

  /**
   * setter for value - sets
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setValue(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_value), v);
  }

  // *--------------*
  // * Feature: publishedIdType

  /**
   * getter for publishedIdType - gets The type of PublishedID that this particular annotation
   * refers to
   *
   * @generated
   * @return value of the feature
   */
  public String getPublishedIdType() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_publishedIdType));
  }

  /**
   * setter for publishedIdType - sets The type of PublishedID that this particular annotation
   * refers to
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setPublishedIdType(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_publishedIdType), v);
  }
}
