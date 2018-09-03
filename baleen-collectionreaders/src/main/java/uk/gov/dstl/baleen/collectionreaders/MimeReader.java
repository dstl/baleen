// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.collectionreaders;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.Deque;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.mail.Address;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.base.Splitter;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Text;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.types.semantic.Temporal;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;
import uk.gov.dstl.baleen.uima.UimaSupport;
import uk.gov.dstl.baleen.uima.utils.TemporalUtils;

/**
 * Collection reader for the Multipurpose Internet Mail Extensions (MIME) format.
 *
 * <p>For example, this can be used with the Enron dataset <a href="https://www.cs.cmu.edu/~enron/">
 * https://www.cs.cmu.edu/~enron/</a>
 */
public class MimeReader extends BaleenCollectionReader {

  private static final Splitter COMMA_SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings();

  /**
   * Root folder to start from
   *
   * @baleen.config <i>Current directory</i>
   */
  public static final String PARAM_FOLDER = "folder";

  @ConfigurationParameter(name = PARAM_FOLDER, defaultValue = ".")
  private String rootFolder;

  private Deque<Path> files = new LinkedList<>();

  private int total;

  @Override
  protected void doInitialize(final UimaContext context) throws ResourceInitializationException {

    final File rootDir = new File(rootFolder);

    if (!rootDir.exists() || !rootDir.isDirectory()) {
      throw new ResourceInitializationException(new BaleenException("Root dir is not a folder"));
    }

    try (Stream<Path> stream = Files.walk(rootDir.toPath())) {
      stream
          .filter(Files::isRegularFile)
          .filter(f -> !f.toFile().getName().equalsIgnoreCase(".DS_Store"))
          .forEach(files::add);

      if (files.isEmpty()) {
        throw new BaleenException("No files found in root folder");
      }

      total = files.size();

      getMonitor().info("Processing {} emails", files.size());

    } catch (final Exception e) {
      throw new ResourceInitializationException(e);
    }
  }

  @Override
  protected void doClose() throws IOException {
    files.clear();
  }

  @Override
  public boolean doHasNext() throws IOException, CollectionException {
    return !files.isEmpty();
  }

  @Override
  protected void doGetNext(final JCas jCas) throws IOException, CollectionException {
    final Path path = files.pop();
    final File file = path.toFile();

    final int left = files.size();

    getMonitor()
        .info(
            "Processing {} ({} %)",
            file.getAbsolutePath(), String.format("%.2f", 100 * (total - left) / (double) total));

    try (FileInputStream is = new FileInputStream(file)) {
      final Session s = Session.getDefaultInstance(new Properties());
      final MimeMessageParser parser = new MimeMessageParser(new MimeMessage(s, is));
      parser.parse();
      final MimeMessage message = parser.getMimeMessage();

      final DocumentAnnotation da = UimaSupport.getDocumentAnnotation(jCas);
      da.setTimestamp(calculateBestDate(message, file));
      da.setDocType("email");
      da.setDocumentClassification("O");
      String source = file.getAbsolutePath().substring(rootFolder.length());
      da.setSourceUri(source);
      da.setLanguage("en");

      // Add all headers as metadata, with email prefix
      final Enumeration<Header> allHeaders = message.getAllHeaders();
      while (allHeaders.hasMoreElements()) {
        final Header header = allHeaders.nextElement();
        addMetadata(jCas, "email." + header.getName(), header.getValue());
      }

      addMetadata(jCas, "from", parser.getFrom());
      addMetadata(jCas, "to", parser.getTo());
      addMetadata(jCas, "cc", parser.getCc());
      addMetadata(jCas, "bcc", parser.getBcc());
      addMetadata(jCas, "subject", parser.getSubject());

      // Add fake title
      addMetadata(jCas, "title", parser.getSubject());

      String actualContent = parser.getPlainContent();

      if (actualContent == null) {
        actualContent = "";
      }

      // TODO: At this point we could create a representation of the addresses, etc in the content
      // eg a table of to, from, and etc
      // then annotate them a commsidentifier, date, person.
      // We could also create relations between sender and receiver

      String content = actualContent + "\n\n---\n\n";

      final String headerBlock = createHeaderBlock(content.length(), jCas, parser);
      content = content + headerBlock;

      final Text text = new Text(jCas);
      text.setBegin(0);
      text.setEnd(actualContent.length());
      text.addToIndexes();

      extractContent(new ByteArrayInputStream(content.getBytes()), source, jCas);
    } catch (final Exception e) {
      getMonitor().warn("Discarding message", e);
    }
  }

