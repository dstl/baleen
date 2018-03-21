// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.collectionreaders.re3d;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Streams;

import uk.gov.dstl.baleen.collectionreaders.testing.AbstractReaderTest;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;

public class Re3dReaderTest extends AbstractReaderTest {

  public Re3dReaderTest() {
    super(Re3dReader.class);
  }

  private static Path tmpDir;

  private static final String SOURCE_URL = "http://www.url.co.uk/123456";
  private static final String RE3D_DOCUMENT =
      "{ \"_id\" : \"DC40EB7B4BDB1\", \"sourceName\" : \"News Online\", \"sourceUrl\" : \""
          + SOURCE_URL
          + "\", \"wordCount\" : 40, \"sentenceCount\" : 2, \"title\" : \"Title of the article\", \"text\" : \"This is the text of the article.\\nIt may be multilined and contain Entities.\" }\n";

  private static final String RE3D_ENTITY =
      "{ \"_id\" : \"DC40EB7B4BDB1-0-100-118-Location\", \"begin\" : 100, \"end\" : 118, \"type\" : \"Location\", \"value\" : \"Location\", \"documentId\" : \"DC40EB7B4BDB1\", \"confidence\" : 1 }\n"
          + "{ \"_id\" : \"DC40EB7B4BDB1-0-12-18-Organisation\", \"begin\" : 12, \"end\" : 18, \"type\" : \"Organisation\", \"value\" : \"Organisation Ltd\", \"documentId\" : \"DC40EB7B4BDB1\", \"confidence\" : 1 }\n"
          + "  { \"_id\" : \"DC40EB7B4BDB1-0-9-11-Quantity\", \"begin\" : 9, \"end\" : 11, \"type\" : \"Quantity\", \"value\" : \"42\", \"documentId\" : \"DC40EB7B4BDB1\", \"confidence\" : 1 }";

  private static final String RE3D_RELATION =
      "{ \"_id\" : \"DC40EB7B4BDB1254DAF8A430A8E5383B-0-12-18-9-11-HasAttrOf\", \"begin\" : 11, \"end\" : 12, \"sourceBegin\" : 12, \"sourceEnd\" : 18, \"source\" : \"people\", \"targetBegin\" : 9, \"targetEnd\" : 11, \"target\" : \"43\", \"type\" : \"HasAttrOf\", \"value\" : \"\", \"documentId\" : \"DC40EB7B4BDB1254DAF8A430A8E5383B\", \"confidence\" : 1 }";

  @BeforeClass
  public static void beforeClass() throws IOException {
    tmpDir = Files.createTempDirectory("r3dtest");
    Path subDir = Files.createDirectory(tmpDir.resolve("subdir"));
    Files.write(subDir.resolve("documents.json"), RE3D_DOCUMENT.getBytes(StandardCharsets.UTF_8));
    Files.write(subDir.resolve("entities.json"), RE3D_ENTITY.getBytes(StandardCharsets.UTF_8));
    Files.write(subDir.resolve("relations.json"), RE3D_RELATION.getBytes(StandardCharsets.UTF_8));
  }

  @AfterClass
  public static void afterClass() {
    tmpDir.toFile().delete();
  }

  @Test
  public void testDocumentText() throws Exception {
    BaleenCollectionReader bcr =
        getCollectionReader(
            Re3dReader.PARAM_FOLDER,
            tmpDir.toAbsolutePath().toString(),
            Re3dReader.PARAM_ENTITIES,
            true,
            Re3dReader.PARAM_RANDOM_DATES,
            true);

    assertTrue(bcr.doHasNext());
    bcr.getNext(jCas.getCas());

    assertTrue(jCas.getDocumentText().contains("This is the text of the article."));

    bcr.close();
  }

  @Test
  public void testDocumentMetadata() throws Exception {
    BaleenCollectionReader bcr =
        getCollectionReader(
            Re3dReader.PARAM_FOLDER,
            tmpDir.toAbsolutePath().toString(),
            Re3dReader.PARAM_ENTITIES,
            true,
            Re3dReader.PARAM_RANDOM_DATES,
            true);

    assertTrue(bcr.doHasNext());
    bcr.getNext(jCas.getCas());

    AnnotationIndex<DocumentAnnotation> annotationIndex =
        jCas.getAnnotationIndex(DocumentAnnotation.class);
    assertTrue(contains(annotationIndex, d -> d.getSourceUri().equals(SOURCE_URL)));

    bcr.close();
  }

  @Test
  public void testEntities() throws Exception {
    BaleenCollectionReader bcr =
        getCollectionReader(
            Re3dReader.PARAM_FOLDER,
            tmpDir.toAbsolutePath().toString(),
            Re3dReader.PARAM_ENTITIES,
            true,
            Re3dReader.PARAM_RANDOM_DATES,
            true);

    assertTrue(bcr.doHasNext());
    bcr.getNext(jCas.getCas());

    AnnotationIndex<Entity> annotationIndex = jCas.getAnnotationIndex(Entity.class);

    contains(annotationIndex, e -> e.getValue().equals("Location"));
    contains(annotationIndex, e -> e.getBegin() == 100);
    contains(annotationIndex, e -> e.getEnd() == 118);
    contains(annotationIndex, e -> e.getValue().equals("Organisation Ltd"));
    contains(annotationIndex, e -> e.getBegin() == 12);
    contains(annotationIndex, e -> e.getEnd() == 18);

    bcr.close();
  }

  @Test
  public void testRelations() throws Exception {
    BaleenCollectionReader bcr =
        getCollectionReader(
            Re3dReader.PARAM_FOLDER,
            tmpDir.toAbsolutePath().toString(),
            Re3dReader.PARAM_ENTITIES,
            true,
            Re3dReader.PARAM_RELATIONSHIPS,
            true,
            Re3dReader.PARAM_RANDOM_DATES,
            true);

    assertTrue(bcr.doHasNext());
    bcr.getNext(jCas.getCas());

    AnnotationIndex<Entity> entityIndex = jCas.getAnnotationIndex(Entity.class);

    Entity source = Streams.stream(entityIndex).filter(e -> e.getBegin() == 12).findAny().get();
    Entity target = Streams.stream(entityIndex).filter(e -> e.getBegin() == 9).findAny().get();

    AnnotationIndex<Relation> relationIndex = jCas.getAnnotationIndex(Relation.class);

    contains(relationIndex, r -> r.getBegin() == 11);
    contains(relationIndex, r -> r.getEnd() == 12);
    contains(relationIndex, r -> r.getValue().equals(""));
    contains(relationIndex, r -> r.getRelationshipType().equals("HasAttrOf"));
    contains(relationIndex, r -> r.getSource().equals(source));
    contains(relationIndex, r -> r.getTarget().equals(target));

    bcr.close();
  }

  private <T extends AnnotationFS> boolean contains(
      AnnotationIndex<T> index, Predicate<? super T> predicate) {
    return Streams.stream(index).anyMatch(predicate);
  }
}
