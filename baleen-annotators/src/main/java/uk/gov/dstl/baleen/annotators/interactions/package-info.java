/**
 * This package contains annotators for use with interactions.
 *
 * <p>An interaction is also a Baleen type. It is used to annotate a word or phrase in a document
 * which specifies an potential interaction word. In the example above, "lives" would be highlighted
 * as an annotator. Typically interaction words will be specified from say a gazetteer, or in the
 * most general case be derived from say the sentence structure (for example a verb).
 *
 * <p>For usage (to extract relationships) involves a pipelines which will contain interaction word
 * markup, relationship extraction and likely clean up of relationships, in addition to entity
 * extraction (needed for relationship extraction) and any other components:
 *
 * <ul>
 *   <li>Interaction matchup will use the previously recorded interaction words to annotate instance
 *       within the processed document.
 *   <li>The relationship extraction algorithm will used and ties entities to each other based on
 *       interaction words connecting them (for some algorithm specific definition of connection).
 *   <li>In the optional clean up annotators, multiple relationships are merged, relationships
 *       between invalid types are removed.
 *   <li>The relationships, and other information, are persisted to the database.
 * </ul>
 *
 * Only the first point is considered here.
 *
 * <p>We assume that interaction words have been processed, and uploaded to Mongo, @see
 * uk.gov.dstl.baleen.annotators.patterns for details.
 *
 * <p>To annotate interaction words in a document, we use a MongoStemming gazetteer. Stemming is
 * useful so we match "jumping" to our "jump" relations.
 *
 * <p>As the gazetteer is not aware of entities or existing annotations it might annotate a
 * interaction inside an entity. For example "Jumping Jack Flash" might be a Person but "Jumping"
 * will be annotated as an interaction. We discard these with the RemoveInteractionInEntities
 * annotator/cleaner.
 *
 * <p>Finally we note that the gazetteer only marks up the interaction, but does not add additional
 * detail. (Gazetteers are unabel to cope with the case that two different entries have the same
 * word to annotate). We need to carefully mark interactions by their type based on the part of
 * speech. The AssignTypeToInteraction adds and removes intreactions, updating them with extra
 * information such as type, based on the part of speech they have annotated.
 *
 * <p>Thus the final pipeline becomes:
 *
 * <pre>
 * - class: gazetteer.MongoStemming
 *   collection: interactions
 *   type: Interaction
 * - interactions.RemoveInteractionInEntities
 * - interactions.AssignTypeToInteraction
 * </pre>
 *
 * At this stage you can run say relation extraction.
 */
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.interactions;
