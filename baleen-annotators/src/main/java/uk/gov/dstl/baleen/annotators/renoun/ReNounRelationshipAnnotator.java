// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.renoun;

import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;
import org.elasticsearch.common.Strings;

import com.google.common.base.Predicates;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.resources.data.ReNounFact;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.grammar.DependencyParseException;
import uk.gov.dstl.baleen.uima.grammar.DependencyTree;

/**
 * A dependency tree based relation extractor for ReNoun.
 *
 * <p>The dependency patterns are provided from a mongo collection.
 *
 * @baleen.javadoc
 * @see http://emnlp2014.org/papers/pdf/EMNLP2014038.pdf
 */
public class ReNounRelationshipAnnotator extends AbstractReNounRelationshipAnnotator {

  /**
   * Connection to Mongo
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedMongoResource
   */
  public static final String KEY_MONGO = SharedMongoResource.RESOURCE_KEY;

  @ExternalResource(key = KEY_MONGO)
  private SharedMongoResource mongoResource;

  /**
   * The name of the Mongo collection to output to
   *
   * @baleen.config renoun_patterns
   */
  public static final String PARAM_OUPUT_COLLECTION = "ouputCollection";

  @ConfigurationParameter(name = PARAM_OUPUT_COLLECTION, defaultValue = "renoun_patterns")
  private String patternsCollection;

  /**
   * The name of the Mongo collection to output facts to for scoring
   *
   * @baleen.config renoun_patterns
   */
  public static final String PARAM_FACT_COLLECTION = "factCollection";

  @ConfigurationParameter(name = PARAM_FACT_COLLECTION, mandatory = false)
  private String factCollection;

  /**
   * The name of the field in Mongo that contains the pattern
   *
   * @baleen.config sourceValue
   */
  public static final String PARAM_PATTERN_FIELD = "patternField";

  @ConfigurationParameter(name = PARAM_PATTERN_FIELD, defaultValue = ReNounFact.PATTERN_FIELD)
  private String patternField;

  private MongoCollection<Document> factsColl;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);
    type = "ReNoun";
    if (!Strings.isEmpty(factCollection)) {
      MongoDatabase db = mongoResource.getDB();
      factsColl = db.getCollection(factCollection);
    }
  }

  @Override
  protected Supplier<Stream<DependencyTreeMatcher>> createPatternsSupplier()
      throws DependencyParseException {

    return new Supplier<Stream<DependencyTreeMatcher>>() {

      MongoDatabase db = mongoResource.getDB();
      final MongoCollection<Document> coll = db.getCollection(patternsCollection);

      @Override
      public Stream<DependencyTreeMatcher> get() {
        return StreamSupport.stream(coll.find().spliterator(), false)
            .map(
                document -> {
                  try {
                    return DependencyTreeMatcher.create(document.getString(patternField));
                  } catch (DependencyParseException e) {
                    return null;
                  }
                })
            .filter(Predicates.notNull());
      }
    };
  }

  @Override
  protected void recordRelation(DependencyTree dependencyTree, Relation relation) {
    if (factsColl == null) {
      return;
    }

    new ReNounFact(
            relation.getSource().getValue(),
            relation.getValue(),
            relation.getTarget().getValue(),
            dependencyTree.toString())
        .save(factsColl);
  }
}
