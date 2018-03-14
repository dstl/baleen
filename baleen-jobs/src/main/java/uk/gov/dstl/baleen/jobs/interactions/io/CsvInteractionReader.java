// Dstl (c) Crown Copyright 2017

package uk.gov.dstl.baleen.jobs.interactions.io;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.StreamSupport;

import net.sf.extjwnl.data.POS;

import com.opencsv.CSVReader;

import uk.gov.dstl.baleen.jobs.interactions.data.InteractionDefinition;
import uk.gov.dstl.baleen.jobs.interactions.data.Word;

/**
 * Reads interactions from CSV.
 *
 * <p>This reads interaction data as written by the {@link CsvInteractionWriter}.
 */
public class CsvInteractionReader {

  private final String inputFilename;

  /**
   * Instantiates a new CSV interaction reader.
   *
   * @param inputFilename the input filename
   */
  public CsvInteractionReader(String inputFilename) {
    this.inputFilename = inputFilename;
  }

  /**
   * Read the CSV file and send interactions to the consumer
   *
   * @param consumer the consumer (first param is the InteractionRelation and the second the list of
   *     alternative words)
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void read(BiConsumer<InteractionDefinition, Collection<String>> consumer)
      throws IOException {
    try (CSVReader reader = new CSVReader(new FileReader(inputFilename))) {
      StreamSupport.stream(reader.spliterator(), false).forEach(r -> processRecord(r, consumer));
    }
  }

  private void processRecord(
      String[] r, BiConsumer<InteractionDefinition, Collection<String>> consumer) {
    if (r.length < 2) {
      return;
    }

    String type = r[0];
    String subType = r[1];

    if ("Type".equalsIgnoreCase(type) && "Subtype".equalsIgnoreCase(subType)) {
      // Header, ignore
      return;
    }

    String source = getOrEmpty(r, 2);
    String target = getOrEmpty(r, 3);
    String lemma = getOrEmpty(r, 4);
    POS pos = POS.getPOSForLabel(getOrEmpty(r, 5).toLowerCase());

    if (pos == null) {
      // Can't include words without a POS
      return;
    }

    InteractionDefinition i =
        new InteractionDefinition(type, subType, new Word(lemma, pos), source, target);

    List<String> alternatives = new ArrayList<>(r.length - 6);
    for (int j = 6; j < r.length; j++) {
      String alternative = r[j].trim();
      if (!alternative.isEmpty()) {
        alternatives.add(alternative);
      }
    }

    consumer.accept(i, alternatives);
  }

  private String getOrEmpty(String[] array, int index) {
    if (array.length < index) return "";

    return array[index];
  }
}
