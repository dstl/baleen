//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.consumers.utils.EntityRelationConverter;
import uk.gov.dstl.baleen.consumers.utils.IEntityConverterFields;
import uk.gov.dstl.baleen.consumers.utils.mongo.AbstractSingleDocumentMongoConsumer;
import uk.gov.dstl.baleen.consumers.utils.mongo.LegacyMongoFields;
import uk.gov.dstl.baleen.core.utils.ConfigUtils;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.metadata.PublishedId;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.types.semantic.Temporal;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;







//Mongo imports
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * <b>Output processed CAS object into MongoDB</b>
 * <p>
 * All the relevant annotations (see expected inputs) are iterated over and
 * added to a BSON object, which is then pushed to Mongo. Where possible, values
 * are converted to the correct types before insertion into Mongo (e.g. floats
 * should be put into Mongo as a float). If more than one Source, DocType or
 * PublishedId is found, then only the first instance of each is used. If more
 * than one ProtectiveMarking is found then the highest classification, the set
 * of releasable countries that appear in all ProtectiveMarkings and the set of
 * all caveats will be used. If a document with the same uniqueId already
 * exists, the information is appended to that document.
 * <p>
 *
 * The output document format of the ORIGINAL Baleen (Baleen version 1) was:
 *
 * <pre>
 * {
 * 	content,
 * 	language,
 * 	uniqueId,
 * 	source: {
 * 		dateAccessed,
 * 		location
 * 	},
 * 	docType: {
 * 		value,
 * 		confidence,
 * 		annotator
 * 	},
 * 	publishedId,
 * 	protectiveMarking: {
 * 		classification,
 * 		caveats: [],
 * 		releasability: []
 * 	},
 * 	documentInfo: [
 * 		{
 * 			key,
 * 			value
 * 		}
 * 	],
 * 	entities: [
 * 		{
 * 			uniqueId,
 * 			value,
 * 			confidence,
 * 			annotator,
 * 			type,
 * 			begin,
 * 			end,
 * 			references: [
 * 				{
 * 				begin,
 * 				end,
 * 				value
 * 				}
 * 			],
 * 			...
 * 		}
 * 	],
 * 	events: [
 * 		{
 * 			uniqueId,
 * 			value,
 * 			confidence,
 * 			annotator,
 * 			description,
 * 			begin,
 * 			end,
 * 			location,
 * 			occurrence
 * 		}
 * 	],
 * 	relationships: [
 * 		{
 * 			uniqueId,
 * 			value,
 * 			confidence,
 * 			annotator,
 * 			type,
 * 			begin,
 * 			end,
 * 			source,
 * 			target
 * 		}
 * 	],
 * 	media: [
 * 		{
 * 			type: Image,
 * 			hash,
 * 			description,
 * 			mimeType,
 * 			savedLocation,
 * 			annotator,
 * 			begin,
 * 			end,
 * 			extractedText,
 * 			features: [
 * 				{
 * 					centre: [x, y],
 * 					coordinates: [[x, y], [x, y], ...],
 * 					type
 * 				}
 * 			]
 * 		}
 * 	]
 * }
 * </pre>
 *
 * This legacy consumer follows the same structure with the following exceptions:
 * - The entities.reference list will always be empty. The use of coreference changed
 * so significantly it made little sense to port the data over.
 * - All annotators will be labelled as this class (LegacyMongoConsumer) since this
 * has become history under Baleen 2.
 * - Baleen 2 does not currently support media extraction (so there will be no media field).
 * - docType confidence will always be 1.0
 *
 * Some types aren't supported by the consumer and these are ignored (with a
 * warning).
 *
 * 
 * @baleen.javadoc
 */
public class LegacyMongo extends AbstractSingleDocumentMongoConsumer {

	private static final String FIELD_RELATIONSHIP_TARGET = "target";
	private static final String FIELD_RELATIONSHIP_SOURCE = "source";
	private static final String FIELD_CONFIDENCE = "confidence";
	private static final String FIELD_ANNOTATOR = "annotator";
	private static final String FIELD_END = "end";
	private static final String FIELD_BEGIN = "begin";
	private static final String FIELD_KEY = "key";
	private static final String FIELD_VALUE = "value";

	// Old baleen had an annotator per entity. This is now part of the history, but as it was never used.
	private static final Object FAKE_ANNOTATOR = LegacyMongo.class.getCanonicalName();

	/**
	 * Should we output the document content to Mongo?
	 * 
	 * @baleen.config true
	 */
	public static final String PARAM_OUTPUT_CONTENT = "outputContent";
	@ConfigurationParameter(name = PARAM_OUTPUT_CONTENT, defaultValue = "true")
	boolean outputContent = false;

	/**
	 * Maximum number of characters from content to store in Mongo. Set to 0
	 * to store all content.
	 * 
	 * @baleen.config "0"
	 */
	public static final String PARAM_MAX_CONTENT_LENGTH = "maxContentLength";
	@ConfigurationParameter(name = PARAM_MAX_CONTENT_LENGTH, defaultValue = "0")
	String maxContentLengthString;
	//Parse the maxContentLengthString config parameter into this variable to avoid issues with parameter types
	int maxContentLength = 0;

