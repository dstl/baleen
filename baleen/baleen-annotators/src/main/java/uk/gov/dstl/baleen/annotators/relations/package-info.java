/**
 * This package contains annotators which help process interaction to relations.
 *
 * A relationship is defined by the Baleen type system. A relationship as both a Baleen annotation
 * and concept is a linkage between two entities which appear in a document. Relationships have a
 * main type and a subtype, though Baleen is not perscriptive of the meaning of these allows for
 * domain / corpus interpretation of the level of granularity required. For example, the sentence
 * "John lives in London" has highlights a relationship between John and London, the type of
 * location should be "located" and the subtype "lives".
 *
 * <h2>Interactions</h2>
 * 
 * A subset of the classes in this package are based on the idea of interactions and extracting relationships
 * that contain an 'interaction' word. For more information on this, see {@link uk.gov.dstl.baleen.annotators.interactions}
 * and {@link uk.gov.dstl.baleen.annotators.patterns}.
 *
 * Assuming that interaction words have been identified they must first be annotated, generally
 * using a MongoStemming gazetteer.
 *
 * Then we need to add one or more interaction based annotators. These may be derived from
 * {@link uk.gov.dstl.baleen.annotators.relations.helpers.AbstractInteractionBasedRelationshipAnnotator}
 * or
 * {@link uk.gov.dstl.baleen.annotators.relations.helpers.AbstractInteractionBasedSentenceRelationshipAnnotator}
 * . For this example we will use the
 * {@link uk.gov.dstl.baleen.annotators.relations.SimpleInteraction} which is a
 * toy example and should not be used in a production pipeline!
 *
 * Relationship extraction associates entities and as such should occur after Entity extraction
 *
 * <pre>
 * annotators:
 * - # Entity extraction and cleaners
 * - # Interaction markup
 * - relations.SimpleInteraction
 * - cleaners.NaiveMergeRelations
 * - cleaners.RelationTypeFilter
 * </pre>
 *
 * Interaction based relationship extraction may generate a lot of erroneous relations. The relation
 * may be valid, but not between the two particularly entry types. For this reason most pipelines
 * will wish to include the CleanRelations (to remove duplicate relations) and the
 * FilterRelationType (to remove based relations where they link entities of invalid types).
 *
 * Note that to use the RelationTypeFilter you require a Mongo resource.
 *
 * Finally you may want to output relations, see
 * {@link uk.gov.dstl.baleen.consumers.print.Relations} to print to console. Or the Mongo
 * consumer will also saves relation information.
 *
 *
 * <h3>Building your own extractor</h3>
 *
 * To build a relationship extractor based on interaction processes a helper class is available
 * through the AbstractInteractionRelationshipExtractor. The abstract class provides numerous helper
 * functions, common to the needs of relationships extractors. It also simplifies the doProcess
 * annotator by performing common processing and offering a per sentence processing instead.
 * 
 * <h2>UBMRE Relation Extractors</h2>
 * 
 * Relationship extraction based on the paper,
 * "An Unsupervised Text Mining Method for Relation Extraction from Biomedical Literature" available
 * from http://journals.plos.org/plosone/article?id=10.1371/journal.pone.0102039.
 *
 * Whilst the paper considers a combined approach our implementation is divided into two separate
 * annotators, and can be used independently or togeher in the same pipeline.
 *
 * Both annotators are based on the the use of grammatical parsing constructions to inform
 * relationship extraction. One algorithm uses constituent parsing (available through then
 * OpenNlpParser) and the other uses dependency grammar parsing (available through the MaltParser or
 * ClearNlp annotator). The annotators use the location of entities and interaction words within the
 * grammatical structure of the sentence to infer relations against a set of rules.
 *
 * Prior to use, the document must have been marked with entities, standard information (sentences,
 * wordtokens), interactions and grammatical information.
 *
 * Thus a typical pipeline will look like:
 *
 * <pre>
 * annotators:
 * - # Standard entity extraction pipeline first
 * # Perform grammar parking
 * - language.WordNetLemmatizer
 * - language.OpenNLPParser
 * - language.MaltParser
 * # Mark up the interaction words
 * - class: gazetteer.MongoStemming
 *   collection: interactions
 *   type: Interaction
 * - interactions.RemoveInteractionInEntities
 * - interactions.AssignTypeToInteraction
 * # Clean up entities prior to relations
 * - cleaners.RemoveOverlappingEntities
 * # Perform relation extraction
 * - relations.UbmreDependencyRelationship
 * - relations.UbmreConstituentRelationship
 * # Clean up relations
 * - cleaners.NaiveMergeRelations
 * - cleaners.RelationTypeFilter
 * </pre>
 *
 */
package uk.gov.dstl.baleen.annotators.relations;