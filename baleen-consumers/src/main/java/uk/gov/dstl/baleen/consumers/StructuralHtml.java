// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;

import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.consumers.utils.AbstractHtmlConsumer;
import uk.gov.dstl.baleen.types.structure.Anchor;
import uk.gov.dstl.baleen.types.structure.Aside;
import uk.gov.dstl.baleen.types.structure.Break;
import uk.gov.dstl.baleen.types.structure.Caption;
import uk.gov.dstl.baleen.types.structure.DefinitionDescription;
import uk.gov.dstl.baleen.types.structure.DefinitionItem;
import uk.gov.dstl.baleen.types.structure.DefinitionList;
import uk.gov.dstl.baleen.types.structure.Details;
import uk.gov.dstl.baleen.types.structure.Document;
import uk.gov.dstl.baleen.types.structure.Figure;
import uk.gov.dstl.baleen.types.structure.Footer;
import uk.gov.dstl.baleen.types.structure.Footnote;
import uk.gov.dstl.baleen.types.structure.Header;
import uk.gov.dstl.baleen.types.structure.Heading;
import uk.gov.dstl.baleen.types.structure.Link;
import uk.gov.dstl.baleen.types.structure.ListItem;
import uk.gov.dstl.baleen.types.structure.Ordered;
import uk.gov.dstl.baleen.types.structure.Page;
import uk.gov.dstl.baleen.types.structure.Paragraph;
import uk.gov.dstl.baleen.types.structure.Preformatted;
import uk.gov.dstl.baleen.types.structure.Quotation;
import uk.gov.dstl.baleen.types.structure.Section;
import uk.gov.dstl.baleen.types.structure.Sentence;
import uk.gov.dstl.baleen.types.structure.Sheet;
import uk.gov.dstl.baleen.types.structure.Slide;
import uk.gov.dstl.baleen.types.structure.SlideShow;
import uk.gov.dstl.baleen.types.structure.SpreadSheet;
import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.types.structure.Style;
import uk.gov.dstl.baleen.types.structure.Summary;
import uk.gov.dstl.baleen.types.structure.Table;
import uk.gov.dstl.baleen.types.structure.TableBody;
import uk.gov.dstl.baleen.types.structure.TableCell;
import uk.gov.dstl.baleen.types.structure.TableFooter;
import uk.gov.dstl.baleen.types.structure.TableHeader;
import uk.gov.dstl.baleen.types.structure.TableRow;
import uk.gov.dstl.baleen.types.structure.TextDocument;
import uk.gov.dstl.baleen.types.structure.Unordered;
import uk.gov.dstl.baleen.uima.utils.StructureHierarchy;
import uk.gov.dstl.baleen.uima.utils.StructureUtil;
import uk.gov.dstl.baleen.uima.utils.select.Node;

/**
 * Creates a HTML5 version of the structured annotations of a document.
 *
 * <p>The tag structure replicates to the extent possible the input from the Format Extractor
 * library, used by StructureContentExtractor. That is for example 'aside' to 'footnote' to 'aside'.
 * That said the purpose is not to reproduce a faithful original, but instead to produce something
 * which visibly and structurally looks like high quality HTML representing a document.
 *
 * <p>This annotator optionally adds class information in the form of 'baleen-structure-[type]' to
 * allow the originating Baleen type to be identified. (Optional on the outputData parameter.)
 *
 * <p>Naturally the HTML is best viewed with CSS, an an example CSS style sheet:
 *
 * <pre>
 * html {
 *   background-color: #eee;
 * }
 *
 * body {
 *  max-width: 1200px;
 *  margin: 5px auto;
 *   padding: 25px;
 * }
 *
 * article {
 *   padding: 25px;
 *   background-color: #fff;
 *   border: 1px solid black;
 *   overflow: auto;
 *   margin-bottom: 25px;
 * }
 *
 * section, div {
 *   margin: 5px;
 *   padding: 25px;
 *   border: 1px dashed #eee;
 *   background-color: #fff;
 * }
 *
 * table {
 *     border-collapse: collapse;
 * }
 *
 * table, th, td {
 *     border: 1px solid #666;
 * }
 *
 * th {
 *   font-weight: bold;
 * }
 *
 * h1,h2,h3,h4,h5,h6 {
 *   margin-left: -20px;
 * }
 *
 * </pre>
 *
 * This will NOT output entity, relation or other semantic annotations.
 *
 * @baleen.javadoc
 */
