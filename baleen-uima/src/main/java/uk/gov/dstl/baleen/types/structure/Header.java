// Dstl (c) Crown Copyright 2017

/* First created by JCasGen Thu Oct 13 13:31:25 BST 2016 */
package uk.gov.dstl.baleen.types.structure;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

/**
 * A header of a Document. Updated by JCasGen Thu Dec 22 22:42:17 CET 2016 XML source:
 * /Users/chrisflatley/Projects/railroad/baleen/baleen/baleen-uima/src/main/resources/types/structure_type_system.xml
 *
 * @generated
 */
public class Header extends Structure {
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Header.class);
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

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Header() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   */
  public Header(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Header(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Header(JCas jcas, int begin, int end) {
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
   * getter for page - gets The page this is on.
   *
   * @generated
   * @return value of the feature
   */
  public Page getPage() {
    if (Header_Type.featOkTst && ((Header_Type) jcasType).casFeat_page == null)
      jcasType.jcas.throwFeatMissing("page", "uk.gov.dstl.baleen.types.structure.Header");
    return (Page)
        (jcasType.ll_cas.ll_getFSForRef(
            jcasType.ll_cas.ll_getRefValue(addr, ((Header_Type) jcasType).casFeatCode_page)));
  }

  /**
   * setter for page - sets The page this is on.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setPage(Page v) {
    if (Header_Type.featOkTst && ((Header_Type) jcasType).casFeat_page == null)
      jcasType.jcas.throwFeatMissing("page", "uk.gov.dstl.baleen.types.structure.Header");
    jcasType.ll_cas.ll_setRefValue(
        addr, ((Header_Type) jcasType).casFeatCode_page, jcasType.ll_cas.ll_getFSRef(v));
  }
}
