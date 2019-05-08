/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:41:50 BST 2019 */

package uk.gov.dstl.baleen.types.geo;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.semantic.Location;

/**
 * A well-formed coordinate value - MGRS or WGS84 DD or DMS coordinate system - explicitly defined
 * in source document. Updated by JCasGen Wed Apr 17 13:41:50 BST 2019 XML source:
 * types/geo_type_system.xml
 *
 * @generated
 */
public class Coordinate extends Location {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.geo.Coordinate";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Coordinate.class);
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

  public static final String _FeatName_coordinateValue = "coordinateValue";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_coordinateValue =
      TypeSystemImpl.createCallSite(Coordinate.class, "coordinateValue");
  private static final MethodHandle _FH_coordinateValue = _FC_coordinateValue.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Coordinate() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Coordinate(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Coordinate(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Coordinate(JCas jcas, int begin, int end) {
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
  // * Feature: coordinateValue

  /**
   * getter for coordinateValue - gets A normalised value for the coordinate.
   *
   * @generated
   * @return value of the feature
   */
  public String getCoordinateValue() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_coordinateValue));
  }

  /**
   * setter for coordinateValue - sets A normalised value for the coordinate.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setCoordinateValue(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_coordinateValue), v);
  }
}
