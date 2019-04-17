/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:42:16 BST 2019 */

package uk.gov.dstl.baleen.types.structure;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.StringArray;

/**
 * To capture the style applied to a span of text. Updated by JCasGen Wed Apr 17 13:42:16 BST 2019
 * XML source: types/structure_type_system.xml
 *
 * @generated
 */
public class Style extends Structure {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.structure.Style";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Style.class);
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

  public static final String _FeatName_font = "font";
  public static final String _FeatName_color = "color";
  public static final String _FeatName_decoration = "decoration";
  public static final String _FeatName_size = "size";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_font = TypeSystemImpl.createCallSite(Style.class, "font");
  private static final MethodHandle _FH_font = _FC_font.dynamicInvoker();
  private static final CallSite _FC_color = TypeSystemImpl.createCallSite(Style.class, "color");
  private static final MethodHandle _FH_color = _FC_color.dynamicInvoker();
  private static final CallSite _FC_decoration =
      TypeSystemImpl.createCallSite(Style.class, "decoration");
  private static final MethodHandle _FH_decoration = _FC_decoration.dynamicInvoker();
  private static final CallSite _FC_size = TypeSystemImpl.createCallSite(Style.class, "size");
  private static final MethodHandle _FH_size = _FC_size.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Style() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Style(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Style(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Style(JCas jcas, int begin, int end) {
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
  // * Feature: font

  /**
   * getter for font - gets The font
   *
   * @generated
   * @return value of the feature
   */
  public String getFont() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_font));
  }

  /**
   * setter for font - sets The font
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setFont(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_font), v);
  }

  // *--------------*
  // * Feature: color

  /**
   * getter for color - gets The color of the text
   *
   * @generated
   * @return value of the feature
   */
  public String getColor() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_color));
  }

  /**
   * setter for color - sets The color of the text
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setColor(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_color), v);
  }

  // *--------------*
  // * Feature: decoration

  /**
   * getter for decoration - gets Decoration applied to the text. For example, [italic, bold,
   * underline, strikethrough, small, superscript, subscript].
   *
   * @generated
   * @return value of the feature
   */
  public StringArray getDecoration() {
    return (StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_decoration)));
  }

  /**
   * setter for decoration - sets Decoration applied to the text. For example, [italic, bold,
   * underline, strikethrough, small, superscript, subscript].
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setDecoration(StringArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_decoration), v);
  }

  /**
   * indexed getter for decoration - gets an indexed value - Decoration applied to the text. For
   * example, [italic, bold, underline, strikethrough, small, superscript, subscript].
   *
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i
   */
  public String getDecoration(int i) {
    return ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_decoration)))).get(i);
  }

  /**
   * indexed setter for decoration - sets an indexed value - Decoration applied to the text. For
   * example, [italic, bold, underline, strikethrough, small, superscript, subscript].
   *
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array
   */
  public void setDecoration(int i, String v) {
    ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_decoration)))).set(i, v);
  }

  // *--------------*
  // * Feature: size

  /**
   * getter for size - gets The size of the text
   *
   * @generated
   * @return value of the feature
   */
  public String getSize() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_size));
  }

  /**
   * setter for size - sets The size of the text
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setSize(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_size), v);
  }
}
