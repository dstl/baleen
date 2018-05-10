// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.supplier.mongo;

import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Filters.regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import uk.gov.dstl.baleen.entity.linking.Candidate;
import uk.gov.dstl.baleen.entity.linking.CandidateSupplier;
import uk.gov.dstl.baleen.entity.linking.EntityInformation;
import uk.gov.dstl.baleen.entity.linking.util.DefaultCandidate;
import uk.gov.dstl.baleen.entity.linking.util.StringArgumentsHandler;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * Candidate Supplier for retrieving candidates from Mongo
 *
 * @param <T> The type of Entity the Candidates relate to
 */
public class MongoCandidateSupplier<T extends Entity> implements CandidateSupplier<T> {

  private static final String DEFAULT_MONGO_ID = "_id";

  /** The Mongo Collection name */
  public static final String PARAM_COLLECTION = "collection";

  /** The Mongo field to search against */
  public static final String PARAM_SEARCH_FIELD = "searchField";

  /** Thew document ID field */
  public static final String PARAM_ID_FIELD = "idField";

  private Map<String, String> argumentsMap = new HashMap<>();

  private final Function<Map<String, String>, MongoFactory> factorySupplier;

  private MongoFactory mongoFactory;
  private MongoDatabase mongoDatabase;

  /** Default constructor */
  public MongoCandidateSupplier() {
    this(RealMongoFactory::new);
  }

  MongoCandidateSupplier(Function<Map<String, String>, MongoFactory> factorySupplier) {
    this.factorySupplier = factorySupplier;
  }

  @Override
  public Collection<Candidate> getCandidates(EntityInformation<T> entityInformation) {

    Collection<Candidate> candidates = new HashSet<>();

    MongoCollection<Document> collection =
        mongoDatabase.getCollection(argumentsMap.get(PARAM_COLLECTION));

    Optional<Bson> buildQuery = buildQuery(entityInformation);
    if (buildQuery.isPresent()) {
      FindIterable<Document> documents = collection.find(buildQuery.get());

      for (Document document : documents) {
        Map<String, String> map = new MongoDocumentFlattener(document).flatten();
        String candidateID =
            document.get(argumentsMap.getOrDefault(PARAM_ID_FIELD, DEFAULT_MONGO_ID)).toString();
        String candidateName = document.get(argumentsMap.get(PARAM_SEARCH_FIELD)).toString();
        candidates.add(new DefaultCandidate(candidateID, candidateName, map));
      }
    }
    return candidates;
  }

  @Override
  public void configure(String[] argumentPairs) throws BaleenException {
    argumentsMap = new StringArgumentsHandler(argumentPairs).createStringsMap();
    mongoFactory = factorySupplier.apply(argumentsMap);
    mongoDatabase = mongoFactory.createDatabase();
  }

  private Optional<Bson> buildQuery(EntityInformation<T> entityInformation) {
    List<String> searchValues = new ArrayList<>();
    entityInformation
        .getMentions()
        .stream()
        .filter(m -> m.getValue() != null)
        .forEach(
            mention -> {
              String[] mentionSearchTerms = mention.getValue().split(" ");
              searchValues.addAll(Arrays.asList(mentionSearchTerms));
            });

    if (searchValues.isEmpty()) {
      return Optional.empty();
    }

    List<Bson> bsonList = new ArrayList<>();
    for (String partialSearchTerm : searchValues) {
      bsonList.add(regex(argumentsMap.get(PARAM_SEARCH_FIELD), partialSearchTerm, "i"));
    }

    return Optional.of(or(bsonList));
  }

  @Override
  public void close() throws BaleenException {
    if (mongoFactory != null) {
      try {
        mongoFactory.close();
      } catch (Exception e) {
        throw new BaleenException(e);
      }
    }
  }
}
