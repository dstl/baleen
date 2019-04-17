/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:42:08 BST 2019 */

package uk.gov.dstl.baleen.types.language;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.FSArray;

import uk.gov.dstl.baleen.types.Base;

/**
 * The text pattern between two annotations (usually entities) which has been processed to be more
 * meaningful than simply the covered text between them Updated by JCasGen Wed Apr 17 13:42:08 BST
 * 2019 XML source: types/military_type_system.xml
 *
 * @generated
 */
public class Pattern extends Base {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.language.Pattern";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Pattern.class);
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

  public static final String _FeatName_source = "source";
  public static final String _FeatName_target = "target";
  public static final String _FeatName_words = "words";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_source = TypeSystemImpl.createCallSite(Pattern.class, "source");
  private static final MethodHandle _FH_source = _FC_source.dynamicInvoker();
  private static final CallSite _FC_target = TypeSystemImpl.createCallSite(Pattern.class, "target");
  private static final MethodHandle _FH_target = _FC_target.dynamicInvoker();
  private static final CallSite _FC_words = TypeSystemImpl.createCallSite(Pattern.class, "words");
  private static final MethodHandle _FH_words = _FC_words.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Pattern() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Pattern(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Pattern(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Pattern(JCas jcas, int begin, int end) {
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
  // * Feature: source

  /**
   * getter for source - gets The source entity (first entity in the sentence)
   *
   * @generated
   * @return value of the feature
   */
  public Base getSource() {
    return (Base) (_getFeatureValueNc(wrapGetIntCatchException(_FH_source)));
  }

  /**
   * setter for source - sets The source entity (first entity in the sentence)
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setSource(Base v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_source), v);
  }

  // *--------------*
  // * Feature: target

  /**
   * getter for target - gets The target entity (last entity in the sentence)
   *
   * @generated
   * @return value of the feature
   */
  public Base getTarget() {
    return (Base) (_getFeatureValueNc(wrapGetIntCatchException(_FH_target)));
  }

  /**
   * setter for target - sets The target entity (last entity in the sentence)
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setTarget(Base v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_target), v);
  }

  // *--------------*
  // * Feature: words

  /**
   * getter for words - gets The collection of word tokens which form this pattern.
   *
   * @generated
   * @return value of the feature
   */
  public FSArray getWords() {
    return (FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_words)));
  }

  /**
   * setter for words - sets The collection of word tokens which form this pattern.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setWords(FSArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_words), v);
  }

  /**
   * indexed getter for words - gets an indexed value - The collection of word tokens which form
   * this pattern.
   *
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i
   */
  public WordToken getWords(int i) {
    return (WordToken)
        (((FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_words)))).get(i));
  }

  /**
   * indexed setter for words - sets an indexed value - The collection of word tokens which form
   * this pattern.
   *
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array
   */
  public void setWords(int i, WordToken v) {
    ((FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_words)))).set(i, v);
  }
}
