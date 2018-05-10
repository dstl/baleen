// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.supplier.dbpedia;

import java.util.Collection;
import java.util.HashSet;
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
import uk.gov.dstl.baleen.types.common.Organisation;

/** Candidate Supplier for querying DBPedia for Locations (Places) */
public class DBPediaOrganisationCandidateSupplier extends DBPediaCandidateSupplier<Organisation> {

  /** ID field for organisation */
  public static final String ORGANISATION_ID_FIELD = "organisation";

  public static final String NAME_FIELD = "name";

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DBPediaOrganisationCandidateSupplier.class);

  static final String ORGANISATION_PLACEHOLDER = "?" + ORGANISATION_ID_FIELD;
  static final String NAME_PLACEHOLDER = "?" + NAME_FIELD;
  static final String ABSTRACT_PLACEHOLDER = "?abstract";

  /**
   * Constructor
   *
   * @param dbPediaService The service to communicate with DBPedia
   * @param resultsLimit The maximum number of results to be returned
   */
  public DBPediaOrganisationCandidateSupplier(DBPediaService dbPediaService, int resultsLimit) {
    super(dbPediaService, resultsLimit);
  }

  /** Default constructor */
  public DBPediaOrganisationCandidateSupplier() {
    super();
  }

  @Override
  public Collection<Candidate> getCandidates(EntityInformation<Organisation> entityInformation) {
    Collection<Organisation> organisations = entityInformation.getMentions();

    Collection<Candidate> candidates = new HashSet<>();

    for (Organisation organisation : organisations) {
      try {
        candidates.addAll(queryByNameContains(organisation.getValue()));
      } catch (ParseException e) {
        LOGGER.error(e.getMessage());
      }
    }

    return candidates;
  }

  private Set<DefaultCandidate> queryByNameContains(String name) throws ParseException {
    if (name == null || name.length() < 3) {
      return ImmutableSet.of();
    }
    String[] selectVars = {NAME_PLACEHOLDER, ORGANISATION_PLACEHOLDER, ABSTRACT_PLACEHOLDER};

    StringTriple[] whereClauses = {
      new StringTriple(
          ORGANISATION_PLACEHOLDER,
          DBPediaService.FOAF + ":" + DBPediaService.FOAF_NAME_LABEL,
          NAME_PLACEHOLDER),
      new StringTriple(
          ORGANISATION_PLACEHOLDER,
          "a",
          DBPediaService.DBPEDIA_OWL + ":" + DBPediaService.ORGANISATION_ONTOLOGY_LABEL),
      new StringTriple(
          ORGANISATION_PLACEHOLDER,
          DBPediaService.DBPEDIA_OWL + ":" + DBPediaService.ABSTRACT_ONTOLOGY_LABEL,
          ABSTRACT_PLACEHOLDER)
    };

    String filterClause =
        SparqlUtils.createFilterByPlaceholderContainsClause(NAME_PLACEHOLDER, name);

    return dbPediaService.searchForCandidates(
        selectVars, whereClauses, filterClause, resultsLimit, ORGANISATION_ID_FIELD, NAME_FIELD);
  }
}
