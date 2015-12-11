//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.resources;

import java.util.Arrays;
import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import uk.gov.dstl.baleen.core.utils.ConfigUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.uima.BaleenResource;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * <b>Shared resource for accessing Mongo</b>
 * <p>
 * This resource removes the need for individual annotators to establish their
 * own connections to Mongo, instead providing a single MongoClient instance for
 * Baleen that can be used. This provides benefits such as reduced
 * configuration, reduced repeated code, and support for connection pooling.</p>
 *
 * 
 * @baleen.javadoc
 */
public class SharedMongoResource extends BaleenResource {
	private MongoClient m;
	private DB db;

	/**
	 * The Mongo host to connect to
	 * 
	 * @baleen.config localhost
	 */
	public static final String PARAM_HOST = "mongo.host";
	@ConfigurationParameter(name = PARAM_HOST, defaultValue="localhost")
	private String mongoHost;

	/**
	 * The port to connect to Mongo on
	 * 
	 * @baleen.config 27017
	 */
	public static final String PARAM_PORT = "mongo.port";
	@ConfigurationParameter(name = PARAM_PORT, defaultValue="27017")
	private String mongoPortString;
	
	//Parse the port config parameter into this variable to avoid issues with parameter types
	private int mongoPort;

	/**
	 * The Mongo database to connect to
	 * 
	 * @baleen.config baleen
	 */
	public static final String PARAM_DB = "mongo.db";
	@ConfigurationParameter(name = PARAM_DB, defaultValue="baleen")
	private String mongoDb;

	/**
	 * The username to use for authentication.
	 * If left blank, then authentication will not be used.
	 * 
	 * @baleen.config
	 */
	public static final String PARAM_USER = "mongo.user";
	@ConfigurationParameter(name = PARAM_USER, defaultValue="")
	private String mongoUser;

	/**
	 * The password to use for authentication.
	 * If left blank, then authentication will not be used.
	 * 
	 * @baleen.config
	 */
	public static final String PARAM_PASS = "mongo.pass";
	@ConfigurationParameter(name = PARAM_PASS, defaultValue="")
	private String mongoPass;

	@Override
	protected boolean doInitialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams) throws ResourceInitializationException {
		mongoPort = ConfigUtils.stringToInteger(mongoPortString, 27017);
		
		try{
			connectToMongo(mongoHost, mongoPort, mongoDb, mongoUser, mongoPass);
		}catch(BaleenException be){
			throw new ResourceInitializationException(be);
		}

		getMonitor().info("Initialised shared Mongo resource");
		return true;
	}

	private void connectToMongo(String host, int port, String database, String username, String password) throws BaleenException{
		try {
			// Connect to Mongo
			ServerAddress sa = new ServerAddress(host, port);
			Optional<MongoCredential> cred = createCredentials(username, password, database);

			m = createMongoClient(sa, cred);

			getMonitor().debug("Getting Mongo Database '{}'", db);
			db = m.getDB(database);
		} catch (Exception e) {
			throw new BaleenException("Unable to connect to Mongo", e);
		}
	}

	protected MongoClient createMongoClient(ServerAddress sa, Optional<MongoCredential> credentials){
		if (!credentials.isPresent()) {
			getMonitor().debug("Connecting to Mongo without authentication");
			return new MongoClient(sa);
		} else {
			getMonitor().debug("Connecting to Mongo with authentication as user '{}'", credentials.get().getUserName());
			return new MongoClient(sa, Arrays.asList(credentials.get()));
		}
	}

	/**
	 * Creates a MongoCredential if a username, password and database are supplied, or returns absent() otherwise
	 *
	 * @param username The username to connect to Mongo with
	 * @param password The password to connect to Mongo with
	 * @param database The database to connect to
	 */
	public static Optional<MongoCredential> createCredentials(String username, String password, String database){
		if (!Strings.isNullOrEmpty(username) && !Strings.isNullOrEmpty(password) && !Strings.isNullOrEmpty(database)) {
			return Optional.of(MongoCredential.createMongoCRCredential(username, database, password.toCharArray()));
		}else{
			return Optional.absent();
		}
	}

	@Override
	protected void doDestroy() {
		getMonitor().debug("Disconnecting from Mongo");
		m.close();

		db = null;
		m = null;
	}

	/**
	 * Get the Mongo DB accessor.
	 *
	 * @return the monogo db, or null if the source has been destroyed or an
	 *         initialisation error occurred.
	 */
	public DB getDB() {
		return db;
	}
	
	/**
	 * Creates a Mongo URI string, without the username:password
	 * 
	 * @return
	 */
	public String getMongoURI(){
		return "mongodb://" + mongoHost + ":" + mongoPort + "/" + mongoDb;
	}
}