  private String createHeaderBlock(
      final int offset, final JCas jCas, final MimeMessageParser parser) throws Exception {

    final StringBuilder sb = new StringBuilder();

    final Date sentDate = parser.getMimeMessage().getSentDate();
    if (sentDate != null) {
      final Temporal temporal = new Temporal(jCas);
      temporal.setPrecision(TemporalUtils.PRECISION_EXACT);
      temporal.setScope(TemporalUtils.SCOPE_SINGLE);
      temporal.setTimestampStart(sentDate.getTime());
      temporal.setTimestampStop(sentDate.getTime());
      addToHeaderBlock(sentDate.toString(), offset, sb, temporal);
    }

    final CommsIdentifier from = addEmailToHeaderBlock(parser.getFrom(), offset, sb, jCas);

    // Create relations for from to destination
    Stream.of(parser.getTo().stream(), parser.getBcc().stream(), parser.getCc().stream())
        .flatMap(s -> s)
        .map(a -> addEmailToHeaderBlock(a, offset, sb, jCas))
        .forEach(
            reciever -> {
              final Relation r = new Relation(jCas);
              r.setBegin(offset);
              r.setEnd(offset);
              r.setRelationshipType("emails");
              r.setValue("emails");
              r.setSource(from);
              r.setTarget(reciever);
              r.addToIndexes();
            });

    final MimeMessage mimeMessage = parser.getMimeMessage();
    addPeopleToHeader(mimeMessage, "X-From", offset, jCas, sb);
    addPeopleToHeader(mimeMessage, "X-To", offset, jCas, sb);
    addPeopleToHeader(mimeMessage, "X-cc", offset, jCas, sb);
    addPeopleToHeader(mimeMessage, "X-bcc", offset, jCas, sb);

    return sb.toString();
  }

  private void addPeopleToHeader(
      final MimeMessage mimeMessage,
      final String header,
      final int offset,
      final JCas jCas,
      final StringBuilder sb)
      throws MessagingException {
    final String[] people = mimeMessage.getHeader(header);
    if (people == null) {
      return;
    }

    Arrays.stream(people)
        .flatMap(p -> COMMA_SPLITTER.splitToList(p).stream())
        // TODO: might want to strip off any emails in <> here
        .forEach(p -> addPersonToHeaderBlock(p, offset, sb, jCas));
  }

  private Person addPersonToHeaderBlock(
      final String s, final int offset, final StringBuilder sb, final JCas jCas) {
    final Person p = new Person(jCas);
    return addToHeaderBlock(s, offset, sb, p);
  }

  private CommsIdentifier addEmailToHeaderBlock(
      final Address a, final int offset, final StringBuilder sb, final JCas jCas) {
    final String v = a.toString();
    return addEmailToHeaderBlock(v, offset, sb, jCas);
  }

  private CommsIdentifier addEmailToHeaderBlock(
      final String v, final int offset, final StringBuilder sb, final JCas jCas) {

    final CommsIdentifier ci = new CommsIdentifier(jCas);
    ci.setSubType("email");

    return addToHeaderBlock(v, offset, sb, ci);
  }

  private <T extends Entity> T addToHeaderBlock(
      final String v, final int offset, final StringBuilder sb, final T annotation) {

    final int begin = sb.length() + offset;
    sb.append(v);
    final int end = sb.length() + offset;
    sb.append("\n");

    annotation.setBegin(begin);
    annotation.setEnd(end);
    annotation.setConfidence(1);
    annotation.setValue(v);
    annotation.addToIndexes();

    return annotation;
  }

  private long calculateBestDate(final MimeMessage message, final File file) {
    try {
      return message.getSentDate().getTime();
    } catch (final Exception e) {
      return file.lastModified();
    }
  }

  private void addMetadata(final JCas jCas, final String key, final List<Address> list) {
    if (list == null) {
      return;
    }

    list.stream().map(Address::toString).forEach(s -> addMetadata(jCas, key, s));
  }

  private void addMetadata(final JCas jCas, final String key, @Nullable final String value) {
    if (value == null || value.isEmpty()) {
      return;
    }

    final Metadata m = new Metadata(jCas);
    m.setKey(key);
    m.setValue(value);
    m.addToIndexes();
  }
}
