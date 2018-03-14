// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmappers.helpers;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import uk.gov.dstl.baleen.types.structure.Structure;

/**
 * Holds and constructs text and annotations whilst the HTML is being converted to JCas.
 *
 * <p>Note that build should only be called once (as it sets the text of the jCas).
 */
public class JCasBuilder {
  private List<Annotation> annotations = new LinkedList<>();
  private StringBuilder documentText = new StringBuilder();
  private JCas jCas;

  /**
   * Instantiates a new builder.
   *
   * @param jCas the jCas
   */
  public JCasBuilder(JCas jCas) {
    this.jCas = jCas;
  }

  /**
   * Gets the jcas.
   *
   * @return the jcas
   */
  public JCas getJCas() {
    return jCas;
  }

  /**
   * Gets the current offset within the underconstruction text buffer.
   *
   * @return the current offset
   */
  public int getCurrentOffset() {
    return documentText.length();
  }

  /**
   * Adds text.
   *
   * @param text the text
   */
  public void addText(String text) {
    documentText.append(text);
  }

  /**
   * Adds annotations.
   *
   * @param collection the collection
   * @param begin the begin
   * @param end the end
   * @param depth the depth (within the tags)
   */
  public void addAnnotations(Collection<Annotation> collection, int begin, int end, int depth) {
    collection.forEach(
        a -> {
          a.setBegin(begin);
          a.setEnd(end);
          if (a instanceof Structure) {
            ((Structure) a).setDepth(depth);
          }
          annotations.add(a);
        });
  }

  /**
   * Apply the text and annotations to the jCas.
   *
   * <p>Once call once.
   */
  public void build() {
    jCas.setDocumentText(documentText.toString());
    for (Annotation a : annotations) {
      a.addToIndexes(jCas);
    }
  }
}
