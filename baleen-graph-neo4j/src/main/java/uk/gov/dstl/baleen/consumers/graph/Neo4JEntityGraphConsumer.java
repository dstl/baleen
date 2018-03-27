// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.graph;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

import com.steelbridgelabs.oss.neo4j.structure.Neo4JElementIdProvider;
import com.steelbridgelabs.oss.neo4j.structure.Neo4JGraph;
import com.steelbridgelabs.oss.neo4j.structure.providers.DatabaseSequenceElementIdProvider;

import uk.gov.dstl.baleen.consumers.AbstractMigratingEntityGraphConsumer;
import uk.gov.dstl.baleen.graph.EntityGraphFactory;
import uk.gov.dstl.baleen.graph.EntityGraphOptions;

/**
 * Insert each entity graph into a Neo4J graph.
 *
 * <p>A graph is created for each document and sent to the graph database.
 *
 * <p>
 *
 * @see EntityGraphFactory
 * @baleen.javadoc
 */
public class Neo4JEntityGraphConsumer extends AbstractMigratingEntityGraphConsumer {

  /**
   * Bolt url for neo4j
   *
   * @baleen.config bolt://localhost
   */
  public static final String PARAM_NEO4J_URL = "url";

  @ConfigurationParameter(name = PARAM_NEO4J_URL, defaultValue = "bolt://localhost")
  private String neo4jUrl;

  /**
   * Username neo4j
   *
   * @baleen.config bolt://localhost
   */
  public static final String PARAM_NEO4J_USERNAME = "username";

  @ConfigurationParameter(name = PARAM_NEO4J_USERNAME, defaultValue = "neo4j")
  private String neo4jUsername;

  /**
   * Neo4j password
   *
   * @baleen.config neo4j
   */
  @SuppressWarnings("squid:S2068" /* Not a password */)
  public static final String PARAM_NEO4J_PASSWORD = "password";

  @ConfigurationParameter(name = PARAM_NEO4J_PASSWORD, defaultValue = "neo4j")
  private String neo4jPassword;

  private Driver driver;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    defaultValueStrategyType = "Json";
    super.doInitialize(aContext);
  }

  @Override
  protected Graph createGraph() {
    driver = GraphDatabase.driver(neo4jUrl, AuthTokens.basic(neo4jUsername, neo4jPassword));
    Neo4JElementIdProvider<?> vertexIdProvider = new DatabaseSequenceElementIdProvider(driver);
    Neo4JElementIdProvider<?> edgeIdProvider = new DatabaseSequenceElementIdProvider(driver);

    // create graph instance
    return new Neo4JGraph(driver, vertexIdProvider, edgeIdProvider);
  }

  @Override
  protected void addOptions(EntityGraphOptions.Builder builder) {
    builder.withValueCoercer(new Neo4JValueCoercer());
  }

  @Override
  protected void doDestroy() {
    try {
      driver.close();
    } catch (Exception e) {
      getMonitor().warn("Error closing graph " + createGraph().configuration(), e);
    }
    super.doDestroy();
  }
}