	/**
	 * Holds the types of features that we're not interested in persisting (stuff from UIMA for example)
	 * We're storing these so that we can loop through the features (and then ignore some of them)
	 */
	private Set<String> stopFeatures;
	
	//Fields
	public static final String FIELD_METADATA = "documentInfo";
	public static final String FIELD_DOCUMENT_CLASSIFICATION = "classification";
	public static final String FIELD_DOCUMENT_CAVEATS = "caveats";
	public static final String FIELD_DOCUMENT_RELEASABILITY = "releasability";
	public static final String FIELD_ENTITIES = "entities";
	public static final String FIELD_DOCUMENT_TYPE = "docType";
	public static final String FIELD_DOCUMENT_SOURCE = "location";
	public static final String FIELD_DOCUMENT_LANGUAGE = "language";
	public static final String FIELD_DOCUMENT_TIMESTAMP = "dateAccessed";
	public static final String FIELD_PUBLISHEDIDS = "publishedId";
	public static final String FIELD_SOURCE = "source";
	
	private IEntityConverterFields fields = new LegacyMongoFields();
	/** New instance.
	 *
	 */
	public LegacyMongo() {
	}

	/**
	 * Get the MongoDB, collection and create some indexes
	 */
	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {

		super.doInitialize(aContext);

		// Create indexes
		getCollection().createIndex(new BasicDBObject(FIELD_PUBLISHEDIDS, 1));

		stopFeatures = new HashSet<String>();
		stopFeatures.add("uima.cas.AnnotationBase:sofa");
		stopFeatures.add("uk.gov.dstl.baleen.types.BaleenAnnotation:internalId");
		maxContentLength = ConfigUtils.stringToInteger(maxContentLengthString, 0);
	}

	@Override
	protected DBObject convert(JCas aJCas)  {

		BasicDBObject doc = new BasicDBObject();

		getMonitor().info("Beginning persistance of document to MongoDB");

		if (outputContent) {
			if (maxContentLength>0 && aJCas.getDocumentText().length()>maxContentLength) {
				getMonitor().debug("Adding (truncated) content to document object");
				doc.put("content", aJCas.getDocumentText().substring(0, maxContentLength-1)+"\u2026");
			} else {
				getMonitor().debug("Adding content to document object");
				doc.put("content", aJCas.getDocumentText());
			}
		}

		if (aJCas.getDocumentLanguage() != null && !aJCas.getDocumentLanguage().isEmpty()) {
			getMonitor().debug("Adding language '" + aJCas.getDocumentLanguage() + "' to document object");
			doc.put("language", aJCas.getDocumentLanguage());
		}

		// Add MetaData to object
		addDocumentMetadata(doc, aJCas);

		addPublishedId(doc, aJCas);

		addDocumentInfo(doc, aJCas);

		addEntities(doc, aJCas);

		addEvents(doc, aJCas);

		addRelations(doc, aJCas);

		return doc;
	}

	private String addDocumentInfo(BasicDBObject doc, JCas aJCas) {
		// Document Info
		getMonitor().debug("Adding DocumentInfo to document object");
		List<BasicDBObject> diObjs = new ArrayList<>();

		// Add the unique id
		doc.put(FIELD_UNIQUE_ID, getUniqueId(aJCas));

		FSIterator<Annotation> iter = aJCas.getAnnotationIndex(Metadata.type).iterator();
		String docUniqueId = null;
		while (iter.hasNext()) {
			Metadata di = (Metadata) iter.next();

			getMonitor().trace("Adding DocumentInfo (" + di.getKey() + ") to document object");

			BasicDBObject diObj = new BasicDBObject();
			diObj.put(FIELD_KEY, di.getKey());
			diObj.put(FIELD_VALUE, di.getValue());

			diObjs.add(diObj);

		}

		doc.put(FIELD_METADATA, diObjs);

		return docUniqueId;
	}

	private void addEvents(BasicDBObject doc, JCas aJCas) {
		// Add events to object
		getMonitor().debug("Adding events to document object");
		List<BasicDBObject> eventObjs = new ArrayList<>();

		FSIterator<Annotation> iter = aJCas.getAnnotationIndex(Event.type).iterator();
		while (iter.hasNext()) {
			Event e = (Event) iter.next();
			getMonitor().debug("Generating event object for event " + e.getExternalId());

			BasicDBObject eventObj = new BasicDBObject();
			eventObj.put(FIELD_UNIQUE_ID, e.getExternalId());
			eventObj.put(FIELD_VALUE, e.getValue());
			eventObj.put(FIELD_BEGIN, e.getBegin());
			eventObj.put(FIELD_END, e.getEnd());
			eventObj.put(FIELD_ANNOTATOR, FAKE_ANNOTATOR);
			eventObj.put(FIELD_CONFIDENCE, e.getConfidence());
			eventObj.put("description", e.getDescription());

			Location l = e.getLocation();
			if (l != null) {
				eventObj.put("location", l.getExternalId());
			}

			Temporal t = e.getOccurrence();
			if (t != null) {
				eventObj.put("occurence", t.getExternalId());
			}

			eventObjs.add(eventObj);
		}
		doc.put("events", eventObjs);
	}

