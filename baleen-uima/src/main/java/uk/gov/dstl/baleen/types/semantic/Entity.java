/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:42:08 BST 2019 */

package uk.gov.dstl.baleen.types.semantic;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.core.history.Recordable;
import uk.gov.dstl.baleen.types.Base;

/**
 * Type to represent named entities - values that are assigned a semantic type. Updated by JCasGen
 * Wed Apr 17 13:42:08 BST 2019 XML source: types/military_type_system.xml
 *
 * @generated
 */
public class Entity extends Base implements Recordable {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.semantic.Entity";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Entity.class);
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
  public static final String _FeatName_referent = "referent";
  public static final String _FeatName_isNormalised = "isNormalised";
  public static final String _FeatName_subType = "subType";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_value = TypeSystemImpl.createCallSite(Entity.class, "value");
  private static final MethodHandle _FH_value = _FC_value.dynamicInvoker();
  private static final CallSite _FC_referent =
      TypeSystemImpl.createCallSite(Entity.class, "referent");
  private static final MethodHandle _FH_referent = _FC_referent.dynamicInvoker();
  private static final CallSite _FC_isNormalised =
      TypeSystemImpl.createCallSite(Entity.class, "isNormalised");
  private static final MethodHandle _FH_isNormalised = _FC_isNormalised.dynamicInvoker();
  private static final CallSite _FC_subType =
      TypeSystemImpl.createCallSite(Entity.class, "subType");
  private static final MethodHandle _FH_subType = _FC_subType.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Entity() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Entity(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Entity(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Entity(JCas jcas, int begin, int end) {
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
   * getter for value - gets A value which reflects the name of the entity. May or may not differ
   * from underlying span from the document.
   *
   * @generated
   * @return value of the feature
   */
  public String getValue() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_value));
  }

  /**
   * setter for value - sets A value which reflects the name of the entity. May or may not differ
   * from underlying span from the document.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setValue(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_value), v);
  }

  // *--------------*
  // * Feature: referent

  /**
   * getter for referent - gets Can be used to link a corefence to an entity to another (presuambly
   * more definitive) mention of the same entity elsewhere in the text.
   *
   * @generated
   * @return value of the feature
   */
  public ReferenceTarget getReferent() {
    return (ReferenceTarget) (_getFeatureValueNc(wrapGetIntCatchException(_FH_referent)));
  }

  /**
   * setter for referent - sets Can be used to link a corefence to an entity to another (presuambly
   * more definitive) mention of the same entity elsewhere in the text.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setReferent(ReferenceTarget v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_referent), v);
  }

  // *--------------*
  // * Feature: isNormalised

  /**
   * getter for isNormalised - gets Marks the entity value as having been normalised from the
   * original value
   *
   * @generated
   * @return value of the feature
   */
  public boolean getIsNormalised() {
    return _getBooleanValueNc(wrapGetIntCatchException(_FH_isNormalised));
  }

  /**
   * setter for isNormalised - sets Marks the entity value as having been normalised from the
   * original value
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setIsNormalised(boolean v) {
    _setBooleanValueNfc(wrapGetIntCatchException(_FH_isNormalised), v);
  }

  // *--------------*
  // * Feature: subType

  /**
   * getter for subType - gets String identifying sub type of entity.
   *
   * @generated
   * @return value of the feature
   */
  public String getSubType() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_subType));
  }

  /**
   * setter for subType - sets String identifying sub type of entity.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setSubType(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_subType), v);
  }
}
