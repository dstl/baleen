// Dstl (c) Crown Copyright 2017

/* First created by JCasGen Thu Oct 13 13:37:40 BST 2016 */
package uk.gov.dstl.baleen.types.structure;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

/**
 * A header of a Table. Updated by JCasGen Thu Dec 22 22:42:18 CET 2016 XML source:
 * /Users/chrisflatley/Projects/railroad/baleen/baleen/baleen-uima/src/main/resources/types/structure_type_system.xml
 *
 * @generated
 */
public class TablePart extends Structure {
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(TablePart.class);
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
  protected TablePart() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   */
  public TablePart(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public TablePart(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public TablePart(JCas jcas, int begin, int end) {
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
  // * Feature: table

  /**
   * getter for table - gets The table this is part of
   *
   * @generated
   * @return value of the feature
   */
  public Table getTable() {
    if (TablePart_Type.featOkTst && ((TablePart_Type) jcasType).casFeat_table == null)
      jcasType.jcas.throwFeatMissing("table", "uk.gov.dstl.baleen.types.structure.TablePart");
    return (Table)
        (jcasType.ll_cas.ll_getFSForRef(
            jcasType.ll_cas.ll_getRefValue(addr, ((TablePart_Type) jcasType).casFeatCode_table)));
  }

  /**
   * setter for table - sets The table this is part of
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setTable(Table v) {
    if (TablePart_Type.featOkTst && ((TablePart_Type) jcasType).casFeat_table == null)
      jcasType.jcas.throwFeatMissing("table", "uk.gov.dstl.baleen.types.structure.TablePart");
    jcasType.ll_cas.ll_setRefValue(
        addr, ((TablePart_Type) jcasType).casFeatCode_table, jcasType.ll_cas.ll_getFSRef(v));
  }
}
