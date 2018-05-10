// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.consumers.analysis.converters.DocumentConverter;
import uk.gov.dstl.baleen.consumers.analysis.converters.EntityConverter;
import uk.gov.dstl.baleen.consumers.analysis.converters.MentionConverter;
import uk.gov.dstl.baleen.consumers.analysis.converters.RelationConverter;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenDocument;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenEntity;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenMention;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenRelation;
import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.consumers.utils.DefaultFields;
import uk.gov.dstl.baleen.consumers.utils.EntityRelationConverter;
import uk.gov.dstl.baleen.consumers.utils.IEntityConverterFields;
import uk.gov.dstl.baleen.resources.SharedIdGenerator;
import uk.gov.dstl.baleen.uima.BaleenConsumer;

/**
 * A standardised approach to outputting data from baleen in a analytically usable form.
 *
 * <p>Prior to Baleen 2.4 Mongo data was saved in one format and Elasticsearch in another. Other
 * specialist outputs where also used (eg Postgres). None of these formats had all elements (eg
 * Elasticsearch has no notion of reference target) or standard terminology.
 *
 * <p>This consumer attempts to format the output in a way that can be output to multiple datasets
 * and which contains sufficient information to satisfy common querying in a NoSQL (denormalised
 * fashion).
 *
 * <p>The consumer works by creating a set of POJOs (see the
 * uk.gov.dstl.baleen.consumers.analysis.data package) using a set of converters
 * (uk.gov.dstl.baleen.consumers.analysis.converters).
 *
 * <p>The naming is as follows. This is a terminology conflict between UIMA Type Entity and the
 * BaleenEntity here. The names have been chosen to reflect the user of Baleen output understanding:
 * any mention of "United Kingdom" represents the same entity, that is an entity is a 'thing' and
 * not merely a mention of that thing.
 *
 * <ul>
 *   <li>BaleenDocument represents the document being processed by Baleen along with its metadata.
 *   <li>BaleenMention is a UIMA annotation in Baleen (the annotation is a subclass of
 *       semantic.Entity). Thus it is a span of text which refers to a BaleenEntity.
 *   <li>BaleenEntity is a thing within a document, which is mentioned one or more times. It is
 *       effectively defined as the sum of its mentions (in terms of properties, type, and value).
 *   <li>BaleenRelation is a relation between two mentions (corresponding exactly to Baleen's type
 *       system notion)
 *   <li>BaleenFullDocument is a BaleneDocument plus all its entities, mentions, relations)
 * </ul>
 *
 * Rather than have each of these having a very distinct structure, they adopt a standard properties
 * approach for (some) metadata/attributes. This is arguable too flexible for Baleen's type system
 * but it allows a variety of information to be stored without conflict or need to change code.
 */
public abstract class AbstractAnalysisConsumer extends BaleenConsumer {

  /**
   * Should output documents
   *
   * @baleen.config true
   */
  public static final String PARAM_OUTPUT_DOCUMENTS = "documents";

  @ConfigurationParameter(name = PARAM_OUTPUT_DOCUMENTS, defaultValue = "true")
  private boolean outputDocuments;

  /**
   * Should output mentions
   *
   * @baleen.config true
   */
  public static final String PARAM_OUTPUT_MENTIONS = "mentions";

  @ConfigurationParameter(name = PARAM_OUTPUT_MENTIONS, defaultValue = "true")
  private boolean outputMentions;

  /**
   * Should output relations
   *
   * @baleen.config true
   */
  public static final String PARAM_OUTPUT_RELATIONS = "relations";

  @ConfigurationParameter(name = PARAM_OUTPUT_RELATIONS, defaultValue = "true")
  private boolean outputRelations;

  /**
   * Should output entities
   *
   * @baleen.config true
   */
  public static final String PARAM_OUTPUT_ENTITIES = "entities";

  @ConfigurationParameter(name = PARAM_OUTPUT_ENTITIES, defaultValue = "true")
  private boolean outputEntities;

  /**
   * Should a hash of the content be used to generate the ID? If false, then a hash of the Source
   * URI is used instead.
   *
   * @baleen.config false
   */
  public static final String PARAM_CONTENT_HASH_AS_ID = "contentHashAsId";

