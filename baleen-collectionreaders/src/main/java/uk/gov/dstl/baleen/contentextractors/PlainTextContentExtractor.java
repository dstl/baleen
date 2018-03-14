// Dstl (c) Crown Copyright 2017
// Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentextractors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.contentextractors.helpers.AbstractContentExtractor;

/** Extracts the content assuming it is plain text */
public class PlainTextContentExtractor extends AbstractContentExtractor {
  @Override
  public void doProcessStream(InputStream stream, String source, JCas jCas) throws IOException {
    super.doProcessStream(stream, source, jCas);

    String lines =
        new BufferedReader(new InputStreamReader(stream)).lines().collect(Collectors.joining("\n"));
    jCas.setDocumentText(lines);
  }
}
