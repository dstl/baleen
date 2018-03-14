// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmanipulators;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import uk.gov.dstl.baleen.contentmanipulators.helpers.ContentManipulator;

/**
 * Convert JSP101 style paragraph text to headings.
 *
 * <p>Supports only subject headings (mapped to h1) and group/main headings (both mapped to h2)
 *
 * <p>See notes in source code (or JSP101 itself) to definitions of each heading type.
 */
public class Jsp101Headings implements ContentManipulator {

  @Override
  public void manipulate(Document document) {

    // Descriptions taken from the JSP101 Chapter 6, Paragraph 23

    // Documents start with a subject heading, which helps the
    // reader know at a glance the general subject of the document. However, you may
    // omit a subject heading from a letter to a member of the public. The subject heading
    // is written in bold capitals (not underlined and not followed by a full stop).

    // => H1 if bold and in CAPS, no full stop.

    addHeading(document, true, "h1");

    // A main heading introduces 2 or more groups of paragraphs
    // relating to the same general topic. Use main headings only when the document is
    // lengthy or complicated. A main heading shows the general content as far as the
    // next main heading. Centre a main heading above the text and use bold letters (not
    // numbered, not underlined and not followed by a full stop)

    // => H2 if bold, no full stop
    addHeading(document, false, "h2");

    // A group heading introduces one or more paragraphs relating
    // to the same general topic. It shows the content as far as the next group or main
    // heading. Starting at the left margin, write a group heading in bold letters (not
    // numbered, not underlined and not followed by a full stop).

    // => h3 if bold, no full stop and centred
    // BUT can't do centred, so the same as the main heading covered above

    // A paragraph heading indicates the content only of its
    // own paragraph (including any sub-paragraphs and further subdivisions), but not of
    // any following text. Once you use a paragraph heading, give all following paragraphs
    // a heading until the next main or group heading. Put a paragraph heading on the
    // same line as the opening words of the paragraph, preceded by a paragraph number
    // if appropriate. Write a paragraph heading in bold (but not underlined), with a full
    // stop to show where the heading ends.
    // Ditto sub paragraph heading.

    // => h4 if p starts (after a para number eg 1. / a.) with a bold sentence, ending in full stop.
    // BUT seems little reason to treat this differently to the a real sentence.
    // Plus there's the complexity of list item vs paragraph number.

  }

  /**
   * Adds the heading for all bold paragraphs which don't end in full stop (which are captials
   * depending on the boolean)
   *
   * @param document the document
   * @param capitals does the heading need to be in capitals
   * @param headingTag the HTML heading tag to use
   */
  private void addHeading(Document document, boolean capitals, String headingTag) {
    document
        .select("p")
        .forEach(
            p -> {
              String text = p.text().trim();

              if (!isBold(p, text)) return;

              // No full stop (or similar) at the end of a title
              if (text.isEmpty() || text.substring(text.length() - 1).matches("[\\.:!\\?,;]"))
                return;

              if (!capitals || isAllUpperCase(text)) p.tagName(headingTag);
            });
  }

  private boolean isAllUpperCase(String text) {
    return text.toUpperCase().equals(text);
  }

  private boolean isBold(Element e, String text) {
    // Bold if one of the parents is bold
    if (!e.getElementsByTag("b").select("b").isEmpty()) {
      return true;
    }

    // Or if Bold child which has the same text.
    Elements boldChildren = e.select("b");
    if (boldChildren.isEmpty()) {
      return false;
    }

    return boldChildren.first().text().equals(text);
  }
}
