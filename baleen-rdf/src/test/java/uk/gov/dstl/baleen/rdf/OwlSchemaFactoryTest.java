// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.rdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.Optional;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.vocabulary.XSD;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;

import uk.gov.dstl.baleen.graph.DocumentGraphFactory;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.types.structure.Document;

public class OwlSchemaFactoryTest {

  private TypeSystem typeSystem;

  @Before
  public void setUp() throws UIMAException {
    JCas jCas = JCasFactory.createJCas();
    typeSystem = jCas.getTypeSystem();
  }

  @Test
  public void canGenerateDocumentSchema() throws CASRuntimeException, UIMAException {

    String ns = "http://baleen.dstl.gov.uk/document/";
    OwlSchemaFactory owlTypeSystem =
        new OwlSchemaFactory(ns, typeSystem, ImmutableList.of("internalId"));
    OntModel model = owlTypeSystem.createDocumentOntology();
    model.setNsPrefix("baleen", ns);

    try {
      assertNotNull(model);
      assertNotNull(model.getOntClass(ns + Document.class.getSimpleName()));
      assertNotNull(model.getOntClass(ns + ReferenceTarget.class.getSimpleName()));
      assertNotNull(model.getOntClass(ns + Relation.class.getSimpleName()));
      OntClass entity = model.getOntClass(ns + Entity.class.getSimpleName());
      assertNotNull(entity);
      assertEquals(
          "Type to represent named entities - values that are assigned a semantic type.",
          entity.getComment("EN"));

      assertNotNull(entity.getSuperClass());
      assertEquals(getEntityChildrenCount(), Streams.stream(entity.listSubClasses()).count());

      OntClass location = model.getOntClass(ns + Location.class.getSimpleName());
      assertEquals(entity, location.getSuperClass());
      assertEquals(1, Streams.stream(location.listSubClasses()).count());

      OntClass person = model.getOntClass(ns + Person.class.getSimpleName());
      assertEquals(entity, person.getSuperClass());
      assertEquals(0, Streams.stream(person.listSubClasses()).count());
      assertEquals(2, Streams.stream(person.listDeclaredProperties(true)).count());
      assertEquals(13, Streams.stream(person.listDeclaredProperties()).count());
      Optional<OntProperty> findFirst =
          Streams.stream(person.listDeclaredProperties(true))
              .filter(p -> "gender".equals(p.getLocalName()))
              .findFirst();
      assertTrue(findFirst.isPresent());
      assertEquals(XSD.xstring, findFirst.get().getRange());

      Optional<OntProperty> mentionOf =
          Streams.stream(person.listDeclaredProperties())
              .filter(p -> DocumentGraphFactory.MENTION_OF.equals(p.getLocalName()))
              .findFirst();
      assertTrue(mentionOf.isPresent());
      assertEquals(
          ReferenceTarget.class.getSimpleName(), mentionOf.get().getRange().getLocalName());

    } finally {
      writeToConsole(model);
    }
  }

  @Test
  public void canGenerateEntitySchema() throws ResourceInitializationException {

    String ns = "http://baleen.dstl.gov.uk/entity/";

    OwlSchemaFactory owlTypeSystem =
        new OwlSchemaFactory(ns, typeSystem, ImmutableList.of("isNormalised", "internalId"));
    OntModel model = owlTypeSystem.createEntityOntology();
    model.setNsPrefix("baleen", ns);

    try {
      assertNotNull(model);
      assertNull(model.getOntClass(ns + Document.class.getSimpleName()));
      assertNull(model.getOntClass(ns + ReferenceTarget.class.getSimpleName()));
      assertNull(model.getOntClass(ns + Relation.class.getSimpleName()));
      OntClass entity = model.getOntClass(ns + Entity.class.getSimpleName());
      assertNotNull(entity);
      assertEquals(
          "Type to represent named entities - values that are assigned a semantic type.",
          entity.getComment("EN"));

      assertNull(entity.getSuperClass());
      assertEquals(17, Streams.stream(entity.listSubClasses()).count());

      OntClass location = model.getOntClass(ns + Location.class.getSimpleName());
      assertEquals(entity, location.getSuperClass());
      assertEquals(1, Streams.stream(location.listSubClasses()).count());

      OntClass person = model.getOntClass(ns + Person.class.getSimpleName());
      assertEquals(entity, person.getSuperClass());
      assertEquals(0, Streams.stream(person.listSubClasses()).count());
      assertEquals(2, Streams.stream(person.listDeclaredProperties(true)).count());
      assertEquals(12, Streams.stream(person.listDeclaredProperties()).count());
      Optional<OntProperty> findFirst =
          Streams.stream(person.listDeclaredProperties(true))
              .filter(p -> "gender".equals(p.getLocalName()))
              .findFirst();
      Optional<OntProperty> mentions =
          Streams.stream(person.listDeclaredProperties())
              .filter(p -> "mentions".equals(p.getLocalName()))
              .findFirst();
      assertTrue(mentions.isPresent());
      assertEquals(XSD.xstring, findFirst.get().getRange());

      Optional<OntProperty> relation =
          Streams.stream(person.listDeclaredProperties())
              .filter(p -> DocumentGraphFactory.RELATION.equals(p.getLocalName()))
              .findFirst();
      assertTrue(relation.isPresent());
      assertEquals(Entity.class.getSimpleName(), relation.get().getRange().getLocalName());
      assertEquals(Entity.class.getSimpleName(), relation.get().getDomain().getLocalName());

    } finally {
      writeToConsole(model);
    }
  }

  private void writeToConsole(OntModel model) {
    StringWriter writer = new StringWriter();
    model.write(writer, RdfFormat.TURTLE.getKey());
    System.out.println(writer.toString());
  }

  private int getEntityChildrenCount() throws CASRuntimeException, UIMAException {
    Type type = typeSystem.getType(Entity.class.getName());
    return typeSystem.getDirectSubtypes(type).size();
  }
}
