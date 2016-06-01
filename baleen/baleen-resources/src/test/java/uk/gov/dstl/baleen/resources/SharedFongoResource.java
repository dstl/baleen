//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.resources;

import java.util.List;
import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import com.github.fakemongo.Fongo;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.util.JSON;

/**
 * Fake Mongo (fongo) for unit testing.
 *
 * Match fongo.collection to the collection your annotator (other) is expecting. Provide fixture
 * data as fongo.data for example:
 *
 * <pre>
 * private static final List<DBObject> DATA = Lists.newArrayList(
 * 		new BasicDBObject("fake", "doc1"),
 * 		new BasicDBObject("fake", "doc2"),
 * 		new BasicDBObject("fake", "doc3"));
 *
 * ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription("mongo",
 * 		SharedFongoResource.class, SharedFongoResource.PARAM_FONGO_COLLECTION, "documents",
 * 		SharedFongoResource.PARAM_FONGO_DATA, JSON.serialize(DATA));
 *
 * </pre>
 */
public class SharedFongoResource extends SharedMongoResource {

	private static final String BALEEN = "baleen";

	private final Fongo fongo = new Fongo("baleen_test_server");

	public static final String PARAM_FONGO_COLLECTION = "fongo.collection";
	@ConfigurationParameter(name = PARAM_FONGO_COLLECTION, defaultValue = "baleen_test_collection")
	private String fongoCollection;

	public static final String PARAM_FONGO_DATA = "fongo.data";
	@ConfigurationParameter(name = PARAM_FONGO_DATA, defaultValue = "{}")
	private String fongoData;

	@SuppressWarnings("unchecked")
	@Override
	protected boolean doInitialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {
		// Work whether it's a list of DB Objects or a single
		if ("{}".equals(fongoData) || Strings.isNullOrEmpty(fongoData)) {
			return true;
		}

		Object obj = JSON.parse(fongoData);

		if (obj instanceof List<?>) {
			List<DBObject> data = (List<DBObject>) JSON.parse(fongoData);
			fongo.getDB(BALEEN).getCollection(fongoCollection).insert(data);

		} else if (obj instanceof DBObject) {
			DBObject data = (DBObject) JSON.parse(fongoData);
			fongo.getDB(BALEEN).getCollection(fongoCollection).insert(data);

		} else {
			getMonitor().error("Unsupported type");
			throw new ResourceInitializationException();
		}

		return true;
	}

	@Override
	protected MongoClient createMongoClient(ServerAddress sa, Optional<MongoCredential> credentials) {
		// Doesn't test credentials
		return fongo.getMongo();
	}

	@Override
	protected void doDestroy() {
		// Do nothing
	}

	@Override
	public DB getDB() {
		return fongo.getDB(BALEEN);
	}
}
