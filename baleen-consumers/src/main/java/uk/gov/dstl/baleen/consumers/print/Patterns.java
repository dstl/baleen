// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.print;

import uk.gov.dstl.baleen.types.language.Pattern;

/** Print out all patterns. */
public class Patterns extends AbstractPrintConsumer<Pattern> {

  /** Instantiates a new consumer. */
  public Patterns() {
    super(Pattern.class);
  }

  @Override
  protected String print(Pattern t) {
    final StringBuilder sb = new StringBuilder();

    writeLine(sb, "Text", t.getCoveredText());
    writeLine(sb, "Words", t.getWords());

    return sb.toString();
  }
}
