/* First created by JCasGen Wed Jan 14 12:58:27 GMT 2015 */
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.types.semantic;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import uk.gov.dstl.baleen.core.history.Recordable;
import uk.gov.dstl.baleen.types.Base;

/**
 * Records a relationship between named entities, explicitly mentioned within the source document.
 * Updated by JCasGen Wed Apr 13 13:23:16 BST 2016 XML source:
 * H:/git/TextProcessing/core/baleen/baleen-uima/src/main/resources/types/common_type_system.xml
 *
 * @generated
 */
// ***************************************************************************************
// WARNING Edited generated class to add Recordable interface, be careful on regeneration.
// ***************************************************************************************
public class Relation extends Base implements Recordable {
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
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   */
  public Relation(int addr, TOP_Type type) {
    super(addr, type);
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
    if (Relation_Type.featOkTst && ((Relation_Type) jcasType).casFeat_relationshipType == null)
      jcasType.jcas.throwFeatMissing(
          "relationshipType", "uk.gov.dstl.baleen.types.semantic.Relation");
    return jcasType.ll_cas.ll_getStringValue(
        addr, ((Relation_Type) jcasType).casFeatCode_relationshipType);
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
    if (Relation_Type.featOkTst && ((Relation_Type) jcasType).casFeat_relationshipType == null)
      jcasType.jcas.throwFeatMissing(
          "relationshipType", "uk.gov.dstl.baleen.types.semantic.Relation");
    jcasType.ll_cas.ll_setStringValue(
        addr, ((Relation_Type) jcasType).casFeatCode_relationshipType, v);
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
    if (Relation_Type.featOkTst && ((Relation_Type) jcasType).casFeat_source == null)
      jcasType.jcas.throwFeatMissing("source", "uk.gov.dstl.baleen.types.semantic.Relation");
    return (Entity)
        (jcasType.ll_cas.ll_getFSForRef(
            jcasType.ll_cas.ll_getRefValue(addr, ((Relation_Type) jcasType).casFeatCode_source)));
  }

  /**
   * setter for source - sets The source of the relationship (subject)
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setSource(Entity v) {
    if (Relation_Type.featOkTst && ((Relation_Type) jcasType).casFeat_source == null)
      jcasType.jcas.throwFeatMissing("source", "uk.gov.dstl.baleen.types.semantic.Relation");
    jcasType.ll_cas.ll_setRefValue(
        addr, ((Relation_Type) jcasType).casFeatCode_source, jcasType.ll_cas.ll_getFSRef(v));
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
    if (Relation_Type.featOkTst && ((Relation_Type) jcasType).casFeat_target == null)
      jcasType.jcas.throwFeatMissing("target", "uk.gov.dstl.baleen.types.semantic.Relation");
    return (Entity)
        (jcasType.ll_cas.ll_getFSForRef(
            jcasType.ll_cas.ll_getRefValue(addr, ((Relation_Type) jcasType).casFeatCode_target)));
  }

  /**
   * setter for target - sets The target of the relationship (object)
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setTarget(Entity v) {
    if (Relation_Type.featOkTst && ((Relation_Type) jcasType).casFeat_target == null)
      jcasType.jcas.throwFeatMissing("target", "uk.gov.dstl.baleen.types.semantic.Relation");
    jcasType.ll_cas.ll_setRefValue(
        addr, ((Relation_Type) jcasType).casFeatCode_target, jcasType.ll_cas.ll_getFSRef(v));
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
    if (Relation_Type.featOkTst && ((Relation_Type) jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.semantic.Relation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Relation_Type) jcasType).casFeatCode_value);
  }

  /**
   * setter for value - sets The value of the relationship, typically the spanning text within the
   * sentence
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setValue(String v) {
    if (Relation_Type.featOkTst && ((Relation_Type) jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "uk.gov.dstl.baleen.types.semantic.Relation");
    jcasType.ll_cas.ll_setStringValue(addr, ((Relation_Type) jcasType).casFeatCode_value, v);
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
    if (Relation_Type.featOkTst && ((Relation_Type) jcasType).casFeat_relationSubType == null)
      jcasType.jcas.throwFeatMissing(
          "relationSubType", "uk.gov.dstl.baleen.types.semantic.Relation");
    return jcasType.ll_cas.ll_getStringValue(
        addr, ((Relation_Type) jcasType).casFeatCode_relationSubType);
  }

  /**
   * setter for relationSubType - sets Used to record any sub-type information for the relation, for
   * example the sub-relations defined within the ACE dataset.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setRelationSubType(String v) {
    if (Relation_Type.featOkTst && ((Relation_Type) jcasType).casFeat_relationSubType == null)
      jcasType.jcas.throwFeatMissing(
          "relationSubType", "uk.gov.dstl.baleen.types.semantic.Relation");
    jcasType.ll_cas.ll_setStringValue(
        addr, ((Relation_Type) jcasType).casFeatCode_relationSubType, v);
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
    if (Relation_Type.featOkTst && ((Relation_Type) jcasType).casFeat_sentenceDistance == null) {
      jcasType.jcas.throwFeatMissing(
          "sentenceDistance", "uk.gov.dstl.baleen.types.semantic.Relation");
    }
    return jcasType.ll_cas.ll_getIntValue(
        addr, ((Relation_Type) jcasType).casFeatCode_sentenceDistance);
  }

  /**
   * setter for sentenceDistance - sets A measure of the distance in the document between the two
   * entities. The number of sentences between the two entities.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setSentenceDistance(int v) {
    if (Relation_Type.featOkTst && ((Relation_Type) jcasType).casFeat_sentenceDistance == null) {
      jcasType.jcas.throwFeatMissing(
          "sentenceDistance", "uk.gov.dstl.baleen.types.semantic.Relation");
    }
    jcasType.ll_cas.ll_setIntValue(
        addr, ((Relation_Type) jcasType).casFeatCode_sentenceDistance, v);
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
    if (Relation_Type.featOkTst && ((Relation_Type) jcasType).casFeat_wordDistance == null) {
      jcasType.jcas.throwFeatMissing("wordDistance", "uk.gov.dstl.baleen.types.semantic.Relation");
    }
    return jcasType.ll_cas.ll_getIntValue(
        addr, ((Relation_Type) jcasType).casFeatCode_wordDistance);
  }

  /**
   * setter for wordDistance - sets A measure of the distance in the sentence between the two
   * entities. The number of words between the two entities.
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setWordDistance(int v) {
    if (Relation_Type.featOkTst && ((Relation_Type) jcasType).casFeat_wordDistance == null) {
      jcasType.jcas.throwFeatMissing("wordDistance", "uk.gov.dstl.baleen.types.semantic.Relation");
    }
    jcasType.ll_cas.ll_setIntValue(addr, ((Relation_Type) jcasType).casFeatCode_wordDistance, v);
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
    if (Relation_Type.featOkTst && ((Relation_Type) jcasType).casFeat_dependencyDistance == null) {
      jcasType.jcas.throwFeatMissing(
          "dependencyDistance", "uk.gov.dstl.baleen.types.semantic.Relation");
    }
    return jcasType.ll_cas.ll_getIntValue(
        addr, ((Relation_Type) jcasType).casFeatCode_dependencyDistance);
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
    if (Relation_Type.featOkTst && ((Relation_Type) jcasType).casFeat_dependencyDistance == null) {
      jcasType.jcas.throwFeatMissing(
          "dependencyDistance", "uk.gov.dstl.baleen.types.semantic.Relation");
    }
    jcasType.ll_cas.ll_setIntValue(
        addr, ((Relation_Type) jcasType).casFeatCode_dependencyDistance, v);
  }
}
