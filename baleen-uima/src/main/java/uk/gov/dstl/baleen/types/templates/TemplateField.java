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
 * An field identified from a template Updated by JCasGen Wed Apr 17 13:42:22 BST 2019 XML source:
 * types/template_type_system.xml
 *
 * @generated
 */
public class TemplateField extends Base {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.templates.TemplateField";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(TemplateField.class);
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
  public static final String _FeatName_value = "value";
  public static final String _FeatName_source = "source";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_name =
      TypeSystemImpl.createCallSite(TemplateField.class, "name");
  private static final MethodHandle _FH_name = _FC_name.dynamicInvoker();
  private static final CallSite _FC_value =
      TypeSystemImpl.createCallSite(TemplateField.class, "value");
  private static final MethodHandle _FH_value = _FC_value.dynamicInvoker();
  private static final CallSite _FC_source =
      TypeSystemImpl.createCallSite(TemplateField.class, "source");
  private static final MethodHandle _FH_source = _FC_source.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected TemplateField() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public TemplateField(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public TemplateField(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public TemplateField(JCas jcas, int begin, int end) {
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
   * getter for name - gets The name of the identified template field.
   *
   * @generated
   * @return value of the feature
   */
  public String getName() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_name));
  }

  /**
   * setter for name - sets The name of the identified template field.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setName(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_name), v);
  }

  // *--------------*
  // * Feature: value

  /**
   * getter for value - gets An value assigned to this field. Typically taken from the text, but can
   * be assigned using default value.
   *
   * @generated
   * @return value of the feature
   */
  public String getValue() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_value));
  }

  /**
   * setter for value - sets An value assigned to this field. Typically taken from the text, but can
   * be assigned using default value.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setValue(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_value), v);
  }

  // *--------------*
  // * Feature: source

  /**
   * getter for source - gets
   *
   * @generated
   * @return value of the feature
   */
  public String getSource() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_source));
  }

  /**
   * setter for source - sets
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setSource(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_source), v);
  }
}
