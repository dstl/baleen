//Dstl (c) Crown Copyright 2017

/* First created by JCasGen Thu Oct 13 13:37:40 BST 2016 */
package uk.gov.dstl.baleen.types.structure;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** A header of a Table.
 * Updated by JCasGen Thu Dec 22 22:42:18 CET 2016
 * @generated */
public class TablePart_Type extends Structure_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TablePart.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("uk.gov.dstl.baleen.types.structure.TablePart");
 
  /** @generated */
  final Feature casFeat_table;
  /** @generated */
  final int     casFeatCode_table;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getTable(int addr) {
        if (featOkTst && casFeat_table == null)
      jcas.throwFeatMissing("table", "uk.gov.dstl.baleen.types.structure.TablePart");
    return ll_cas.ll_getRefValue(addr, casFeatCode_table);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTable(int addr, int v) {
        if (featOkTst && casFeat_table == null)
      jcas.throwFeatMissing("table", "uk.gov.dstl.baleen.types.structure.TablePart");
    ll_cas.ll_setRefValue(addr, casFeatCode_table, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public TablePart_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_table = jcas.getRequiredFeatureDE(casType, "table", "uk.gov.dstl.baleen.types.structure.Table", featOkTst);
    casFeatCode_table  = (null == casFeat_table) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_table).getCode();

  }
}



    