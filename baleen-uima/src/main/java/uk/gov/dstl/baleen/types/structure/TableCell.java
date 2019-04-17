/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:42:16 BST 2019 */

package uk.gov.dstl.baleen.types.structure;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

/**
 * A cell in a Table. Updated by JCasGen Wed Apr 17 13:42:16 BST 2019 XML source:
 * types/structure_type_system.xml
 *
 * @generated
 */
public class TableCell extends TablePart {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.structure.TableCell";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(TableCell.class);
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

  public static final String _FeatName_row = "row";
  public static final String _FeatName_column = "column";
  public static final String _FeatName_rowSpan = "rowSpan";
  public static final String _FeatName_columnSpan = "columnSpan";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_row = TypeSystemImpl.createCallSite(TableCell.class, "row");
  private static final MethodHandle _FH_row = _FC_row.dynamicInvoker();
  private static final CallSite _FC_column =
      TypeSystemImpl.createCallSite(TableCell.class, "column");
  private static final MethodHandle _FH_column = _FC_column.dynamicInvoker();
  private static final CallSite _FC_rowSpan =
      TypeSystemImpl.createCallSite(TableCell.class, "rowSpan");
  private static final MethodHandle _FH_rowSpan = _FC_rowSpan.dynamicInvoker();
  private static final CallSite _FC_columnSpan =
      TypeSystemImpl.createCallSite(TableCell.class, "columnSpan");
  private static final MethodHandle _FH_columnSpan = _FC_columnSpan.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected TableCell() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public TableCell(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public TableCell(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public TableCell(JCas jcas, int begin, int end) {
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
  // * Feature: row

  /**
   * getter for row - gets The row number in the table
   *
   * @generated
   * @return value of the feature
   */
  public int getRow() {
    return _getIntValueNc(wrapGetIntCatchException(_FH_row));
  }

  /**
   * setter for row - sets The row number in the table
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setRow(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_row), v);
  }

  // *--------------*
  // * Feature: column

  /**
   * getter for column - gets The column number of the cell in the table
   *
   * @generated
   * @return value of the feature
   */
  public int getColumn() {
    return _getIntValueNc(wrapGetIntCatchException(_FH_column));
  }

  /**
   * setter for column - sets The column number of the cell in the table
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setColumn(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_column), v);
  }

  // *--------------*
  // * Feature: rowSpan

  /**
   * getter for rowSpan - gets The number of rows spanned by this cell. Assume any value less than 2
   * (or null) to be 1 (ie the cell covers 1 row).
   *
   * @generated
   * @return value of the feature
   */
  public int getRowSpan() {
    return _getIntValueNc(wrapGetIntCatchException(_FH_rowSpan));
  }

  /**
   * setter for rowSpan - sets The number of rows spanned by this cell. Assume any value less than 2
   * (or null) to be 1 (ie the cell covers 1 row).
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setRowSpan(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_rowSpan), v);
  }

  // *--------------*
  // * Feature: columnSpan

  /**
   * getter for columnSpan - gets The number of columns spanned by this cell. Assume any value less
   * than 2 (or null) to be 1 (ie the cell covers 1 column).
   *
   * @generated
   * @return value of the feature
   */
  public int getColumnSpan() {
    return _getIntValueNc(wrapGetIntCatchException(_FH_columnSpan));
  }

  /**
   * setter for columnSpan - sets The number of columns spanned by this cell. Assume any value less
   * than 2 (or null) to be 1 (ie the cell covers 1 column).
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setColumnSpan(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_columnSpan), v);
  }
}
