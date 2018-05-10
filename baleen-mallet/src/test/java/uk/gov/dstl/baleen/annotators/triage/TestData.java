package uk.gov.dstl.baleen.annotators.triage;

import java.util.List;

import org.bson.Document;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.consumers.Mongo;

/** Simple test data for testing triage classifiers */
public class TestData {

  private static final String SOURCE = Mongo.FIELD_DOCUMENT_SOURCE;
  private static final String CONTENT = Mongo.FIELD_CONTENT;
  private static final String DOCUMENT = Mongo.FIELD_DOCUMENT;
  private static final String METADATA = Mongo.FIELD_METADATA;
  private static final String LABEL = "label";

  private final List<String> data;

  /** Constructor */
  public TestData() {
    data =
        ImmutableList.of(
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "pos1.txt"))
                .append(METADATA, new Document().append(LABEL, "pos"))
                .append(CONTENT, "I love this sandwich.")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "pos2.txt"))
                .append(METADATA, new Document().append(LABEL, "pos"))
                .append(CONTENT, "this is an amazing place!'")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "pos3.txt"))
                .append(METADATA, new Document().append(LABEL, "pos"))
                .append(CONTENT, "I feel very good about these beers.")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "pos4.txt"))
                .append(METADATA, new Document().append(LABEL, "pos"))
                .append(CONTENT, "this is my best work.")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "pos5.txt"))
                .append(METADATA, new Document().append(LABEL, "pos"))
                .append(CONTENT, "what an awesome view")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "pos6.txt"))
                .append(METADATA, new Document().append(LABEL, "pos"))
                .append(CONTENT, "the beer was good.")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "pos7.txt"))
                .append(METADATA, new Document().append(LABEL, "pos"))
                .append(CONTENT, "I feel amazing!")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "pos8.txt"))
                .append(METADATA, new Document().append(LABEL, "pos"))
                .append(CONTENT, "Gary is a friend of mine.")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "neg1.txt"))
                .append(METADATA, new Document().append(LABEL, "neg"))
                .append(CONTENT, "I do not like this restaurant")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "neg2.txt"))
                .append(METADATA, new Document().append(LABEL, "neg"))
                .append(CONTENT, "I am tired of this stuff.")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "neg3.txt"))
                .append(METADATA, new Document().append(LABEL, "neg"))
                .append(CONTENT, "I can't deal with this")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "neg4.txt"))
                .append(METADATA, new Document().append(LABEL, "neg"))
                .append(CONTENT, "he is my sworn enemy!")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "neg5.txt"))
                .append(METADATA, new Document().append(LABEL, "neg"))
                .append(CONTENT, "my boss is horrible.")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "neg6.txt"))
                .append(METADATA, new Document().append(LABEL, "neg"))
                .append(CONTENT, "I do not enjoy my job")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "neg7.txt"))
                .append(METADATA, new Document().append(LABEL, "neg"))
                .append(CONTENT, "I ain't feeling dandy today.")
                .toJson(),
            new Document()
                .append(DOCUMENT, new Document().append(SOURCE, "neg8.txt"))
                .append(METADATA, new Document().append(LABEL, "neg"))
                .append(CONTENT, "I can't believe I'm doing this.")
                .toJson());
  }

  /** @return a list of JSON strings */
  public List<String> asList() {
    return data;
  }
}
