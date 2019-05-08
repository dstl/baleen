// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis;

import static org.junit.Assert.assertEquals;
import static uk.gov.dstl.baleen.resources.SharedElasticsearchResource.PARAM_CLUSTER;
import static uk.gov.dstl.baleen.resources.SharedElasticsearchResource.PARAM_PORT;

import java.io.IOException;
import java.util.Collections;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.consumers.analysis.convertors.AnalysisMockData;
import uk.gov.dstl.baleen.resources.EmbeddedElasticsearch5;
import uk.gov.dstl.baleen.resources.SharedElasticsearchResource;
import uk.gov.dstl.baleen.resources.SharedIdGenerator;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;

public class ElasticsearchTest {

  private static final String RESOURCE_KEY = "elasticsearch";

  private EmbeddedElasticsearch5 elasticsearch;
  private AnalysisEngine ae;

  @Before
  public void before() throws Exception {

    elasticsearch = new EmbeddedElasticsearch5();

    final ExternalResourceDescription erd =
        ExternalResourceFactory.createNamedResourceDescription(
            RESOURCE_KEY,
            SharedElasticsearchResource.class,
            PARAM_CLUSTER,
            elasticsearch.getClusterName(),
            PARAM_PORT,
            Integer.toString(elasticsearch.getTransportPort()));
    final ExternalResourceDescription idErd =
        ExternalResourceFactory.createNamedResourceDescription(
            SharedIdGenerator.RESOURCE_KEY, SharedIdGenerator.class);

    final AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            Elasticsearch.class,
            TypeSystemSingleton.getTypeSystemDescriptionInstance(),
            RESOURCE_KEY,
            erd,
            SharedIdGenerator.RESOURCE_KEY,
            idErd);

    ae = AnalysisEngineFactory.createEngine(aed);
    ae.initialize(new CustomResourceSpecifier_impl(), Collections.emptyMap());

    assertEquals(0, countTypeInIndex(Elasticsearch.DEFAULT_DOCUMENT_TYPE));
    assertEquals(0, countTypeInIndex(Elasticsearch.DEFAULT_ENTITY_TYPE));
    assertEquals(0, countTypeInIndex(Elasticsearch.DEFAULT_RELATION_TYPE));
    assertEquals(0, countTypeInIndex(Elasticsearch.DEFAULT_MENTION_TYPE));
  }

  private long countTypeInIndex(final String type) {
    return elasticsearch
        .client()
        .prepareSearch(Elasticsearch.DEFAULT_DOCUMENT_INDEX)
        .setQuery(QueryBuilders.matchAllQuery())
        .setTypes(type)
        .execute()
        .actionGet()
        .getHits()
        .getTotalHits();
  }

  @After
  public void afterTest() {
    if (ae != null) {
      ae.destroy();
    }

    try {
      elasticsearch.close();
    } catch (final IOException ioe) {
      // Do nothing
    }
    elasticsearch = null;
  }

  @Test
  public void test() throws AnalysisEngineProcessException {
    final AnalysisMockData data = new AnalysisMockData();

    ae.process(data.getJCas());

    elasticsearch.flush(Elasticsearch.DEFAULT_DOCUMENT_INDEX);

    assertEquals(1, countTypeInIndex(Elasticsearch.DEFAULT_DOCUMENT_TYPE));
    assertEquals(4, countTypeInIndex(Elasticsearch.DEFAULT_ENTITY_TYPE));
    assertEquals(2, countTypeInIndex(Elasticsearch.DEFAULT_RELATION_TYPE));
    assertEquals(5, countTypeInIndex(Elasticsearch.DEFAULT_MENTION_TYPE));
  }
}
