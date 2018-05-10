// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.supplier.dbpedia;

import java.util.Collection;
import java.util.HashSet;

import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.entity.linking.Candidate;
import uk.gov.dstl.baleen.entity.linking.EntityInformation;
import uk.gov.dstl.baleen.entity.linking.util.DefaultCandidate;
import uk.gov.dstl.baleen.entity.linking.util.SparqlUtils;
import uk.gov.dstl.baleen.entity.linking.util.StringTriple;
import uk.gov.dstl.baleen.types.semantic.Location;

/** Candidate Supplier for querying DBPedia for Locations (Places) */
public class DBPediaLocationCandidateSupplier extends DBPediaCandidateSupplier<Location> {

  /** ID field for location */
  public static final String LOCATION_ID_FIELD = "location";

  public static final String NAME_FIELD = "name";

  static final String NAME_PLACEHOLDER = "?" + NAME_FIELD;
  static final String LOCATION_PLACEHOLDER = "?" + LOCATION_ID_FIELD;
  static final String ABSTRACT_PLACEHOLDER = "?abstract";

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DBPediaLocationCandidateSupplier.class);

  /**
   * Constructor
   *
   * @param dbPediaService The service to communicate with DBPedia
   * @param resultsLimit The maximum number of results to be returned
   */
  public DBPediaLocationCandidateSupplier(DBPediaService dbPediaService, int resultsLimit) {
    super(dbPediaService, resultsLimit);
  }

  /** Default constructor */
  public DBPediaLocationCandidateSupplier() {
    super();
  }

  @Override
  public Collection<Candidate> getCandidates(EntityInformation<Location> entityInformation) {
    Collection<Location> locations = entityInformation.getMentions();

    Collection<Candidate> candidates = new HashSet<>();

    for (Location location : locations) {
      try {
        candidates.addAll(queryByNameContains(location.getValue()));
      } catch (ParseException e) {
        LOGGER.error(e.getMessage());
      }
    }

    return candidates;
  }

  private Collection<DefaultCandidate> queryByNameContains(String name) throws ParseException {
    if (name == null || name.length() < 3) {
      return ImmutableSet.of();
    }
    String[] selectVars = {NAME_PLACEHOLDER, LOCATION_PLACEHOLDER, ABSTRACT_PLACEHOLDER};

    StringTriple[] whereClauses = {
      new StringTriple(
          LOCATION_PLACEHOLDER,
          DBPediaService.FOAF + ":" + DBPediaService.FOAF_NAME_LABEL,
          NAME_PLACEHOLDER),
      new StringTriple(
          LOCATION_PLACEHOLDER,
          "a",
          DBPediaService.DBPEDIA_OWL + ":" + DBPediaService.LOCATION_ONTOLOGY_LABEL),
      new StringTriple(
          LOCATION_PLACEHOLDER,
          DBPediaService.DBPEDIA_OWL + ":" + DBPediaService.ABSTRACT_ONTOLOGY_LABEL,
          ABSTRACT_PLACEHOLDER)
    };

    String filterClause =
        SparqlUtils.createFilterByPlaceholderContainsClause(NAME_PLACEHOLDER, name);

    return dbPediaService.searchForCandidates(
        selectVars, whereClauses, filterClause, resultsLimit, LOCATION_ID_FIELD, NAME_FIELD);
  }
}
