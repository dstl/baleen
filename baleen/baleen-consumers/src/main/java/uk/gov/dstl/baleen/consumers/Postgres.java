//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.elasticsearch.common.base.Strings;

import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.resources.SharedPostgresResource;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.uima.BaleenConsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * Consumer to persist Baleen outputs into Postgres.
 * 
 * Assumes that the provided schema has the following tables.
 * If these do not exist, then they are created when the consumer is first run.
 * If they exist, but have the wrong definition, then the consumer will be unable to insert into them.
 * A prefix, defaulting to 'baleen_', can be specified to avoid name conflicts.
 * 
 * <ul>
 * <li>docs</li>
 * <li>doc_metadata</li>
 * <li>entities</li>
 * <li>entity_geos</li>
 * </ul>
 * 
 * Requires PostGIS 2 or later.
 *  
 * 
 * @baleen.javadoc
 */
public class Postgres extends BaleenConsumer {
	/**
	 * Connection to PostgreSQL
	 * 
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedPostgresResource
	 */
	public static final String KEY_POSTGRES = "postgres";
	@ExternalResource(key = KEY_POSTGRES)
	private SharedPostgresResource postgresResource;
	
	/**
	 * The Postgres schema containing the tables
	 * 
	 * @baleen.config 
	 */
	public static final String PARAM_SCHEMA = "schema";
	@ConfigurationParameter(name = PARAM_SCHEMA, defaultValue = "")
	private String schema;
	
	/**
	 * The prefix to add to table names
	 * 
	 * @baleen.config baleen_
	 */
	public static final String PARAM_PREFIX = "prefix";
	@ConfigurationParameter(name = PARAM_PREFIX, defaultValue = "baleen_")
	private String prefix;
	
	/**
	 * Should a hash of the content be used to generate the ID?
	 * If false, then a hash of the Source URI is used instead.
	 * 
	 * @baleen.config true
	 */
	public static final String PARAM_CONTENT_HASH_AS_ID = "contentHashAsId";
	@ConfigurationParameter(name = PARAM_CONTENT_HASH_AS_ID, defaultValue = "true")
	private boolean contentHashAsId = true;
	
	private PreparedStatement insertDocStatement;
	private PreparedStatement insertDocMetadataStatement;
	private PreparedStatement insertEntityStatement;
	private PreparedStatement insertEntityGeoStatement;
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final MapLikeType MAP_LIKE_TYPE = TypeFactory
			.defaultInstance()
			.constructMapLikeType(Map.class, String.class, Object.class);
	
	private static final String DOC_ROOT = "docs";
	private static final String DOC_METADATA_ROOT = "doc_metadata";
	private static final String ENTITY_ROOT = "entities";
	private static final String ENTITY_GEO_ROOT = "entity_geos";
	
	private static final String VARCHAR = "varchar";
	private static final String CREATE_TABLE_PREFIX = "CREATE TABLE IF NOT EXISTS ";
	private static final String INSERT_INTO_PREFIX = "INSERT INTO ";
	
	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		checkVersion();
		createTables();
		
