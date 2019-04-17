/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:42:07 BST 2019 */

package uk.gov.dstl.baleen.types.language;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.Base;

/**
 * Grammatical dependencies between wordtokens, as output from a Dependency Grammar Parser Updated
 * by JCasGen Wed Apr 17 13:42:07 BST 2019 XML source: types/military_type_system.xml
 *
 * @generated
 */
public class Dependency extends Base {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.language.Dependency";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Dependency.class);
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

  public static final String _FeatName_governor = "governor";
  public static final String _FeatName_dependent = "dependent";
  public static final String _FeatName_dependencyType = "dependencyType";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_governor =
      TypeSystemImpl.createCallSite(Dependency.class, "governor");
  private static final MethodHandle _FH_governor = _FC_governor.dynamicInvoker();
  private static final CallSite _FC_dependent =
      TypeSystemImpl.createCallSite(Dependency.class, "dependent");
  private static final MethodHandle _FH_dependent = _FC_dependent.dynamicInvoker();
  private static final CallSite _FC_dependencyType =
      TypeSystemImpl.createCallSite(Dependency.class, "dependencyType");
  private static final MethodHandle _FH_dependencyType = _FC_dependencyType.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Dependency() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Dependency(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Dependency(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Dependency(JCas jcas, int begin, int end) {
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
  // * Feature: governor

  /**
   * getter for governor - gets Governor of the dependency
   *
   * @generated
   * @return value of the feature
   */
  public WordToken getGovernor() {
    return (WordToken) (_getFeatureValueNc(wrapGetIntCatchException(_FH_governor)));
  }

  /**
   * setter for governor - sets Governor of the dependency
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setGovernor(WordToken v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_governor), v);
  }

  // *--------------*
  // * Feature: dependent

  /**
   * getter for dependent - gets Dependent of the dependency
   *
   * @generated
   * @return value of the feature
   */
  public WordToken getDependent() {
    return (WordToken) (_getFeatureValueNc(wrapGetIntCatchException(_FH_dependent)));
  }

  /**
   * setter for dependent - sets Dependent of the dependency
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setDependent(WordToken v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_dependent), v);
  }

  // *--------------*
  // * Feature: dependencyType

  /**
   * getter for dependencyType - gets The type of grammatical dependency (eg ROOT, VBMOD, etc)
   *
   * @generated
   * @return value of the feature
   */
  public String getDependencyType() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_dependencyType));
  }

  /**
   * setter for dependencyType - sets The type of grammatical dependency (eg ROOT, VBMOD, etc)
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setDependencyType(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_dependencyType), v);
  }
}
