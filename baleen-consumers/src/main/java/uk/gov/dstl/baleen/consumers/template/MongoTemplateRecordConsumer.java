// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

import java.util.Collection;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.resources.SharedMongoResource;

/**
 * A RecordConsumer that writes RecordsDocuments to MongoDB.
 *
 * <p>This stores the extracted records in a MongoDB collection, specified using the records
 * configuration parameter, using a shared Mongo resource as supplied through the mongo
 * configuration parameter. Document IDs are, by default, a hash of the document content but can be
 * optionally configured to use the document source URI by setting the contentHashAsId parameter to
 * false.
 */
public class MongoTemplateRecordConsumer extends AbstractTemplateRecordConsumer {

  /**
   * Connection to Mongo
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedMongoResource
   */
  public static final String KEY_MONGO = "mongo";

  @ExternalResource(key = KEY_MONGO)
  private SharedMongoResource mongoResource;

  /**
   * Should a hash of the content be used to generate the ID? If false, then a hash of the Source
   * URI is used instead.
   *
   * @baleen.config true
   */
  public static final String PARAM_CONTENT_HASH_AS_ID = "contentHashAsId";

  @ConfigurationParameter(name = PARAM_CONTENT_HASH_AS_ID, defaultValue = "true")
  private boolean contentHashAsId = true;

  /**
   * The collection to output records to.
   *
   * @baleen.config records
   */
  public static final String PARAM_RECORDS_COLLECTION = "records";

  @ConfigurationParameter(name = PARAM_RECORDS_COLLECTION, defaultValue = "records")
  private String recordsCollectionName;

  private MongoCollection<Document> recordsCollection;

  private ObjectMapper objectMapper;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);
    MongoDatabase db = mongoResource.getDB();
    recordsCollection = db.getCollection(recordsCollectionName);
    objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(Include.NON_NULL);
  }

  @Override
  protected void doDestroy() {
    recordsCollection = null;
  }

  @Override
  protected void writeRecords(
      JCas jCas, String documentSourceName, Map<String, Collection<ExtractedRecord>> records)
      throws AnalysisEngineProcessException {
    MongoExtractedRecords mongoRecords =
        new MongoExtractedRecords(getUniqueId(jCas), documentSourceName, records);
    save(mongoRecords);
  }

  /**
   * Write records to MongoDB.
   *
   * @param mongoRecords the mongo records
   */
  private void save(MongoExtractedRecords mongoRecords) {
    Document document;
    try {
      document = createMongoDocument(mongoRecords);
    } catch (JsonProcessingException e) {
      getMonitor().warn("Failed to serialise records for Mongo", e);
      return;
    }
    recordsCollection.insertOne(document);
  }

  /**
   * Creates a mongo document pojo for serialisation.
   *
   * @param mongoRecords the mongo records
   * @return the document
   * @throws JsonProcessingException the json processing exception
   */
  private Document createMongoDocument(MongoExtractedRecords mongoRecords)
      throws JsonProcessingException {
    String json = objectMapper.writeValueAsString(mongoRecords);
    return Document.parse(json);
  }

  /**
   * Gets the unique id for a document (if contentHashAsId is true then as hash of the content is
   * used, otherwise a hash of the source URI is used).
   *
   * @param jCas the JCas
   * @return the unique id
   */
  private String getUniqueId(JCas jCas) {
    return ConsumerUtils.getExternalId(getDocumentAnnotation(jCas), contentHashAsId);
  }
}
