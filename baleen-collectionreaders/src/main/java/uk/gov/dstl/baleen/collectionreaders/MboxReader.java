// NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.james.mime4j.dom.BinaryBody;
import org.apache.james.mime4j.dom.Body;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.SingleBody;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.mboxiterator.CharBufferWrapper;
import org.apache.james.mime4j.mboxiterator.MboxIterator;
import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.apache.james.mime4j.stream.Field;
import org.apache.james.mime4j.stream.MimeConfig;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.io.CharStreams;

import uk.gov.dstl.baleen.core.utils.BaleenDefaults;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;
import uk.gov.dstl.baleen.uima.IContentExtractor;
import uk.gov.dstl.baleen.uima.UimaSupport;

/**
 * Read messages from an MBOX file, treating messages as plain text and using a content extractor to
 * process attachments.
 *
 * @baleen.javadoc
 */
public class MboxReader extends BaleenCollectionReader {
  /**
   * The MBOX file to process
   *
   * @baleen.config
   */
  public static final String PARAM_MBOX = "mbox";

  @ConfigurationParameter(name = PARAM_MBOX)
  private String mbox;

  /**
   * The charset to use to process the mbox
   *
   * @baleen.config UTF-8
   */
  public static final String PARAM_CHARSET = "charset";

  @ConfigurationParameter(name = PARAM_CHARSET, defaultValue = "UTF-8")
  private String charsetName;

  /**
   * The maximum message size in bytes.
   *
   * <p>Note that if it encounters a message bigger than this, it will stop processing.
   *
   * @baleen.config 10485760 (10MB)
   */
  public static final String PARAM_MAX_SIZE = "maxSize";

  @ConfigurationParameter(name = PARAM_MAX_SIZE, defaultValue = "10485760")
  private int messageSize;

  /**
   * Attachment extensions to ignore
   *
   * @baleen.config zip,tar,rar,jpg,gif,png
   */
  public static final String PARAM_IGNORE = "ignoreExtensions";

  @ConfigurationParameter(
    name = PARAM_IGNORE,
    defaultValue = {"zip", "tar", "rar", "jpg", "gif", "png"}
  )
  private String[] ignoreExtensions;

  /**
   * The content extractor to use to extract content from attachments
   *
   * @baleen.config Value of BaleenDefaults.DEFAULT_CONTENT_EXTRACTOR
   */
  public static final String PARAM_CONTENT_EXTRACTOR = "contentExtractor";

  @ConfigurationParameter(
    name = PARAM_CONTENT_EXTRACTOR,
    defaultValue = BaleenDefaults.DEFAULT_CONTENT_EXTRACTOR
  )
  private String contentExtractor;

  private IContentExtractor extractor;
  private Iterator<CharBufferWrapper> mboxIterator;
  private Charset charset;
  private DefaultMessageBuilder messageBuilder;

  private int count = 0;

  private TreeMap<String, Body> attachments = new TreeMap<>();
  private List<String> ignoreExtensionsList = new ArrayList<>();

  @Override
  protected void doInitialize(UimaContext context) throws ResourceInitializationException {
    // Initialise content extractor for attachments
    try {
      extractor = getContentExtractor(contentExtractor);
    } catch (InvalidParameterException ipe) {
      throw new ResourceInitializationException(ipe);
    }
    extractor.initialize(context, getConfigParameters(context));

    // Initialise charset for MBOX processing
    try {
      charset = Charset.forName(charsetName);
    } catch (UnsupportedCharsetException | IllegalCharsetNameException ce) {
      getMonitor().warn("Unsupported charset, {}. UTF-8 will be used.", charsetName, ce);
      charset = StandardCharsets.UTF_8;
    }

    // Initialise MBOX iterator
    try {
      mboxIterator =
          MboxIterator.fromFile(mbox)
              .charset(charset)
              .maxMessageSize(messageSize)
              .build()
              .iterator();
    } catch (IOException ioe) {
      throw new ResourceInitializationException(ioe);
    }

    // Initialise message parser
    messageBuilder = new DefaultMessageBuilder();
    messageBuilder.setContentDecoding(true);

    MimeConfig config = new MimeConfig.Builder().setMaxLineLen(10000).build();
    messageBuilder.setMimeEntityConfig(config);

    // Build list of extensions to ignore
    for (String s : ignoreExtensions) {
      ignoreExtensionsList.add(s.trim().toLowerCase());
    }
  }

