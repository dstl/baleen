//Dstl (c) Crown Copyright 2017
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.jsoup.Jsoup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;

import uk.gov.dstl.baleen.types.structure.Anchor;
import uk.gov.dstl.baleen.types.structure.Aside;
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
import uk.gov.dstl.baleen.types.structure.TableRow;
import uk.gov.dstl.baleen.types.structure.TextDocument;
import uk.gov.dstl.baleen.types.structure.Unordered;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;

public class StructuralHtmlTest {
  private static final String EXPECTED = "<!doctype html>" +
      "<html lang=\"x-unspecified\">" +
      " <head> " +
      "  <meta charset=\"utf-8\"> " +
      "  <meta name=\"document.sourceUri\" content=\"test.txt\"> " +
      "  <meta name=\"externalId\" content=\"52bfa1307972d3b9158718d6a6abede86a0315b4daaf14ca1e3682310a75705d\"> "
      +
      " </head> " +
      " <body> " +
      "  <div> " +
      "   <main class=\"baleen-structure-document\"> " +
      "    <section class=\"baleen-structure-section\"> " +
      "     <p class=\"baleen-structure-paragraph\">This is a test document, that contains structure</p>. "
      +
      "     <details class=\"baleen-structure-details\">" +
      "       This test was written by " +
      "      <span style=\"font-weight:bold; \" class=\"baleen-structure-style\">Chris</span> " +
      "     </details>. " +
      "     <aside class=\"baleen-structure-aside\">" +
      "       On the on " +
      "      <span style=\"font-style:italic; \" class=\"baleen-structure-style\">24 December 2016</span> "
      +
      "     </aside>. " +
      "    </section> " +
      "   </main>" +
      "  </div>  " +
      " </body>" +
      "</html>";

  private static final String EXPECTED_TABLE = "<!doctype html><html lang=\"x-unspecified\">" +
      " <head> " +
      "  <meta charset=\"utf-8\"> " +
      "  <meta name=\"document.sourceUri\" content=\"test.txt\"> " +
      "  <meta name=\"externalId\" content=\"5ced5f586e63306bf2232843be27dc6fbb531732bc73d6e3d521fe72edb894a1\"> "
      +
      " </head> " +
      " <body> " +
      "  <div> " +
      "   <table class=\"baleen-structure-table\"> " +
      "    <tbody class=\"baleen-structure-tablebody\"> " +
      "     <tr class=\"baleen-structure-tablerow\"> " +
      "      <td class=\"baleen-structure-tablecell\">A</td> "
      +
      "      <td class=\"baleen-structure-tablecell\">B</td> "
      +
      "      <td class=\"baleen-structure-tablecell\">C</td> "
      +
      "     </tr> " +
      "     <tr class=\"baleen-structure-tablerow\"> " +
      "      <td class=\"baleen-structure-tablecell\">1</td> "
      +
      "      <td class=\"baleen-structure-tablecell\">2</td> "
      +
      "      <td class=\"baleen-structure-tablecell\">3</td> "
      +
      "     </tr> " +
      "    </tbody> " +
      "   </table> " +
      "  </div>  " +
      "</body>" +
      "</html>";

