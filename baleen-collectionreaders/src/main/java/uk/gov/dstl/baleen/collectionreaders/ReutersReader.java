// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.gov.dstl.baleen.collectionreaders.helpers.AbstractStreamCollectionReader;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * A collection reader which loads SGM files from the Reuters21578 archive. Archive must be
 * extracted prior to use.
 *
 * <p>Available for download at http://www.daviddlewis.com/resources/testcollections/reuters21578/
 *
 * @baleen.javadoc
 */
public class ReutersReader extends AbstractStreamCollectionReader<String> {

  /**
   * Location of the directory containing the sgm files.
   *
   * @baleen.resource String
   */
  public static final String KEY_PATH = "path";

  @ConfigurationParameter(name = KEY_PATH, mandatory = true)
  private String sgmPath;

  public ReutersReader() {
    // Do nothing
  }

  @Override
  protected Stream<String> initializeStream(UimaContext context) throws BaleenException {
    final File[] files =
        new File(sgmPath).listFiles(f -> f.getName().endsWith(".sgm") && f.isFile());

    DocumentBuilder documentBuilder;
    try {
      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      documentBuilder = factory.newDocumentBuilder();
    } catch (final Exception e) {
      throw new BaleenException(e);
    }

    return Arrays.stream(files)
        .flatMap(sgmlFile -> fileToStream(sgmlFile, documentBuilder))
        .flatMap(e -> nodeListToText(e.getElementsByTagName("BODY")))
        .filter(s -> !s.isEmpty());
  }

  private Stream<Element> fileToStream(File sgmlFile, DocumentBuilder documentBuilder) {
    try {
      final byte[] bytes = Files.readAllBytes(sgmlFile.toPath());
      final String sgml = new String(bytes, "UTF-8");

      // Remove the <!DOCTYPE lewis SYSTEM "lewis.dtd">
      // Then add a root element
      String xml =
          "<root>" + sgml.substring("<!DOCTYPE lewis SYSTEM \"lewis.dtd\">".length()) + "</root>";

      // Remove the
      xml = xml.replaceAll("&#\\d+;", "");

      final ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));
      final Document doc = documentBuilder.parse(input);
      final NodeList reutersDocument = doc.getElementsByTagName("REUTERS");
      return nodeListToElements(reutersDocument);
    } catch (final Exception e) {
      getMonitor().warn("Unable to process SGML file {}", sgmlFile.getAbsolutePath(), e);
    }

    return Stream.<Element>empty();
  }

  private Stream<String> nodeListToText(final NodeList list) {
    final List<String> elements = new ArrayList<>(list.getLength());

    for (int i = 0; i < list.getLength(); i++) {
      final Node n = list.item(i);
      String text = n.getTextContent();
      text = text.replaceAll("Reuter?\\s*$", "");
      elements.add(text.trim());
    }

    return elements.stream();
  }

  private Stream<Element> nodeListToElements(final NodeList list) {
    final List<Element> elements = new ArrayList<>(list.getLength());

    for (int i = 0; i < list.getLength(); i++) {
      final Node n = list.item(i);
      if (n.getNodeType() == Element.ELEMENT_NODE) {
        elements.add((Element) n);
      }
    }

    return elements.stream();
  }

  @Override
  protected void apply(String text, JCas jCas) {
    jCas.setDocumentLanguage("en");
    jCas.setDocumentText(text);
  }

  @Override
  protected void doClose() throws IOException {
    // Do nothing
  }
}
