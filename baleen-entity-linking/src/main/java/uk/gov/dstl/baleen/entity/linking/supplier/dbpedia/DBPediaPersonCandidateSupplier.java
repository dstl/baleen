// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.supplier.dbpedia;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.entity.linking.Candidate;
import uk.gov.dstl.baleen.entity.linking.EntityInformation;
import uk.gov.dstl.baleen.entity.linking.util.DefaultCandidate;
import uk.gov.dstl.baleen.entity.linking.util.SparqlUtils;
import uk.gov.dstl.baleen.entity.linking.util.StringTriple;
import uk.gov.dstl.baleen.types.common.Person;

/**
 * Candidate Supplier for getting candidates from DBPedia using SPARQL.
 *
 * <p>Note - An internet connection is required to communicate with the DBPedia SPARQL endpoint
 *
 * <p>See <a href="http://wiki.dbpedia.org/">http://wiki.dbpedia.org/</a>
 */
public class DBPediaPersonCandidateSupplier extends DBPediaCandidateSupplier<Person> {

  private List<String> pronouns =
      Arrays.asList(
          "i",
          "me",
          "myself",
          "you",
          "yourself",
          "he",
          "she",
          "him",
          "himself",
          "her",
          "herself",
          "we",
          "they",
          "them",
          "ourselves",
          "yourselves",
          "themselves");

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DBPediaPersonCandidateSupplier.class);

  static final String PERSON = "person";
  static final String NAME = "name";
  static final String NAME_PLACEHOLDER = "?" + NAME;
  static final String PERSON_PLACEHOLDER = "?" + PERSON;
  static final String ABSTRACT_PLACEHOLDER = "?abstract";

  /**
   * The constructor
   *
   * @param dbPediaService The service for getting candidates from DBPedia
   * @param resultsLimit The maximum number of results to return from the generated SPARQL query
   */
  public DBPediaPersonCandidateSupplier(DBPediaService dbPediaService, int resultsLimit) {
    super(dbPediaService, resultsLimit);
  }

  /** Default constructor */
  public DBPediaPersonCandidateSupplier() {
    super();
  }

  @Override
  public Collection<Candidate> getCandidates(EntityInformation<Person> entityInformation) {
    Collection<Person> people = entityInformation.getMentions();
    Set<Candidate> candidates = new HashSet<>();
    people.forEach(
        person -> {
          try {
            candidates.addAll(queryByNameContains(person.getValue()));
          } catch (ParseException e) {
            LOGGER.error(e.getMessage());
          }
        });
    return candidates;
  }

  private Set<DefaultCandidate> queryByNameContains(String name) throws ParseException {
    if (name == null || pronouns.contains(name.toLowerCase())) {
      return ImmutableSet.of();
    }
    String[] selectVars = {NAME_PLACEHOLDER, PERSON_PLACEHOLDER, ABSTRACT_PLACEHOLDER};

    StringTriple[] whereClauses = {
      new StringTriple(
          PERSON_PLACEHOLDER,
          DBPediaService.FOAF + ":" + DBPediaService.FOAF_NAME_LABEL,
          NAME_PLACEHOLDER),
      new StringTriple(
          PERSON_PLACEHOLDER,
          "a",
          DBPediaService.DBPEDIA_OWL + ":" + DBPediaService.PERSON_ONTOLOGY_LABEL),
      new StringTriple(
          PERSON_PLACEHOLDER,
          DBPediaService.DBPEDIA_OWL + ":" + DBPediaService.ABSTRACT_ONTOLOGY_LABEL,
          ABSTRACT_PLACEHOLDER)
    };

    String filterClause =
        SparqlUtils.createFilterByPlaceholderContainsClause(NAME_PLACEHOLDER, name);

    return dbPediaService.searchForCandidates(
        selectVars, whereClauses, filterClause, resultsLimit, PERSON, NAME);
  }
}
