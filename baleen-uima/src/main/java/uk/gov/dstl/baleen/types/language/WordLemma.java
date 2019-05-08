/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:42:08 BST 2019 */

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
 * Specifies a lemma form for a word token Updated by JCasGen Wed Apr 17 13:42:08 BST 2019 XML
 * source: types/military_type_system.xml
 *
 * @generated
 */
public class WordLemma extends Base {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.language.WordLemma";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(WordLemma.class);
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

  public static final String _FeatName_partOfSpeech = "partOfSpeech";
  public static final String _FeatName_lemmaForm = "lemmaForm";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_partOfSpeech =
      TypeSystemImpl.createCallSite(WordLemma.class, "partOfSpeech");
  private static final MethodHandle _FH_partOfSpeech = _FC_partOfSpeech.dynamicInvoker();
  private static final CallSite _FC_lemmaForm =
      TypeSystemImpl.createCallSite(WordLemma.class, "lemmaForm");
  private static final MethodHandle _FH_lemmaForm = _FC_lemmaForm.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected WordLemma() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public WordLemma(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public WordLemma(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public WordLemma(JCas jcas, int begin, int end) {
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
  // * Feature: partOfSpeech

  /**
   * getter for partOfSpeech - gets The part of speech (POS) tag for this lemma. Usually a Penn
   * Treebank tag.
   *
   * @generated
   * @return value of the feature
   */
  public String getPartOfSpeech() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_partOfSpeech));
  }

  /**
   * setter for partOfSpeech - sets The part of speech (POS) tag for this lemma. Usually a Penn
   * Treebank tag.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setPartOfSpeech(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_partOfSpeech), v);
  }

  // *--------------*
  // * Feature: lemmaForm

  /**
   * getter for lemmaForm - gets The normal form for this lemma.
   *
   * @generated
   * @return value of the feature
   */
  public String getLemmaForm() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_lemmaForm));
  }

  /**
   * setter for lemmaForm - sets The normal form for this lemma.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setLemmaForm(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_lemmaForm), v);
  }
}
