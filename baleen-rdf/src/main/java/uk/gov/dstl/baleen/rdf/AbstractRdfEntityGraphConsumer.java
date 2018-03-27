// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.rdf;

import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.ENTITY;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.EVENT;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.Lists;

import uk.gov.dstl.baleen.consumers.AbstractEntityGraphConsumer;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.graph.value.Mode;
import uk.gov.dstl.baleen.graph.value.ValueStrategy;

/** An abstract Consumer translating the entity graph into RDF for storage by extending classes. */
public abstract class AbstractRdfEntityGraphConsumer extends AbstractEntityGraphConsumer {

  /**
   * The namespace to use in the owl schema
   *
   * @baleen.config http://baleen.dstl.gov.uk/
   */
  public static final String PARAM_NAMESPACE = "namespace";

  @ConfigurationParameter(name = PARAM_NAMESPACE, defaultValue = "http://baleen.dstl.gov.uk/")
  private String namespace;

  /**
   * The prefix to use for the namespace
   *
   * @baleen.config baleen
   */
  public static final String PARAM_PREFIX = "prefix";

  @ConfigurationParameter(name = PARAM_PREFIX, defaultValue = "baleen")
  private String prefix;

  /**
   * The properties to ignore in owl schema
   *
   * @baleen.config internalId
   */
  public static final String PARAM_IGNORE = "ignoreProperties";

  @ConfigurationParameter(
    name = PARAM_IGNORE,
    defaultValue = {"internalId", "inNormalised"}
  )
  private String[] ignoreProperties;

  private OntModel documentOntology;

  private final ValueStrategy<String, String> mode = new Mode<>();

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    multiValueProperties = true;
    try {
      TypeSystem typeSystem = JCasFactory.createJCas().getTypeSystem();
      OwlSchemaFactory schemaFactory =
          new OwlSchemaFactory(namespace, typeSystem, Arrays.asList(ignoreProperties));
      documentOntology = schemaFactory.createEntityOntology();
    } catch (CASRuntimeException | UIMAException e) {
      throw new ResourceInitializationException(e);
    }
    super.doInitialize(aContext);
  }

  @Override
  protected void processGraph(String documentSourceName, Graph graph)
      throws AnalysisEngineProcessException {

    OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, documentOntology);
    model.setNsPrefix("baleen", namespace);
    GraphTraversalSource traversal = graph.traversal();
    traversal.V().forEachRemaining(v -> addNodeToModel(model, v));
    traversal.E().forEachRemaining(e -> addRelationToModel(model, e));

    outputModel(documentSourceName, model);
  }

  private ObjectProperty addRelationToModel(OntModel model, Edge e) {
    Individual source = model.getIndividual(namespace + e.outVertex().id());
    Individual target = model.getIndividual(namespace + e.inVertex().id());
    ObjectProperty property = model.getObjectProperty(namespace + e.label());

    if (source != null && target != null && property != null) {
      source.addProperty(property, target);
      return property;
    } else {
      getMonitor()
          .warn(
              "Missing individuals {} or {} or relation {}",
              e.outVertex(),
              e.inVertex(),
              e.label());
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private Object addNodeToModel(OntModel model, Vertex v) {
    try {
      String label = v.label();
      if (EVENT.equals(label)) {
        return addIndividual(model, v, EVENT);
      }
      if (ENTITY.equals(label)) {
        Iterator<VertexProperty<Object>> properties = v.properties("type");
        List<?> types =
            Lists.newArrayList(properties)
                .stream()
                .filter(VertexProperty::isPresent)
                .map(VertexProperty::value)
                .collect(Collectors.toList());
        Optional<String> aggregate = mode.aggregate((List<String>) types);
        if (aggregate.isPresent()) {
          return addIndividual(model, v, aggregate.get());
        }

        getMonitor().warn("Not type information for {} using entity", v);
        return addIndividual(model, v, ENTITY);
      }
      getMonitor().warn("Unrecognized Label {}", label);
    } catch (BaleenException e) {
      getMonitor().warn("Error adding node {} - {} ", v, e.getMessage());
    }
    return null;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private Individual addIndividual(OntModel model, Vertex v, String className) {
    OntClass ontClass = model.getOntClass(namespace + className);
    if (ontClass != null) {
      Individual individual = ontClass.createIndividual(namespace + v.id());
      Map<String, List> propertyValueMap = ElementHelper.vertexPropertyValueMap(v);
      propertyValueMap
          .entrySet()
          .forEach(
              e -> {
                Property property = model.getProperty(namespace + e.getKey());
                if (property != null) {
                  e.getValue().forEach(value -> individual.addProperty(property, value.toString()));
                }
              });
      return individual;
    } else {
      getMonitor().warn("Missing ontology class {}", className);
    }
    return null;
  }

  /**
   * Output the rdf representation of the document
   *
   * @param model to be output
   * @throws AnalysisEngineProcessException
   */
  protected abstract void outputModel(String documentSource, OntModel model)
      throws AnalysisEngineProcessException;
}
