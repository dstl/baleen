/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:42:22 BST 2019 */

package org.apache.uima.jcas.tcas;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.StringArray;

import uk.gov.dstl.baleen.core.utils.IdentityUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * Overriding the base DocumentAnntation to add additional features. The JCasGen code generated from
 * this annotation replaces the default type in uima-document-annotation.jar (which should be
 * removed from the classpath). Updated by JCasGen Wed Apr 17 13:42:22 BST 2019 XML source:
 * types/template_type_system.xml
 *
 * @generated
 */
public class DocumentAnnotation extends Annotation {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "org.apache.uima.jcas.tcas.DocumentAnnotation";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(DocumentAnnotation.class);
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

  public static final String _FeatName_language = "language";
  public static final String _FeatName_docType = "docType";
  public static final String _FeatName_sourceUri = "sourceUri";
  public static final String _FeatName_timestamp = "timestamp";
  public static final String _FeatName_documentClassification = "documentClassification";
  public static final String _FeatName_documentCaveats = "documentCaveats";
  public static final String _FeatName_documentReleasability = "documentReleasability";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_language =
      TypeSystemImpl.createCallSite(DocumentAnnotation.class, "language");
  private static final MethodHandle _FH_language = _FC_language.dynamicInvoker();
  private static final CallSite _FC_docType =
      TypeSystemImpl.createCallSite(DocumentAnnotation.class, "docType");
  private static final MethodHandle _FH_docType = _FC_docType.dynamicInvoker();
  private static final CallSite _FC_sourceUri =
      TypeSystemImpl.createCallSite(DocumentAnnotation.class, "sourceUri");
  private static final MethodHandle _FH_sourceUri = _FC_sourceUri.dynamicInvoker();
  private static final CallSite _FC_timestamp =
      TypeSystemImpl.createCallSite(DocumentAnnotation.class, "timestamp");
  private static final MethodHandle _FH_timestamp = _FC_timestamp.dynamicInvoker();
  private static final CallSite _FC_documentClassification =
      TypeSystemImpl.createCallSite(DocumentAnnotation.class, "documentClassification");
  private static final MethodHandle _FH_documentClassification =
      _FC_documentClassification.dynamicInvoker();
  private static final CallSite _FC_documentCaveats =
      TypeSystemImpl.createCallSite(DocumentAnnotation.class, "documentCaveats");
  private static final MethodHandle _FH_documentCaveats = _FC_documentCaveats.dynamicInvoker();
  private static final CallSite _FC_documentReleasability =
      TypeSystemImpl.createCallSite(DocumentAnnotation.class, "documentReleasability");
  private static final MethodHandle _FH_documentReleasability =
      _FC_documentReleasability.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected DocumentAnnotation() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public DocumentAnnotation(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public DocumentAnnotation(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public DocumentAnnotation(JCas jcas, int begin, int end) {
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
  // * Feature: language

  /**
   * getter for language - gets
   *
   * @generated
   * @return value of the feature
   */
  public String getLanguage() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_language));
  }

  /**
   * setter for language - sets
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setLanguage(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_language), v);
  }

  // *--------------*
  // * Feature: docType

  /**
   * getter for docType - gets The document type
   *
   * @generated
   * @return value of the feature
   */
  public String getDocType() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_docType));
  }

  /**
   * setter for docType - sets The document type
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setDocType(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_docType), v);
  }

  // *--------------*
  // * Feature: sourceUri

  /**
   * getter for sourceUri - gets A URI representing the source of the document
   *
   * @generated
   * @return value of the feature
   */
  public String getSourceUri() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_sourceUri));
  }

  /**
   * setter for sourceUri - sets A URI representing the source of the document
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setSourceUri(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_sourceUri), v);
  }

  // *--------------*
  // * Feature: timestamp

  /**
   * getter for timestamp - gets The time at which the document was processed
   *
   * @generated
   * @return value of the feature
   */
  public long getTimestamp() {
    return _getLongValueNc(wrapGetIntCatchException(_FH_timestamp));
  }

  /**
   * setter for timestamp - sets The time at which the document was processed
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setTimestamp(long v) {
    _setLongValueNfc(wrapGetIntCatchException(_FH_timestamp), v);
  }

  // *--------------*
  // * Feature: documentClassification

  /**
   * getter for documentClassification - gets The security classification of the document
   *
   * @generated
   * @return value of the feature
   */
  public String getDocumentClassification() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_documentClassification));
  }

  /**
   * setter for documentClassification - sets The security classification of the document
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setDocumentClassification(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_documentClassification), v);
  }

  // *--------------*
  // * Feature: documentCaveats

  /**
   * getter for documentCaveats - gets An array of string values specifying handling caveats for the
   * document.
   *
   * @generated
   * @return value of the feature
   */
  public StringArray getDocumentCaveats() {
    return (StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_documentCaveats)));
  }

  /**
   * setter for documentCaveats - sets An array of string values specifying handling caveats for the
   * document.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setDocumentCaveats(StringArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_documentCaveats), v);
  }

  /**
   * indexed getter for documentCaveats - gets an indexed value - An array of string values
   * specifying handling caveats for the document.
   *
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i
   */
  public String getDocumentCaveats(int i) {
    return ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_documentCaveats))))
        .get(i);
  }

  /**
   * indexed setter for documentCaveats - sets an indexed value - An array of string values
   * specifying handling caveats for the document.
   *
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array
   */
  public void setDocumentCaveats(int i, String v) {
    ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_documentCaveats)))).set(i, v);
  }

  // *--------------*
  // * Feature: documentReleasability

  /**
   * getter for documentReleasability - gets Array of country designators to which the document is
   * releasable.
   *
   * @generated
   * @return value of the feature
   */
  public StringArray getDocumentReleasability() {
    return (StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_documentReleasability)));
  }

  /**
   * setter for documentReleasability - sets Array of country designators to which the document is
   * releasable.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setDocumentReleasability(StringArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_documentReleasability), v);
  }

  /**
   * indexed getter for documentReleasability - gets an indexed value - Array of country designators
   * to which the document is releasable.
   *
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i
   */
  public String getDocumentReleasability(int i) {
    return ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_documentReleasability))))
        .get(i);
  }

  /**
   * indexed setter for documentReleasability - sets an indexed value - Array of country designators
   * to which the document is releasable.
   *
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array
   */
  public void setDocumentReleasability(int i, String v) {
    ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_documentReleasability))))
        .set(i, v);
  }

  /** Get hash of current document text */
  public String getHash() {
    try {
      return IdentityUtils.hashStrings(getCAS().getDocumentText());
    } catch (BaleenException e) {
      return "";
    }
  }
}