  @Override
  protected void doGetNext(JCas jCas) throws IOException, CollectionException {
    if (!attachments.isEmpty()) {
      // If we have attachments, first process those
      Map.Entry<String, Body> entry = attachments.firstEntry();
      getMonitor().info("Processing attachment {}", entry.getKey());

      processBody(jCas, entry.getValue(), entry.getKey());

      attachments.remove(entry.getKey());
    } else {
      // No attachments so process the next message
      String raw = mboxIterator.next().toString();
      count++;

      String uri = "mbox://" + mbox + "#" + count;
      getMonitor().info("Processing message {}", uri);

      // Parse message and get body
      Message msg = messageBuilder.parseMessage(new ByteArrayInputStream(raw.getBytes(charset)));
      Body body = msg.getBody();

      boolean doneBody = false;

      // Decide how to process body of message
      if (body instanceof SingleBody) {
        doneBody = processBody(jCas, body, uri);
      } else if (body instanceof Multipart) {
        Multipart mp = (Multipart) body;
        doneBody = processMultipart(jCas, mp, uri);
      }

      // No body found (just attachments? Or invalid message?)
      if (!doneBody) {
        throw new IOException("No processable body found");
      }
    }
  }

  /** Process a multipart body part */
  private boolean processMultipart(JCas jCas, Multipart mp, String sourceUri) throws IOException {
    boolean doneBody = false;

    for (Entity e : mp.getBodyParts()) {
      if (e.getFilename() != null) {
        // Part has a filename, and is therefore an attachment
        String extension = FilenameUtils.getExtension(e.getFilename()).toLowerCase();
        if (ignoreExtensionsList.contains(extension)) {
          getMonitor().info("Skipping attachment {}", e.getFilename());
          continue;
        }

        attachments.put(sourceUri + "/" + e.getFilename(), e.getBody());
      } else if (!doneBody) {
        // Part has no filename, and we've not already processed a part to use as a body
        processBody(jCas, e.getBody(), sourceUri);

        // Add metadata
        for (Field f : e.getParent().getHeader().getFields()) {
          addMetadata(jCas, f.getName(), f.getBody());
        }

        doneBody = true;
      }
    }

    return doneBody;
  }

  /** Process a single body part */
  private boolean processBody(JCas jCas, Body body, String sourceUri) throws IOException {
    if (body instanceof TextBody) {
      // Process plain text body
      processTextBody(jCas, (TextBody) body);

      // Add fields from parent
      for (Field f : body.getParent().getHeader().getFields()) {
        addMetadata(jCas, f.getName(), f.getBody());
      }

      // Set up document annotation - this is done by the content extractor in other cases
      DocumentAnnotation doc = UimaSupport.getDocumentAnnotation(jCas);
      doc.setSourceUri(sourceUri);
      doc.setTimestamp(System.currentTimeMillis());
    } else if (body instanceof BinaryBody) {
      processBinaryBody(jCas, (BinaryBody) body, sourceUri);
    } else if (body instanceof Multipart) {
      // Multipart message, so recurse
      Multipart mp = (Multipart) body;
      return processMultipart(jCas, mp, sourceUri);
    } else {
      // No body processed
      return false;
    }

    return true;
  }

  /** Process body of message as an attachment */
  private void processBinaryBody(JCas jCas, BinaryBody binaryBody, String sourceUrl)
      throws IOException {
    extractor.processStream(binaryBody.getInputStream(), sourceUrl, jCas);
  }

  /** Process body of message as plain text */
  private void processTextBody(JCas jCas, TextBody textBody) throws IOException {
    String text = CharStreams.toString(textBody.getReader());
    jCas.setDocumentText(text.trim());
  }

  /** Helper function to add metadata to JCas */
  private void addMetadata(JCas jCas, String key, String value) {
    Metadata md = new Metadata(jCas);
    md.setKey(key);
    md.setValue(value);
    md.addToIndexes();
  }

  @Override
  protected void doClose() throws IOException {
    extractor.destroy();
  }

  @Override
  public boolean doHasNext() throws IOException, CollectionException {
    return !attachments.isEmpty() || mboxIterator.hasNext();
  }
}
