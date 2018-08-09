// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.rdf;

import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.ENTITY;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.EVENT;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.MENTION;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.MENTION_IN;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.MENTION_OF;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.PARTICIPANT_IN;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.REFERENCE_TARGET;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.RELATION;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.SOURCE;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.TARGET;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.XSD;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.jcas.cas.AnnotationBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.FeatureDescription;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.structure.Document;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * A factory for creating OWL schema from the TypeSystem.
 *
 * <p>Specific top level types are added then the {@link Entity} type is used to create the
 * subtypes. This means the OWL will be updated for changes to the Entity types.
 */
public class OwlSchemaFactory {

  private static final String EN = "EN";

  private final String namespace;

  private final TypeSystem typeSystem;

  private TypeSystemDescription descriptions;

  private List<String> ignoreProperties;

  /** New instance */
  public OwlSchemaFactory(String namespace, TypeSystem typeSystem, List<String> ignoreProperties)
      throws ResourceInitializationException {
    this.namespace = namespace;
    this.typeSystem = typeSystem;
    this.ignoreProperties = ignoreProperties;
    descriptions = TypeSystemDescriptionFactory.createTypeSystemDescription();
  }

  /** Creates a document ontology */
  public OntModel createDocumentOntology() {
    OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

    OntClass document = addType(ontModel, null, getType(Document.class.getSimpleName()));
    OntClass mention = ontModel.createClass(namespace + MENTION);

    mention.addComment("Root mention type", EN);
    addProperty(ontModel, mention, "begin", "The start of the mention offset", XSD.xint);
    addProperty(ontModel, mention, "end", "The end of the mention offset", XSD.xint);
    addProperty(ontModel, mention, "value", "The value of the mention", XSD.xstring);
    addProperty(ontModel, mention, "docId", "The docId the mention came from", XSD.xstring);

    OntClass reference = addType(ontModel, null, getType(REFERENCE_TARGET));
    OntClass relation = addType(ontModel, mention, getType(RELATION));

    OntClass entity = addType(ontModel, mention, getType(ENTITY));
    OntClass event = addType(ontModel, mention, getType(EVENT));

    addRelation(ontModel, entity, entity, RELATION, "A relationship between two entities");
    addRelation(ontModel, entity, relation, SOURCE, "The source of the relationship");
    addRelation(ontModel, relation, entity, TARGET, "The target of the relationship");
    addRelation(ontModel, mention, document, MENTION_IN, "The document this is mentioned in");
    addRelation(ontModel, entity, reference, MENTION_OF, "The mention of the reference");
    addRelation(ontModel, entity, event, PARTICIPANT_IN, "A participant in the event");

    return ontModel;
  }

  /** Creates an entity ontology */
  public OntModel createEntityOntology() {
    OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

    OntClass entity = addType(ontModel, null, getType(ENTITY));
    OntClass event = addType(ontModel, null, getType(EVENT));

    addProperty(ontModel, entity, "value", "The value of the mention", XSD.xstring);
    addProperty(ontModel, entity, "longestValue", "The longest value of the mention", XSD.xstring);
    addProperty(
        ontModel, entity, "mostCommonValue", "The most common value of the mention", XSD.xstring);
    addProperty(ontModel, entity, "mentions", "The details of the mentions", XSD.xstring);
    addProperty(ontModel, "docId", "The docId the mention came from", XSD.xstring);

    addRelation(ontModel, entity, entity, RELATION, "A relationship between two entities");
    addRelation(ontModel, entity, event, PARTICIPANT_IN, "A participant in the event");

    return ontModel;
  }

  private void addRelation(
      OntModel ontModel, OntClass domain, OntClass range, String property, String comment) {
    ObjectProperty objectProperty = ontModel.createObjectProperty(namespace + property);
    objectProperty.addComment(comment, EN);
    objectProperty.setDomain(domain);
    objectProperty.setRange(range);
  }

  private void addProperty(OntModel ontModel, String name, String comment, Resource range) {
    DatatypeProperty begin = ontModel.createDatatypeProperty(namespace + name);
    begin.addComment(comment, EN);
    begin.addRange(range);
  }

  private void addProperty(
      OntModel ontModel, OntClass domain, String name, String comment, Resource range) {
    DatatypeProperty begin = ontModel.createDatatypeProperty(namespace + name);
    begin.addComment(comment, EN);
    begin.addDomain(domain);
    begin.addRange(range);
  }

  private Type getType(String typeName) {
    Class<AnnotationBase> typeClass = TypeUtils.getType(typeName, typeSystem);
    return typeSystem.getType(typeClass.getCanonicalName());
  }

  private OntClass addType(OntModel ontModel, OntClass parent, Type type) {

    TypeDescription description = descriptions.getType(type.getName());

    OntClass ontClass = ontModel.createClass(namespace + type.getShortName());
    String comment = description.getDescription();
    if (comment != null) {
      ontClass.addComment(comment, EN);
    }

    HashSet<FeatureDescription> features = new HashSet<>();
    if (parent != null) {
      parent.addSubClass(ontClass);
      features.addAll(Arrays.asList(description.getFeatures()));
    } else {

      for (Feature feature : type.getFeatures()) {
        TypeDescription typeDescription = descriptions.getType(feature.getDomain().getName());
        if (typeDescription != null) {
          features.addAll(Arrays.asList(typeDescription.getFeatures()));
        }
      }
    }

    for (FeatureDescription feature : features) {
      if (!ignoreProperties.contains(feature.getName())) {
        addFeature(ontModel, ontClass, feature);
      }
    }

    for (Type child : typeSystem.getDirectSubtypes(type)) {
      addType(ontModel, ontClass, child);
    }

    return ontClass;
  }

  private void addFeature(OntModel ontModel, OntClass ontClass, FeatureDescription feature) {
    if (ontModel.getDatatypeProperty(namespace + feature.getName()) == null) {
      DatatypeProperty property = ontModel.createDatatypeProperty(namespace + feature.getName());
      String propertyComment = feature.getDescription();
      if (propertyComment != null) {
        property.addComment(propertyComment, EN);
      }

      property.addDomain(ontClass);
      property.addRange(getRange(feature));
    }
  }

  private Resource getRange(FeatureDescription feature) {

    String rangeTypeName = feature.getRangeTypeName();
    Resource range;
    switch (rangeTypeName) {
      case "uima.cas.Boolean":
        range = XSD.xboolean;
        break;
      case "uima.cas.Byte":
        range = XSD.xbyte;
        break;
      case "uima.cas.Short":
        range = XSD.xshort;
        break;
      case "uima.cas.Integer":
        range = XSD.xint;
        break;
      case "uima.cas.Long":
        range = XSD.xlong;
        break;
      case "uima.cas.Float":
        range = XSD.xfloat;
        break;
      case "uima.cas.Double":
        range = XSD.xdouble;
        break;
      case "uima.cas.String":
        // Fall
      default:
        range = XSD.xstring;
        break;
    }

    return range;
  }
}
