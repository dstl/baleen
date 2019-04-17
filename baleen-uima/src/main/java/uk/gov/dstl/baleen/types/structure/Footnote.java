/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:42:16 BST 2019 */

package uk.gov.dstl.baleen.types.structure;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

/**
 * A footnote in a Document. Updated by JCasGen Wed Apr 17 13:42:16 BST 2019 XML source:
 * types/structure_type_system.xml
 *
 * @generated
 */
public class Footnote extends Structure {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.structure.Footnote";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Footnote.class);
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

  public static final String _FeatName_page = "page";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_page = TypeSystemImpl.createCallSite(Footnote.class, "page");
  private static final MethodHandle _FH_page = _FC_page.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Footnote() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Footnote(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Footnote(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Footnote(JCas jcas, int begin, int end) {
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
  // * Feature: page

  /**
   * getter for page - gets The page this is on
   *
   * @generated
   * @return value of the feature
   */
  public Page getPage() {
    return (Page) (_getFeatureValueNc(wrapGetIntCatchException(_FH_page)));
  }

  /**
   * setter for page - sets The page this is on
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setPage(Page v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_page), v);
  }
}
