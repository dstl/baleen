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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.gov.dstl.baleen.core.jobs.JobTestHelper;
import uk.gov.dstl.baleen.core.manager.BaleenManager;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.testing.servlets.WebApiTestServer;

/**
 * A full server test for {@link JobManagerServlet}.
 *
 *
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class JobManagerServletIntTest {

	private static final String PIPELINE_NAME = "new";
	private static final String RUNNING = "running";
	private static final String URL = "/jobs";
	private static final Logger LOGGER = LoggerFactory.getLogger(JobManagerServletIntTest.class);

	@Test
	public void testWithServer() throws BaleenException {
		BaleenManager manager = new BaleenManager(Optional.empty());
		try {
			manager.run(manager1 -> {

				try {
					ObjectMapper mapper = new ObjectMapper();

					String yaml = JobTestHelper.getCpeYamlResourceAsString();

					// Create

					String created = WebApiTestServer.getBodyForPost(null, null, URL, new BasicNameValuePair(
							"name", PIPELINE_NAME), new BasicNameValuePair("yaml", yaml));
					LOGGER.info("Created {}", created);
					ObjectNode createdNode = (ObjectNode) mapper.readTree(created);
					assertEquals(PIPELINE_NAME, createdNode.get("name").asText());
					assertEquals(false, createdNode.get(RUNNING).asBoolean());

					String getByName = WebApiTestServer.getBodyForGet(null, null, URL + "?name=" + PIPELINE_NAME);
					LOGGER.info("ByName {}", getByName);
					ObjectNode byNameNode = (ObjectNode) mapper.readTree(getByName).get(0);
					assertEquals(PIPELINE_NAME, byNameNode.get("name").asText());
					assertEquals(false, byNameNode.get(RUNNING).asBoolean());

					// Start

					String start = WebApiTestServer.getBodyForPost(null, null,
							URL + "/start?name=" + PIPELINE_NAME);
					LOGGER.info("Start {}", start);

					// Wait for the pipeline to start
					Thread.sleep(1000);

					// Ideally we'd check its running, but in practise it'll already have stopped

					String stop = WebApiTestServer.getBodyForPost(null, null,
							URL + "/stop?name=" + PIPELINE_NAME);
					LOGGER.info("Stop {}", stop);
					ObjectNode stopNode = (ObjectNode) mapper.readTree(stop).get(0);
					assertEquals(false, stopNode.get(RUNNING).asBoolean());

					String stopGetAll = WebApiTestServer.getBodyForGet(null, null, URL);
					LOGGER.info("Stop all {}", stopGetAll);
					ObjectNode stopAllNode = (ObjectNode) mapper.readTree(stopGetAll).get(0);
					assertEquals(false, stopAllNode.get(RUNNING).asBoolean());

					// Delete

					String delete = WebApiTestServer.getBodyForDelete(null, null, URL + "?name=" + PIPELINE_NAME);
					LOGGER.info("Delete {}", delete);

					String getNone = WebApiTestServer.getBodyForGet(null, null, URL);
					LOGGER.info("None {}", getNone);
					assertEquals("[ ]", getNone);

				} catch (Exception e) {
					fail("Exception thrown: " + e.getMessage());
				}
			});
		} finally {
			// In case of exception (or assertion!) make sure the server is stopped
			manager.stop();
			manager.shutdown();
		}
	}

}
