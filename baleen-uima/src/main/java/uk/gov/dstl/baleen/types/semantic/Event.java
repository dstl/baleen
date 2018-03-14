/* First created by JCasGen Wed Jan 21 11:22:53 GMT 2015 */
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.types.semantic;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.core.history.Recordable;
import uk.gov.dstl.baleen.types.Base;
import uk.gov.dstl.baleen.types.language.WordToken;

/**
 * An event relates one or more entities with an associated action. Updated by JCasGen Wed Apr 13
 * 13:23:16 BST 2016 XML source:
 * H:/git/TextProcessing/core/baleen/baleen-uima/src/main/resources/types/common_type_system.xml
 *
 * @generated
 */
public class Event extends Base implements Recordable {
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
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   */
  public Event(int addr, TOP_Type type) {
    super(addr, type);
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
    if (Event_Type.featOkTst && ((Event_Type) jcasType).casFeat_eventType == null)
      jcasType.jcas.throwFeatMissing("eventType", "uk.gov.dstl.baleen.types.semantic.Event");
    return (StringArray)
        (jcasType.ll_cas.ll_getFSForRef(
            jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type) jcasType).casFeatCode_eventType)));
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
    if (Event_Type.featOkTst && ((Event_Type) jcasType).casFeat_eventType == null)
      jcasType.jcas.throwFeatMissing("eventType", "uk.gov.dstl.baleen.types.semantic.Event");
    jcasType.ll_cas.ll_setRefValue(
        addr, ((Event_Type) jcasType).casFeatCode_eventType, jcasType.ll_cas.ll_getFSRef(v));
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
    if (Event_Type.featOkTst && ((Event_Type) jcasType).casFeat_eventType == null)
      jcasType.jcas.throwFeatMissing("eventType", "uk.gov.dstl.baleen.types.semantic.Event");
    jcasType.jcas.checkArrayBounds(
        jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type) jcasType).casFeatCode_eventType), i);
    return jcasType.ll_cas.ll_getStringArrayValue(
        jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type) jcasType).casFeatCode_eventType), i);
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
    if (Event_Type.featOkTst && ((Event_Type) jcasType).casFeat_eventType == null)
      jcasType.jcas.throwFeatMissing("eventType", "uk.gov.dstl.baleen.types.semantic.Event");
    jcasType.jcas.checkArrayBounds(
        jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type) jcasType).casFeatCode_eventType), i);
    jcasType.ll_cas.ll_setStringArrayValue(
        jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type) jcasType).casFeatCode_eventType), i, v);
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
    if (Event_Type.featOkTst && ((Event_Type) jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.semantic.Event");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Event_Type) jcasType).casFeatCode_value);
  }

  /**
   * setter for value - sets A textual representation of the event, typically this may be one or
   * more verbs from the sentence.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setValue(String v) {
    if (Event_Type.featOkTst && ((Event_Type) jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.semantic.Event");
    jcasType.ll_cas.ll_setStringValue(addr, ((Event_Type) jcasType).casFeatCode_value, v);
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
    if (Event_Type.featOkTst && ((Event_Type) jcasType).casFeat_entities == null)
      jcasType.jcas.throwFeatMissing("entities", "uk.gov.dstl.baleen.types.semantic.Event");
    return (FSArray)
        (jcasType.ll_cas.ll_getFSForRef(
            jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type) jcasType).casFeatCode_entities)));
  }

  /**
   * setter for entities - sets The entities which are involved / related / associated with the
   * event.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setEntities(FSArray v) {
    if (Event_Type.featOkTst && ((Event_Type) jcasType).casFeat_entities == null)
      jcasType.jcas.throwFeatMissing("entities", "uk.gov.dstl.baleen.types.semantic.Event");
    jcasType.ll_cas.ll_setRefValue(
        addr, ((Event_Type) jcasType).casFeatCode_entities, jcasType.ll_cas.ll_getFSRef(v));
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
    if (Event_Type.featOkTst && ((Event_Type) jcasType).casFeat_entities == null)
      jcasType.jcas.throwFeatMissing("entities", "uk.gov.dstl.baleen.types.semantic.Event");
    jcasType.jcas.checkArrayBounds(
        jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type) jcasType).casFeatCode_entities), i);
    return (Entity)
        (jcasType.ll_cas.ll_getFSForRef(
            jcasType.ll_cas.ll_getRefArrayValue(
                jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type) jcasType).casFeatCode_entities),
                i)));
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
    if (Event_Type.featOkTst && ((Event_Type) jcasType).casFeat_entities == null)
      jcasType.jcas.throwFeatMissing("entities", "uk.gov.dstl.baleen.types.semantic.Event");
    jcasType.jcas.checkArrayBounds(
        jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type) jcasType).casFeatCode_entities), i);
    jcasType.ll_cas.ll_setRefArrayValue(
        jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type) jcasType).casFeatCode_entities),
        i,
        jcasType.ll_cas.ll_getFSRef(v));
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
    if (Event_Type.featOkTst && ((Event_Type) jcasType).casFeat_arguments == null)
      jcasType.jcas.throwFeatMissing("arguments", "uk.gov.dstl.baleen.types.semantic.Event");
    return (StringArray)
        (jcasType.ll_cas.ll_getFSForRef(
            jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type) jcasType).casFeatCode_arguments)));
  }

  /**
   * setter for arguments - sets Additional text information, such as subject/object, in addition to
   * the entities.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setArguments(StringArray v) {
    if (Event_Type.featOkTst && ((Event_Type) jcasType).casFeat_arguments == null)
      jcasType.jcas.throwFeatMissing("arguments", "uk.gov.dstl.baleen.types.semantic.Event");
    jcasType.ll_cas.ll_setRefValue(
        addr, ((Event_Type) jcasType).casFeatCode_arguments, jcasType.ll_cas.ll_getFSRef(v));
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
    if (Event_Type.featOkTst && ((Event_Type) jcasType).casFeat_arguments == null)
      jcasType.jcas.throwFeatMissing("arguments", "uk.gov.dstl.baleen.types.semantic.Event");
    jcasType.jcas.checkArrayBounds(
        jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type) jcasType).casFeatCode_arguments), i);
    return jcasType.ll_cas.ll_getStringArrayValue(
        jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type) jcasType).casFeatCode_arguments), i);
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
    if (Event_Type.featOkTst && ((Event_Type) jcasType).casFeat_arguments == null)
      jcasType.jcas.throwFeatMissing("arguments", "uk.gov.dstl.baleen.types.semantic.Event");
    jcasType.jcas.checkArrayBounds(
        jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type) jcasType).casFeatCode_arguments), i);
    jcasType.ll_cas.ll_setStringArrayValue(
        jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type) jcasType).casFeatCode_arguments), i, v);
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
    if (Event_Type.featOkTst && ((Event_Type) jcasType).casFeat_tokens == null)
      jcasType.jcas.throwFeatMissing("tokens", "uk.gov.dstl.baleen.types.semantic.Event");
    return (FSArray)
        (jcasType.ll_cas.ll_getFSForRef(
            jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type) jcasType).casFeatCode_tokens)));
  }

  /**
   * setter for tokens - sets WordTokens which relate to the event type (eg verbs in the sentence)
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setTokens(FSArray v) {
    if (Event_Type.featOkTst && ((Event_Type) jcasType).casFeat_tokens == null)
      jcasType.jcas.throwFeatMissing("tokens", "uk.gov.dstl.baleen.types.semantic.Event");
    jcasType.ll_cas.ll_setRefValue(
        addr, ((Event_Type) jcasType).casFeatCode_tokens, jcasType.ll_cas.ll_getFSRef(v));
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
    if (Event_Type.featOkTst && ((Event_Type) jcasType).casFeat_tokens == null)
      jcasType.jcas.throwFeatMissing("tokens", "uk.gov.dstl.baleen.types.semantic.Event");
    jcasType.jcas.checkArrayBounds(
        jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type) jcasType).casFeatCode_tokens), i);
    return (WordToken)
        (jcasType.ll_cas.ll_getFSForRef(
            jcasType.ll_cas.ll_getRefArrayValue(
                jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type) jcasType).casFeatCode_tokens),
                i)));
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
    if (Event_Type.featOkTst && ((Event_Type) jcasType).casFeat_tokens == null)
      jcasType.jcas.throwFeatMissing("tokens", "uk.gov.dstl.baleen.types.semantic.Event");
    jcasType.jcas.checkArrayBounds(
        jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type) jcasType).casFeatCode_tokens), i);
    jcasType.ll_cas.ll_setRefArrayValue(
        jcasType.ll_cas.ll_getRefValue(addr, ((Event_Type) jcasType).casFeatCode_tokens),
        i,
        jcasType.ll_cas.ll_getFSRef(v));
  }
}
