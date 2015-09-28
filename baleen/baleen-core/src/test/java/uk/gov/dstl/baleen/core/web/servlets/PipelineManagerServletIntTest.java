//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.web.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Optional;

import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.manager.BaleenManager;
import uk.gov.dstl.baleen.core.manager.BaleenManager.BaleenManagerListener;
import uk.gov.dstl.baleen.core.pipelines.PipelineTestHelper;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.testing.servlets.WebApiTestServer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A full server test for {@link PipelineManagerServlet}.
 *
 * 
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PipelineManagerServletIntTest {

	private static final String PIPELINE_NAME = "new";
	private static final String RUNNING = "running";
	private static final String PIPELINES = "/pipelines";
	private static final Logger LOGGER = LoggerFactory.getLogger(PipelineManagerServletIntTest.class);

	@Test
	public void testWithServer() throws BaleenException {
		BaleenManager manager = new BaleenManager(Optional.empty());
		try {
			manager.run(new BaleenManagerListener() {
	
				@Override
				public void onStarted(BaleenManager manager) {
	
					try {
						ObjectMapper mapper = new ObjectMapper();
	
						String yaml = PipelineTestHelper.getCpeYamlResourceAsString();
						
						// Create

	
						String created = WebApiTestServer.getBodyForPost(null, null, PIPELINES, new BasicNameValuePair(
								"name", PIPELINE_NAME), new BasicNameValuePair("yaml", yaml));
						LOGGER.info("Created {}", created);
						ObjectNode createdNode = (ObjectNode) mapper.readTree(created);
						assertEquals(PIPELINE_NAME, createdNode.get("name").asText());
						assertEquals(false, createdNode.get(RUNNING).asBoolean());
						
	
						String getByName = WebApiTestServer.getBodyForGet(null, null, PIPELINES+"?name="+PIPELINE_NAME);
						LOGGER.info("ByName {}", getByName);
						ObjectNode byNameNode = (ObjectNode) mapper.readTree(getByName).get(0);
						assertEquals(PIPELINE_NAME, byNameNode.get("name").asText());
						assertEquals(false, byNameNode.get(RUNNING).asBoolean());
						
						// Start
	
						String start = WebApiTestServer.getBodyForPost(null, null, PIPELINES+"/start?name="+PIPELINE_NAME);
						LOGGER.info("Start {}", start);
	
						// Wait for the pipeline to start
						Thread.sleep(1000);
	
						String getAll = WebApiTestServer.getBodyForGet(null, null, PIPELINES);
						LOGGER.info("All {}", getAll);
						ObjectNode allNode = (ObjectNode) mapper.readTree(getAll).get(0);
						assertEquals(true, allNode.get(RUNNING).asBoolean());
	
						String stop = WebApiTestServer.getBodyForPost(null, null,  PIPELINES+"/stop?name="+PIPELINE_NAME);
						LOGGER.info("Stop {}", stop);
						ObjectNode stopNode = (ObjectNode) mapper.readTree(stop).get(0);
						assertEquals(false, stopNode.get(RUNNING).asBoolean());
						
						
						String stopGetAll = WebApiTestServer.getBodyForGet(null, null, PIPELINES);
						LOGGER.info("Stop all {}", stopGetAll);
						ObjectNode stopAllNode = (ObjectNode) mapper.readTree(stopGetAll).get(0);
						assertEquals(false, stopAllNode.get(RUNNING).asBoolean());
						
						// Check a restart
	
						String restart = WebApiTestServer.getBodyForPost(null, null,  PIPELINES+"/start?name="+PIPELINE_NAME);
						LOGGER.info("Start {}", restart);
	
						// Wait for the pipeline to start
						Thread.sleep(1000);
	
						String restartGetAll = WebApiTestServer.getBodyForGet(null, null, PIPELINES);
						LOGGER.info("Restart all {}", restartGetAll);
						ObjectNode restartAllNode = (ObjectNode) mapper.readTree(restartGetAll).get(0);
						assertEquals(true, restartAllNode.get(RUNNING).asBoolean());
	
						String restartStop = WebApiTestServer.getBodyForPost(null, null,  PIPELINES+"/stop?name="+PIPELINE_NAME);
						LOGGER.info("Stop {}", restartStop);
						ObjectNode restartStopNode = (ObjectNode) mapper.readTree(stop).get(0);
						assertEquals(false, restartStopNode.get(RUNNING).asBoolean());
						
						// Delete
						
						String delete = WebApiTestServer.getBodyForDelete(null, null,  PIPELINES+"?name="+PIPELINE_NAME);
						LOGGER.info("Delete {}", delete);
	
						String getNone = WebApiTestServer.getBodyForGet(null, null, PIPELINES);
						LOGGER.info("None {}", getNone);
						assertEquals("[ ]", getNone);
	
					} catch (Exception e) {
						fail("Exception thrown: "+e.getMessage());
					}
				}
			});
		} finally {
			// In case of exception (or assertion!) make sure the server is stopped
			manager.stop();
			manager.shutdown();
		}
	}

}
