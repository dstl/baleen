/* First created by JCasGen Wed Jan 21 11:22:53 GMT 2015 */
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.types.semantic;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.Base_Type;

/**
 * An event relates one or more entities with an associated action. Updated by JCasGen Wed Apr 13
 * 13:23:16 BST 2016
 *
 * @generated
 */
public class Event_Type extends Base_Type {
  /** @generated */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = Event.typeIndexID;
  /**
   * @generated
   * @modifiable
   */
  @SuppressWarnings("hiding")
  public static final boolean featOkTst =
      JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.semantic.Event");

  /** @generated */
  final Feature casFeat_eventType;
  /** @generated */
  final int casFeatCode_eventType;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getEventType(int addr) {
    if (featOkTst && casFeat_eventType == null)
      jcas.throwFeatMissing("eventType", "uk.gov.dstl.baleen.types.semantic.Event");
    return ll_cas.ll_getRefValue(addr, casFeatCode_eventType);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setEventType(int addr, int v) {
    if (featOkTst && casFeat_eventType == null)
      jcas.throwFeatMissing("eventType", "uk.gov.dstl.baleen.types.semantic.Event");
    ll_cas.ll_setRefValue(addr, casFeatCode_eventType, v);
  }

  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array
   */
  public String getEventType(int addr, int i) {
    if (featOkTst && casFeat_eventType == null)
      jcas.throwFeatMissing("eventType", "uk.gov.dstl.baleen.types.semantic.Event");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(
          ll_cas.ll_getRefValue(addr, casFeatCode_eventType), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_eventType), i);
    return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_eventType), i);
  }

  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */
  public void setEventType(int addr, int i, String v) {
    if (featOkTst && casFeat_eventType == null)
      jcas.throwFeatMissing("eventType", "uk.gov.dstl.baleen.types.semantic.Event");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_eventType), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_eventType), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_eventType), i, v);
  }

  /** @generated */
  final Feature casFeat_value;
  /** @generated */
  final int casFeatCode_value;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public String getValue(int addr) {
    if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.semantic.Event");
    return ll_cas.ll_getStringValue(addr, casFeatCode_value);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setValue(int addr, String v) {
    if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.semantic.Event");
    ll_cas.ll_setStringValue(addr, casFeatCode_value, v);
  }

  /** @generated */
  final Feature casFeat_entities;
  /** @generated */
  final int casFeatCode_entities;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getEntities(int addr) {
    if (featOkTst && casFeat_entities == null)
      jcas.throwFeatMissing("entities", "uk.gov.dstl.baleen.types.semantic.Event");
    return ll_cas.ll_getRefValue(addr, casFeatCode_entities);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setEntities(int addr, int v) {
    if (featOkTst && casFeat_entities == null)
      jcas.throwFeatMissing("entities", "uk.gov.dstl.baleen.types.semantic.Event");
    ll_cas.ll_setRefValue(addr, casFeatCode_entities, v);
  }

  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array
   */
  public int getEntities(int addr, int i) {
    if (featOkTst && casFeat_entities == null)
      jcas.throwFeatMissing("entities", "uk.gov.dstl.baleen.types.semantic.Event");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_entities), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_entities), i);
    return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_entities), i);
  }

  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */
  public void setEntities(int addr, int i, int v) {
    if (featOkTst && casFeat_entities == null)
      jcas.throwFeatMissing("entities", "uk.gov.dstl.baleen.types.semantic.Event");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_entities), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_entities), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_entities), i, v);
  }

  /** @generated */
  final Feature casFeat_arguments;
  /** @generated */
  final int casFeatCode_arguments;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getArguments(int addr) {
    if (featOkTst && casFeat_arguments == null)
      jcas.throwFeatMissing("arguments", "uk.gov.dstl.baleen.types.semantic.Event");
    return ll_cas.ll_getRefValue(addr, casFeatCode_arguments);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setArguments(int addr, int v) {
    if (featOkTst && casFeat_arguments == null)
      jcas.throwFeatMissing("arguments", "uk.gov.dstl.baleen.types.semantic.Event");
    ll_cas.ll_setRefValue(addr, casFeatCode_arguments, v);
  }

  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array
   */
  public String getArguments(int addr, int i) {
    if (featOkTst && casFeat_arguments == null)
      jcas.throwFeatMissing("arguments", "uk.gov.dstl.baleen.types.semantic.Event");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(
          ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i);
    return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i);
  }

  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */
  public void setArguments(int addr, int i, String v) {
    if (featOkTst && casFeat_arguments == null)
      jcas.throwFeatMissing("arguments", "uk.gov.dstl.baleen.types.semantic.Event");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i, v);
  }

  /** @generated */
  final Feature casFeat_tokens;
  /** @generated */
  final int casFeatCode_tokens;
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getTokens(int addr) {
    if (featOkTst && casFeat_tokens == null)
      jcas.throwFeatMissing("tokens", "uk.gov.dstl.baleen.types.semantic.Event");
    return ll_cas.ll_getRefValue(addr, casFeatCode_tokens);
  }
  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setTokens(int addr, int v) {
    if (featOkTst && casFeat_tokens == null)
      jcas.throwFeatMissing("tokens", "uk.gov.dstl.baleen.types.semantic.Event");
    ll_cas.ll_setRefValue(addr, casFeatCode_tokens, v);
  }

  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array
   */
  public int getTokens(int addr, int i) {
    if (featOkTst && casFeat_tokens == null)
      jcas.throwFeatMissing("tokens", "uk.gov.dstl.baleen.types.semantic.Event");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tokens), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_tokens), i);
    return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tokens), i);
  }

  /**
   * @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */
  public void setTokens(int addr, int i, int v) {
    if (featOkTst && casFeat_tokens == null)
      jcas.throwFeatMissing("tokens", "uk.gov.dstl.baleen.types.semantic.Event");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tokens), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_tokens), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tokens), i, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   *
   * @generated
   * @param jcas JCas
   * @param casType Type
   */
  public Event_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_eventType =
        jcas.getRequiredFeatureDE(casType, "eventType", "uima.cas.StringArray", featOkTst);
    casFeatCode_eventType =
        (null == casFeat_eventType)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_eventType).getCode();

    casFeat_value = jcas.getRequiredFeatureDE(casType, "value", "uima.cas.String", featOkTst);
    casFeatCode_value =
        (null == casFeat_value)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_value).getCode();

    casFeat_entities =
        jcas.getRequiredFeatureDE(casType, "entities", "uima.cas.FSArray", featOkTst);
    casFeatCode_entities =
        (null == casFeat_entities)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_entities).getCode();

    casFeat_arguments =
        jcas.getRequiredFeatureDE(casType, "arguments", "uima.cas.StringArray", featOkTst);
    casFeatCode_arguments =
        (null == casFeat_arguments)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_arguments).getCode();

    casFeat_tokens = jcas.getRequiredFeatureDE(casType, "tokens", "uima.cas.FSArray", featOkTst);
    casFeatCode_tokens =
        (null == casFeat_tokens)
            ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_tokens).getCode();
  }
}
