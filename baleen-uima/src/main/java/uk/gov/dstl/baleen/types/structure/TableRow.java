//Dstl (c) Crown Copyright 2017


/* First created by JCasGen Thu Oct 13 13:31:25 BST 2016 */
package uk.gov.dstl.baleen.types.structure;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** A Row in a Table.
 * Updated by JCasGen Thu Dec 22 22:42:18 CET 2016
 * XML source: /Users/chrisflatley/Projects/railroad/baleen/baleen/baleen-uima/src/main/resources/types/structure_type_system.xml
 * @generated */
public class TableRow extends TablePart {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TableRow.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected TableRow() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public TableRow(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public TableRow(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public TableRow(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
  //*--------------*
  //* Feature: row

  /** getter for row - gets The row number in the table
   * @generated
   * @return value of the feature 
   */
  public int getRow() {
    if (TableRow_Type.featOkTst && ((TableRow_Type)jcasType).casFeat_row == null)
      jcasType.jcas.throwFeatMissing("row", "uk.gov.dstl.baleen.types.structure.TableRow");
    return jcasType.ll_cas.ll_getIntValue(addr, ((TableRow_Type)jcasType).casFeatCode_row);}
    
  /** setter for row - sets The row number in the table 
   * @generated
   * @param v value to set into the feature 
   */
  public void setRow(int v) {
    if (TableRow_Type.featOkTst && ((TableRow_Type)jcasType).casFeat_row == null)
      jcasType.jcas.throwFeatMissing("row", "uk.gov.dstl.baleen.types.structure.TableRow");
    jcasType.ll_cas.ll_setIntValue(addr, ((TableRow_Type)jcasType).casFeatCode_row, v);}    
  }

    