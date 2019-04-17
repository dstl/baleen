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
 * A reference to a place, country, administrative region or geo-political entity within the source
 * document. This is a general purpose type that is extended in "geo" types. Updated by JCasGen Wed
 * Apr 17 13:42:08 BST 2019 XML source: types/military_type_system.xml
 *
 * @generated
 */
public class Location extends Entity {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.semantic.Location";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Location.class);
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

  public static final String _FeatName_geoJson = "geoJson";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_geoJson =
      TypeSystemImpl.createCallSite(Location.class, "geoJson");
  private static final MethodHandle _FH_geoJson = _FC_geoJson.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Location() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Location(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Location(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Location(JCas jcas, int begin, int end) {
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
  // * Feature: geoJson

  /**
   * getter for geoJson - gets A strnig representation of geoJson format represention of geographic
   * information associated with the location, including where possible coordinate(s).
   *
   * @generated
   * @return value of the feature
   */
  public String getGeoJson() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_geoJson));
  }

  /**
   * setter for geoJson - sets A strnig representation of geoJson format represention of geographic
   * information associated with the location, including where possible coordinate(s).
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setGeoJson(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_geoJson), v);
  }
}
