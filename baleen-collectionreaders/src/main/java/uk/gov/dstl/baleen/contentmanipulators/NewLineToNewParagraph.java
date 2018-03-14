// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmanipulators;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import uk.gov.dstl.baleen.contentmanipulators.helpers.ContentManipulator;

/**
 * Converts BR tags into new paragraphs.
 *
 * <p>If BR tags exist in a paragraph or other tag we probably want Baleen to process these are as
 * separate block of text. This manipulator uses BR tags to introduce new paragraphs.
 *
 * <p>If the BR tag occurs within a paragraph, then the paragraph is split into multiple sub
 * paragraphs. If the br occurs elsewhere (eg in a td or li) then the a set of paragraphs are
 * introduced into the element.
 */
public class NewLineToNewParagraph implements ContentManipulator {

  @Override
  public void manipulate(Document document) {

    // Find elements which need to be spilt up
    Set<Element> elementsWithBr = new HashSet<>();
    document.select("br").forEach(e -> elementsWithBr.add(e.parent()));

    // For each parent
    elementsWithBr.forEach(
        e -> {
          List<Element> runs = collectRuns(document, e);
          if (!runs.isEmpty()) {
            addRunsToDom(e, runs);
          }
        });
  }

  /**
   * Collect tags which are on the same line (unbroken by BRs)
   *
   * @param document the document
   * @param e the e
   * @return the list
   */
  private List<Element> collectRuns(Document document, Element e) {
    List<Element> runs = new LinkedList<>();
    Element run = null;
    for (Node c : e.childNodesCopy()) {

      if (c instanceof Element && ("br".equalsIgnoreCase(((Element) c).tagName()))) {
        // If we hit a br then add the old run and start a new one
        if (run != null) {
          runs.add(run);
          run = null;
        }
      } else {
        // If not a br then add this node to the other
        if (run == null) {
          run = document.createElement("p");
        }
        run.appendChild(c);
      }
    }

    // Add the last run
    if (run != null) {
      runs.add(run);
    }

    return runs;
  }

  /**
   * Adds each new line (a run) to the documnet as a paragraph.
   *
   * @param e the element at which to add the runs.
   * @param runs the runs
   */
  private void addRunsToDom(Element e, List<Element> runs) {
    // Add these new spans into the DOM
    if ("p".equalsIgnoreCase(e.tagName())) {
      // If this is a p, then just add below it
      // reverse order so the first element of runs ends up closest to p as it should be
      Collections.reverse(runs);
      runs.forEach(e::after);
      // Delete the old paragraph
      e.remove();
    } else {
      // If we aren't in a p (eg in a li) then lets add paragraphs to this element
      // But first clear it out
      e.children().remove();
      runs.forEach(e::appendChild);
    }
  }
}
