//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.resources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.core.utils.ConfigUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.uima.BaleenResource;

/**
 * <b>Shared resource for accessing PostgreSQL</b>
 * <p>
 * This resource removes the need for individual annotators to establish their
 * own connections to PostgreSQL, instead providing a single JDBC Connection for
 * Baleen that can be used. This provides benefits such as reduced
 * configuration, reduced repeated code, and support for connection pooling.</p>
 *
 * 
 * @baleen.javadoc
 */
public class SharedPostgresResource extends BaleenResource {
	/**
	 * The host to connect to PostgreSQL on
	 * 
	 * @baleen.config localhost
	 */
	public static final String PARAM_HOST = "postgres.host";
	@ConfigurationParameter(name = PARAM_HOST, defaultValue="localhost")
	private String postgresqlHost;

	/**
	 * The port to connect to PostgreSQL on
	 * 
	 * @baleen.config 5432
	 */
	public static final String PARAM_PORT = "postgres.port";
	@ConfigurationParameter(name = PARAM_PORT, defaultValue="5432")
	private String postgresqlPortString;
	
	//Parse the port config parameter into this variable to avoid issues with parameter types
	private int postgresqlPort;

	/**
	 * The PostgreSQL database to connect to
	 * 
	 * @baleen.config baleen
	 */
	public static final String PARAM_DB = "postgres.db";
	@ConfigurationParameter(name = PARAM_DB, defaultValue="baleen")
	private String postgresqlDb;

	/**
	 * The username to use for authentication.
	 * If left blank, then authentication will not be used.
	 * 
	 * @baleen.config
	 */
	public static final String PARAM_USER = "postgres.user";
	@ConfigurationParameter(name = PARAM_USER, defaultValue="")
	private String postgresqlUser;

	/**
	 * The password to use for authentication.
	 * If left blank, then authentication will not be used.
	 * 
	 * @baleen.config
	 */
	public static final String PARAM_PASS = "postgres.pass";
	@ConfigurationParameter(name = PARAM_PASS, defaultValue="")
	private String postgresqlPass;
	
	
	private Connection connection = null;
	
	@Override
	protected boolean doInitialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams) throws ResourceInitializationException {
		postgresqlPort = ConfigUtils.stringToInteger(postgresqlPortString, 5432);
		
		try{
			Class.forName("org.postgresql.Driver");
		}catch(ClassNotFoundException e){
			getMonitor().error("Couldn't find PostgreSQL JDBC Driver", e);
			throw new ResourceInitializationException(e);
		}
		
		try{
			if(!Strings.isNullOrEmpty(postgresqlUser) && !Strings.isNullOrEmpty(postgresqlPass)){
				connection = DriverManager.getConnection(getJdbcString(), postgresqlUser, postgresqlPass);
			}else{
				connection = DriverManager.getConnection(getJdbcString());
			}
			
			if(connection == null){
				throw new BaleenException("Couldn't establish PostgreSQL connection");
			}
		}catch(SQLException | BaleenException e){
			getMonitor().error("Couldn't establish PostgreSQL connection", e);
			throw new ResourceInitializationException(e);
		}
		
		return true;
	}
	
	@Override
	protected void doDestroy() {
		if(connection != null){
			try {
				connection.close();
			} catch (SQLException e) {
				getMonitor().debug("Failed to close PostgreSQL Connection", e);
				// Do nothing
			}
			
			connection = null;
		}
	}
	
	/**
	 * Get the JDBC String for this connection
	 */
	public String getJdbcString(){
		return "jdbc:postgresql://" + postgresqlHost + ":" + postgresqlPort + "/" + postgresqlDb;
	}
	
	/**
	 * Get the JDBC connection
	 */
	public Connection getConnection(){
		return connection;
	}
}
