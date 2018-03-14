// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmanipulators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import uk.gov.dstl.baleen.contentmanipulators.helpers.ContentManipulator;
import uk.gov.dstl.baleen.contentmanipulators.helpers.MarkupUtils;

/**
 * Creates HTML nodes which capture the paragraph classification markings.
 *
 * <p>If this manipulator sees (CLASSIFICATION) The rest of the paragraph. Then it removes the
 * CLASSIFICATION prefix and records the classification in the paragraph tag under data- tags. This
 * cleans up the text and allows a classification annotation to be added later.
 *
 * <p>This is a basic example, and may not work in all cases. It could be more robust.
 *
 * <p>NOTE this will only output classification tags if used in conjunction with the
 * DataAttributeMapper.
 */
public class ParagraphMarkedClassification implements ContentManipulator {

  private static final String CLASSFICATION_GROUP = "classfication";
  private static final Pattern PARAGRAPH_MARKING =
      Pattern.compile("^\\s*\\((?<" + CLASSFICATION_GROUP + ">.*?)\\).*");

  @Override
  public void manipulate(Document document) {
    document.select("p").forEach(this::processParagraph);
  }

  private void processParagraph(Element p) {
    String text = p.text();
    Matcher matcher = PARAGRAPH_MARKING.matcher(text);
    if (matcher.find()) {
      String classification = matcher.group(CLASSFICATION_GROUP);

      MarkupUtils.additionallyAnnotateAsType(
          p, "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
      // TODO: We override this for simplicity but we could select the best classification etc
      // (or output everything later and let a cleaner decide)
      MarkupUtils.setAttribute(p, "classification", classification.trim());

      // TODO: Ideally delete text the classification from the front.
      // That needs a util as we need to eat up the children of p until we've got to the end.
      // That's quite complex, you'd need to split down the text nodes across multiple children.
      // We'll just remove the the first text node matching the classification we've found as an
      // interim.

      String marking = "(" + classification + ')';
      for (org.jsoup.nodes.TextNode t : p.textNodes()) {
        if (t.text().contains(marking)) {
          String newText = t.text().replace(marking, "");
          t.text(newText);
        }
      }
    }
  }
}
