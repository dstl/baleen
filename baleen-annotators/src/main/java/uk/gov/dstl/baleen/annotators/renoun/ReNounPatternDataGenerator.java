// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.renoun;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import uk.gov.dstl.baleen.resources.data.ReNounFact;

/**
 * An annotator for the generation of ReNoun dependency patterns based on known facts.
 *
 * <p>The facts, provided by a mongo collection, can be manually generated or from the {@link
 * ReNounDefaultSeedsRelationshipAnnotator}.
 *
 * <p>This annotator looks for the fact elements, the source, target and attribute, and, if found,
 * assumes the sentence is a statement of the fact and extract the minimal dependency tree
 * containing those facts. These are stored to another mongo collection.
 *
 * @baleen.javadoc
 * @see http://emnlp2014.org/papers/pdf/EMNLP2014038.pdf
 */
public class ReNounPatternDataGenerator extends AbstractPatternDataGenerator {

  /**
   * The name of the Mongo collection containing the source data
   *
   * @baleen.config renoun_facts
   */
  public static final String PARAM_COLLECTION = "collection";

  @ConfigurationParameter(name = PARAM_COLLECTION, defaultValue = "renoun_facts")
  private String factsCollection;

  /**
   * The name of the field in Mongo that contains the fact subject
   *
   * @baleen.config sourceValue
   */
  public static final String PARAM_SUBJECT_FIELD = "subjectField";

  @ConfigurationParameter(name = PARAM_SUBJECT_FIELD, defaultValue = "sourceValue")
  private String subjectField;
  /**
   * The name of the field in Mongo that contains the fact attribute
   *
   * @baleen.config value
   */
  public static final String PARAM_ATTRIBUTE_FIELD = "attributeField";

  @ConfigurationParameter(name = PARAM_ATTRIBUTE_FIELD, defaultValue = "value")
  private String attributeField;

  /**
   * The name of the field in Mongo that contains the target values
   *
   * @baleen.config targetValue
   */
  public static final String PARAM_OBJECT_FIELD = "objectField";

  @ConfigurationParameter(name = PARAM_OBJECT_FIELD, defaultValue = "targetValue")
  private String objectField;

  @Override
  protected Set<ReNounFact> initializeFacts() {
    MongoDatabase db = mongoResource.getDB();
    MongoCollection<Document> coll = db.getCollection(factsCollection);

    return StreamSupport.stream(coll.find().spliterator(), false)
        .map(
            document ->
                new ReNounFact(
                    document.getString(subjectField),
                    document.getString(attributeField),
                    document.getString(objectField)))
        .collect(Collectors.toSet());
  }
}