  private static final String EXPECTED_TAGS = "<!doctype html>" +
      "<html lang=\"x-unspecified\">" +
      " <head> " +
      "  <meta charset=\"utf-8\"> " +
      "  <meta name=\"document.sourceUri\" content=\"test.txt\"> " +
      "  <meta name=\"externalId\" content=\"2664ee3899bb360a9b29a62ff24694258527dc641449329b0f842d77cb21172d\"> "
      +
      " </head> " +
      " <body> " +
      "  <div> " +
      "   <a id=\"a83fbdbc9c736caacfd7367fe2afc081caaa7df2982cced9a9751e02645e0175\" class=\"baleen-structure-anchor\">Anchor</a> "
      +
      "   <figcaption class=\"baleen-structure-caption\">" +
      "     Caption " +
      "   </figcaption> " +
      "   <main class=\"baleen-structure-document\">" +
      "     Document " +
      "   </main> " +
      "   <main class=\"baleen-structure-spreadsheet\">" +
      "     SpreadSheet " +
      "   </main> " +
      "   <main class=\"baleen-structure-slideshow\">" +
      "     SlideShow " +
      "   </main> " +
      "   <main class=\"baleen-structure-textdocument\">" +
      "     TextDocument " +
      "   </main> " +
      "   <figure class=\"baleen-structure-figure\">" +
      "     Figure " +
      "   </figure> " +
      "   <footer class=\"baleen-structure-footer\">" +
      "     Footer " +
      "   </footer> " +
      "   <aside class=\"baleen-structure-footnote\">" +
      "     Footnote " +
      "   </aside> " +
      "   <header class=\"baleen-structure-header\">" +
      "     Header " +
      "   </header> " +
      "   <h1 class=\"baleen-structure-heading\">Heading</h1> " +
      "   <a class=\"baleen-structure-link\">Link</a> " +
      "   <li class=\"baleen-structure-listitem\">ListItem</li> " +
      "   <ol class=\"baleen-structure-ordered\">" +
      "     Ordered " +
      "   </ol> " +
      "   <ul class=\"baleen-structure-unordered\">" +
      "     Unordered " +
      "   </ul> " +
      "   <dl class=\"baleen-structure-definitionlist\">" +
      "     DefinitionList " +
      "   </dl> " +
      "   <dt class=\"baleen-structure-definitionitem\">" +
      "     DefinitionItem " +
      "   </dt> " +
      "   <dd class=\"baleen-structure-definitiondescription\">" +
      "     DefinitionDescription " +
      "   </dd> " +
      "   <article class=\"baleen-structure-page\">" +
      "     Page " +
      "   </article>" +
      "   <article class=\"baleen-structure-slide\">" +
      "     Slide " +
      "   </article> " +
      "   <article class=\"baleen-structure-sheet\">" +
      "     Sheet " +
      "   </article> " +
      "   <p class=\"baleen-structure-paragraph\">Paragraph</p> " +
      "   <section class=\"baleen-structure-section\">" +
      "     Section " +
      "   </section> " +
      "   <summary class=\"baleen-structure-summary\">Summary</summary>" +
      "   <details class=\"baleen-structure-details\">" +
      "     Details " +
      "   </details> " +
      "   <aside class=\"baleen-structure-aside\">" +
      "     Aside " +
      "   </aside> " +
      "   <pre class=\"baleen-structure-preformatted\">Preformatted</pre> " +
      "   <q class=\"baleen-structure-quotation\">Quotation</q> " +
      "   <span class=\"baleen-structure-sentence\">Sentence</span> " +
      "   <span class=\"baleen-structure-style\">Style</span> " +
      "   <table class=\"baleen-structure-table\">" +
      "     Table " +
      "   </table> " +
      "  </div>  " +
      " </body>" +
      "</html>";

  private static final String EXPECTED_DATA = "<!doctype html>" +
      "<html lang=\"x-unspecified\">" +
      " <head> " +
      "  <meta charset=\"utf-8\">" +
      "  <meta name=\"document.sourceUri\" content=\"test.txt\">" +
      "  <meta name=\"externalId\" content=\"d029f87e3d80f8fd9b1be67c7426b4cc1ff47b4a9d0a8461c826a59d8c5eb6cd\">"
      +
      " </head> " +
      " <body> " +
      "  <div> " +
      "   <main class=\"baleen-structure-document\" data-baleen-structure-depth=\"0\" data-baleen-id=\"012b13a6b7a5a0b69c66d6b017c53c3f70be35ac1263766e6ce16776dee58caa\" data-baleen-begin=\"0\" data-baleen-end=\"7\">"
      +
      "     Example " +
      "   </main> " +
      "  </div>  " +
      " </body>" +
      "</html>";

  private static final String EXPECTED_EMPTY = "<!doctype html>" +
      "<html lang=\"x-unspecified\">" +
      " <head> " +
      "  <meta charset=\"utf-8\"> " +
      "  <meta name=\"document.sourceUri\" content=\"test.txt\"> " +
      "  <meta name=\"externalId\" content=\"d92797e9f108f7dcf8beb2449d1aa046b037bdbec554960e2314f916118c37f3\"> "
      +
      " </head> " +
      " <body> " +
      "  <div> " +
      "   <p class=\"baleen-structure-paragraph\"></p> " +
      "  </div>  " +
      " </body>" +
      "</html>";