public class StructuralHtml extends AbstractHtmlConsumer {

  /**
   * Apply styling information in the original document to the output.
   *
   * <p>Examples include colour, underline, etc.
   *
   * <p>Unless your documents encode important information through styles, you should use a CSS
   * style sheet and leave this off.
   *
   * @baleen.config true
   */
  public static final String PARAM_APPLY_STYLES = "applyStyles";

  @ConfigurationParameter(name = PARAM_APPLY_STYLES, defaultValue = "true")
  private Boolean applyStyles;

  /**
   * Outputs data-* attributes on the tags using Baleen information (begin, end, id, etc).
   *
   * <p>This increases the overall size of the HTML, but if very useful for onward machine
   * processing.
   *
   * @baleen.config false
   */
  public static final String PARAM_OUTPUT_DATA = "outputData";

  @ConfigurationParameter(name = PARAM_OUTPUT_DATA, defaultValue = "false")
  private Boolean outputData;

  /**
   * Output empty tags.
   *
   * <p>Should tags which have no text and no content be output to the HTML.
   *
   * <p>There is little reason to do this unless debugging the structural processing of Baleen, as
   * it unnecessarily complicates the documents.
   *
   * <p>NOTE: This does not apply to empty table cells (th, td) because they are needed to preserve
   * the table structure.
   *
   * @baleen.config false
   */
  public static final String PARAM_OUTPUT_EMPTY_TAGS = "outputEmptyTags";

  @ConfigurationParameter(name = PARAM_OUTPUT_EMPTY_TAGS, defaultValue = "false")
  private Boolean outputEmptyTags;

  /**
   * A list of structural types which will be considered during record path analysis.
   *
   * @baleen.config Paragraph,TableCell,ListItem,Aside, ...
   */
  public static final String PARAM_TYPE_NAMES = "types";

  /** The type names. */
  @ConfigurationParameter(name = PARAM_TYPE_NAMES, mandatory = false)
  private String[] typeNames;

