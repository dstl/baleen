// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.graph.coerce.Id;
import uk.gov.dstl.baleen.graph.coerce.ValueCoercer;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * Options for the {@link DocumentGraphFactory}.
 *
 * <p>Use the {@link Builder} for construction.
 */
public class DocumentGraphOptions {

  private final boolean contentHashAsId;
  private final boolean outputMeta;
  private final boolean outputContent;
  private final boolean outputDocument;
  private final boolean outputReferents;
  private final boolean outputRelations;
  private final boolean outputRelationsAsLinks;
  private final boolean outputEvents;
  private final Set<String> stopFeatures;
  private final Set<Class<? extends Entity>> typeClasses;
  private final ValueCoercer valueCoercer;

  protected DocumentGraphOptions(
      boolean contentHashAsId,
      boolean outputMeta,
      boolean outputContent,
      boolean outputDocument,
      boolean outputReferents,
      boolean outputRelations,
      boolean outputRelationsAsLinks,
      boolean outputEvents,
      ImmutableSet<Class<? extends Entity>> typeClasses,
      Set<String> stopFeatures,
      ValueCoercer valueCoercer) {
    this.contentHashAsId = contentHashAsId;
    this.outputMeta = outputMeta;
    this.outputContent = outputContent;
    this.outputDocument = outputDocument;
    this.outputReferents = outputReferents;
    this.outputRelations = outputRelations;
    this.outputRelationsAsLinks = outputRelationsAsLinks;
    this.outputEvents = outputEvents;
    this.typeClasses = typeClasses;
    this.stopFeatures = stopFeatures;
    this.valueCoercer = valueCoercer;
  }

  /**
   * @return true if the content hash should be used for document id, the source is used otherwise.
   */
  public boolean isContentHashAsId() {
    return contentHashAsId;
  }

  /** @return true if the metadata is to be included as graph variables. */
  public boolean isOutputMeta() {
    return outputMeta;
  }

  /** @return true if the content of the document should also be added to the document node. */
  public boolean isOutputContent() {
    return outputContent;
  }

  /** @return true to include the document as a node. */
  public boolean isOutputDocument() {
    return outputDocument;
  }

  /** @return true to include the ReferenceTargets as nodes */
  public boolean isOutputReferenceTargets() {
    return outputReferents;
  }

  /** @return true to include the relations in the output. */
  public boolean isOutputRelations() {
    return outputRelations;
  }

  /**
   * @return true to shortcut included relations so they are just links from the source mention to
   *     the target mention.
   */
  public boolean isOutputRelationAsLinks() {
    return outputRelationsAsLinks;
  }

  /** @return true to include events in the output */
  public boolean isOutputEvents() {
    return outputEvents;
  }

  /** @return the features to ignore */
  public Set<String> getStopFeatures() {
    return stopFeatures;
  }

  /** @return the set of annotation classes to include as nodes. */
  public Set<Class<? extends Entity>> getTypeClasses() {
    return typeClasses;
  }

  /** @return the value coercer to use */
  public ValueCoercer getValueCoercer() {
    return valueCoercer;
  }

  /** A builder for {@link DocumentGraphOptions} */
  public static class Builder {

    private static final ValueCoercer IDENTITY = new Id();

    private boolean contentHashAsId = false;
    private boolean outputMeta = false;
    private boolean outputContent = false;
    private boolean outputDocument = false;
    private boolean outputReferents = true;
    private boolean outputRelations = true;
    private boolean outputRelationsAsLinks = false;
    private boolean outputEvents = true;
    private Set<Class<? extends Entity>> typeClasses;
    private ValueCoercer valueCoercer = IDENTITY;
    private final Set<String> stopFeatures = new HashSet<>(ConsumerUtils.getDefaultStopFeatures());

    /**
     * true to use the content hash as id, false to use the hash of the source (default false)
     *
     * @param value
     * @return this
     */
    public Builder withContentHashAsId(boolean value) {
      contentHashAsId = value;
      return this;
    }

    /**
     * true to include the document metadata (default false)
     *
     * @param value
     * @return this
     */
    public Builder withMeta(boolean value) {
      outputMeta = value;
      return this;
    }

    /**
     * true to include the content of the document (default false)
     *
     * @param value
     * @return this
     */
    public Builder withContent(boolean value) {
      outputContent = value;
      return this;
    }

    /**
     * true to include the ReferenceTarget (default true)
     *
     * @param value
     * @return this
     */
    public Builder withReferenceTargets(boolean value) {
      outputReferents = value;
      return this;
    }

    /**
     * true to include the relations (default true)
     *
     * @param value
     * @return this
     */
    public Builder withRelations(boolean value) {
      outputRelations = value;
      return this;
    }

    /**
     * true to include relations as links (default false)
     *
     * @param value
     * @return this
     */
    public Builder withRelationsAsLinks(boolean value) {
      outputRelationsAsLinks = value;
      return this;
    }

    /**
     * true to include events (default true)
     *
     * @param value
     * @return this
     */
    public Builder withEvents(boolean value) {
      outputEvents = value;
      return this;
    }

    /**
     * true to include the document node (default false)
     *
     * @param value
     * @return this
     */
    public Builder withDocument(boolean value) {
      outputDocument = value;
      return this;
    }

    /**
     * set features to be ignored. Already include defaults from {@link
     * ConsumerUtils#getDefaultStopFeatures()}.
     *
     * @param values
     * @return this
     */
    public Builder withStopFeatures(String... values) {
      stopFeatures.addAll(Arrays.asList(values));
      return this;
    }

    /**
     * set features to be ignored. Already include defaults from {@link
     * ConsumerUtils#getDefaultStopFeatures()}.
     *
     * @param values
     * @return this
     */
    public Builder withStopFeatures(Set<String> stopFeatures) {
      this.stopFeatures.addAll(stopFeatures);
      return this;
    }

    /**
     * Use the given types, defaults to all {@link Entity} types
     *
     * @param typeClasses to be included
     * @return this
     */
    public Builder withTypeClasses(Set<Class<? extends Entity>> typeClasses) {
      this.typeClasses = typeClasses;
      return this;
    }

    /**
     * Use the given value coercer, defaults to the identity function
     *
     * @param valueCoercer to use
     * @return this
     */
    public Builder withValueCoercer(ValueCoercer valueCoercer) {
      this.valueCoercer = valueCoercer;
      return this;
    }

    /** @return the build {@link DocumentGraphOptions} */
    public DocumentGraphOptions build() {
      if (typeClasses == null) {
        typeClasses = TypeUtils.getAnnotationClasses(Entity.class);
      }

      return new DocumentGraphOptions(
          contentHashAsId,
          outputMeta,
          outputContent,
          outputDocument,
          outputReferents,
          outputRelations,
          outputRelationsAsLinks,
          outputEvents,
          ImmutableSet.copyOf(typeClasses),
          ImmutableSet.copyOf(stopFeatures),
          valueCoercer);
    }
  }

  /** @return a Builder to create {@link DocumentGraphOptions}. */
  public static Builder builder() {
    return new DocumentGraphOptions.Builder();
  }
}
