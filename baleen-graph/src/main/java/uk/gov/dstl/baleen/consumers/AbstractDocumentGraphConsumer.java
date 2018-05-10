// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers;

import java.util.Set;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.consumers.utils.SourceUtils;
import uk.gov.dstl.baleen.graph.DocumentGraphFactory;
import uk.gov.dstl.baleen.graph.DocumentGraphOptions;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * Base abstract consumer for the document graph. Contains the properties for the {@link
 * DocumentGraphOptions}.
 */
public abstract class AbstractDocumentGraphConsumer extends AbstractGraphConsumer {

  /**
   * Should we output the meta information as graph properties?
   *
   * @baleen.config false
   */
  public static final String PARAM_OUTPUT_META = "outputMeta";

  @ConfigurationParameter(name = PARAM_OUTPUT_META, defaultValue = "false")
  protected boolean outputMeta;

  /**
   * Should we output the content as a graph property?
   *
   * @baleen.config false
   */
  public static final String PARAM_OUTPUT_CONTENT = "outputContent";

  @ConfigurationParameter(name = PARAM_OUTPUT_CONTENT, defaultValue = "false")
  protected boolean outputContent;

  /**
   * Should we output the document to the graph?
   *
   * @baleen.config false
   */
  public static final String PARAM_OUTPUT_DOCUMENT = "outputDocument";

  @ConfigurationParameter(name = PARAM_OUTPUT_DOCUMENT, defaultValue = "false")
  protected boolean outputDocument;

  /**
   * Should we output the referents to the graph?
   *
   * @baleen.config true
   */
  public static final String PARAM_OUTPUT_REFERENTS = "outputReferenceTargets";

  @ConfigurationParameter(name = PARAM_OUTPUT_REFERENTS, defaultValue = "true")
  protected boolean outputReferents;

  /**
   * Should we output the relations to the graph?
   *
   * @baleen.config true
   */
  public static final String PARAM_OUTPUT_RELATIONS = "outputRelations";

  @ConfigurationParameter(name = PARAM_OUTPUT_RELATIONS, defaultValue = "true")
  protected boolean outputRelations;

  /**
   * Should we output the relations to the graph as links?
   *
   * @baleen.config true
   */
  public static final String PARAM_OUTPUT_RELATIONS_AS_LINKS = "outputRelationsAsLinks";

  @ConfigurationParameter(name = PARAM_OUTPUT_RELATIONS_AS_LINKS, defaultValue = "false")
  protected boolean outputRelationsAsLinks;

  private DocumentGraphFactory factory;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);

    Set<Class<? extends Entity>> typeClasses = TypeUtils.getTypeClasses(Entity.class, typeNames);

    DocumentGraphOptions.Builder builder =
        DocumentGraphOptions.builder()
            .withContentHashAsId(contentHashAsId)
            .withContent(outputContent)
            .withMeta(outputMeta)
            .withReferenceTargets(outputReferents)
            .withRelations(outputRelations)
            .withRelationsAsLinks(outputRelationsAsLinks)
            .withEvents(outputEvents)
            .withDocument(outputDocument)
            .withStopFeatures(filterFeatures)
            .withValueCoercer(valueCoercer)
            .withTypeClasses(typeClasses);

    addOptions(builder);
    factory = new DocumentGraphFactory(getMonitor(), builder.build());
  }

  /**
   * Chance for implementations to manipulate the options
   *
   * @param builder
   */
  protected void addOptions(DocumentGraphOptions.Builder builder) {}

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
    String documentSourceName = SourceUtils.getDocumentSourceBaseName(jCas);
    Graph documentGraph = factory.create(jCas);
    processGraph(documentSourceName, documentGraph);
  }

  /**
   * This method is called once the data from the document has been added to the graph.
   *
   * @param graph containing document information
   * @param documentSourceName the source name for the document
   * @throws AnalysisEngineProcessException
   */
  protected abstract void processGraph(String documentSourceName, Graph graph)
      throws AnalysisEngineProcessException;
}
