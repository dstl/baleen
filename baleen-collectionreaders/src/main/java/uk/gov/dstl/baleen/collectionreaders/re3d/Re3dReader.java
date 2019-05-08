// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.collectionreaders.re3d;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;
import uk.gov.dstl.baleen.types.common.DocumentReference;
import uk.gov.dstl.baleen.types.common.Frequency;
import uk.gov.dstl.baleen.types.common.Money;
import uk.gov.dstl.baleen.types.common.Nationality;
import uk.gov.dstl.baleen.types.common.Organisation;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.common.Quantity;
import uk.gov.dstl.baleen.types.common.Url;
import uk.gov.dstl.baleen.types.common.Vehicle;
import uk.gov.dstl.baleen.types.military.MilitaryPlatform;
import uk.gov.dstl.baleen.types.military.Weapon;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.types.semantic.Temporal;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;
import uk.gov.dstl.baleen.uima.UimaSupport;
import uk.gov.dstl.baleen.uima.utils.Offset;

/**
 * Collection reader for re3d: Relationship and Entity Extraction Evaluation Dataset
 *
 * <p>Recurses through subfolders and to look for documents.json entities.json and relations.json
 * one level down
 *
 * <p>Clone or download data from <a
 * href="https://github.com/dstl/re3d">https://github.com/dstl/re3d</a>
 */
public class Re3dReader extends BaleenCollectionReader {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /**
   * Root folder to start from
   *
   * @baleen.config <i>Current directory</i>
   */
  public static final String PARAM_FOLDER = "folder";

  @ConfigurationParameter(name = PARAM_FOLDER, defaultValue = ".")
  private String rootFolder;

  /**
   * Add entities to the document from RE3D
   *
   * @baleen.config false
   */
  public static final String PARAM_ENTITIES = "entities";

  @ConfigurationParameter(name = PARAM_ENTITIES, defaultValue = "false")
  private boolean loadEntities;

  /**
   * Add relations to the document from RE3D
   *
   * @baleen.config false
   */
  public static final String PARAM_RELATIONSHIPS = "relations";

  @ConfigurationParameter(name = PARAM_RELATIONSHIPS, defaultValue = "false")
  private boolean loadRelations;

  /**
   * Add dates to the document over the past month
   *
   * @baleen.config false
   */
  public static final String PARAM_RANDOM_DATES = "dates";

  @ConfigurationParameter(name = PARAM_RANDOM_DATES, defaultValue = "false")
  private boolean randomDates;

  private final Random random = new Random();

  private final LinkedList<Re3dDocument> documents = new LinkedList<>();

  @Override
  protected void doInitialize(final UimaContext context) throws ResourceInitializationException {

    try {
      final File rootDir = new File(rootFolder);

      if (!rootDir.exists() || !rootDir.isDirectory()) {
        throw new BaleenException("Root dir is not a folder");
      }

      // Recurse through subfolders and look for documents.json. only one level though

      final List<Re3dEntity> entities = new ArrayList<>();
      final List<Re3dRelation> relations = new ArrayList<>();

      for (final File subDir : rootDir.listFiles(File::isDirectory)) {
        final File documentJson = new File(subDir, "documents.json");
        if (documentJson.exists() && documentJson.isFile()) {
          readDocuments(documentJson);

          final File entitiesJson = new File(subDir, "entities.json");
          if (loadEntities && entitiesJson.exists() && entitiesJson.isFile()) {
            readEntities(entitiesJson, entities);
          }

          final File relationsJson = new File(subDir, "relations.json");
          if (loadRelations && relationsJson.exists() && relationsJson.isFile()) {
            readRelations(relationsJson, relations);
          }
        }
      }

      join(entities, relations);

    } catch (final Exception e) {
      throw new ResourceInitializationException(e);
    }
  }

  private void join(final List<Re3dEntity> entities, final List<Re3dRelation> relations) {
    final Map<String, Re3dDocument> idToDoc =
        documents.stream().collect(Collectors.toMap(Re3dDocument::getId, d -> d));

    entities.forEach(
        e -> {
          final Re3dDocument d = idToDoc.get(e.getDocumentId());
          if (d != null) {
            d.addEntity(e);
          }
        });

    relations.forEach(
        r -> {
          final Re3dDocument d = idToDoc.get(r.getDocumentId());
          if (d != null) {
            d.addRelation(r);
          }
        });
  }

  private void readDocuments(final File file) throws IOException {
    addAllFromFile(file, Re3dDocument.class, documents);
  }

