/* First created by JCasGen Wed Jan 21 11:20:35 GMT 2015 */
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.types.semantic;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

/**
 * Type to record all temporal references in the text and, where possible, information about them
 * (e.g. timestamp). This includes all times, dates, datetimes, periods, etc. Updated by JCasGen Thu
 * Oct 06 15:46:19 BST 2016 XML source:
 * H:/git/TextProcessing/core/baleen/baleen-uima/src/main/resources/types/semantic_type_system.xml
 *
 * @generated
 */
public class Temporal extends Entity {
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Temporal.class);
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
  protected Temporal() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   */
  public Temporal(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Temporal(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Temporal(JCas jcas, int begin, int end) {
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
  // * Feature: timestampStart

  /**
   * getter for timestampStart - gets Timestamp of the point when the temporal reference starts
   * (inclusive), in seconds.
   *
   * @generated
   * @return value of the feature
   */
  public long getTimestampStart() {
    if (Temporal_Type.featOkTst && ((Temporal_Type) jcasType).casFeat_timestampStart == null)
      jcasType.jcas.throwFeatMissing(
          "timestampStart", "uk.gov.dstl.baleen.types.semantic.Temporal");
    return jcasType.ll_cas.ll_getLongValue(
        addr, ((Temporal_Type) jcasType).casFeatCode_timestampStart);
  }

  /**
   * setter for timestampStart - sets Timestamp of the point when the temporal reference starts
   * (inclusive), in seconds.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setTimestampStart(long v) {
    if (Temporal_Type.featOkTst && ((Temporal_Type) jcasType).casFeat_timestampStart == null)
      jcasType.jcas.throwFeatMissing(
          "timestampStart", "uk.gov.dstl.baleen.types.semantic.Temporal");
    jcasType.ll_cas.ll_setLongValue(addr, ((Temporal_Type) jcasType).casFeatCode_timestampStart, v);
  }

  // *--------------*
  // * Feature: timestampStop

  /**
   * getter for timestampStop - gets Timestamp of the point when the temporal reference ends
   * (exclusive), in seconds.
   *
   * @generated
   * @return value of the feature
   */
  public long getTimestampStop() {
    if (Temporal_Type.featOkTst && ((Temporal_Type) jcasType).casFeat_timestampStop == null)
      jcasType.jcas.throwFeatMissing("timestampStop", "uk.gov.dstl.baleen.types.semantic.Temporal");
    return jcasType.ll_cas.ll_getLongValue(
        addr, ((Temporal_Type) jcasType).casFeatCode_timestampStop);
  }

  /**
   * setter for timestampStop - sets Timestamp of the point when the temporal reference ends
   * (exclusive), in seconds.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setTimestampStop(long v) {
    if (Temporal_Type.featOkTst && ((Temporal_Type) jcasType).casFeat_timestampStop == null)
      jcasType.jcas.throwFeatMissing("timestampStop", "uk.gov.dstl.baleen.types.semantic.Temporal");
    jcasType.ll_cas.ll_setLongValue(addr, ((Temporal_Type) jcasType).casFeatCode_timestampStop, v);
  }

  // *--------------*
  // * Feature: scope

  /**
   * getter for scope - gets Does this temporal entity describe a single temporal instance (e.g. 12
   * Oct 2016) or a temporal range (12-16 Oct 2016). If unknown, then leave as null (or empty).
   *
   * <p>Expected values: SINGLE, RANGE, null
   *
   * @generated
   * @return value of the feature
   */
  public String getScope() {
    if (Temporal_Type.featOkTst && ((Temporal_Type) jcasType).casFeat_scope == null)
      jcasType.jcas.throwFeatMissing("scope", "uk.gov.dstl.baleen.types.semantic.Temporal");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Temporal_Type) jcasType).casFeatCode_scope);
  }

  /**
   * setter for scope - sets Does this temporal entity describe a single temporal instance (e.g. 12
   * Oct 2016) or a temporal range (12-16 Oct 2016). If unknown, then leave as null (or empty).
   *
   * <p>Expected values: SINGLE, RANGE, null
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setScope(String v) {
    if (Temporal_Type.featOkTst && ((Temporal_Type) jcasType).casFeat_scope == null)
      jcasType.jcas.throwFeatMissing("scope", "uk.gov.dstl.baleen.types.semantic.Temporal");
    jcasType.ll_cas.ll_setStringValue(addr, ((Temporal_Type) jcasType).casFeatCode_scope, v);
  }

  // *--------------*
  // * Feature: temporalType

  /**
   * getter for temporalType - gets Does this temporal entity describe a date, a time or a datetime.
   * If unknown, then leave as null (or empty).
   *
   * <p>Expected values: DATE, TIME, DATETIME, null
   *
   * @generated
   * @return value of the feature
   */
  public String getTemporalType() {
    if (Temporal_Type.featOkTst && ((Temporal_Type) jcasType).casFeat_temporalType == null)
      jcasType.jcas.throwFeatMissing("temporalType", "uk.gov.dstl.baleen.types.semantic.Temporal");
    return jcasType.ll_cas.ll_getStringValue(
        addr, ((Temporal_Type) jcasType).casFeatCode_temporalType);
  }

  /**
   * setter for temporalType - sets Does this temporal entity describe a date, a time or a datetime.
   * If unknown, then leave as null (or empty).
   *
   * <p>Expected values: DATE, TIME, DATETIME, null
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setTemporalType(String v) {
    if (Temporal_Type.featOkTst && ((Temporal_Type) jcasType).casFeat_temporalType == null)
      jcasType.jcas.throwFeatMissing("temporalType", "uk.gov.dstl.baleen.types.semantic.Temporal");
    jcasType.ll_cas.ll_setStringValue(addr, ((Temporal_Type) jcasType).casFeatCode_temporalType, v);
  }

  // *--------------*
  // * Feature: precision

  /**
   * getter for precision - gets What level of precision does this temporal entity have. Do we know
   * exactly when it refers to (i.e. we would expect to know timestampStart and timestampEnd); or is
   * it relative to something else (i.e. we would expect to know timestampStart and timestampEnd
   * only if we know when it is relative to); or is it unqualified (i.e. we would not expect to know
   * timestampStart and timestampStop)?
   *
   * <p>If unknown, then leave as null (or empty).
   *
   * <p>Expected values: EXACT, RELATIVE, UNQUALIFIED, null
   *
   * @generated
   * @return value of the feature
   */
  public String getPrecision() {
    if (Temporal_Type.featOkTst && ((Temporal_Type) jcasType).casFeat_precision == null)
      jcasType.jcas.throwFeatMissing("precision", "uk.gov.dstl.baleen.types.semantic.Temporal");
    return jcasType.ll_cas.ll_getStringValue(
        addr, ((Temporal_Type) jcasType).casFeatCode_precision);
  }

  /**
   * setter for precision - sets What level of precision does this temporal entity have. Do we know
   * exactly when it refers to (i.e. we would expect to know timestampStart and timestampEnd); or is
   * it relative to something else (i.e. we would expect to know timestampStart and timestampEnd
   * only if we know when it is relative to); or is it unqualified (i.e. we would not expect to know
   * timestampStart and timestampStop)?
   *
   * <p>If unknown, then leave as null (or empty).
   *
   * <p>Expected values: EXACT, RELATIVE, UNQUALIFIED, null
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setPrecision(String v) {
    if (Temporal_Type.featOkTst && ((Temporal_Type) jcasType).casFeat_precision == null)
      jcasType.jcas.throwFeatMissing("precision", "uk.gov.dstl.baleen.types.semantic.Temporal");
    jcasType.ll_cas.ll_setStringValue(addr, ((Temporal_Type) jcasType).casFeatCode_precision, v);
  }
}