		try{
			insertDocStatement = postgresResource.getConnection().prepareStatement(INSERT_INTO_PREFIX + getTableName(DOC_ROOT) + " (externalId, type, source, content, language, processed, classification, caveats, releasability) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			insertDocMetadataStatement = postgresResource.getConnection().prepareStatement(INSERT_INTO_PREFIX + getTableName(DOC_METADATA_ROOT) + " (doc_key, name, value) VALUES (?, ?, ?)");
			insertEntityStatement = postgresResource.getConnection().prepareStatement(INSERT_INTO_PREFIX + getTableName(ENTITY_ROOT) + " (doc_key, externalId, type, value) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			insertEntityGeoStatement = postgresResource.getConnection().prepareStatement(INSERT_INTO_PREFIX + getTableName(ENTITY_GEO_ROOT) + " (entity_key, geo) VALUES (?, ST_GeomFromGeoJSON(?))");
		}catch(SQLException e){
			throw new ResourceInitializationException(e);
		}
	}
	
	/**
	 * Check that Postgres has at least version 2 of PostGIS installed
	 */
	private void checkVersion() throws ResourceInitializationException{
		try(
			Statement s = postgresResource.getConnection().createStatement();
		){
			ResultSet rs = s.executeQuery("SELECT PostGIS_Lib_Version() AS version");
			
			rs.next();
			String version = rs.getString("version");
			
			String[] versionParts = version.split("\\.");
			Integer majorVersion = Integer.parseInt(versionParts[0]);
			
			if(majorVersion < 2){
				throw new BaleenException("Unsupported PostGIS Version");
			}
				
		}catch(SQLException | NumberFormatException | NullPointerException e){
			getMonitor().error("Unable to retrieve PostGIS version");
			throw new ResourceInitializationException(e);
		}catch(BaleenException e){
			throw new ResourceInitializationException(e);
		}
	}
	
	/**
	 * If the tables don't already exist, then create them.
	 */
	private void createTables() throws ResourceInitializationException{
		try(
			Statement s = postgresResource.getConnection().createStatement();
		){
			s.execute(CREATE_TABLE_PREFIX + getTableName(DOC_ROOT) + " (key serial primary key, externalId character varying, type character varying, source character varying, content character varying, language character varying, processed timestamp, classification character varying, caveats character varying[], releasability character varying[])");
			s.execute(CREATE_TABLE_PREFIX + getTableName(DOC_METADATA_ROOT) + " (key serial primary key, doc_key integer references " + getTableName(DOC_ROOT) + "(key), name character varying, value character varying)");
			s.execute(CREATE_TABLE_PREFIX + getTableName(ENTITY_ROOT) + " (key serial primary key, doc_key integer references " + getTableName(DOC_ROOT) + "(key), externalId character varying[], type character varying, value character varying[])");
			s.execute(CREATE_TABLE_PREFIX + getTableName(ENTITY_GEO_ROOT) + " (key serial primary key, entity_key integer references " + getTableName(ENTITY_ROOT) + "(key), geo geometry(Geometry, 4326))");
			
			postgresResource.getConnection().setAutoCommit(false);
		}catch(SQLException e){
			throw new ResourceInitializationException(e);
		}
	}
	
	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		Connection conn = postgresResource.getConnection();
		
		try{
			//Insert document and metadata into database
			Integer docKey = executeDocInsert(jCas);
			for(Metadata md : JCasUtil.select(jCas, Metadata.class)){
				executeDocMetadataInsert(docKey, md);
			}
			
			processEntities(jCas, docKey);
			
			conn.commit();
		}catch(SQLException | BaleenException e){
			getMonitor().error("Unable to insert document into Postgres database", e);
			if(conn != null){
				try{
					conn.rollback();
				}catch(SQLException e2){
					getMonitor().error("Unable to rollback insertion - state of the database may have been left inconsistent", e2);
				}
			}
		}
	}

	private Integer executeDocInsert(JCas jCas) throws SQLException, BaleenException{
		DocumentAnnotation da = getDocumentAnnotation(jCas);
		String documentId = ConsumerUtils.getExternalId(da, contentHashAsId);
		
		insertDocStatement.clearParameters();
		insertDocStatement.setString(1, documentId);
		insertDocStatement.setString(2, da.getDocType());
		insertDocStatement.setString(3, da.getSourceUri());
		insertDocStatement.setString(4, jCas.getDocumentText());
		insertDocStatement.setString(5, jCas.getDocumentLanguage());
		insertDocStatement.setTimestamp(6, new Timestamp(da.getTimestamp()));
		insertDocStatement.setString(7, da.getDocumentClassification());
		insertDocStatement.setArray(8, createVarcharArray(postgresResource.getConnection(), da.getDocumentCaveats()));
		insertDocStatement.setArray(9, createVarcharArray(postgresResource.getConnection(), da.getDocumentReleasability()));
		insertDocStatement.executeUpdate();
		
		Integer docKey = getKey(insertDocStatement);
		if(docKey == null){
			throw new BaleenException("No document key returned");
		}
		
		return docKey;
	}
	
	private void executeDocMetadataInsert(Integer docKey, Metadata md) throws SQLException{
		insertDocMetadataStatement.clearParameters();
		insertDocMetadataStatement.setInt(1, docKey);
		insertDocMetadataStatement.setString(2, md.getKey());
		insertDocMetadataStatement.setString(3, md.getValue());
		insertDocMetadataStatement.executeUpdate();
	}
	
	private void processEntities(JCas jCas, Integer docKey) throws SQLException{
		//Insert entities
		Map<ReferenceTarget, List<Entity>> coreferenceEntities = new HashMap<>();
		
		for(Entity ent : JCasUtil.select(jCas, Entity.class)){
			ReferenceTarget rt = ent.getReferent();
			if(rt == null){
				rt = new ReferenceTarget(jCas);
			}
			List<Entity> entities = coreferenceEntities.getOrDefault(rt, new ArrayList<>());
			entities.add(ent);
			coreferenceEntities.put(rt, entities);
		}
		
		for(List<Entity> entities : coreferenceEntities.values()){
			processCoreferencedEntities(docKey, entities);
		}
	}
	