	private void addRelations(BasicDBObject doc, JCas aJCas) {
		// Add relationships to object
		getMonitor().debug("Adding relationships to document object");
		List<BasicDBObject> relObjs = new ArrayList<>();

		FSIterator<Annotation> iter = aJCas.getAnnotationIndex(Relation.type).iterator();
		while (iter.hasNext()) {
			Relation r = (Relation) iter.next();
			getMonitor().trace("Generating relationship object for relationship " + r.getExternalId());

			BasicDBObject relObj = new BasicDBObject();
			relObj.put(FIELD_UNIQUE_ID, r.getExternalId());
			relObj.put("type", r.getRelationshipType());
			relObj.put(FIELD_BEGIN, r.getBegin());
			relObj.put(FIELD_END, r.getEnd());
			relObj.put(FIELD_ANNOTATOR, FAKE_ANNOTATOR);
			relObj.put(FIELD_CONFIDENCE, r.getConfidence());
			relObj.put(FIELD_VALUE, r.getValue());

			relObj.put(FIELD_RELATIONSHIP_SOURCE, r.getSource().getExternalId());
			relObj.put(FIELD_RELATIONSHIP_TARGET, r.getTarget().getExternalId());

			relObjs.add(relObj);
		}
		doc.put("relationships", relObjs);
	}

	private void addEntities(BasicDBObject doc, JCas aJCas) {
		getMonitor().debug("Adding entities to document object");
		List<Map<String,Object>> entities = new ArrayList<>();
		
		EntityRelationConverter convertor = new EntityRelationConverter(getMonitor(), false, null, stopFeatures, fields);
		
		FSIterator<Annotation> iter = aJCas.getAnnotationIndex(Entity.type).iterator();
		while (iter.hasNext()) {
			Entity bt = (Entity) iter.next();

			getMonitor().trace("Creating entity object of type " + bt.getClass().getName());

			Map<String, Object> map = convertor.convertEntity(bt);
			
			if(bt instanceof Location && map.containsKey(fields.getGeoJSON())){
				Map<String, Object> geoFeature = new HashMap<>();
				geoFeature.put("type","Feature");
				geoFeature.put("geometry", map.remove(fields.getGeoJSON()));
				map.put(fields.getGeoJSON(), geoFeature);
			}
			
			map.put("references", Collections.emptyList());
			entities.add(map);

		}
		doc.put(FIELD_ENTITIES, entities);
	}

	private void addProtectiveMarking(BasicDBObject doc, DocumentAnnotation documentAnnotation) {

		getMonitor().debug("Adding protective marking to document object");

		BasicDBObject pmObj = new BasicDBObject();
		pmObj.put(FIELD_DOCUMENT_CLASSIFICATION, documentAnnotation.getDocumentClassification());
		pmObj.put(FIELD_DOCUMENT_CAVEATS, UimaTypesUtils.toArray(documentAnnotation.getDocumentCaveats()));
		pmObj.put(FIELD_DOCUMENT_RELEASABILITY, UimaTypesUtils.toArray(documentAnnotation.getDocumentReleasability()));

		doc.put("protectiveMarking", pmObj);
	}

	private void addPublishedId(BasicDBObject doc, JCas aJCas) {
		// Published ID - If more than one present, only use first one
		FSIterator<Annotation> iter = aJCas.getAnnotationIndex(PublishedId.type).iterator();
		if (iter.hasNext()) {
			getMonitor().debug("Adding PublishedID to document object");
			PublishedId publId = (PublishedId) iter.next();
			doc.put(FIELD_PUBLISHEDIDS, publId.getValue());
		}

		if (iter.hasNext()) { // Warn if more than one PublishedID found
			getMonitor().warn(
					"More than one PublishedID annotation found. Only the first one has been outputted to Mongo.");
		}
	}

	private void addDocumentMetadata(BasicDBObject doc, JCas aJCas) {
		DocumentAnnotation documentAnnotation = getDocumentAnnotation(aJCas);

		// Source
		BasicDBObject srcObj = new BasicDBObject();
		srcObj.put(FIELD_DOCUMENT_TIMESTAMP, new Date(documentAnnotation.getTimestamp()));
		srcObj.put(FIELD_DOCUMENT_SOURCE, documentAnnotation.getSourceUri());
		doc.put(FIELD_SOURCE, srcObj);

		// Document type
		BasicDBObject dtObj = new BasicDBObject();
		dtObj.put(FIELD_VALUE, documentAnnotation.getDocType());

		// We fake these values
		dtObj.put(FIELD_CONFIDENCE, 1.0);
		dtObj.put(FIELD_ANNOTATOR, FAKE_ANNOTATOR);
		doc.put(FIELD_DOCUMENT_TYPE, dtObj);

		addProtectiveMarking(doc, documentAnnotation);
	}

}
