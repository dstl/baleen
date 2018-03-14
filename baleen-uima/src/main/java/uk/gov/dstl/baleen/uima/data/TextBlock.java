// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.data;

import java.util.Collection;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import uk.gov.dstl.baleen.exceptions.BaleenRuntimeException;
import uk.gov.dstl.baleen.types.language.Text;

/** The Class TextBlock. */
public class TextBlock {

  private final JCas jCas;

  private final Text text;

  private final int blockBegin;

  private final int blockEnd;

  /**
   * Instantiates a new text block which represents a text annotation
   *
   * @param jCas the jCas
   * @param text the text
   */
  public TextBlock(final JCas jCas, final Text text) {
    this.jCas = jCas;
    this.text = text;
    this.blockBegin = text.getBegin();
    this.blockEnd = text.getEnd();
  }

  /**
   * Instantiates a new text block which represents the entire JCas
   *
   * @param jCas the jCas
   */
  public TextBlock(final JCas jCas) {
    this.jCas = jCas;
    this.blockBegin = 0;
    this.blockEnd = jCas.getDocumentText().length();
    this.text = null;
  }

  /**
   * Checks if is whole document (ie the JCas vs a Text annotation).
   *
   * <p>Note that if a text annotation covers the entire document this will still be true.
   *
   * @return true, if is whole document
   */
  public boolean isWholeDocument() {
    return text == null
        || (text.getBegin() == 0 && text.getEnd() == jCas.getDocumentText().length());
  }

  /**
   * Gets the text annotation.
   *
   * @return the text (null if this is a JCas)
   */
  public Text getText() {
    return text;
  }

  /**
   * Gets the jCas.
   *
   * @return the jCas
   */
  public JCas getJCas() {
    return jCas;
  }

  /**
   * Gets the begin offset.
   *
   * @return the begin (0 if whole document)
   */
  public int getBegin() {
    return blockBegin;
  }

  /**
   * Gets the end.
   *
   * @return the end (jCas.getDocumentText().length() if whole document)
   */
  public int getEnd() {
    return blockEnd;
  }

  /**
   * Gets the covered text.
   *
   * @return the covered text (will be the same as getDocumentText if this is JCas)
   */
  public String getCoveredText() {
    if (isWholeDocument()) {
      return jCas.getDocumentText();
    } else {
      return text.getCoveredText();
    }
  }

  /**
   * Gets the JCas document text.
   *
   * @return the document text
   */
  public String getDocumentText() {
    return jCas.getDocumentText();
  }

  // JCasUtil helpers

  /**
   * Helper function providing same functionality as JCasUtil.select
   *
   * @param <T> the generic type
   * @param type the type
   * @return the collection
   */
  public <T extends Annotation> Collection<T> select(final Class<T> type) {
    if (isWholeDocument()) {
      return JCasUtil.select(jCas, type);
    } else {
      return JCasUtil.selectCovered(jCas, type, getBegin(), getEnd());
    }
  }

  // Creating annotation helpers

  /**
   * Create a new annotation, correcting the being&end to be the document offset rather than within
   * this text block.
   *
   * <p>Note this uses reflection, so may not be as performant as simply new Type().
   *
   * @param <T> the generic type
   * @param type the type
   * @param begin the begin offset within this text block
   * @param end the end offset within this text block
   * @return the annotation
   */
  public <T extends Annotation> T newAnnotation(
      final Class<T> type, final int begin, final int end) {

    try {
      return type.getConstructor(JCas.class, int.class, int.class)
          .newInstance(jCas, toDocumentOffset(begin), toDocumentOffset(end));
    } catch (final Exception e) {
      throw new BaleenRuntimeException("Required type not found", e);
    }
  }

  /**
   * Sets the begin and end of the annotation against the document (rather than this block)
   *
   * @param <T> the generic type
   * @param annotation the annotation
   * @param begin the begin offset within this text block
   * @param end the end offset within this text block
   * @return the annotaiton (with begin and end set to the document offsets)
   */
  public <T extends Annotation> T setBeginAndEnd(
      final T annotation, final int begin, final int end) {
    annotation.setBegin(toDocumentOffset(begin));
    annotation.setEnd(toDocumentOffset(end));
    return annotation;
  }

  /**
   * Convert an offset within this text span to a document offset.
   *
   * @param blockOffset the block offset
   * @return the document offset
   */
  public int toDocumentOffset(final int blockOffset) {
    return blockOffset + getBegin();
  }

  /**
   * Convert an offset within the document to an offset within this text span
   *
   * @param documentOffset the document offset
   * @return the block offset
   * @throws IllegalArgumentException if the documentOffset is outside of this text block
   */
  public int toBlockOffset(final int documentOffset) {
    if (documentOffset < getBegin() || documentOffset > getEnd()) {
      throw new IllegalArgumentException("documentOffset is outside block");
    }
    return documentOffset - getBegin();
  }
}
