/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:42:08 BST 2019 */

package uk.gov.dstl.baleen.types.semantic;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

import uk.gov.dstl.baleen.types.Base;

/**
 * Records a relationship between named entities, explicitly mentioned within the source document.
 * Updated by JCasGen Wed Apr 17 13:42:08 BST 2019 XML source: types/military_type_system.xml
 *
 * @generated
 */
public class Relation extends Base {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.semantic.Relation";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(Relation.class);
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

  public static final String _FeatName_relationshipType = "relationshipType";
  public static final String _FeatName_source = "source";
  public static final String _FeatName_target = "target";
  public static final String _FeatName_value = "value";
  public static final String _FeatName_relationSubType = "relationSubType";
  public static final String _FeatName_sentenceDistance = "sentenceDistance";
  public static final String _FeatName_wordDistance = "wordDistance";
  public static final String _FeatName_dependencyDistance = "dependencyDistance";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_relationshipType =
      TypeSystemImpl.createCallSite(Relation.class, "relationshipType");
  private static final MethodHandle _FH_relationshipType = _FC_relationshipType.dynamicInvoker();
  private static final CallSite _FC_source =
      TypeSystemImpl.createCallSite(Relation.class, "source");
  private static final MethodHandle _FH_source = _FC_source.dynamicInvoker();
  private static final CallSite _FC_target =
      TypeSystemImpl.createCallSite(Relation.class, "target");
  private static final MethodHandle _FH_target = _FC_target.dynamicInvoker();
  private static final CallSite _FC_value = TypeSystemImpl.createCallSite(Relation.class, "value");
  private static final MethodHandle _FH_value = _FC_value.dynamicInvoker();
  private static final CallSite _FC_relationSubType =
      TypeSystemImpl.createCallSite(Relation.class, "relationSubType");
  private static final MethodHandle _FH_relationSubType = _FC_relationSubType.dynamicInvoker();
  private static final CallSite _FC_sentenceDistance =
      TypeSystemImpl.createCallSite(Relation.class, "sentenceDistance");
  private static final MethodHandle _FH_sentenceDistance = _FC_sentenceDistance.dynamicInvoker();
  private static final CallSite _FC_wordDistance =
      TypeSystemImpl.createCallSite(Relation.class, "wordDistance");
  private static final MethodHandle _FH_wordDistance = _FC_wordDistance.dynamicInvoker();
  private static final CallSite _FC_dependencyDistance =
      TypeSystemImpl.createCallSite(Relation.class, "dependencyDistance");
  private static final MethodHandle _FH_dependencyDistance =
      _FC_dependencyDistance.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected Relation() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public Relation(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Relation(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public Relation(JCas jcas, int begin, int end) {
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
  // * Feature: relationshipType

  /**
   * getter for relationshipType - gets Denotes the semantic type of the relationship between
   * entities.
   *
   * <p>Currently based on the ACE2002 top-level relationships: {"AT" , "NEAR" , "PART" , "ROLE" ,
   * "SOCIAL"} Additional relationship types that have been added: {"QUANTITY", "ALIAS"}
   *
   * @generated
   * @return value of the feature
   */
  public String getRelationshipType() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_relationshipType));
  }

  /**
   * setter for relationshipType - sets Denotes the semantic type of the relationship between
   * entities.
   *
   * <p>Currently based on the ACE2002 top-level relationships: {"AT" , "NEAR" , "PART" , "ROLE" ,
   * "SOCIAL"} Additional relationship types that have been added: {"QUANTITY", "ALIAS"}
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setRelationshipType(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_relationshipType), v);
  }

  // *--------------*
  // * Feature: source

  /**
   * getter for source - gets The source of the relationship (subject)
   *
   * @generated
   * @return value of the feature
   */
  public Entity getSource() {
    return (Entity) (_getFeatureValueNc(wrapGetIntCatchException(_FH_source)));
  }

  /**
   * setter for source - sets The source of the relationship (subject)
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setSource(Entity v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_source), v);
  }

  // *--------------*
  // * Feature: target

  /**
   * getter for target - gets The target of the relationship (object)
   *
   * @generated
   * @return value of the feature
   */
  public Entity getTarget() {
    return (Entity) (_getFeatureValueNc(wrapGetIntCatchException(_FH_target)));
  }

  /**
   * setter for target - sets The target of the relationship (object)
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setTarget(Entity v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_target), v);
  }

  // *--------------*
  // * Feature: value

  /**
   * getter for value - gets The value of the relationship, typically the spanning text within the
   * sentence
   *
   * @generated
   * @return value of the feature
   */
  public String getValue() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_value));
  }

  /**
   * setter for value - sets The value of the relationship, typically the spanning text within the
   * sentence
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setValue(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_value), v);
  }

  // *--------------*
  // * Feature: relationSubType

  /**
   * getter for relationSubType - gets Used to record any sub-type information for the relation, for
   * example the sub-relations defined within the ACE dataset.
   *
   * @generated
   * @return value of the feature
   */
  public String getRelationSubType() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_relationSubType));
  }

  /**
   * setter for relationSubType - sets Used to record any sub-type information for the relation, for
   * example the sub-relations defined within the ACE dataset.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setRelationSubType(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_relationSubType), v);
  }

  // *--------------*
  // * Feature: sentenceDistance

  /**
   * getter for sentenceDistance - gets A measure of the distance in the document between the two
   * entities. The number of sentences between the two entities.
   *
   * @generated
   * @return value of the feature
   */
  public int getSentenceDistance() {
    return _getIntValueNc(wrapGetIntCatchException(_FH_sentenceDistance));
  }

  /**
   * setter for sentenceDistance - sets A measure of the distance in the document between the two
   * entities. The number of sentences between the two entities.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setSentenceDistance(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_sentenceDistance), v);
  }

  // *--------------*
  // * Feature: wordDistance

  /**
   * getter for wordDistance - gets A measure of the distance in the sentence between the two
   * entities. The number of words between the two entities.
   *
   * @generated
   * @return value of the feature
   */
  public int getWordDistance() {
    return _getIntValueNc(wrapGetIntCatchException(_FH_wordDistance));
  }

  /**
   * setter for wordDistance - sets A measure of the distance in the sentence between the two
   * entities. The number of words between the two entities.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setWordDistance(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_wordDistance), v);
  }

  // *--------------*
  // * Feature: dependencyDistance

  /**
   * getter for dependencyDistance - gets A measure of the dependency distance in the sentence
   * between the two entities. The number of dependant words between the two entities according to a
   * dependency parser.
   *
   * @generated
   * @return value of the feature
   */
  public int getDependencyDistance() {
    return _getIntValueNc(wrapGetIntCatchException(_FH_dependencyDistance));
  }

  /**
   * setter for dependencyDistance - sets A measure of the dependency distance in the sentence
   * between the two entities. The number of dependant words between the two entities according to a
   * dependency parser.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setDependencyDistance(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_dependencyDistance), v);
  }
}
