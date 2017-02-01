//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.resources;

import java.util.Arrays;
import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.bson.BsonArray;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.codecs.BsonArrayCodec;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.json.JsonReader;

import com.github.fakemongo.Fongo;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

/**
 * Fake Mongo (fongo) for unit testing.
 *
 * Match fongo.collection to the collection your annotator (other) is expecting. Provide fixture
 * data as fongo.data for example:
 *
 * <pre>
 * private static final List<Document> DATA = Lists.newArrayList(
 * 		new Document("fake", "doc1"),
 * 		new Document("fake", "doc2"),
 * 		new Document("fake", "doc3"));
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

	@Override
	protected boolean doInitialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {
		// Work whether it's a list of DB Objects or a single
		if ("{}".equals(fongoData) || "[]".equals(fongoData) || Strings.isNullOrEmpty(fongoData)) {
			return true;
		}

		if (fongoData.trim().startsWith("[")) {
			CodecRegistry codecRegistry = CodecRegistries.fromProviders(Arrays.asList(new ValueCodecProvider(), new BsonValueCodecProvider(), new DocumentCodecProvider()));
			JsonReader reader = new JsonReader(fongoData);
			BsonArrayCodec arrayReader = new BsonArrayCodec(codecRegistry);
			
			BsonArray docArray = arrayReader.decode(reader, DecoderContext.builder().build());
			
			for(BsonValue doc : docArray.getValues()){
				fongo.getDatabase(BALEEN).getCollection(fongoCollection).insertOne(Document.parse(doc.asDocument().toJson()));
			}		
		} else if (fongoData.trim().startsWith("{")) {
			Document data = Document.parse(fongoData);
			fongo.getDatabase(BALEEN).getCollection(fongoCollection).insertOne(data);
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
	public MongoDatabase getDB() {
		return fongo.getDatabase(BALEEN);
	}
}
