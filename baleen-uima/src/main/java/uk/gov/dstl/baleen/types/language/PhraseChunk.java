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
 * Annotation to store the result of shallow parsing, which provide noun phrase and verb phrase
 * constituents, rather than just WordTokens. Updated by JCasGen Wed Apr 17 13:42:08 BST 2019 XML
 * source: types/military_type_system.xml
 *
 * @generated
 */
public class PhraseChunk extends Base {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.language.PhraseChunk";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(PhraseChunk.class);
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

  public static final String _FeatName_chunkType = "chunkType";
  public static final String _FeatName_constituentWords = "constituentWords";
  public static final String _FeatName_headWord = "headWord";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_chunkType =
      TypeSystemImpl.createCallSite(PhraseChunk.class, "chunkType");
  private static final MethodHandle _FH_chunkType = _FC_chunkType.dynamicInvoker();
  private static final CallSite _FC_constituentWords =
      TypeSystemImpl.createCallSite(PhraseChunk.class, "constituentWords");
  private static final MethodHandle _FH_constituentWords = _FC_constituentWords.dynamicInvoker();
  private static final CallSite _FC_headWord =
      TypeSystemImpl.createCallSite(PhraseChunk.class, "headWord");
  private static final MethodHandle _FH_headWord = _FC_headWord.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected PhraseChunk() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public PhraseChunk(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public PhraseChunk(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public PhraseChunk(JCas jcas, int begin, int end) {
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
  // * Feature: chunkType

  /**
   * getter for chunkType - gets The Penn Treebank constituent annotation
   *
   * @generated
   * @return value of the feature
   */
  public String getChunkType() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_chunkType));
  }

  /**
   * setter for chunkType - sets The Penn Treebank constituent annotation
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setChunkType(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_chunkType), v);
  }

  // *--------------*
  // * Feature: constituentWords

  /**
   * getter for constituentWords - gets Word tokens which comprise the constituent.
   *
   * @generated
   * @return value of the feature
   */
  public FSArray getConstituentWords() {
    return (FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_constituentWords)));
  }

  /**
   * setter for constituentWords - sets Word tokens which comprise the constituent.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setConstituentWords(FSArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_constituentWords), v);
  }

  /**
   * indexed getter for constituentWords - gets an indexed value - Word tokens which comprise the
   * constituent.
   *
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i
   */
  public WordToken getConstituentWords(int i) {
    return (WordToken)
        (((FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_constituentWords)))).get(i));
  }

  /**
   * indexed setter for constituentWords - sets an indexed value - Word tokens which comprise the
   * constituent.
   *
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array
   */
  public void setConstituentWords(int i, WordToken v) {
    ((FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_constituentWords)))).set(i, v);
  }

  // *--------------*
  // * Feature: headWord

  /**
   * getter for headWord - gets The head word of the constituent phrase
   *
   * @generated
   * @return value of the feature
   */
  public WordToken getHeadWord() {
    return (WordToken) (_getFeatureValueNc(wrapGetIntCatchException(_FH_headWord)));
  }

  /**
   * setter for headWord - sets The head word of the constituent phrase
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setHeadWord(WordToken v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_headWord), v);
  }
}
