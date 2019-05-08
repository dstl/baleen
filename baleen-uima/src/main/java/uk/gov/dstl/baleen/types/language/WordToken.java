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
 * The output from some text tokenization process. Updated by JCasGen Wed Apr 17 13:42:08 BST 2019
 * XML source: types/military_type_system.xml
 *
 * @generated
 */
public class WordToken extends Base {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.language.WordToken";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(WordToken.class);
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
  public static final String _FeatName_sentenceOrder = "sentenceOrder";
  public static final String _FeatName_lemmas = "lemmas";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_partOfSpeech =
      TypeSystemImpl.createCallSite(WordToken.class, "partOfSpeech");
  private static final MethodHandle _FH_partOfSpeech = _FC_partOfSpeech.dynamicInvoker();
  private static final CallSite _FC_sentenceOrder =
      TypeSystemImpl.createCallSite(WordToken.class, "sentenceOrder");
  private static final MethodHandle _FH_sentenceOrder = _FC_sentenceOrder.dynamicInvoker();
  private static final CallSite _FC_lemmas =
      TypeSystemImpl.createCallSite(WordToken.class, "lemmas");
  private static final MethodHandle _FH_lemmas = _FC_lemmas.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected WordToken() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public WordToken(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public WordToken(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public WordToken(JCas jcas, int begin, int end) {
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
   * getter for partOfSpeech - gets The part of speech (POS) tag. Usually a Penn Treebank tag.
   *
   * @generated
   * @return value of the feature
   */
  public String getPartOfSpeech() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_partOfSpeech));
  }

  /**
   * setter for partOfSpeech - sets The part of speech (POS) tag. Usually a Penn Treebank tag.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setPartOfSpeech(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_partOfSpeech), v);
  }

  // *--------------*
  // * Feature: sentenceOrder

  /**
   * getter for sentenceOrder - gets If not null, this should be the index position of the word
   * token within parent sentence.
   *
   * @generated
   * @return value of the feature
   */
  public int getSentenceOrder() {
    return _getIntValueNc(wrapGetIntCatchException(_FH_sentenceOrder));
  }

  /**
   * setter for sentenceOrder - sets If not null, this should be the index position of the word
   * token within parent sentence.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setSentenceOrder(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_sentenceOrder), v);
  }

  // *--------------*
  // * Feature: lemmas

  /**
   * getter for lemmas - gets A list of alternative lemmas for this word token.
   *
   * @generated
   * @return value of the feature
   */
  public FSArray getLemmas() {
    return (FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_lemmas)));
  }

  /**
   * setter for lemmas - sets A list of alternative lemmas for this word token.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setLemmas(FSArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_lemmas), v);
  }

  /**
   * indexed getter for lemmas - gets an indexed value - A list of alternative lemmas for this word
   * token.
   *
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i
   */
  public WordLemma getLemmas(int i) {
    return (WordLemma)
        (((FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_lemmas)))).get(i));
  }

  /**
   * indexed setter for lemmas - sets an indexed value - A list of alternative lemmas for this word
   * token.
   *
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array
   */
  public void setLemmas(int i, WordLemma v) {
    ((FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_lemmas)))).set(i, v);
  }
}
