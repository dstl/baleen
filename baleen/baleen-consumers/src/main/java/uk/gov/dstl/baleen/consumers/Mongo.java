//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.consumers.utils.EntityRelationConverter;
import uk.gov.dstl.baleen.consumers.utils.IEntityConverterFields;
import uk.gov.dstl.baleen.consumers.utils.mongo.MongoFields;
import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.metadata.PublishedId;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.BaleenConsumer;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;

/**
 * Output processed CAS object into MongoDB.
 * 
 * <p>This consumer will output to Mongo using the new Baleen schema, which consists of 3 collections with the formats described below.
 * For each CAS processed, any existing reference to a document with the same external ID is deleted.</p>
 * <p><b>documents</b></p>
 * <pre>
{
    document: {
        type,
        source,
        language,
        ts,
        classification,
        caveats: [],
        releasability: []
    },
    publishedIds: [],
    metadata: {
    	key: [value, ...],
    	...
    },
    content,
    externalId
}
 * </pre>
 * 
 * <p><b>entities</b></p>
 * <p>Entities are grouped by their reference target, so all the entities in one Mongo document refer to the same thing.
 * Additional fields may be present depending on the entity type.</p>
 * <pre>
{
    docId,
    entities: [ 
        {
            confidence,
            externalId,
            begin,
            end,
            type,
            value,
            ...
        }
    ]
}
 * </pre>
 * 
 * <p><b>relations</b></p>
 * <p>Relations link two entities that are stored in the <em>entities</em> collection, which are referred to by their externalId.</p>
 * Additional fields may be present depending on the relation type.</p>
 * <pre>
{
    docId,
    source,
    target,
    begin,
    end,
    type,
    relationshipType,
    relationSubtype,
    value,
    confidence,
    ...
}
 * </pre>
 *
 * 
 * @baleen.javadoc
 */
public class Mongo extends BaleenConsumer {
	/**
	 * Connection to Mongo
	 * 
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedMongoResource
	 */
	public static final String KEY_MONGO = "mongo";
	@ExternalResource(key = KEY_MONGO)
	private SharedMongoResource mongoResource;

	/**
	 * Should a hash of the content be used to generate the ID?
	 * If false, then a hash of the Source URI is used instead.
	 * 
	 * @baleen.config true
	 */
	public static final String PARAM_CONTENT_HASH_AS_ID = "contentHashAsId";
	@ConfigurationParameter(name = PARAM_CONTENT_HASH_AS_ID, defaultValue = "true")
	private boolean contentHashAsId = true;

	/**
	 * Should we output the history to Mongo?
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_OUTPUT_HISTORY = "outputHistory";
	@ConfigurationParameter(name = PARAM_OUTPUT_HISTORY,  defaultValue = "false")
	private boolean outputHistory = false;

	/**
	 * The collection to output entities to
	 * 
	 * @baleen.config entities
	 */
	public static final String PARAM_ENTITIES_COLLECTION = "entities";
	@ConfigurationParameter(name = PARAM_ENTITIES_COLLECTION, defaultValue = "entities")
	private String entitiesCollectionName;

	/**
	 * The collection to output relationships to
	 * 
	 * @baleen.config relations
	 */
	public static final String PARAM_RELATIONS_COLLECTION = "relations";
	@ConfigurationParameter(name = PARAM_RELATIONS_COLLECTION, defaultValue = "relations")
	private String relationsCollectionName;

	/**
	 * The collection to output documents to
	 * 
	 * @baleen.config documents
	 */
	public static final String PARAM_DOCUMENTS_COLLECTION = "documents";
	@ConfigurationParameter(name = PARAM_DOCUMENTS_COLLECTION, defaultValue = "documents")
	private String documentsCollectionName;

	/**
	 * Should we output the document content to Mongo?
	 * 
	 * @baleen.config true
	 */
	public static final String PARAM_OUTPUT_CONTENT = "outputContent";
	@ConfigurationParameter(name = PARAM_OUTPUT_CONTENT, defaultValue = "true")
	private boolean outputContent = false;

	private DBCollection entitiesCollection;

	private DBCollection relationsCollection;

	private DBCollection documentsCollection;

	/**
	 * Holds the types of features that we're not interested in persisting  (stuff from UIMA for example)
	 * We're storing these so that we can loop through the features (and then ignore some of them)
	 */
	private Set<String> stopFeatures;
	
	//Fields
	public static final String FIELD_DOCUMENT_ID = "docId";
	public static final String FIELD_ENTITIES = "entities";
	public static final String FIELD_DOCUMENT = "document";
	public static final String FIELD_DOCUMENT_TYPE = "type";
	public static final String FIELD_DOCUMENT_SOURCE = "source";
	public static final String FIELD_DOCUMENT_LANGUAGE = "language";
	public static final String FIELD_DOCUMENT_TIMESTAMP = "timestamp";
	public static final String FIELD_DOCUMENT_CLASSIFICATION = "classification";
	public static final String FIELD_DOCUMENT_CAVEATS = "caveats";
	public static final String FIELD_DOCUMENT_RELEASABILITY = "releasability";
	public static final String FIELD_PUBLISHEDIDS = "publishedIds";
	public static final String FIELD_PUBLISHEDIDS_ID = "id";
	public static final String FIELD_PUBLISHEDIDS_TYPE = "type";
	public static final String FIELD_METADATA = "metadata";
	public static final String FIELD_CONTENT = "content";
	
	//Entity fields are defined in MongoFields()
	private final IEntityConverterFields fields = new MongoFields();

