package uk.gov.dstl.baleen.mallet;

import java.util.Iterator;

import org.bson.Document;

import com.mongodb.client.FindIterable;

/** Utility to wrap Mongo output so it implements Iterable. */
public class MongoIterable implements Iterable<Document> {

  private final FindIterable<Document> find;

  /**
   * Wrap the FindIterable so it implements Iterable.
   *
   * @param find
   */
  public MongoIterable(FindIterable<Document> find) {
    this.find = find;
  }

  @Override
  public Iterator<Document> iterator() {
    return find.iterator();
  }
}
