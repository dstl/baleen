// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.jobs.patterns.interactions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.POS;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.gov.dstl.baleen.jobs.interactions.data.InteractionWord;
import uk.gov.dstl.baleen.jobs.interactions.data.PatternReference;
import uk.gov.dstl.baleen.jobs.interactions.data.Word;
import uk.gov.dstl.baleen.jobs.interactions.impl.InteractionIdentifier;
import uk.gov.dstl.baleen.uima.UimaMonitor;

@RunWith(MockitoJUnitRunner.Silent.class)
public class InteractionIdentifierTest {

  @Mock UimaMonitor monitor;

  private InteractionIdentifier identifier;

  @Before
  public void before() throws JWNLException {
    identifier = new InteractionIdentifier(monitor, 1, 2, 0.2);
  }

  @Test
  public void testProcess() {
    // Note in this test we are using non-lemma versions of the words (hence jumps / jumped are
    // different)
    List<PatternReference> patterns =
        Arrays.asList(
            new PatternReference("1", new Word("jumps", POS.VERB)),
            new PatternReference("13", new Word("jumped", POS.VERB)),
            new PatternReference("2", new Word("springs", POS.VERB)),
            new PatternReference("3", new Word("leaps", POS.VERB)),
            new PatternReference("4", new Word("brother", POS.NOUN)),
            new PatternReference("11", new Word("brother", POS.NOUN), new Word("law", POS.NOUN)),
            new PatternReference(
                "12",
                new Word("step", POS.NOUN),
                new Word("brother", POS.NOUN),
                new Word("law", POS.NOUN)),
            new PatternReference("5", new Word("sister", POS.NOUN)),
            new PatternReference("6", new Word("sibling", POS.NOUN)),
            new PatternReference("7", new Word("sister", POS.NOUN), new Word("law", POS.NOUN)),
            new PatternReference("8", new Word("step", POS.NOUN), new Word("mother", POS.NOUN)),
            new PatternReference("9", new Word("mother", POS.NOUN)),
            new PatternReference(
                "10",
                new Word("was", POS.VERB),
                new Word("penalised", POS.VERB),
                new Word("extent", POS.NOUN),
                new Word("law", POS.NOUN)));

    Stream<InteractionWord> words = identifier.process(patterns);

    List<String> list = words.map(w -> w.getWord().getLemma()).collect(Collectors.toList());
    // Only mother, brother and law appear often enough to be consider interaction words
    assertTrue(list.contains("mother"));
    assertTrue(list.contains("law"));
    assertTrue(list.contains("brother"));
    assertEquals(3, list.size());
  }
}
