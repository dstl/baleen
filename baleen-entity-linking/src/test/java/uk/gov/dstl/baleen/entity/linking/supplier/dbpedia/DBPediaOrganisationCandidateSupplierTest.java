// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.supplier.dbpedia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.dstl.baleen.entity.linking.supplier.dbpedia.DBPediaOrganisationCandidateSupplier.ABSTRACT_PLACEHOLDER;
import static uk.gov.dstl.baleen.entity.linking.supplier.dbpedia.DBPediaOrganisationCandidateSupplier.NAME_FIELD;
import static uk.gov.dstl.baleen.entity.linking.supplier.dbpedia.DBPediaOrganisationCandidateSupplier.NAME_PLACEHOLDER;
import static uk.gov.dstl.baleen.entity.linking.supplier.dbpedia.DBPediaOrganisationCandidateSupplier.ORGANISATION_ID_FIELD;
import static uk.gov.dstl.baleen.entity.linking.supplier.dbpedia.DBPediaOrganisationCandidateSupplier.ORGANISATION_PLACEHOLDER;
import static uk.gov.dstl.baleen.entity.linking.util.SparqlUtils.createFilterByPlaceholderContainsClause;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.entity.linking.Candidate;
import uk.gov.dstl.baleen.entity.linking.EntityInformation;
import uk.gov.dstl.baleen.entity.linking.util.DefaultCandidate;
import uk.gov.dstl.baleen.entity.linking.util.StringTriple;
import uk.gov.dstl.baleen.types.common.Organisation;

public class DBPediaOrganisationCandidateSupplierTest
    extends AbstractDBPediaSupplierTest<Organisation> {

  private static final String MOCK_ORGANISATION1_NAME = "Facebook";

  private DBPediaOrganisationCandidateSupplier dbPediaOrganisationCandidateSupplier;
  private final int resultsLimit = 10;

  @Before
  public void setup() throws ParseException {

    setupMockEntityInformation();

    setupMockServiceToReturnMockCandidates();

    dbPediaOrganisationCandidateSupplier =
        new DBPediaOrganisationCandidateSupplier(mockService, resultsLimit);
  }

  @Test
  public void testCandidatesAreReturned() {
    Collection<Candidate> candidates =
        dbPediaOrganisationCandidateSupplier.getCandidates(mockEntityInformation);

    assertEquals("Should be 2 candidates", 2, candidates.size());
  }

  private void setupMockEntityInformation() {
    Set<Organisation> mockOrganisations = new HashSet<>();
    Organisation mockOrganisation1 = mock(Organisation.class);
    when(mockOrganisation1.getValue()).thenReturn(MOCK_ORGANISATION1_NAME);
    mockOrganisations.add(mockOrganisation1);
    Organisation mockOrganisation2 = mock(Organisation.class);
    when(mockOrganisation2.getValue()).thenReturn("Google");
    mockOrganisations.add(mockOrganisation2);
    Organisation nullOrganisation = mock(Organisation.class);
    mockOrganisations.add(nullOrganisation);

    when(mockEntityInformation.getMentions()).thenReturn(mockOrganisations);
  }

  private void setupMockServiceToReturnMockCandidates() throws ParseException {

    Set<DefaultCandidate> mockCandidates = createMockCandidates();

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

    when(mockService.searchForCandidates(
            eq(selectVars),
            eq(whereClauses),
            eq(
                createFilterByPlaceholderContainsClause(
                    DBPediaOrganisationCandidateSupplier.NAME_PLACEHOLDER,
                    MOCK_ORGANISATION1_NAME)),
            eq(resultsLimit),
            eq(ORGANISATION_ID_FIELD),
            eq(NAME_FIELD)))
        .thenReturn(mockCandidates);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testNoErrorForEmtityInformation() {
    Collection<Candidate> candidates =
        dbPediaOrganisationCandidateSupplier.getCandidates(mock(EntityInformation.class));

    assertTrue(candidates.isEmpty());
  }
}
