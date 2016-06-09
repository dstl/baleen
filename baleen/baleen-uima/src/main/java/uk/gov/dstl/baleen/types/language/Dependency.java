

/* First created by JCasGen Tue Apr 12 12:06:25 BST 2016 */
package uk.gov.dstl.baleen.types.language;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.types.Base;


/** Grammatical dependencies between wordtokens, as output from a Dependency Grammar Parser
 * Updated by JCasGen Wed Apr 13 13:23:16 BST 2016
 * XML source: H:/git/TextProcessing/core/baleen/baleen-uima/src/main/resources/types/common_type_system.xml
 * @generated */
public class Dependency extends Base {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Dependency.class);
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
  protected Dependency() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Dependency(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Dependency(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Dependency(JCas jcas, int begin, int end) {
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
  //* Feature: governor

  /** getter for governor - gets Governor of the dependency
   * @generated
   * @return value of the feature 
   */
  public WordToken getGovernor() {
    if (Dependency_Type.featOkTst && ((Dependency_Type)jcasType).casFeat_governor == null)
      jcasType.jcas.throwFeatMissing("governor", "uk.gov.dstl.baleen.types.language.Dependency");
    return (WordToken)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Dependency_Type)jcasType).casFeatCode_governor)));}
    
  /** setter for governor - sets Governor of the dependency 
   * @generated
   * @param v value to set into the feature 
   */
  public void setGovernor(WordToken v) {
    if (Dependency_Type.featOkTst && ((Dependency_Type)jcasType).casFeat_governor == null)
      jcasType.jcas.throwFeatMissing("governor", "uk.gov.dstl.baleen.types.language.Dependency");
    jcasType.ll_cas.ll_setRefValue(addr, ((Dependency_Type)jcasType).casFeatCode_governor, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: dependent

  /** getter for dependent - gets Dependent of the dependency
   * @generated
   * @return value of the feature 
   */
  public WordToken getDependent() {
    if (Dependency_Type.featOkTst && ((Dependency_Type)jcasType).casFeat_dependent == null)
      jcasType.jcas.throwFeatMissing("dependent", "uk.gov.dstl.baleen.types.language.Dependency");
    return (WordToken)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Dependency_Type)jcasType).casFeatCode_dependent)));}
    
  /** setter for dependent - sets Dependent of the dependency 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDependent(WordToken v) {
    if (Dependency_Type.featOkTst && ((Dependency_Type)jcasType).casFeat_dependent == null)
      jcasType.jcas.throwFeatMissing("dependent", "uk.gov.dstl.baleen.types.language.Dependency");
    jcasType.ll_cas.ll_setRefValue(addr, ((Dependency_Type)jcasType).casFeatCode_dependent, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: dependencyType

  /** getter for dependencyType - gets The type of grammatical dependency (eg ROOT, VBMOD, etc)
   * @generated
   * @return value of the feature 
   */
  public String getDependencyType() {
    if (Dependency_Type.featOkTst && ((Dependency_Type)jcasType).casFeat_dependencyType == null)
      jcasType.jcas.throwFeatMissing("dependencyType", "uk.gov.dstl.baleen.types.language.Dependency");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Dependency_Type)jcasType).casFeatCode_dependencyType);}
    
  /** setter for dependencyType - sets The type of grammatical dependency (eg ROOT, VBMOD, etc) 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDependencyType(String v) {
    if (Dependency_Type.featOkTst && ((Dependency_Type)jcasType).casFeat_dependencyType == null)
      jcasType.jcas.throwFeatMissing("dependencyType", "uk.gov.dstl.baleen.types.language.Dependency");
    jcasType.ll_cas.ll_setStringValue(addr, ((Dependency_Type)jcasType).casFeatCode_dependencyType, v);}    
  }

    