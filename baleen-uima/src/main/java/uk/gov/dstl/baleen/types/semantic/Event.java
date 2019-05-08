/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:42:08 BST 2019 */

package uk.gov.dstl.baleen.types.semantic;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;

import uk.gov.dstl.baleen.types.Base;
import uk.gov.dstl.baleen.types.language.WordToken;

/**
 * An event relates one or more entities with an associated action. Updated by JCasGen Wed Apr 17
 * 13:42:08 BST 2019 XML source: types/military_type_system.xml
 *
 * @generated
 */
public class Event extends Base {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.semantic.Event";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Event.class);
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

  public static final String _FeatName_eventType = "eventType";
  public static final String _FeatName_value = "value";
  public static final String _FeatName_entities = "entities";
  public static final String _FeatName_arguments = "arguments";
  public static final String _FeatName_tokens = "tokens";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_eventType =
      TypeSystemImpl.createCallSite(Event.class, "eventType");
  private static final MethodHandle _FH_eventType = _FC_eventType.dynamicInvoker();
  private static final CallSite _FC_value = TypeSystemImpl.createCallSite(Event.class, "value");
  private static final MethodHandle _FH_value = _FC_value.dynamicInvoker();
  private static final CallSite _FC_entities =
      TypeSystemImpl.createCallSite(Event.class, "entities");
  private static final MethodHandle _FH_entities = _FC_entities.dynamicInvoker();
  private static final CallSite _FC_arguments =
      TypeSystemImpl.createCallSite(Event.class, "arguments");
  private static final MethodHandle _FH_arguments = _FC_arguments.dynamicInvoker();
  private static final CallSite _FC_tokens = TypeSystemImpl.createCallSite(Event.class, "tokens");
  private static final MethodHandle _FH_tokens = _FC_tokens.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Event() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Event(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Event(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Event(JCas jcas, int begin, int end) {
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
  // * Feature: eventType

  /**
   * getter for eventType - gets The event type which classifies the event.
   *
   * <p>For example Currently based on the ACE2002 top-level relationships: {"AT" , "NEAR" , "PART"
   * , "ROLE" , "SOCIAL"} Additional relationship types that have been added: {"QUANTITY", "ALIAS"}
   *
   * @generated
   * @return value of the feature
   */
  public StringArray getEventType() {
    return (StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_eventType)));
  }

  /**
   * setter for eventType - sets The event type which classifies the event.
   *
   * <p>For example Currently based on the ACE2002 top-level relationships: {"AT" , "NEAR" , "PART"
   * , "ROLE" , "SOCIAL"} Additional relationship types that have been added: {"QUANTITY", "ALIAS"}
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setEventType(StringArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_eventType), v);
  }

  /**
   * indexed getter for eventType - gets an indexed value - The event type which classifies the
   * event.
   *
   * <p>For example Currently based on the ACE2002 top-level relationships: {"AT" , "NEAR" , "PART"
   * , "ROLE" , "SOCIAL"} Additional relationship types that have been added: {"QUANTITY", "ALIAS"}
   *
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i
   */
  public String getEventType(int i) {
    return ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_eventType)))).get(i);
  }

  /**
   * indexed setter for eventType - sets an indexed value - The event type which classifies the
   * event.
   *
   * <p>For example Currently based on the ACE2002 top-level relationships: {"AT" , "NEAR" , "PART"
   * , "ROLE" , "SOCIAL"} Additional relationship types that have been added: {"QUANTITY", "ALIAS"}
   *
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array
   */
  public void setEventType(int i, String v) {
    ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_eventType)))).set(i, v);
  }

  // *--------------*
  // * Feature: value

  /**
   * getter for value - gets A textual representation of the event, typically this may be one or
   * more verbs from the sentence.
   *
   * @generated
   * @return value of the feature
   */
  public String getValue() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_value));
  }

  /**
   * setter for value - sets A textual representation of the event, typically this may be one or
   * more verbs from the sentence.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setValue(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_value), v);
  }

  // *--------------*
  // * Feature: entities

  /**
   * getter for entities - gets The entities which are involved / related / associated with the
   * event.
   *
   * @generated
   * @return value of the feature
   */
  public FSArray getEntities() {
    return (FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_entities)));
  }

  /**
   * setter for entities - sets The entities which are involved / related / associated with the
   * event.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setEntities(FSArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_entities), v);
  }

  /**
   * indexed getter for entities - gets an indexed value - The entities which are involved / related
   * / associated with the event.
   *
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i
   */
  public Entity getEntities(int i) {
    return (Entity)
        (((FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_entities)))).get(i));
  }

  /**
   * indexed setter for entities - sets an indexed value - The entities which are involved / related
   * / associated with the event.
   *
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array
   */
  public void setEntities(int i, Entity v) {
    ((FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_entities)))).set(i, v);
  }

  // *--------------*
  // * Feature: arguments

  /**
   * getter for arguments - gets Additional text information, such as subject/object, in addition to
   * the entities.
   *
   * @generated
   * @return value of the feature
   */
  public StringArray getArguments() {
    return (StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_arguments)));
  }

  /**
   * setter for arguments - sets Additional text information, such as subject/object, in addition to
   * the entities.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setArguments(StringArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_arguments), v);
  }

  /**
   * indexed getter for arguments - gets an indexed value - Additional text information, such as
   * subject/object, in addition to the entities.
   *
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i
   */
  public String getArguments(int i) {
    return ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_arguments)))).get(i);
  }

  /**
   * indexed setter for arguments - sets an indexed value - Additional text information, such as
   * subject/object, in addition to the entities.
   *
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array
   */
  public void setArguments(int i, String v) {
    ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_arguments)))).set(i, v);
  }

  // *--------------*
  // * Feature: tokens

  /**
   * getter for tokens - gets WordTokens which relate to the event type (eg verbs in the sentence)
   *
   * @generated
   * @return value of the feature
   */
  public FSArray getTokens() {
    return (FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_tokens)));
  }

  /**
   * setter for tokens - sets WordTokens which relate to the event type (eg verbs in the sentence)
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setTokens(FSArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_tokens), v);
  }

  /**
   * indexed getter for tokens - gets an indexed value - WordTokens which relate to the event type
   * (eg verbs in the sentence)
   *
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i
   */
  public WordToken getTokens(int i) {
    return (WordToken)
        (((FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_tokens)))).get(i));
  }

  /**
   * indexed setter for tokens - sets an indexed value - WordTokens which relate to the event type
   * (eg verbs in the sentence)
   *
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array
   */
  public void setTokens(int i, WordToken v) {
    ((FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_tokens)))).set(i, v);
  }
}
