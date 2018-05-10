// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.supplier.dbpedia;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.entity.linking.supplier.dbpedia.DBPediaService.DBPEDIA_OWL;

import java.util.Set;

import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.gov.dstl.baleen.entity.linking.util.DefaultCandidate;
import uk.gov.dstl.baleen.entity.linking.util.SparqlUtils;
import uk.gov.dstl.baleen.entity.linking.util.StringTriple;

/** Integration Test - requires external internet connection */
public class DBPediaServiceIT {

  private static final int TEST_LIMIT = 5;
  private static final String PERSON_PLACEHOLDER = "?person";
  private static final String NAME_PLACEHOLDER = "?name";
  private static final String ABSTRACT_PLACEHOLDER = "?abstract";
  private static final String ORGANISATION_PLACEHOLDER = "?organisation";
  private static final String LOCATION_PLACEHOLDER = "?location";

  private static DBPediaService dbPediaService;

  private static Set<DefaultCandidate> personCandidateSet;
  private static Set<DefaultCandidate> organisationCandidateSet;
  private static Set<DefaultCandidate> locationCandidateSet;

  @BeforeClass
  public static void setup() throws ParseException {

    dbPediaService = new DBPediaService();

    personCandidateSet = queryForPeople();
    organisationCandidateSet = queryForOrganisations();
    locationCandidateSet = queryForLocations();
  }

  @Test
  public void testDBPediaQueryReturnsResults() {
    assertFalse(personCandidateSet.isEmpty());
    assertFalse(organisationCandidateSet.isEmpty());
    assertFalse(locationCandidateSet.isEmpty());
  }

  @Test
  public void testDBPediaResultsAreLimited() {
    assertTrue(personCandidateSet.size() <= TEST_LIMIT);
    assertTrue(organisationCandidateSet.size() <= TEST_LIMIT);
    assertTrue(locationCandidateSet.size() <= TEST_LIMIT);
  }

  private static Set<DefaultCandidate> queryForPeople() throws ParseException {
    String[] selectVars = {NAME_PLACEHOLDER, PERSON_PLACEHOLDER, ABSTRACT_PLACEHOLDER};

    StringTriple[] whereClauses = {
      new StringTriple(
          PERSON_PLACEHOLDER, "a", DBPEDIA_OWL + ":" + DBPediaService.PERSON_ONTOLOGY_LABEL),
      new StringTriple(
          PERSON_PLACEHOLDER,
          DBPediaService.FOAF + ":" + DBPediaService.FOAF_NAME_LABEL,
          NAME_PLACEHOLDER),
      new StringTriple(
          PERSON_PLACEHOLDER,
          DBPEDIA_OWL + ":" + DBPediaService.ABSTRACT_ONTOLOGY_LABEL,
          ABSTRACT_PLACEHOLDER)
    };

    String filterClause =
        SparqlUtils.createFilterByPlaceholderContainsClause(NAME_PLACEHOLDER, "Clarkson");

    dbPediaService = new DBPediaService("en");

    return dbPediaService.searchForCandidates(
        selectVars, whereClauses, filterClause, TEST_LIMIT, "person", "name");
  }

  private static Set<DefaultCandidate> queryForOrganisations() throws ParseException {

    String[] selectVars = {NAME_PLACEHOLDER, ORGANISATION_PLACEHOLDER, ABSTRACT_PLACEHOLDER};

    String[] languageSpecificVars = {ABSTRACT_PLACEHOLDER};

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
        SparqlUtils.createFilterByPlaceholderContainsClause(NAME_PLACEHOLDER, "Facebook");

    return dbPediaService.searchForCandidates(
        selectVars,
        whereClauses,
        filterClause,
        TEST_LIMIT,
        DBPediaOrganisationCandidateSupplier.ORGANISATION_ID_FIELD,
        DBPediaOrganisationCandidateSupplier.NAME_FIELD,
        languageSpecificVars);
  }

  private static Set<DefaultCandidate> queryForLocations() throws ParseException {
    String[] selectVars = {NAME_PLACEHOLDER, LOCATION_PLACEHOLDER, ABSTRACT_PLACEHOLDER};

    String[] languageSpecificVars = {ABSTRACT_PLACEHOLDER};

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
        SparqlUtils.createFilterByPlaceholderContainsClause(NAME_PLACEHOLDER, "London");

    return dbPediaService.searchForCandidates(
        selectVars,
        whereClauses,
        filterClause,
        TEST_LIMIT,
        DBPediaLocationCandidateSupplier.LOCATION_ID_FIELD,
        DBPediaLocationCandidateSupplier.NAME_FIELD,
        languageSpecificVars);
  }
}