	/**
	 * Get the mongo db, collection and create some indexes
	 */
	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		DB db = mongoResource.getDB();
		entitiesCollection = db.getCollection(entitiesCollectionName);
		relationsCollection = db.getCollection(relationsCollectionName);
		documentsCollection = db.getCollection(documentsCollectionName);

		documentsCollection.createIndex(new BasicDBObject(fields.getExternalId(), 1));
		entitiesCollection.createIndex(new BasicDBObject(fields.getExternalId(), 1));
		relationsCollection.createIndex(new BasicDBObject(fields.getExternalId(), 1));
		relationsCollection.createIndex(new BasicDBObject(FIELD_DOCUMENT_ID, 1));
		entitiesCollection.createIndex(new BasicDBObject(FIELD_DOCUMENT_ID, 1));

		stopFeatures = new HashSet<>();
		stopFeatures.add("uima.cas.AnnotationBase:sofa");
		stopFeatures.add("uk.gov.dstl.baleen.types.BaleenAnnotation:internalId");
	}

	@Override
	public void doDestroy() {
		entitiesCollection = null;
		relationsCollection = null;
		documentsCollection = null;
	}

	protected String getUniqueId(JCas jCas) {
		return ConsumerUtils.getExternalId(getDocumentAnnotation(jCas), contentHashAsId);
	}

	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		String documentId = getUniqueId(jCas);

		// Delete any existing content in the database
		deleteAllContent(documentId);

		// Save
		saveDocument(documentId, jCas);
		saveEntities(documentId, jCas);
		saveRelations(documentId, jCas);
	}

	private void deleteAllContent(String documentId) {
		entitiesCollection.remove(new BasicDBObject(FIELD_DOCUMENT_ID, documentId));
		relationsCollection.remove(new BasicDBObject(FIELD_DOCUMENT_ID, documentId));
		documentsCollection.remove(new BasicDBObject(fields.getExternalId(), documentId));
	}

	private void saveDocument(String documentId, JCas jCas) {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();

		// document level

		builder.push(FIELD_DOCUMENT);

		DocumentAnnotation da = getDocumentAnnotation(jCas);

		builder.append(FIELD_DOCUMENT_TYPE, da.getDocType());
		builder.append(FIELD_DOCUMENT_SOURCE, da.getSourceUri());
		builder.append(FIELD_DOCUMENT_LANGUAGE, da.getLanguage());
		builder.append(FIELD_DOCUMENT_TIMESTAMP, new Date(da.getTimestamp()));

		builder.append(FIELD_DOCUMENT_CLASSIFICATION, da.getDocumentClassification());
		builder.append(FIELD_DOCUMENT_CAVEATS, UimaTypesUtils.toArray(da.getDocumentCaveats()));
		builder.append(FIELD_DOCUMENT_RELEASABILITY, UimaTypesUtils.toArray(da.getDocumentReleasability()));

		builder.pop();

		// Published Ids

		BasicDBList list = new BasicDBList();
		for(PublishedId pid : JCasUtil.select(jCas, PublishedId.class)) {
			list.add(new BasicDBObject(FIELD_PUBLISHEDIDS_TYPE, pid.getPublishedIdType())
					.append(FIELD_PUBLISHEDIDS_ID, pid.getValue()));
		}
		builder.append(FIELD_PUBLISHEDIDS, list);

		// Meta data

		Multimap<String,Object> meta = MultimapBuilder.linkedHashKeys().linkedListValues().build();
		for(Metadata metadata : JCasUtil.select(jCas, Metadata.class)) {
			String key = metadata.getKey();
			if(key.contains(".")){	//Field names can't contain a "." in Mongo, so replace with a _
				key = key.replaceAll("\\.", "_");
			}
			meta.put(key, metadata.getValue());
		}
		builder.append(FIELD_METADATA, meta.asMap());

		// Add content is requried

		if(outputContent) {
			builder.append(FIELD_CONTENT, jCas.getDocumentText());
		}

		// Save

		builder.add(fields.getExternalId(), documentId);

		documentsCollection.save(builder.get());
	}

	private void saveEntities(String documentId, JCas jCas) {
		EntityRelationConverter converter = new EntityRelationConverter(getMonitor(), outputHistory, getSupport().getDocumentHistory(jCas), stopFeatures, fields);

		// Compile all the reference targets together

		Multimap<ReferenceTarget, Entity> targetted = MultimapBuilder.hashKeys().linkedListValues().build();
		for(Entity entity : JCasUtil.select(jCas, Entity.class)) {

			if( entity.getReferent() != null ) {
				targetted.put(entity.getReferent(), entity);
			} else {
				// Create a fake reference target
				targetted.put(new ReferenceTarget(jCas), entity);
			}
		}

		for(Entry<ReferenceTarget, Collection<Entity>> entry : targetted.asMap().entrySet()) {

			BasicDBList list = new BasicDBList();

			for(Entity e : entry.getValue()) {
				list.add(converter.convertEntity(e));
			}

			BasicDBObject dbo = new BasicDBObject();
			dbo.append(FIELD_DOCUMENT_ID, documentId);
			dbo.append(FIELD_ENTITIES, list);

			entitiesCollection.save(dbo);
		}

	}
	
	private void saveRelations(String documentId, JCas jCas) {
		EntityRelationConverter converter = new EntityRelationConverter(getMonitor(), outputHistory, getSupport().getDocumentHistory(jCas), stopFeatures, fields);
		
		for(Relation r : JCasUtil.select(jCas, Relation.class)){
			Map<String, Object> relationFeatures = converter.convertRelation(r);
			BasicDBObjectBuilder builder = BasicDBObjectBuilder.start(relationFeatures);
			
			builder.add(FIELD_DOCUMENT_ID, documentId);
			relationsCollection.save(builder.get());
		}
	}
}