	private void processCoreferencedEntities(Integer docKey, List<Entity> entities) throws SQLException{
		Set<String> values = new HashSet<>();
		Set<String> externalIds = new HashSet<>();
		Set<String> geoJsons = new HashSet<>();
		
		Class<? extends Entity> type = null;
		
		for(Entity e : entities){
			values.add(e.getValue());
			externalIds.add(e.getExternalId());
			
			if(e instanceof Location){
				Location l = (Location) e;	
				try{
					geoJsons.add(addCrsToGeoJSON(l.getGeoJson()));
				}catch(BaleenException ex){
					getMonitor().warn("Unable to add CRS to GeoJSON", ex);
				}
			}
			
			type = getSuperclass(type, e.getClass());
		}
		
		if(type == null){
			//No entities processed
			return;
		}
		
		Integer entityKey = executeEntityInsert(docKey, values, externalIds, type.getName());
		if(entityKey != null){
			for(String geoJson : geoJsons){
				executeEntityGeoInsert(entityKey, geoJson);
			}
		}
	}
	
	private Integer executeEntityInsert(Integer docKey, Collection<String> values, Collection<String> externalIds, String type) throws SQLException{
		insertEntityStatement.clearParameters();
		insertEntityStatement.setInt(1, docKey);
		insertEntityStatement.setArray(2, postgresResource.getConnection().createArrayOf(VARCHAR, externalIds.toArray(new String[0])));
		insertEntityStatement.setString(3, type);
		insertEntityStatement.setArray(4, postgresResource.getConnection().createArrayOf(VARCHAR, values.toArray(new String[0])));
		insertEntityStatement.executeUpdate();
		
		Integer entityKey = getKey(insertEntityStatement);
		if(entityKey == null){
			getMonitor().error("No entity key returned - Geo insertion, if applicable, will be skipped");
		}
		
		return entityKey;
	}
	
	private void executeEntityGeoInsert(Integer entityKey, String geoJson) throws SQLException{
		insertEntityGeoStatement.clearParameters();
		insertEntityGeoStatement.setInt(1, entityKey);
		insertEntityGeoStatement.setString(2, geoJson);
		insertEntityGeoStatement.executeUpdate();
	}
	
	/**
	 * Add CRS (assumed to be EPSG:4326) to a GeoJSON string if it doesn't already exist
	 */
	public static String addCrsToGeoJSON(String geoJson) throws BaleenException{
		if(Strings.isNullOrEmpty(geoJson)){
			return geoJson;
		}
		
		try{
			Map<String, Object> geoJsonObj = MAPPER.readValue(geoJson, MAP_LIKE_TYPE);
			if(geoJsonObj == null){
				throw new InvalidParameterException("Mapper returned null");
			}
			if(geoJsonObj.get("crs") == null){
				Map<String, Object> crs = new HashMap<>();
				crs.put("type", "name");
				
				Map<String, Object> srid = new HashMap<>();
				srid.put("name", "EPSG:4326");
				crs.put("properties", srid);
				
				geoJsonObj.put("crs", crs);
				
				return MAPPER.writeValueAsString(geoJsonObj);
			}else{
				return geoJson;
			}
		}catch(Exception e){
			throw new BaleenException("Unable to parse GeoJSON", e);
		}
	}
	
	/**
	 * From the two classes, return the one that is the superclass.
	 * If the two classes aren't in the same hierarchy, then c1 will be returned.
	 */
	public static Class<? extends Entity> getSuperclass(Class<? extends Entity> c1, Class<? extends Entity> c2){
		if(c1 == null)
			return c2;
		if(c2 == null)
			return c1;
		
		if(c2.isAssignableFrom(c1)){
			return c2;
		}
		
		return c1;
	}
	
	/**
	 * Get the table name with the schema and prefix if one is set
	 */
	public String getTableName(String table){
		String ret = table;
		
		if(!Strings.isNullOrEmpty(prefix)){
			ret =  prefix + ret;
		}
		
		if(!Strings.isNullOrEmpty(schema)){
			ret = schema + "." + ret;
		}
		
		return ret;
	}
	
	private Array createVarcharArray(Connection conn, StringArray s) throws SQLException{
		if(s == null){
			return conn.createArrayOf(VARCHAR, new String[]{});
		}else{
			return conn.createArrayOf(VARCHAR, s.toArray());
		}
	}
	
	private Integer getKey(Statement s) throws SQLException{
		ResultSet generatedKeys = s.getGeneratedKeys();
		if(generatedKeys.next()){
			return generatedKeys.getInt(1);
		}else{
			return null;
		}
	}
}
