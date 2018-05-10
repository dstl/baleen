// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.supplier.mongo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.fakemongo.Fongo;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import uk.gov.dstl.baleen.entity.linking.Candidate;
import uk.gov.dstl.baleen.entity.linking.EntityInformation;
import uk.gov.dstl.baleen.types.common.Person;

@RunWith(MockitoJUnitRunner.class)
public class MongoCandidateSupplierTest {

  private static final String COLLECTION_NAME = "testCandidateSelection";
  private static final String NAME = "name";
  private static final String ID_FIELD = "_id";
  private static final String NAME1 = "Jeremy Clarkson";
  private static final String NAME2 = "Richard Hammond";

  @Mock private EntityInformation<Person> entityInformation;

  private MongoCandidateSupplier<Person> mongoCandidateSupplier;
  private MongoDatabase mongoDatabase;
  private MongoCollection<Document> testCandidatesCollection;

  @Before
  @SuppressWarnings("resource")
  public void setup() throws Exception {
    Fongo fongo = new Fongo("Fake Mongo");
    mongoDatabase = fongo.getDatabase("testDatabase");

    MongoFactory factory =
        new MongoFactory() {

          @Override
          public MongoDatabase createDatabase() {
            return mongoDatabase;
          }

          @Override
          public void close() {
            // TODO Auto-generated method stub
          }
        };

    testCandidatesCollection = mongoDatabase.getCollection(COLLECTION_NAME);
    Document testDocument1 = new Document().append(NAME, NAME1);
    Document testDocument2 = new Document().append(NAME, NAME2);
    testCandidatesCollection.insertOne(testDocument1);
    testCandidatesCollection.insertOne(testDocument2);
    mongoCandidateSupplier = new MongoCandidateSupplier<>((Map<String, String> args) -> factory);

    // @formatter:off
    mongoCandidateSupplier.configure(
        new String[] {
          MongoCandidateSupplier.PARAM_COLLECTION, COLLECTION_NAME,
          MongoCandidateSupplier.PARAM_SEARCH_FIELD, NAME,
          MongoCandidateSupplier.PARAM_ID_FIELD, ID_FIELD
        });
    // @formatter:on

    Collection<Person> people = new HashSet<>();
    Person person1 = mock(Person.class);
    Person person2 = mock(Person.class);
    Person nullPerson = mock(Person.class);
    when(person1.getValue()).thenReturn(NAME1);
    when(person2.getValue()).thenReturn(NAME2);
    people.add(person1);
    people.add(person2);
    people.add(nullPerson);

    when(entityInformation.getMentions()).thenReturn(people);
  }

  @After
  public void tearDown() {
    mongoDatabase.drop();
  }

  @Test
  public void testCandidatesAreRetrievedFromMongo() {
    Collection<Candidate> candidates = mongoCandidateSupplier.getCandidates(entityInformation);

    assertFalse(candidates.isEmpty());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testNoErrorForEmtityInformation() {
    Collection<Candidate> candidates =
        mongoCandidateSupplier.getCandidates(mock(EntityInformation.class));

    assertTrue(candidates.isEmpty());
  }

  @Test
  public void testCandidateIdIsDocumentId() {
    Collection<Candidate> candidates = mongoCandidateSupplier.getCandidates(entityInformation);
    Candidate candidate = findCandidateByName(candidates, NAME1);
    BasicDBObject whereQuery = new BasicDBObject();
    whereQuery.put(NAME, NAME1);
    Document document = testCandidatesCollection.find(whereQuery).iterator().next();
    assertEquals(
        "Mongo ID should be candidate ID", candidate.getId(), document.get(ID_FIELD).toString());
  }

  @Test
  public void testPartialNameMatchReturnsResults() throws Exception {

    List<Person> partialPeople = new ArrayList<>();
    Person partiallyNamedPerson = mock(Person.class);
    when(partiallyNamedPerson.getValue()).thenReturn("Jeremy");
    partialPeople.add(partiallyNamedPerson);
    when(entityInformation.getMentions()).thenReturn(partialPeople);

    Collection<Candidate> candidates = mongoCandidateSupplier.getCandidates(entityInformation);

    assertFalse("Candidates should be returned", candidates.isEmpty());

    Candidate candidate = findCandidateByName(candidates, "Jeremy Clarkson");
    assertNotNull("Returned candidates should include " + NAME1, candidate);
  }

  private Candidate findCandidateByName(Collection<Candidate> candidates, String name) {
    for (Candidate candidate : candidates) {
      if (candidate.getKeyValuePairs().get("name").equals(name)) {
        return candidate;
      }
    }
    return null;
  }
}
