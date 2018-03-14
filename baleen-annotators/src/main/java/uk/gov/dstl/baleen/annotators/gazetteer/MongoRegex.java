// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.gazetteer;

import java.util.Collections;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.gazetteer.helpers.AbstractRegexAhoCorasickAnnotator;
import uk.gov.dstl.baleen.annotators.gazetteer.helpers.GazetteerUtils;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.resources.gazetteer.IGazetteer;
import uk.gov.dstl.baleen.resources.gazetteer.MongoGazetteer;

/**
 * Generic Mongo-backed Regex-RadixTree Gazetteer annotator, that will use a Mongo gazetteer to find
 * and annotate entities.
 *
 * @baleen.javadoc
 */
public class MongoRegex extends AbstractRegexAhoCorasickAnnotator {
  /**
   * Connection to Mongo
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedMongoResource
   */
  public static final String KEY_MONGO = "mongo";

  @ExternalResource(key = KEY_MONGO)
  private SharedMongoResource mongo;

  /**
   * The name of the Mongo collection containing the gazetteer
   *
   * @baleen.config gazetteer
   */
  public static final String PARAM_COLLECTION = "collection";

  @ConfigurationParameter(name = PARAM_COLLECTION, defaultValue = "gazetteer")
  private String collection;

  /**
   * The name of the field in Mongo that contains the gazetteer values
   *
   * @baleen.config value
   */
  public static final String PARAM_VALUE_FIELD = "valueField";

  @ConfigurationParameter(name = PARAM_VALUE_FIELD, defaultValue = "value")
  private String valueField;

  /** Constructor */
  public MongoRegex() {
    // Do nothing
  }

  @Override
  public IGazetteer configureGazetteer() throws BaleenException {
    IGazetteer gaz = new MongoGazetteer();
    gaz.init(mongo, GazetteerUtils.configureMongo(caseSensitive, collection, valueField));

    return gaz;
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(entityType));
  }
}
