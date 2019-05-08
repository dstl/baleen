/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:42:08 BST 2019 */

package uk.gov.dstl.baleen.types.semantic;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

/**
 * Type to record all temporal references in the text and, where possible, information about them
 * (e.g. timestamp). This includes all times, dates, datetimes, periods, etc. Updated by JCasGen Wed
 * Apr 17 13:42:08 BST 2019 XML source: types/military_type_system.xml
 *
 * @generated
 */
public class Temporal extends Entity {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.semantic.Temporal";

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

  /* *******************
   *   Feature Offsets *
   * *******************/

  public static final String _FeatName_timestampStart = "timestampStart";
  public static final String _FeatName_timestampStop = "timestampStop";
  public static final String _FeatName_scope = "scope";
  public static final String _FeatName_temporalType = "temporalType";
  public static final String _FeatName_precision = "precision";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_timestampStart =
      TypeSystemImpl.createCallSite(Temporal.class, "timestampStart");
  private static final MethodHandle _FH_timestampStart = _FC_timestampStart.dynamicInvoker();
  private static final CallSite _FC_timestampStop =
      TypeSystemImpl.createCallSite(Temporal.class, "timestampStop");
  private static final MethodHandle _FH_timestampStop = _FC_timestampStop.dynamicInvoker();
  private static final CallSite _FC_scope = TypeSystemImpl.createCallSite(Temporal.class, "scope");
  private static final MethodHandle _FH_scope = _FC_scope.dynamicInvoker();
  private static final CallSite _FC_temporalType =
      TypeSystemImpl.createCallSite(Temporal.class, "temporalType");
  private static final MethodHandle _FH_temporalType = _FC_temporalType.dynamicInvoker();
  private static final CallSite _FC_precision =
      TypeSystemImpl.createCallSite(Temporal.class, "precision");
  private static final MethodHandle _FH_precision = _FC_precision.dynamicInvoker();

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
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Temporal(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
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
    return _getLongValueNc(wrapGetIntCatchException(_FH_timestampStart));
  }

  /**
   * setter for timestampStart - sets Timestamp of the point when the temporal reference starts
   * (inclusive), in seconds.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setTimestampStart(long v) {
    _setLongValueNfc(wrapGetIntCatchException(_FH_timestampStart), v);
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
    return _getLongValueNc(wrapGetIntCatchException(_FH_timestampStop));
  }

  /**
   * setter for timestampStop - sets Timestamp of the point when the temporal reference ends
   * (exclusive), in seconds.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setTimestampStop(long v) {
    _setLongValueNfc(wrapGetIntCatchException(_FH_timestampStop), v);
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
    return _getStringValueNc(wrapGetIntCatchException(_FH_scope));
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
    _setStringValueNfc(wrapGetIntCatchException(_FH_scope), v);
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
    return _getStringValueNc(wrapGetIntCatchException(_FH_temporalType));
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
    _setStringValueNfc(wrapGetIntCatchException(_FH_temporalType), v);
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
    return _getStringValueNc(wrapGetIntCatchException(_FH_precision));
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
    _setStringValueNfc(wrapGetIntCatchException(_FH_precision), v);
  }
}