  private File outputFolder;
  private JCas jCas;

  @Before
  public void beforeTest() throws UIMAException {
    outputFolder = Files.createTempDir();
    jCas = JCasSingleton.getJCasInstance();
  }

  @After
  public void afterTest() throws IOException {
    FileUtils.deleteDirectory(outputFolder);
  }

  @Test
  public void testDocument() throws UIMAException, IOException {
    final AnalysisEngine consumer =
        AnalysisEngineFactory.createEngine(StructuralHtml.class, Html5.PARAM_OUTPUT_FOLDER,
            outputFolder.getPath());
    final DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
    da.setSourceUri("test.txt");

    final String text =
        "This is a test document, that contains structure. This test was written by Chris. On the on 24 December 2016.";

    jCas.setDocumentText(text);

    final Document doc = new Document(jCas);
    doc.setBegin(0);
    doc.setEnd(text.length());
    doc.addToIndexes();

    final Section section = new Section(jCas);
    section.setDepth(1);
    section.setBegin(0);
    section.setEnd(text.length());
    section.addToIndexes();

    final Paragraph para = new Paragraph(jCas);
    para.setBegin(2 - 2);
    para.setEnd(50 - 2);
    para.addToIndexes();

    final Details details = new Details(jCas);
    details.setBegin(52 - 2);
    details.setEnd(82 - 2);
    details.addToIndexes();

    final Aside aside = new Aside(jCas);
    aside.setBegin(84 - 2);
    aside.setEnd(110 - 2);
    aside.addToIndexes();

    final Style bold = new Style(jCas);
    bold.setDecoration(UimaTypesUtils.toArray(jCas, Arrays.asList("bold")));
    bold.setBegin(77 - 2);
    bold.setEnd(82 - 2);
    bold.addToIndexes();

    final Style italics = new Style(jCas);
    italics.setDecoration(UimaTypesUtils.toArray(jCas, Arrays.asList("italics")));
    italics.setBegin(94 - 2);
    italics.setEnd(110 - 2);
    italics.addToIndexes();


    consumer.process(jCas);

    final File f = new File(outputFolder, "test.txt.html");
    assertTrue(f.exists());


    // Strip out all the whitespace... just to normalise it
    assertEquals(Jsoup.parse(f, "UTF-8").html().replaceAll("\\s*", ""),
        EXPECTED.replaceAll("\\s*", ""));
  }

  @Test
  public void testTables() throws Exception {

    final AnalysisEngine consumer =
        AnalysisEngineFactory.createEngine(StructuralHtml.class, Html5.PARAM_OUTPUT_FOLDER,
            outputFolder.getPath());
    final DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
    da.setSourceUri("test.txt");

    jCas.setDocumentText("A B C\n1 2 3\n");

    final Table table = new Table(jCas);
    table.setBegin(0);
    table.setEnd(11);
    table.addToIndexes();

    final TableBody tbody = new TableBody(jCas);
    tbody.setDepth(1);
    tbody.setBegin(0);
    tbody.setEnd(11);
    tbody.addToIndexes();

    final TableRow first = new TableRow(jCas);
    first.setBegin(0);
    first.setEnd(5);
    first.addToIndexes();

    final TableRow second = new TableRow(jCas);
    second.setBegin(6);
    second.setEnd(11);
    second.addToIndexes();

    final TableCell a = new TableCell(jCas);
    a.setBegin(0);
    a.setEnd(1);
    a.addToIndexes();

    final TableCell b = new TableCell(jCas);
    b.setBegin(2);
    b.setEnd(3);
    b.addToIndexes();

    final TableCell c = new TableCell(jCas);
    c.setBegin(4);
    c.setEnd(5);
    c.addToIndexes();

    final TableCell a1 = new TableCell(jCas);
    a1.setBegin(6);
    a1.setEnd(7);
    a1.addToIndexes();

    final TableCell b1 = new TableCell(jCas);
    b1.setBegin(8);
    b1.setEnd(9);
    b1.addToIndexes();

    final TableCell c1 = new TableCell(jCas);
    c1.setBegin(10);
    c1.setEnd(11);
    c1.addToIndexes();

    consumer.process(jCas);

    final File f = new File(outputFolder, "test.txt.html");
    assertTrue(f.exists());

    assertEquals(Jsoup.parse(f, "UTF-8").html().replaceAll("\\s*", ""),
        EXPECTED_TABLE.replaceAll("\\s*", ""));

  }

