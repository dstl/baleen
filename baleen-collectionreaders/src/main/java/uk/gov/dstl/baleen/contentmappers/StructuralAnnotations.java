// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmappers;

import java.util.Collections;

import org.apache.uima.jcas.JCas;
import org.jsoup.nodes.Element;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

import uk.gov.dstl.baleen.contentmappers.helpers.AnnotationCollector;
import uk.gov.dstl.baleen.contentmappers.helpers.ContentMapper;
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
import uk.gov.dstl.baleen.types.structure.Unordered;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;

/**
 * The default content mapper which convert generic HTML5 elements to Baleen Structural annotations.
 *
 * <p>Since the Baleen structural types are modelled around HTML5 elements this mapper is mostly a
 * simple passthrough, dealing with some special casess (eg HTML a with and without a href has
 * different meanings).
 *
 * <p>Due to the number of HTML tags refer to the source code to see the exact mapping.
 *
 * <p>NOTE: Not all formats will produce rich enough HTML to use all the mappings defined here.
 */
public class StructuralAnnotations implements ContentMapper {

  @Override
  public void map(final JCas jCas, final Element element, final AnnotationCollector collector) {

    Structure s = null;
    switch (element.tagName().toLowerCase()) {
      case "p":
        s = new Paragraph(jCas);
        break;

        // Headings

      case "h1":
        s = createHeading(jCas, 1);
        break;
      case "h2":
        s = createHeading(jCas, 2);
        break;
      case "h3":
        s = createHeading(jCas, 3);
        break;
      case "h4":
        s = createHeading(jCas, 4);
        break;
      case "h5":
        s = createHeading(jCas, 5);
        break;
      case "h6":
        s = createHeading(jCas, 6);
        break;

        // Lists

      case "ul":
        s = new Unordered(jCas);
        break;

      case "ol":
        s = new Ordered(jCas);
        break;

      case "li":
        s = new ListItem(jCas);
        break;

      case "dl":
        s = new DefinitionList(jCas);
        break;
      case "dt":
        s = new DefinitionItem(jCas);
        break;
      case "dd":
        // TODO: It might make sense to refer the dt wihtin the type system (setTerm())
        s = new DefinitionDescription(jCas);
        break;

        // Table

      case "table":
        s = new Table(jCas);
        break;

      case "thead":
        s = new TableHeader(jCas);
        break;

      case "tfoot":
        s = new TableFooter(jCas);
        break;

      case "tbody":
        s = new TableBody(jCas);
        break;

      case "tr":
        final TableRow tr = new TableRow(jCas);
        tr.setRow(findRowIndexOfRow(element));
        s = tr;
        break;

      case "th":
        // fall through
      case "td":
        final TableCell td = new TableCell(jCas);
        td.setColumn(findColIndexOfCell(element));
        td.setRow(findRowIndexOfCell(element));
        td.setRowSpan(getIntegerAttribute(element, "rowspan", 1));
        td.setColumnSpan(getIntegerAttribute(element, "colspan", 1));
        s = td;
        break;

        // Links and anchors

      case "a":
        s = createAnchor(jCas, element);
        break;

        // Images

      case "audio":
      case "video":
      case "embed":
      case "object":
      case "img":
      case "map":
      case "area":
      case "canvas":
      case "figure":
        final Figure figure = new Figure(jCas);
        if (element.hasAttr("src")) {
          figure.setTarget(element.attr("src"));
        }
        s = figure;
        break;

      case "caption":
      case "figcaption":
        s = new Caption(jCas);
        break;

        // Styling

      case "ins":
        // fall through - HTML W3 http://www.w3schools.com/tags/tag_ins.asp says that ins would
        // normally be underlined
      case "u":
        s = createStyle(jCas, "underline");
        break;

      case "i":
      case "em":
        s = createStyle(jCas, "italics");
        break;

      case "b":
      case "strong":
        s = createStyle(jCas, "bold");
        break;

      case "strike":
      case "s":
      case "del":
        s = createStyle(jCas, "strike");
        break;

      case "sup":
        s = createStyle(jCas, "superscript");
        break;

      case "sub":
        s = createStyle(jCas, "subscript");
        break;

      case "small":
        s = createStyle(jCas, "small");
        break;

      case "big":
        // Not HTML5 so not likely to be seen
        s = createStyle(jCas, "big");
        break;

      case "mark":
        s = createStyle(jCas, "highlighted");
        break;

        // Purely structural

      case "aside":
        s = new Aside(jCas);
        break;
      case "details":
        s = new Details(jCas);
        break;
      case "summary":
        s = new Summary(jCas);
        break;
      case "section":
      case "div":
        // Div means very little nothing... but we wrap it in a section
        s = new Section(jCas);
        break;

      case "span":
        // Do nothing
        break;

      case "main":
        s = createFromMain(jCas, element);
        break;
      case "article":
        s = createFromArticle(jCas, element);
        break;
      case "header":
        s = new Header(jCas);
        break;
      case "footer":
        s = new Footer(jCas);
        break;

      case "kbd":
      case "samp":
      case "code":
      case "pre":
        s = new Preformatted(jCas);
        break;

      case "blockquote":
        collector.add(new Section(jCas));
        // Fall through
      case "q":
        s = new Quotation(jCas);
        break;

        // Potential semantic types, but left to other mappers (SemanticHtml) to actually annotate
      case "time":
      case "meter":
      case "dfn":
      case "address":
      case "abbr":
      case "cite":
        return;

      case "hr":
        if (element.hasClass("pagebreak") || element.hasClass("sectionbreak")) {
          s = new Break(jCas);
        }
        break;

        // Misc ignored - head, details of embedded, ui specific, forms
      case "html":
      case "head":
      case "title":
      case "meta":
      case "base":
      case "style":
      case "script":
      case "noscript":
      case "link":
      case "dialog":
      case "nav":
      case "menu":
      case "menuitem":
      case "param":
      case "track":
      case "source":
      case "iframe":
      case "form":
      case "input":
      case "textarea":
      case "button":
      case "select":
      case "optgroup":
      case "option":
      case "label":
      case "fieldset":
      case "legend":
      case "datalist":
      case "keygen":
      case "output":
      case "ruby":
      case "rt":
      case "rp":
      case "progress":
      case "bdo":
      case "bdi":
      default:
        break;
    }

    if (s != null) {
      if (element.hasAttr("class")) {
        s.setElementClass(element.className());
      }
      if (element.hasAttr("id")) {
        s.setElementId(element.id());
      }
      collector.add(s);
    }
  }

