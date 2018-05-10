// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import uk.gov.dstl.baleen.types.BaleenAnnotation;
import uk.gov.dstl.baleen.uima.BaleenResource;
import uk.gov.dstl.baleen.uima.UimaSupport;

/**
 * Generates a globally unique id for each Baleen external id.
 *
 * <p>The id is a string version of the UUID.
 *
 * <p>This class retains a mapping from externalId to UUID so that it will repeatedly return the
 * same UUID for the same external id.
 *
 * <p>If used repeatedly (for different documents) call resetIfNewJCas to remove old externalId
 * (unless you want some form of cross document connection). This is based on the document hash.
 */
public class SharedIdGenerator extends BaleenResource {

  /** default key for accessing the mongo resource */
  public static final String RESOURCE_KEY = "idGenerator";

  private final Map<String, String> ids = new HashMap<>();

  private String currentDocumentId = null;

  /**
   * Reset id generation if new JCas.
   *
   * @param jCas the j cas
   * @return true, if reset
   */
  public boolean resetIfNewJCas(final JCas jCas) {
    final DocumentAnnotation documentAnnotation = UimaSupport.getDocumentAnnotation(jCas);

    final String documentId = documentAnnotation.getHash();

    final boolean isNewDocument =
        currentDocumentId == null || !currentDocumentId.equals(documentId);
    if (isNewDocument) {
      getMonitor().debug("Reset id cache for document {}", documentId);
      clearMappings();

      currentDocumentId = documentId;
    }

    return isNewDocument;
  }

  private void clearMappings() {
    ids.clear();
  }

  protected String generate() {
    return UUID.randomUUID().toString();
  }

  /**
   * Generate (and save) an UUID for the annotation (based on its externalId)
   *
   * @param annotation
   * @return
   */
  public String generateForAnnotation(final BaleenAnnotation annotation) {
    final String externalId = annotation.getExternalId();
    return generateForExternalId(externalId);
  }

  /**
   * Generate (and save) as UUID for the externalId (could be document, entity, etc)
   *
   * @param externalId
   * @return
   */
  public String generateForExternalId(final String externalId) {
    return findByExternalId(externalId)
        .orElseGet(
            () -> {
              final String id = generate();
              addMapping(externalId, id);
              return id;
            });
  }

  protected Optional<String> findByExternalId(final String externalId) {
    final String id = ids.get(externalId);
    return Optional.ofNullable(id);
  }

  protected void addMapping(final String externalId, final String id) {

    if (ids.containsKey(externalId)) {
      getMonitor().warn("Duplicate external id {} added, previous will be discarded", externalId);
    }

    ids.put(externalId, id);
  }
}
