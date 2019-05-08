/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:42:08 BST 2019 */

package uk.gov.dstl.baleen.types.language;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.BaleenAnnotation;

/**
 * A word which acts as a relation in a sentence (eg 'saw' in the 'John saw the car'). Updated by
 * JCasGen Wed Apr 17 13:42:08 BST 2019 XML source: types/military_type_system.xml
 *
 * @generated
 */
public class Interaction extends BaleenAnnotation {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.language.Interaction";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Interaction.class);
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
  public static final String _FeatName_relationshipType = "relationshipType";
  public static final String _FeatName_relationSubType = "relationSubType";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_value =
      TypeSystemImpl.createCallSite(Interaction.class, "value");
  private static final MethodHandle _FH_value = _FC_value.dynamicInvoker();
  private static final CallSite _FC_relationshipType =
      TypeSystemImpl.createCallSite(Interaction.class, "relationshipType");
  private static final MethodHandle _FH_relationshipType = _FC_relationshipType.dynamicInvoker();
  private static final CallSite _FC_relationSubType =
      TypeSystemImpl.createCallSite(Interaction.class, "relationSubType");
  private static final MethodHandle _FH_relationSubType = _FC_relationSubType.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Interaction() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Interaction(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Interaction(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Interaction(JCas jcas, int begin, int end) {
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
   * getter for value - gets The value of the relationship, typically the spanning text within the
   * sentence or the .
   *
   * @generated
   * @return value of the feature
   */
  public String getValue() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_value));
  }

  /**
   * setter for value - sets The value of the relationship, typically the spanning text within the
   * sentence or the .
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setValue(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_value), v);
  }

  // *--------------*
  // * Feature: relationshipType

  /**
   * getter for relationshipType - gets Denotes the semantic type of the relationship between
   * entities.
   *
   * @generated
   * @return value of the feature
   */
  public String getRelationshipType() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_relationshipType));
  }

  /**
   * setter for relationshipType - sets Denotes the semantic type of the relationship between
   * entities.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setRelationshipType(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_relationshipType), v);
  }

  // *--------------*
  // * Feature: relationSubType

  /**
   * getter for relationSubType - gets Used to record any sub-type information for the relation, for
   * example the sub-relations defined within the ACE dataset.
   *
   * @generated
   * @return value of the feature
   */
  public String getRelationSubType() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_relationSubType));
  }

  /**
   * setter for relationSubType - sets Used to record any sub-type information for the relation, for
   * example the sub-relations defined within the ACE dataset.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setRelationSubType(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_relationSubType), v);
  }
}
