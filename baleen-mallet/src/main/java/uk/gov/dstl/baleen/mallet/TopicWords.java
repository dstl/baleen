// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.mallet;

import java.util.List;
import java.util.stream.Collectors;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.IDSorter;

/** Topic words factory for the given model */
public class TopicWords {

  private final ParallelTopicModel model;

  /**
   * Create Topic word factory for the given model
   *
   * @param model
   */
  public TopicWords(ParallelTopicModel model) {
    this.model = model;
  }

  /**
   * @param topic
   * @return key words for topic
   */
  public List<String> forTopic(int topic) {
    return forTopic(topic, 10);
  }

  /**
   * @param topic
   * @param number of keywords required
   * @return key words for topic
   */
  public List<String> forTopic(int topic, int number) {
    return model
        .getSortedWords()
        .get(topic)
        .stream()
        .map(IDSorter::getID)
        .map(model.getAlphabet()::lookupObject)
        .map(Object::toString)
        .limit(number)
        .collect(Collectors.toList());
  }
}
