// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.renoun;

import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;

import com.google.common.base.Predicates;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.resources.data.ReNounFact;
import uk.gov.dstl.baleen.uima.grammar.DependencyParseException;

/**
 * A dependency tree based relation extractor to obtain seed data for ReNoun. This looks for
 * Subject, Attribute, Object triples where the attribute is expressed as a Noun.
 *
 * <p>The seed patterns are supplied from Mongo and can be generated using ReNounSeedGenerator.
 *
 * <p>In the paper, the noun attribute is checked for coreference with the object this is an option
 * here by setting requireCoreference to true.
 *
 * @baleen.javadoc
 * @see http://emnlp2014.org/papers/pdf/EMNLP2014038.pdf
 */
public class ReNounGeneratedSeedsRelationshipAnnotator extends AbstractReNounRelationshipAnnotator {

  /**
   * Connection to Mongo
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedMongoResource
   */
  public static final String KEY_MONGO = SharedMongoResource.RESOURCE_KEY;

  @ExternalResource(key = KEY_MONGO)
  private SharedMongoResource mongoResource;

  /**
   * The name of the Mongo collection to read patterns from
   *
   * @baleen.config renoun_patterns
   */
  public static final String PARAM_COLLECTION = "collection";

  @ConfigurationParameter(name = PARAM_COLLECTION, defaultValue = "renoun_patterns")
  private String patternsCollection;

  /**
   * The name of the field in Mongo that contains the pattern
   *
   * @baleen.config sourceValue
   */
  public static final String PARAM_PATTERN_FIELD = "patternField";

  @ConfigurationParameter(name = PARAM_PATTERN_FIELD, defaultValue = ReNounFact.PATTERN_FIELD)
  private String patternField;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);
    type = "Seed";
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
}
