// Dstl (c) Crown Copyright 2017
// Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.core.utils.BaleenDefaults;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;
import uk.gov.dstl.baleen.uima.IContentExtractor;

/**
 * Reads a file, and treats each line as a separate document. Once completed, the pipeline will stay
 * active (because pipelines are persistent in Baleen), but the file will not be monitored for
 * changes and no further documents will be processed.
 *
 * <p>This could be useful for processing a CSV, where you want each line of the CSV to be processed
 * separately.
 *
 * @baleen.javadoc
 */
public class LineReader extends BaleenCollectionReader {
  /**
   * The file to process
   *
   * @baleen.config
   */
  public static final String PARAM_FILE = "file";

  @ConfigurationParameter(name = PARAM_FILE, defaultValue = "")
  private File file;

  /**
   * The content extractor to use to extract content from files
   *
   * @baleen.config Value of BaleenDefaults.DEFAULT_CONTENT_EXTRACTOR
   */
  public static final String PARAM_CONTENT_EXTRACTOR = "contentExtractor";

  @ConfigurationParameter(
    name = PARAM_CONTENT_EXTRACTOR,
    defaultValue = BaleenDefaults.DEFAULT_CONTENT_EXTRACTOR
  )
  private String contentExtractor;

  private BufferedReader br;
  private String line;
  private Integer lineNumber = 0;

  private IContentExtractor extractor;

  @Override
  protected void doInitialize(UimaContext context) throws ResourceInitializationException {
    if (file == null || !file.canRead() || !file.isFile()) {
      throw new ResourceInitializationException(
          new InvalidParameterException("Specified parameter '" + PARAM_FILE + "' was not valid"));
    }

    try {
      br =
          new BufferedReader(
              new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
    } catch (IOException ioe) {
      throw new ResourceInitializationException(ioe);
    }

    try {
      extractor = getContentExtractor(contentExtractor);
    } catch (InvalidParameterException ipe) {
      throw new ResourceInitializationException(ipe);
    }
    extractor.initialize(context, getConfigParameters(context));
  }

  @Override
  public boolean doHasNext() throws IOException, CollectionException {
    if (br == null) return false;

    while ((line = br.readLine()) != null) {
      lineNumber++;

      line = line.trim();
      if (!line.isEmpty()) {
        return true;
      }
    }

    try {
      br.close();
    } catch (IOException ioe) {
      getMonitor().debug("An error occurred when closing the BufferedReader", ioe);
    }
    br = null;

    return false;
  }

  @Override
  protected void doGetNext(JCas jCas) throws IOException, CollectionException {
    InputStream is = IOUtils.toInputStream(line, Charset.defaultCharset());
    extractor.processStream(is, file.getPath() + "#" + lineNumber, jCas);

    Metadata md = new Metadata(jCas);
    md.setKey("lineNumber");
    md.setValue(lineNumber.toString());
    getSupport().add(md);
  }

  @Override
  protected void doClose() throws IOException {
    if (br != null) {
      try {
        br.close();
      } catch (IOException ioe) {
        getMonitor().debug("An error occurred when closing the BufferedReader", ioe);
      }
      br = null;
    }

    if (extractor != null) {
      extractor.destroy();
      extractor = null;
    }
  }
}
