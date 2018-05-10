// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.supplier.dbpedia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.dstl.baleen.entity.linking.supplier.dbpedia.DBPediaLocationCandidateSupplier.ABSTRACT_PLACEHOLDER;
import static uk.gov.dstl.baleen.entity.linking.supplier.dbpedia.DBPediaLocationCandidateSupplier.LOCATION_ID_FIELD;
import static uk.gov.dstl.baleen.entity.linking.supplier.dbpedia.DBPediaLocationCandidateSupplier.LOCATION_PLACEHOLDER;
import static uk.gov.dstl.baleen.entity.linking.supplier.dbpedia.DBPediaLocationCandidateSupplier.NAME_FIELD;
import static uk.gov.dstl.baleen.entity.linking.supplier.dbpedia.DBPediaLocationCandidateSupplier.NAME_PLACEHOLDER;
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
import uk.gov.dstl.baleen.types.semantic.Location;

public class DBPediaLocationCandidateSupplierTest extends AbstractDBPediaSupplierTest<Location> {

  private static final String MOCK_LOCATION1_NAME = "London";
  private final int resultsLimit = 5;
  private DBPediaLocationCandidateSupplier dbPediaLocationCandidateSupplier;

  @Before
  public void setup() throws ParseException {

    setupMockEntityInformation();
    setupMockServiceToReturnMockCandidates();

    dbPediaLocationCandidateSupplier =
        new DBPediaLocationCandidateSupplier(mockService, resultsLimit);
  }

  @Test
  public void testCandidatesAreReturned() {
    Collection<Candidate> candidates =
        dbPediaLocationCandidateSupplier.getCandidates(mockEntityInformation);

    assertEquals("Should return 2 candidates", 2, candidates.size());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testNoErrorForEmtityInformation() {
    Collection<Candidate> candidates =
        dbPediaLocationCandidateSupplier.getCandidates(mock(EntityInformation.class));

    assertTrue(candidates.isEmpty());
  }

  private void setupMockEntityInformation() {
    Set<Location> mockLocations = new HashSet<>();
    Location mockLocation1 = mock(Location.class);
    when(mockLocation1.getValue()).thenReturn(MOCK_LOCATION1_NAME);
    mockLocations.add(mockLocation1);
    Location mockLocation2 = mock(Location.class);
    when(mockLocation2.getValue()).thenReturn("Google");
    mockLocations.add(mockLocation2);
    Location nullLocation = mock(Location.class);
    mockLocations.add(nullLocation);

    when(mockEntityInformation.getMentions()).thenReturn(mockLocations);
  }

  private void setupMockServiceToReturnMockCandidates() throws ParseException {

    Set<DefaultCandidate> mockCandidates = createMockCandidates();

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

    when(mockService.searchForCandidates(
            eq(selectVars),
            eq(whereClauses),
            eq(
                createFilterByPlaceholderContainsClause(
                    DBPediaLocationCandidateSupplier.NAME_PLACEHOLDER, MOCK_LOCATION1_NAME)),
            eq(resultsLimit),
            eq(LOCATION_ID_FIELD),
            eq(NAME_FIELD)))
        .thenReturn(mockCandidates);
  }
}
