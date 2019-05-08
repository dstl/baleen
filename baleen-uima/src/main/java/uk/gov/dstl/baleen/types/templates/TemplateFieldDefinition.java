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
 * A field definition in a template document. Updated by JCasGen Wed Apr 17 13:42:22 BST 2019 XML
 * source: types/template_type_system.xml
 *
 * @generated
 */
public class TemplateFieldDefinition extends Base {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName =
      "uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(TemplateFieldDefinition.class);
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
  public static final String _FeatName_regex = "regex";
  public static final String _FeatName_defaultValue = "defaultValue";
  public static final String _FeatName_required = "required";
  public static final String _FeatName_repeat = "repeat";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_name =
      TypeSystemImpl.createCallSite(TemplateFieldDefinition.class, "name");
  private static final MethodHandle _FH_name = _FC_name.dynamicInvoker();
  private static final CallSite _FC_regex =
      TypeSystemImpl.createCallSite(TemplateFieldDefinition.class, "regex");
  private static final MethodHandle _FH_regex = _FC_regex.dynamicInvoker();
  private static final CallSite _FC_defaultValue =
      TypeSystemImpl.createCallSite(TemplateFieldDefinition.class, "defaultValue");
  private static final MethodHandle _FH_defaultValue = _FC_defaultValue.dynamicInvoker();
  private static final CallSite _FC_required =
      TypeSystemImpl.createCallSite(TemplateFieldDefinition.class, "required");
  private static final MethodHandle _FH_required = _FC_required.dynamicInvoker();
  private static final CallSite _FC_repeat =
      TypeSystemImpl.createCallSite(TemplateFieldDefinition.class, "repeat");
  private static final MethodHandle _FH_repeat = _FC_repeat.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected TemplateFieldDefinition() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public TemplateFieldDefinition(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public TemplateFieldDefinition(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public TemplateFieldDefinition(JCas jcas, int begin, int end) {
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
  // * Feature: regex

  /**
   * getter for regex - gets An optional regular expresison to extract the field from the structural
   * element.
   *
   * @generated
   * @return value of the feature
   */
  public String getRegex() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_regex));
  }

  /**
   * setter for regex - sets An optional regular expresison to extract the field from the structural
   * element.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setRegex(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_regex), v);
  }

  // *--------------*
  // * Feature: defaultValue

  /**
   * getter for defaultValue - gets An optional default value to be used if the field is not matched
   * for the record.
   *
   * @generated
   * @return value of the feature
   */
  public String getDefaultValue() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_defaultValue));
  }

  /**
   * setter for defaultValue - sets An optional default value to be used if the field is not matched
   * for the record.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setDefaultValue(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_defaultValue), v);
  }

  // *--------------*
  // * Feature: required

  /**
   * getter for required - gets Set true to declare that the field is required. If a default is set,
   * this is redundant as it will always exist
   *
   * @generated
   * @return value of the feature
   */
  public boolean getRequired() {
    return _getBooleanValueNc(wrapGetIntCatchException(_FH_required));
  }

  /**
   * setter for required - sets Set true to declare that the field is required. If a default is set,
   * this is redundant as it will always exist
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setRequired(boolean v) {
    _setBooleanValueNfc(wrapGetIntCatchException(_FH_required), v);
  }

  // *--------------*
  // * Feature: repeat

  /**
   * getter for repeat - gets Indicate that this field can be repeated in the document.
   *
   * @generated
   * @return value of the feature
   */
  public boolean getRepeat() {
    return _getBooleanValueNc(wrapGetIntCatchException(_FH_repeat));
  }

  /**
   * setter for repeat - sets Indicate that this field can be repeated in the document.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setRepeat(boolean v) {
    _setBooleanValueNfc(wrapGetIntCatchException(_FH_repeat), v);
  }
}
