//Dstl (c) Crown Copyright 2017
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.consumers.Html5;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenConsumer;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;

/**
 * Creates HTML5 versions of the document, with entities annotated as spans. The original formatting
 * of the document is lost, and only the content is kept.
 *
 * Relationships are not currently supported.
 *
 * This is largely based off the original {@link Html5} consumer.
 *
 * @baleen.javadoc
 */
public abstract class AbstractHtmlConsumer extends BaleenConsumer {

  /**
   * The folder to output files to
   *
   * @baleen.config <i>Current directory</i>
   */
  public static final String PARAM_OUTPUT_FOLDER = "outputFolder";
  /**
   * Should the external ID be used for the file name? This option is useful if you have lots of
   * files with duplicate names, or you are reading from a source that isn't file system based (e.g.
   * a database).
   *
   * The external ID will be used by default if no Source URI is available, or it is badly formed.
   *
   * @baleen.config false
   */
  public static final String PARAM_USE_EXTERNAL_ID = "useExternalId";
  /**
   * Should a hash of the content be used to generate the ID? If false, then a hash of the Source
   * URI is used instead.
   *
   * @baleen.config true
   */
  public static final String PARAM_CONTENT_HASH_AS_ID = "contentHashAsId";

  /**
   * Set the CSS file for the output to reference. The string, if provided, will be added as a <link
   * ...> element in the document.
   *
   * @baleen.config
   */
  public static final String PARAM_CSS = "css";
  private static final String FILE_EXTENSION = ".html";

  @ConfigurationParameter(name = PARAM_OUTPUT_FOLDER, defaultValue = "")
  private String outputFolderString;
  private File outputFolder;

  @ConfigurationParameter(name = PARAM_USE_EXTERNAL_ID, defaultValue = "false")
  private Boolean useExternalId;
  @ConfigurationParameter(name = PARAM_CONTENT_HASH_AS_ID, defaultValue = "true")
  private Boolean contentHashAsId = true;

  @ConfigurationParameter(name = PARAM_CSS, defaultValue = "")
  private String css;

  /*
   * (non-Javadoc)
   * 
   * @see uk.gov.dstl.baleen.uima.BaleenAnnotator#doInitialize(org.apache.uima.UimaContext)
   */
  @Override
  public void doInitialize(final UimaContext aContext) throws ResourceInitializationException {
    if (Strings.isNullOrEmpty(outputFolderString)) {
      outputFolderString = System.getProperty("user.dir");
    }

    outputFolder = new File(outputFolderString);
    if (!outputFolder.exists()) {
      final Boolean ret = outputFolder.mkdirs();
      if (!ret) {
        throw new ResourceInitializationException(
            new BaleenException("Unable to create output folder"));
      }
    }

    if (!outputFolder.isDirectory() || !outputFolder.canWrite()) {
      throw new ResourceInitializationException(new BaleenException("Unable to write to folder"));
    }
  }

  /**
   * Append meta tag to an element (typically a head).
   *
   * @param el the el
   * @param name the meta name
   * @param content the meta content
   * @return the meta element
   */
  private Element appendMeta(final Element el, final String name, final String content) {
    if (Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(content)) {
      return null;
    }

    final Element meta = el.appendElement("meta");
    meta.attr("name", name);
    meta.attr("content", content);

    return meta;
  }


  /**
   * Gets the file name for the jCas (from either contenthash or the original source).
   *
   * @param jCas the j cas
   * @return the file name
   */
  private File getFileName(final JCas jCas) {
    File f = null;
    final DocumentAnnotation da = getDocumentAnnotation(jCas);
    final String source = da.getSourceUri();

    if (useExternalId || Strings.isNullOrEmpty(source)) {
      final String id = ConsumerUtils.getExternalId(da, contentHashAsId);
      f = new File(outputFolder, id + FILE_EXTENSION);
    } else {
      try {
        final String name = source.substring(source.lastIndexOf(File.separator) + 1);

        f = new File(outputFolder, name + FILE_EXTENSION);

        int append = 0;
        while (f.exists()) {
          append++;
          f = new File(outputFolder, name + "." + append + FILE_EXTENSION);
        }
        if (append != 0) {
          getMonitor().info(
              "File with the same name already exists in {} - source file will be saved as {}",
              outputFolder.getName(), f.getName());
        }
      } catch (final Exception e) {
        getMonitor().warn(
            "An error occurred trying to use the source URI {} as a file name - the external ID will be used instead",
            source, e);

        final String id = ConsumerUtils.getExternalId(da, contentHashAsId);
        f = new File(outputFolder, id + FILE_EXTENSION);
      }
    }

    return f;
  }


  /*
   * (non-Javadoc)
   * 
   * @see uk.gov.dstl.baleen.uima.BaleenAnnotator#doProcess(org.apache.uima.jcas.JCas)
   */
  @Override
  protected void doProcess(final JCas jCas) throws AnalysisEngineProcessException {
    final File f = getFileName(jCas);
    final DocumentAnnotation da = getDocumentAnnotation(jCas);

    final Document doc =
        Jsoup.parse("<!DOCTYPE html>\n<html lang=\"" + da.getLanguage() + "\"></html>");
    doc.outputSettings(new Document.OutputSettings().prettyPrint(false));
    final Element head = doc.head();

    if (!Strings.isNullOrEmpty(css)) {
      final Element cssLink = head.appendElement("link");
      cssLink.attr("rel", "stylesheet");
      cssLink.attr("href", css);
    }

    final Element charset = head.appendElement("meta");
    charset.attr("charset", "utf-8");

    appendMeta(head, "document.type", da.getDocType());
    appendMeta(head, "document.sourceUri", da.getSourceUri());
    appendMeta(head, "externalId", da.getHash());

    appendMeta(head, "document.classification", da.getDocumentClassification());
    appendMeta(head, "document.caveats",
        String.join(",", UimaTypesUtils.toArray(da.getDocumentCaveats())));
    appendMeta(head, "document.releasability",
        String.join(",", UimaTypesUtils.toArray(da.getDocumentReleasability())));

    String title = null;
    for (final Metadata md : JCasUtil.select(jCas, Metadata.class)) {
      appendMeta(head, md.getKey(), md.getValue());
      if ("documentTitle".equalsIgnoreCase(md.getKey())) {
        title = md.getValue();
      }
    }

    if (!Strings.isNullOrEmpty(title)) {
      doc.title(title);
    }

    final Element body = doc.body();

    writeBody(jCas, body);

    try {
      FileUtils.writeStringToFile(f, doc.html());
    } catch (final IOException e) {
      throw new AnalysisEngineProcessException(e);
    }
  }

  /**
   * Called to actually write into the body element, from the jCas.
   *
   * @param jCas the jcas
   * @param body the body
   */
  protected abstract void writeBody(JCas jCas, Element body);
}