  @Test
  public void testTags() throws Exception {

    final AnalysisEngine consumer =
        AnalysisEngineFactory.createEngine(StructuralHtml.class, Html5.PARAM_OUTPUT_FOLDER,
            outputFolder.getPath());
    final DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
    da.setSourceUri("test.txt");

    // The document generated here is obviously nonsense from all perspectives but can visually
    // inspect the output is as expected

    // NOTE: This doesn't' include table sub elements as they need valid nesting
    final Class<?>[] classes =
        new Class<?>[] {Anchor.class, Caption.class,
            Document.class, SpreadSheet.class, SlideShow.class, TextDocument.class, Figure.class,
            Footer.class, Footnote.class, Header.class, Heading.class, Link.class, ListItem.class,
            Ordered.class, Unordered.class, DefinitionList.class,
            DefinitionItem.class, DefinitionDescription.class, Page.class, Slide.class, Sheet.class,
            Paragraph.class, Section.class, Summary.class, Details.class, Aside.class,
            Preformatted.class, Quotation.class,
            Sentence.class, Style.class, Table.class};

    final StringBuilder sb = new StringBuilder();
    for (final Class<?> c : classes) {
      final int b = sb.length();
      sb.append(c.getSimpleName());
      final int e = sb.length();
      sb.append(" ");

      final Structure annotation = (Structure) c.getConstructor(JCas.class).newInstance(jCas);
      annotation.setBegin(b);
      annotation.setEnd(e);
      annotation.addToIndexes();
    }

    jCas.setDocumentText(sb.toString());

    consumer.process(jCas);

    final File f = new File(outputFolder, "test.txt.html");
    assertTrue(f.exists());

    assertEquals(Jsoup.parse(f, "UTF-8").html().replaceAll("\\s*", ""),
        EXPECTED_TAGS.replaceAll("\\s*", ""));

  }

  @Test
  public void testOutputData() throws Exception {
    final AnalysisEngine consumer =
        AnalysisEngineFactory.createEngine(StructuralHtml.class, Html5.PARAM_OUTPUT_FOLDER,
            outputFolder.getPath(), StructuralHtml.PARAM_OUTPUT_DATA, true);
    final DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
    da.setSourceUri("test.txt");


    jCas.setDocumentText("Example");

    final Document d = new Document(jCas);
    d.setBegin(0);
    d.setEnd("Example".length());
    d.addToIndexes();

    consumer.process(jCas);

    final File f = new File(outputFolder, "test.txt.html");
    assertTrue(f.exists());

    assertEquals(Jsoup.parse(f, "UTF-8").html().replaceAll("\\s*", ""),
        EXPECTED_DATA.replaceAll("\\s*", ""));
  }

  @Test
  public void testOutputEmpty() throws Exception {
    final AnalysisEngine consumer =
        AnalysisEngineFactory.createEngine(StructuralHtml.class, Html5.PARAM_OUTPUT_FOLDER,
            outputFolder.getPath(), StructuralHtml.PARAM_OUTPUT_EMPTY_TAGS, true);
    final DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
    da.setSourceUri("test.txt");


    jCas.setDocumentText("Example document: ''");

    final Paragraph d = new Paragraph(jCas);
    d.setBegin(19);
    d.setEnd(19);
    d.addToIndexes();

    consumer.process(jCas);

    final File f = new File(outputFolder, "test.txt.html");
    assertTrue(f.exists());

    System.out.println(Jsoup.parse(f, "UTF-8").html());

    assertEquals(Jsoup.parse(f, "UTF-8").html().replaceAll("\\s*", ""),
        EXPECTED_EMPTY.replaceAll("\\s*", ""));
  }

}