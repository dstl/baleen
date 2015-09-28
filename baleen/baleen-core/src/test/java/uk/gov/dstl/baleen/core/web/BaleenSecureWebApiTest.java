//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.web;

import static org.mockito.Mockito.doReturn;
import static uk.gov.dstl.baleen.testing.servlets.WebApiTestServer.assertForGet;
import static uk.gov.dstl.baleen.testing.servlets.WebApiTestServer.assertForPost;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import uk.gov.dstl.baleen.core.manager.BaleenManager;
import uk.gov.dstl.baleen.core.pipelines.BaleenPipelineManager;
import uk.gov.dstl.baleen.core.utils.YamlConfiguration;

/**
 * Tests for {@link BaleenWebApi} which apply authentication to the system.
 *
 * 
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class BaleenSecureWebApiTest {

	private static final String PIPELINE_PASS = "p";

	private static final String STATS_PASS = "s";

	private static final String GUEST_PASS = "g";

	private static final String PIPELINES = "/pipelines";

	private static final String METRICS = "/metrics";

	private static final String STATUS = "/status";

	private static final String PIPELINE = "pipeline";

	private static final String STATS = "stats";

	private static final String GUEST = "guest";

	@Mock
	BaleenManager baleenManager;

	BaleenPipelineManager pipelineManager = new BaleenPipelineManager();

	@Before
	public void setUp() {
		doReturn(pipelineManager).when(baleenManager).getPipelineManager();
	}

	@Test
	public void runAsUsers() throws Exception {
		BaleenWebApi web = new BaleenWebApi(baleenManager);
		try {

			YamlConfiguration yamlConfiguration = YamlConfiguration.readFromResource(BaleenSecureWebApiTest.class,
					"secure.yaml");

			web.configure(yamlConfiguration);
			web.start();

			// Wait for the server to be up
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// Do nothing
			}

			assertForGet(null, null, HttpStatus.OK_200, STATUS);
			assertForGet(null, null, HttpStatus.UNAUTHORIZED_401, METRICS);
			assertForGet(null, null, HttpStatus.UNAUTHORIZED_401, PIPELINES);

			assertForGet(GUEST, GUEST_PASS, HttpStatus.OK_200, STATUS);
			assertForGet(GUEST, GUEST_PASS, HttpStatus.FORBIDDEN_403, METRICS);
			assertForGet(GUEST, GUEST_PASS, HttpStatus.FORBIDDEN_403, PIPELINES);

			assertForGet(STATS, STATS_PASS, HttpStatus.OK_200, STATUS);
			assertForGet(STATS, STATS_PASS, HttpStatus.OK_200, METRICS);
			assertForGet(STATS, STATS_PASS, HttpStatus.FORBIDDEN_403, PIPELINES);

			assertForGet(PIPELINE, PIPELINE_PASS, HttpStatus.OK_200, PIPELINES);
			assertForPost(PIPELINE, PIPELINE_PASS, HttpStatus.FORBIDDEN_403, PIPELINES);

		} finally {
			web.stop();
		}
	}

}
