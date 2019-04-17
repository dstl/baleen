/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:42:22 BST 2019 */

package uk.gov.dstl.baleen.types.templates;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.Base;

/**
 * Beginning / end marker of a record (multiple fields) in a template document, used to create
 * record definitions for subsequent annotation of real documents. Updated by JCasGen Wed Apr 17
 * 13:42:22 BST 2019 XML source: types/template_type_system.xml
 *
 * @generated
 */
public class TemplateRecordDefinition extends Base {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName =
      "uk.gov.dstl.baleen.types.templates.TemplateRecordDefinition";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(TemplateRecordDefinition.class);
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

  public static final String _FeatName_name = "name";
  public static final String _FeatName_repeat = "repeat";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_name =
      TypeSystemImpl.createCallSite(TemplateRecordDefinition.class, "name");
  private static final MethodHandle _FH_name = _FC_name.dynamicInvoker();
  private static final CallSite _FC_repeat =
      TypeSystemImpl.createCallSite(TemplateRecordDefinition.class, "repeat");
  private static final MethodHandle _FH_repeat = _FC_repeat.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected TemplateRecordDefinition() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public TemplateRecordDefinition(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public TemplateRecordDefinition(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public TemplateRecordDefinition(JCas jcas, int begin, int end) {
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
  // * Feature: name

  /**
   * getter for name - gets The name of the record, eg Address
   *
   * @generated
   * @return value of the feature
   */
  public String getName() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_name));
  }

  /**
   * setter for name - sets The name of the record, eg Address
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setName(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_name), v);
  }

  // *--------------*
  // * Feature: repeat

  /**
   * getter for repeat - gets Declare that this record is repeatable in the document. For example, a
   * repeating record spanning a row of a table would create a record for each row in the table.
   *
   * @generated
   * @return value of the feature
   */
  public boolean getRepeat() {
    return _getBooleanValueNc(wrapGetIntCatchException(_FH_repeat));
  }

  /**
   * setter for repeat - sets Declare that this record is repeatable in the document. For example, a
   * repeating record spanning a row of a table would create a record for each row in the table.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setRepeat(boolean v) {
    _setBooleanValueNfc(wrapGetIntCatchException(_FH_repeat), v);
  }
}
