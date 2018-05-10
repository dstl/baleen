// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.supplier.dbpedia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.dstl.baleen.entity.linking.supplier.dbpedia.DBPediaCandidateSupplier.PARAM_LANGUAGE;
import static uk.gov.dstl.baleen.entity.linking.supplier.dbpedia.DBPediaCandidateSupplier.PARAM_RESULTS_LIMIT;
import static uk.gov.dstl.baleen.entity.linking.supplier.dbpedia.DBPediaPersonCandidateSupplier.ABSTRACT_PLACEHOLDER;
import static uk.gov.dstl.baleen.entity.linking.supplier.dbpedia.DBPediaPersonCandidateSupplier.NAME;
import static uk.gov.dstl.baleen.entity.linking.supplier.dbpedia.DBPediaPersonCandidateSupplier.NAME_PLACEHOLDER;
import static uk.gov.dstl.baleen.entity.linking.supplier.dbpedia.DBPediaPersonCandidateSupplier.PERSON;
import static uk.gov.dstl.baleen.entity.linking.supplier.dbpedia.DBPediaPersonCandidateSupplier.PERSON_PLACEHOLDER;
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
import uk.gov.dstl.baleen.types.common.Person;

public class DBPediaPersonCandidateSupplierTest extends AbstractDBPediaSupplierTest<Person> {

  private static final String MOCK_PERSON_1_NAME = "Jeremy Clarkson";
  private static final String MOCK_PERSON_2_NAME = "Jezza Clarkson";

  private DBPediaPersonCandidateSupplier dbPediaPersonCandidateSupplier;

  @Before
  public void setup() throws Exception {

    setupMockEntityInformation();

    setupMockServiceToReturnMockCandidates();

    dbPediaPersonCandidateSupplier = new DBPediaPersonCandidateSupplier(mockService, 10);
    dbPediaPersonCandidateSupplier.configure(
        new String[] {PARAM_LANGUAGE, "en", PARAM_RESULTS_LIMIT, "10"});
  }

  @Test
  public void testGetCandidatesReturnsCandidates() {
    Collection<Candidate> candidates =
        dbPediaPersonCandidateSupplier.getCandidates(mockEntityInformation);

    assertEquals("Should be 2 candidates", 2, candidates.size());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testNoErrorForEmtityInformation() {
    Collection<Candidate> candidates =
        dbPediaPersonCandidateSupplier.getCandidates(mock(EntityInformation.class));

    assertTrue(candidates.isEmpty());
  }

  private void setupMockEntityInformation() {
    Set<Person> people = new HashSet<>();
    Person mockPerson1 = mock(Person.class);
    Person mockPerson2 = mock(Person.class);
    Person pronoun = mock(Person.class);
    Person nullPerson = mock(Person.class);
    when(mockPerson1.getValue()).thenReturn(MOCK_PERSON_1_NAME);
    when(mockPerson2.getValue()).thenReturn(MOCK_PERSON_2_NAME);
    when(pronoun.getValue()).thenReturn("I");
    people.add(mockPerson1);
    people.add(mockPerson2);
    people.add(nullPerson);
    people.add(pronoun);

    when(mockEntityInformation.getMentions()).thenReturn(people);
  }

  private void setupMockServiceToReturnMockCandidates() throws ParseException {

    Set<DefaultCandidate> mockCandidates = createMockCandidates();
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

    when(mockService.searchForCandidates(
            eq(selectVars),
            eq(whereClauses),
            eq(createFilterByPlaceholderContainsClause(NAME_PLACEHOLDER, MOCK_PERSON_1_NAME)),
            eq(10),
            eq(PERSON),
            eq(NAME)))
        .thenReturn(mockCandidates);
  }
}
