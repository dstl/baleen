//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.history.elasticsearch;

import java.util.Map;

import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.apache.uima.resource.impl.Parameter_impl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.history.helpers.AbstractHistoryTest;
import uk.gov.dstl.baleen.resources.SharedElasticsearchResource;
import uk.gov.dstl.baleen.resources.SharedLocalElasticsearchResource;

import com.google.common.collect.Maps;

public class ElasticsearchHistoryTest extends AbstractHistoryTest {

	protected SharedElasticsearchResource es;
	protected ElasticsearchHistory history;

	@Before
	public void setUp() throws ResourceInitializationException {
		es = new SharedLocalElasticsearchResource();
		CustomResourceSpecifier_impl esSpecifier = new CustomResourceSpecifier_impl();
		esSpecifier.setParameters(new Parameter[] {  new Parameter_impl("elasticsearch.cluster", "baleen-testing") });
		Map<String, Object> config = Maps.newHashMap();
		es.initialize(esSpecifier, config);

		history = new ElasticsearchHistory(es);
		history.initialize(new CustomResourceSpecifier_impl(), Maps.newHashMap());
	}

	@After
	public void tearDown() {
		history.destroy();
	}

	@Test
	public void test() {
		testGenericHistory(history);
	}

}
