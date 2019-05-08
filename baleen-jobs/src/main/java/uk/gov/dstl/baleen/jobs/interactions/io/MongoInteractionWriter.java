// Dstl (c) Crown Copyright 2017
/*
 *
 */
package uk.gov.dstl.baleen.jobs.interactions.io;

import java.util.Collection;

import org.bson.Document;

import com.mongodb.Mongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import uk.gov.dstl.baleen.jobs.interactions.UploadInteractionsToMongo;
import uk.gov.dstl.baleen.jobs.interactions.data.InteractionDefinition;

/**
 * Write interaction data to Mongo database.
 *
 * <p>This will output interaction data into two different collection for different uses by other
 * annotators.
 *
 * <p>It first outputs interaction words into Mongo gazetteer format (see {@link Mongo} for more
 * details). This allows the standard Baleen Mongo gazetteer annotators to mark up Interaction
 * words.
 *
 * <p>Secondly it saves information about relationship type constraints to the
 * relationTypeCollection. This is used by the {@link
 * uk.gov.dstl.baleen.annotators.cleaners.RelationTypeFilter} in order to remove any invalid
 * relationships between types. The relation (DateTime, said, Location) is likely to be invalid for
 * example. Valid options is derived directly from the
 *
 * <p>See {@link UploadInteractionsToMongo} for more details.
 */
public class MongoInteractionWriter implements InteractionWriter {

  /** The relation types. */
  private final MongoCollection<Document> relationTypes;

  /** The interactions. */
  private final MongoCollection<Document> interactions;

  /**
   * Instantiates a new instance.
   *
   * @param db the db the write to
   * @param relationTypesCollection the relation types collection name
   * @param interactionCollection the interaction collection name
   */
  public MongoInteractionWriter(
      MongoDatabase db, String relationTypesCollection, String interactionCollection) {
    interactions = db.getCollection(interactionCollection);
    relationTypes = db.getCollection(relationTypesCollection);
  }

  @Override
  public void write(InteractionDefinition interaction, Collection<String> alternatives) {

    // Write to the interactions collection
    // Add in relationshiptype and subtype (which can be manually changed later)
    final Document interactionObject =
        new Document()
            .append("relationshipType", interaction.getType())
            .append("relationSubType", interaction.getSubType())
            .append("value", alternatives);

    interactions.insertOne(interactionObject);

    // Write out to the relationship constraints
    final Document relationTypeObject =
        new Document()
            .append("source", interaction.getSource())
            .append("target", interaction.getTarget())
            .append("type", interaction.getType())
            .append("subType", interaction.getSubType())
            .append("pos", interaction.getWord().getPos().getLabel())
            .append("value", alternatives);

    relationTypes.insertOne(relationTypeObject);
  }

  /** Clear all collections. */
  public void clear() {
    interactions.deleteMany(new Document());
    relationTypes.deleteMany(new Document());
  }
}