  private int getIntegerAttribute(final Element element, final String key, final int defaultValue) {
    final String value = element.attr(key);
    if (Strings.isNullOrEmpty(value)) {
      return defaultValue;
    }
    final Integer i = Ints.tryParse(value);
    if (i == null) {
      return defaultValue;
    }
    return i;
  }

  private int findRowIndexOfCell(final Element element) {
    for (final Element e : element.parents()) {
      if (e.tagName().equalsIgnoreCase("tr")) {
        return findRowIndexOfRow(e);
      }
    }
    return -1;
  }

  private int findRowIndexOfRow(final Element e) {
    // TODO: The best we can do without rowspan type info
    return e.siblingIndex();
  }

  private int findColIndexOfCell(final Element e) {
    // TODO: The best we can do without colspan type info
    return e.siblingIndex();
  }

  private Structure createAnchor(final JCas jCas, final Element element) {
    String href = element.absUrl("href");
    if (Strings.isNullOrEmpty(href)) {
      href = element.attr("href");
    }
    if (!Strings.isNullOrEmpty(href)) {
      final Link l = new Link(jCas);
      l.setTarget(href);
      return l;
    } else {
      return new Anchor(jCas);
    }
  }

  private Page createFromArticle(final JCas jCas, final Element element) {
    final String clazz = element.attr("class");
    switch (clazz.toLowerCase()) {
      case "sheet":
        return new Sheet(jCas);
      case "slide":
        return new Slide(jCas);
      case "page":
        // fall through
      default:
        return new Page(jCas);
    }
  }

  /**
   * Create a Document, selecting sub type if appropriate.
   *
   * @param jCas the jCas
   * @param element the element name
   * @return the Style
   */
  private Document createFromMain(final JCas jCas, final Element element) {
    final String clazz = element.attr("class");
    switch (clazz.toLowerCase()) {
      case "spreadsheet":
        return new SpreadSheet(jCas);
      case "slideshow":
        return new SlideShow(jCas);
      case "document":
      default:
        return new Document(jCas);
    }
  }

  /**
   * Create a Style
   *
   * @param jCas the jCas
   * @param styleName the style name
   * @return the Style
   */
  private Style createStyle(final JCas jCas, final String styleName) {
    final Style style = new Style(jCas);
    style.setDecoration(UimaTypesUtils.toArray(jCas, Collections.singleton(styleName)));
    return style;
  }

  /**
   * Create a Heading with the given level
   *
   * @param jCas the jCas
   * @param level the level of the heading
   * @return the Heading
   */
  private Heading createHeading(final JCas jCas, final int level) {
    final Heading h = new Heading(jCas);
    h.setLevel(level);
    return h;
  }
}