  @ConfigurationParameter(name = PARAM_CONTENT_HASH_AS_ID, defaultValue = "false")
  private boolean contentHashAsId;

  private EntityRelationConverter entityRelationConverter;

  /**
   * The common id generator
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedIdGenerator
   */
  @ExternalResource(key = SharedIdGenerator.RESOURCE_KEY)
  private SharedIdGenerator idGenerator;

  @Override
  public void doInitialize(final UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);

    final IEntityConverterFields fields = new DefaultFields();

    final HashSet<String> stopFeatures = new HashSet<>();
    stopFeatures.add("uima.cas.AnnotationBase:sofa");
    stopFeatures.add("uk.gov.dstl.baleen.types.BaleenAnnotation:internalId");

    entityRelationConverter =
        new EntityRelationConverter(getMonitor(), stopFeatures, fields, false);

    if (outputDocuments) {
      initialiseForDocuments();
    }

    if (outputMentions) {
      initialiseForMentions();
    }

    if (outputEntities) {
      initialiseForEntities();
    }

    if (outputRelations) {
      initialiseForRelations();
    }
  }

  protected abstract void initialiseForDocuments() throws ResourceInitializationException;

  protected abstract void initialiseForEntities() throws ResourceInitializationException;

  protected abstract void initialiseForRelations() throws ResourceInitializationException;

  protected abstract void initialiseForMentions() throws ResourceInitializationException;

  @Override
  protected void doProcess(final JCas jCas) throws AnalysisEngineProcessException {
    idGenerator.resetIfNewJCas(jCas);

    final DocumentAnnotation documentAnnotation = getDocumentAnnotation(jCas);
    final String baleenDocumentId =
        ConsumerUtils.getExternalId(documentAnnotation, contentHashAsId);
    final String documentId = idGenerator.generateForExternalId(baleenDocumentId);

    // Output the 'basic types' values first

    final DocumentConverter documentConverter = new DocumentConverter();
    final BaleenDocument baleenDocument =
        documentConverter.convert(jCas, documentId, baleenDocumentId, documentAnnotation);

    final MentionConverter mentionConverter =
        new MentionConverter(getMonitor(), idGenerator, entityRelationConverter);
    final Map<String, BaleenMention> mentions =
        mentionConverter.convert(jCas, documentId, baleenDocumentId);

    final EntityConverter entityConverter = new EntityConverter();
    final Map<String, BaleenEntity> entities = entityConverter.convert(mentions);

    final RelationConverter relationConverter =
        new RelationConverter(getMonitor(), idGenerator, entityRelationConverter);
    final Map<String, BaleenRelation> relations =
        relationConverter.convert(jCas, documentId, baleenDocumentId, mentions);

    try {
      deleteDocument(baleenDocument);
    } catch (final AnalysisEngineProcessException e) {
      getMonitor().warn("Unable to delete older content", e);
    }

    if (outputDocuments) {
      try {
        saveDocument(baleenDocument);
      } catch (final AnalysisEngineProcessException e) {
        getMonitor().warn("Unable to save document", e);
      }
    }

    if (outputMentions) {
      try {
        saveMentions(mentions.values());
      } catch (final AnalysisEngineProcessException e) {
        getMonitor().warn("Unable to save mentions", e);
      }
    }

    if (outputEntities) {
      try {
        saveEntities(entities.values());
      } catch (final AnalysisEngineProcessException e) {
        getMonitor().warn("Unable to save entities", e);
      }
    }

    if (outputRelations) {
      try {
        saveRelations(relations.values());
      } catch (final AnalysisEngineProcessException e) {
        getMonitor().warn("Unable to save relations", e);
      }
    }
  }

  protected SharedIdGenerator getIdGenerator() {
    return idGenerator;
  }

  protected abstract void deleteDocument(final BaleenDocument document)
      throws AnalysisEngineProcessException;

  protected abstract void saveDocument(final BaleenDocument document)
      throws AnalysisEngineProcessException;

  protected abstract void saveMentions(final Collection<BaleenMention> mentions)
      throws AnalysisEngineProcessException;

  protected abstract void saveEntities(final Collection<BaleenEntity> entities)
      throws AnalysisEngineProcessException;

  protected abstract void saveRelations(final Collection<BaleenRelation> relations)
      throws AnalysisEngineProcessException;
}