  /** The structural classes. */
  protected Set<Class<? extends Structure>> structuralClasses;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);
    structuralClasses = StructureUtil.getStructureClasses(typeNames);
  }

  /**
   * Walk through the nodes in order to create the complete HTML structure.
   *
   * @param parentElement the parent element
   * @param n the n
   */
  private void walk(final Element parentElement, final Node<Structure> n) {
    final Structure structure = n.getItem();

    // TODO: Here we always create a new element, but in reality we could
    // use parentElement if the
    // element is just a div (that is structure == null etc)
    // That might clean up the HTML
    final Element e = createTag(structure);

    if (structure == null || structure.getCoveredText() == null) {
      // Descend into the children directly
      for (final Node<Structure> child : n.getChildren()) {
        walk(e, child);
      }
    } else {

      final String text = structure.getCoveredText();
      int offset = 0;
      for (final Node<Structure> child : n.getChildren()) {
        appendText(e, text, offset, child.getItem().getBegin() - n.getItem().getBegin());
        walk(e, child);
        offset = child.getItem().getEnd() - n.getItem().getBegin();
      }
      appendText(e, text, offset, n.getItem().getEnd() - n.getItem().getBegin());
    }

    parentElement.appendChild(e);
  }

  /**
   * Add text to element
   *
   * @param e the element
   * @param text the text buffer containing the substring
   * @param start the start offset within the text
   * @param end the end offset within the text
   * @return true, if successful
   */
  private boolean appendText(final Element e, final String text, final int start, final int end) {
    if (start < end && end <= text.length()) {
      e.appendText(text.substring(start, end));
      return true;
    } else {
      return false;
    }
  }

  /**
   * Create a CSS style string for the style annotation
   *
   * @param s the style
   * @return the string
   */
  private String buildCssStyle(final Style s) {
    final String color = s.getColor();
    final StringArray decorations = s.getDecoration();
    final String font = s.getFont();

    // If no style info stop
    if (Strings.isNullOrEmpty(color)
        && Strings.isNullOrEmpty(font)
        && (decorations == null || decorations.size() == 0)) {
      return null;
    }

    final StringBuilder sb = new StringBuilder();

    // This is very naive, it a passthrough of the original formats values
    // Effectively we are just hoping the browser knows what to do.

    if (!Strings.isNullOrEmpty(color)) {
      sb.append(String.format("color:%s; ", color));
    }

    if (!Strings.isNullOrEmpty(font)) {
      sb.append(String.format("font-family:\"%s\"; ", color));
    }

    if (decorations != null && decorations.size() > 0) {
      final String[] array = decorations.toArray();
      for (final String a : array) {
        switch (a.toUpperCase()) {
          case "UNDERLINE":
            sb.append("text-decoration:underline; ");
            break;
          case "BOLD":
            sb.append("font-weight:bold; ");
            break;
          case "ITALIC":
          case "ITALICS":
            sb.append("font-style:italic; ");
            break;
          case "STRIKE":
          case "STRIKETHROUGH":
            sb.append("text-decoration: line-through; ");
            break;
          case "SUPERSCRIPT":
            sb.append("font-size: .7em; vertical-align: super; ");
            break;
          case "SUBSCRIPT":
            sb.append("font-size: .7em; vertical-align: sub; ");
            break;
          case "BIG":
            sb.append("font-size: 1.2em; ");
            break;
          case "SMALL":
            sb.append("font-size: .9em; ");
            break;
          case "highlighted":
            sb.append("background-color:#ffffe0; ");
            break;
          default:
            // No nothing - we don't know what it means
            break;
        }
      }
    }

    return sb.toString();
  }

  /**
   * Creates the element of the given tag name.
   *
   * @param tag the tag
   * @return the element
   */
  private Element createElement(final String tag) {
    return new Element(Tag.valueOf(tag), "");
  }

  /**
   * Creates the tag from the structure annotation.
   *
   * @param s the structure annotation
   * @return the element
   */
  private Element createTag(final Structure s) {
    Element e;

    if (s == null) {
      e = createElement("div");
    } else if (s instanceof Anchor) {
      e = createElement("a");
      e.attr("id", s.getExternalId());
    } else if (s instanceof Caption) {
      e = createElement("figcaption");
    } else if (s instanceof Document
        || s instanceof SpreadSheet
        || s instanceof SlideShow
        || s instanceof TextDocument) {
      e = createElement("main");
    } else if (s instanceof Figure) {
      // TODO This is more complex I guess if we really wanted to put in a
      // img / object tag.
      // but we don't have that info.
      e = createElement("figure");
    } else if (s instanceof Footer) {
      e = createElement("footer");
    } else if (s instanceof Footnote) {
      e = createElement("aside");
    } else if (s instanceof Header) {
      e = createElement("header");
    } else if (s instanceof Heading) {
      final Heading h = (Heading) s;
      final int level = Math.min(6, Math.max(1, h.getLevel()));
      e = createElement("h" + level);
    } else if (s instanceof Link) {
      e = createElement("a");
      final String target = ((Link) s).getTarget();
      if (!Strings.isNullOrEmpty(target)) {
        e.attr("href", target);
      }
    } else if (s instanceof ListItem) {
      e = createElement("li");
    } else if (s instanceof Ordered) {
      e = createElement("ol");
    } else if (s instanceof Unordered) {
      e = createElement("ul");
    } else if (s instanceof DefinitionList) {
      e = createElement("dl");
    } else if (s instanceof DefinitionItem) {
      e = createElement("dt");
    } else if (s instanceof DefinitionDescription) {
      e = createElement("dd");
    } else if (s instanceof Page || s instanceof Slide || s instanceof Sheet) {
      e = createElement("article");
    } else if (s instanceof Paragraph) {
      e = createElement("p");
    } else if (s instanceof Section) {
      e = createElement("section");
    } else if (s instanceof Summary) {
      e = createElement("summary");
    } else if (s instanceof Details) {
      e = createElement("details");
    } else if (s instanceof Aside) {
      e = createElement("aside");
    } else if (s instanceof Preformatted) {
      e = createElement("pre");
    } else if (s instanceof Quotation) {
      e = createElement("q");
    } else if (s instanceof Sentence) {
      e = createElement("span");
    } else if (s instanceof Style) {
      e = createElement("span");
      if (applyStyles) {
        final String cssStyle = buildCssStyle((Style) s);
        if (!Strings.isNullOrEmpty(cssStyle)) {
          e.attr("style", cssStyle);
        }
      }
    } else if (s instanceof Table) {
      e = createElement("table");
    } else if (s instanceof TableBody) {
      e = createElement("tbody");
    } else if (s instanceof TableCell) {
      e = createElement("td");
      final TableCell cell = (TableCell) s;
      addRowOrCol(e, "data-row", cell.getRow());
      addRowOrCol(e, "data-col", cell.getColumn());
      addRowOrColSpan(e, "rowspan", cell.getRowSpan());
      addRowOrColSpan(e, "colspan", cell.getColumnSpan());
    } else if (s instanceof TableHeader) {
      e = createElement("thead");
    } else if (s instanceof TableFooter) {
      e = createElement("tfoot");
    } else if (s instanceof TableRow) {
      e = createElement("tr");
    } else if (s instanceof Break) {
      e = createElement("hr");
    } else {
      e = createElement("div");
    }

    if (s != null) {
      e.attr(
          "class", String.format("baleen-structure-%s", s.getType().getShortName().toLowerCase()));

      // Add generic data attributes
      if (outputData) {
        e.attr("data-baleen-structure-depth", Integer.toString(s.getDepth()));
        e.attr("data-baleen-id", s.getExternalId());
        e.attr("data-baleen-begin", Integer.toString(s.getBegin()));
        e.attr("data-baleen-end", Integer.toString(s.getEnd()));
      }
    }

    return e;
  }

  private void addRowOrColSpan(final Element e, final String key, final int span) {
    if (span > 0) {
      e.attr(key, Integer.toString(span));
    }
  }

  private void addRowOrCol(final Element e, final String key, final int v) {
    if (v > 0) {
      e.attr(key, Integer.toString(v));
    }
  }

  @Override
  protected void writeBody(final JCas jCas, final Element body) {

    final Node<Structure> root = StructureHierarchy.build(jCas, structuralClasses).getRoot();

    walk(body, root);

    // We need to create the proper li tags under ol and ul
    body.select("ul > p").wrap("<li></li>");
    body.select("ol > p").wrap("<li></li>");

    // Correct table cells from td to th in header
    body.select("thead td").tagName("th");

    // Add &nbsp; to any empty td or th's
    body.select("td:empty,th:empty").html("&nbsp");

    if (!outputEmptyTags) {
      Elements e = emptyElements(body);
      while (!e.isEmpty()) {
        e.remove();
        e = emptyElements(body);
      }
    }

    // TODO: In accordance with HTML spec
    // - Captions for Table should be moved inside the table
    // - Captions for Figure should be moved inside the figure

  }

  private Elements emptyElements(final Element body) {
    return body.select("*:empty").not("body").not("hr").not("img").not("a");
  }
}
