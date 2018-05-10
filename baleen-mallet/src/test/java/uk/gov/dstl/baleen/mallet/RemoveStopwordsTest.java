// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.mallet;

import static org.junit.Assert.assertEquals;

import java.util.stream.Collectors;

import org.junit.Test;

import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class RemoveStopwordsTest {

  @Test
  public void testStopwordsAreRemoved() {
    String stop = "stop";
    String word = "word";
    String white = "white";
    String list = "list";

    TokenSequence data =
        new TokenSequence(
            ImmutableList.of(new Token(stop), new Token(word), new Token(white), new Token(list)));
    Instance instance = new Instance(data, null, null, null);

    RemoveStopwords stopwords = new RemoveStopwords(ImmutableList.of(stop, word));
    Instance output = stopwords.pipe(instance);

    TokenSequence ts = (TokenSequence) output.getData();
    assertEquals(2, ts.size());
    assertEquals(
        ImmutableSet.of(white, list), ts.stream().map(Token::getText).collect(Collectors.toSet()));
  }
}
