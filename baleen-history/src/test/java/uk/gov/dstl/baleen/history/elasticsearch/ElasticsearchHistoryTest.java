//Dstl (c) Crown Copyright 2017
//NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.history.elasticsearch;

import com.google.common.collect.Maps;
import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.apache.uima.resource.impl.Parameter_impl;
import org.elasticsearch.node.NodeValidationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dstl.baleen.history.helpers.AbstractHistoryTest;
import uk.gov.dstl.baleen.resources.EmbeddedElasticsearch5;
import uk.gov.dstl.baleen.resources.SharedElasticsearchResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ElasticsearchHistoryTest extends AbstractHistoryTest {

	private Path tmpDir;
	private EmbeddedElasticsearch5 es5;

	protected SharedElasticsearchResource es;
	protected ElasticsearchHistory history;

	private static final String CLUSTER = "test-cluster";

	@Before
	public void setUp() throws ResourceInitializationException {
		try{
			tmpDir = Files.createTempDirectory("elasticsearch");
		}catch(IOException ioe){
			throw new ResourceInitializationException(ioe);
		}

		try {
			es5 = new EmbeddedElasticsearch5(tmpDir.toString(), CLUSTER);
		}catch (NodeValidationException nve){
			throw new ResourceInitializationException(nve);
		}

		es = new SharedElasticsearchResource();
		CustomResourceSpecifier_impl esSpecifier = new CustomResourceSpecifier_impl();
		esSpecifier.setParameters(new Parameter[] {  new Parameter_impl("elasticsearch.cluster", CLUSTER) });
		Map<String, Object> config = Maps.newHashMap();
		es.initialize(esSpecifier, config);

		history = new ElasticsearchHistory(es);
		history.initialize(new CustomResourceSpecifier_impl(), Maps.newHashMap());
	}

	@After
	public void tearDown() {
		history.destroy();

		try {
			es5.close();
		}catch(IOException ioe){
			//Do nothing
		}
		es5 = null;
	}

	@Test
	public void test() {
		testGenericHistory(history);
	}

}