  private void readEntities(final File file, final List<Re3dEntity> entities) throws IOException {
    addAllFromFile(file, Re3dEntity.class, entities);
  }

  private void readRelations(final File file, final List<Re3dRelation> relations)
      throws IOException {
    addAllFromFile(file, Re3dRelation.class, relations);
  }

  private <T> void addAllFromFile(
      final File file, final Class<T> valueType, final List<T> container) throws IOException {
    Files.readAllLines(file.toPath()).stream()
        .map(
            line -> {
              try {
                return OBJECT_MAPPER.readValue(line, valueType);
              } catch (final Exception e) {
                getMonitor().warn("Unable to read file {}", file, e);
                return null;
              }
            })
        .filter(Objects::nonNull)
        .forEach(container::add);
  }

  @Override
  protected void doGetNext(final JCas jCas) throws IOException, CollectionException {
    final Re3dDocument doc = documents.pop();

    jCas.setDocumentText(doc.getText());

    final DocumentAnnotation da = UimaSupport.getDocumentAnnotation(jCas);
    da.setTimestamp(System.currentTimeMillis());
    da.setDocType("re3d");
    da.setDocumentClassification("O");
    da.setSourceUri(doc.getSourceUrl());

    if (randomDates) {
      final LocalDateTime date =
          LocalDate.now()
              .minusDays(random.nextInt(30))
              .atTime(random.nextInt(24), random.nextInt(60), random.nextInt(60));
      da.setTimestamp(date.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli());
    }
    Multimap<Offset, Entity> entityIndex = HashMultimap.create();
    if (loadEntities) {
      for (final Re3dEntity e : doc.getEntities()) {
        final Optional<Entity> entity = createEntity(jCas, e.getType());

        if (entity.isPresent()) {
          final Entity a = entity.get();
          a.setBegin(e.getBegin());
          a.setEnd(e.getEnd());
          a.setValue(e.getValue());
          a.setConfidence(e.getConfidence());

          getSupport().add(a);

          entityIndex.put(new Offset(e.getBegin(), e.getEnd()), a);
        }
      }
    }

    if (loadRelations) {
      for (final Re3dRelation r : doc.getRelations()) {
        final Optional<Entity> source =
            findEntity(entityIndex, r.getSourceBegin(), r.getSourceEnd(), r.getSource());
        final Optional<Entity> target =
            findEntity(entityIndex, r.getSourceBegin(), r.getSourceEnd(), r.getSource());

        if (source.isPresent() && target.isPresent()) {
          final Relation a = new Relation(jCas);
          a.setBegin(r.getBegin());
          a.setEnd(r.getEnd());
          a.setValue(r.getValue());
          a.setConfidence(r.getConfidence());
          a.setRelationshipType(r.getType());
          a.setSource(source.get());
          a.setTarget(target.get());
          getSupport().add(a);
        }
      }
    }
  }

  private Optional<Entity> findEntity(
      Multimap<Offset, Entity> entityIndex, int begin, int end, String value) {
    return entityIndex.get(new Offset(begin, end)).stream()
        .filter(e -> value.equals(e.getValue()))
        .findFirst();
  }

  private Optional<Entity> createEntity(final JCas jCas, final String type) {

    // The verbose, but ultimately more isolated from type system changes

    switch (type) {
      case "CommsIdentifier":
        return Optional.of(new CommsIdentifier(jCas));
      case "Frequency":
        return Optional.of(new Frequency(jCas));
      case "DocumentReference":
        return Optional.of(new DocumentReference(jCas));
      case "Location":
        return Optional.of(new Location(jCas));
      case "MilitaryPlatform":
        return Optional.of(new MilitaryPlatform(jCas));
      case "Money":
        return Optional.of(new Money(jCas));
      case "Nationality":
        return Optional.of(new Nationality(jCas));
      case "Organisation":
        return Optional.of(new Organisation(jCas));
      case "Person":
        return Optional.of(new Person(jCas));
      case "Quantity":
        return Optional.of(new Quantity(jCas));
      case "Temporal":
        return Optional.of(new Temporal(jCas));
      case "Url":
        return Optional.of(new Url(jCas));
      case "Vehicle":
        return Optional.of(new Vehicle(jCas));
      case "Weapon":
        return Optional.of(new Weapon(jCas));

      default:
        return Optional.empty();
    }
  }

  @Override
  protected void doClose() throws IOException {
    documents.clear();
  }

  @Override
  public boolean doHasNext() throws IOException, CollectionException {
    return !documents.isEmpty();
  }
}
