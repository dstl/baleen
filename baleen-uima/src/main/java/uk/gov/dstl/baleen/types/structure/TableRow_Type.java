//Dstl (c) Crown Copyright 2017

/* First created by JCasGen Thu Oct 13 13:31:25 BST 2016 */
package uk.gov.dstl.baleen.types.structure;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** A Row in a Table.
 * Updated by JCasGen Thu Dec 22 22:42:18 CET 2016
 * @generated */
public class TableRow_Type extends TablePart_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TableRow.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.structure.TableRow");



  /** @generated */
  final Feature casFeat_row;
  /** @generated */
  final int     casFeatCode_row;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getRow(int addr) {
        if (featOkTst && casFeat_row == null)
      jcas.throwFeatMissing("row", "uk.gov.dstl.baleen.types.structure.TableRow");
    return ll_cas.ll_getIntValue(addr, casFeatCode_row);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setRow(int addr, int v) {
        if (featOkTst && casFeat_row == null)
      jcas.throwFeatMissing("row", "uk.gov.dstl.baleen.types.structure.TableRow");
    ll_cas.ll_setIntValue(addr, casFeatCode_row, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public TableRow_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_row = jcas.getRequiredFeatureDE(casType, "row", "uima.cas.Integer", featOkTst);
    casFeatCode_row  = (null == casFeat_row) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_row).getCode();

  }
}



    