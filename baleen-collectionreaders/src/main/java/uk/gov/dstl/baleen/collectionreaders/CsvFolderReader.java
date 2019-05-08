// NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;

import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;
import uk.gov.dstl.baleen.uima.UimaSupport;

/**
 * Read a folder of CSV files and process each line in the CSV file as a separate document (with one
 * content column, and other columns being used as metadata).
 *
 * @baleen.javadoc
 */
public class CsvFolderReader extends BaleenCollectionReader {
  /**
   * A list of folders to watch
   *
   * @baleen.config <i>Current directory</i>
   */
  public static final String PARAM_FOLDERS = "folders";

  @ConfigurationParameter(
      name = PARAM_FOLDERS,
      defaultValue = {})
  private String[] folders;

  /**
   * Should files be reprocessed when modified?
   *
   * @baleen.config false
   */
  public static final String PARAM_REPROCESS_ON_MODIFY = "reprocess";

  @ConfigurationParameter(name = PARAM_REPROCESS_ON_MODIFY, defaultValue = "false")
  private boolean reprocessOnModify = false;

  /**
   * Should folders be processed recursively (i.e. should we watch subfolders too)?
   *
   * @baleen.config true
   */
  public static final String PARAM_RECURSIVE = "recursive";

  @ConfigurationParameter(name = PARAM_RECURSIVE, defaultValue = "true")
  private boolean recursive = true;

  /**
   * The separator for the files
   *
   * @baleen.config ,
   */
  public static final String PARAM_SEPARATOR = "separator";

  @ConfigurationParameter(name = PARAM_SEPARATOR, defaultValue = ",")
  private String separator;

  /**
   * The file extension to filter by
   *
   * @baleen.config csv
   */
  public static final String PARAM_EXTENSION = "extension";

  @ConfigurationParameter(name = PARAM_EXTENSION, defaultValue = "csv")
  private String extension;

  /**
   * Text column(s) in CSV - these columns will be used as the main content to process. If more than
   * one column is specified, then the content of the cells will be joined with two newlines.
   *
   * @baleen.config content
   */
  public static final String PARAM_TEXT_COLUMNS = "text";

  @ConfigurationParameter(
      name = PARAM_TEXT_COLUMNS,
      defaultValue = {"content"})
  private String[] textColumn;

  private WatchService watcher;
  private Map<WatchKey, Path> watchKeys = new HashMap<>();
  private List<Path> queue = new ArrayList<>();

  private List<String> columnHeadings = new ArrayList<>();
  private Path currPath;
  private List<String> currLines = new ArrayList<>();

  private CSVParser csvParser;

  private int currentLine = 0;

  @Override
  public void doInitialize(UimaContext context) throws ResourceInitializationException {
    if (folders == null || folders.length == 0) {
      folders = new String[1];
      folders[0] = System.getProperty("user.dir");
    }

    try {
      watcher = FileSystems.getDefault().newWatchService();
    } catch (IOException ioe) {
      throw new ResourceInitializationException(ioe);
    }

    csvParser = new CSVParserBuilder().withSeparator(separator.charAt(0)).build();
    registerFolders();
  }

  private void registerFolders() {
    for (String folder : folders) {
      try {
        Path p = Paths.get(folder);
        p = p.toRealPath();

        if (recursive) {
          Files.walkFileTree(
              p,
              new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attr)
                    throws IOException {
                  registerDirectory(dir);
                  return FileVisitResult.CONTINUE;
                }
              });
        } else {
          registerDirectory(p);
        }
        addFilesFromDir(p.toFile());
      } catch (IOException ioe) {
        getMonitor()
            .warn(
                "Could not find or register folder '{}' or it's subfolders - folder will be skipped",
                folder,
                ioe);
      }
    }
  }

  private void registerDirectory(Path path) throws IOException {
    WatchKey key;
    if (reprocessOnModify) {
      key = path.register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
    } else {
      key = path.register(watcher, ENTRY_CREATE, ENTRY_DELETE);
    }

    watchKeys.put(key, path);

    getMonitor().counter("directories").inc();
  }

  private void addFilesFromDir(File dir) {
    File[] files = dir.listFiles();

    if (files == null) {
      return;
    }

    for (File file : files) {
      if (!file.isDirectory()) {
        if (addFile(file.toPath())) {
          getMonitor().counter("files").inc();
        }
      } else if (recursive) {
        addFilesFromDir(file);
      }
    }
  }

  @Override
  public void doGetNext(JCas jCas) throws IOException, CollectionException {
    if (currLines.isEmpty()) {
      // Read next file
      currPath = queue.remove(0);
      currentLine = 0;
      getMonitor().info("Processing file {}", currPath.toString());

      List<String> lines;
      try (Stream<String> ln = Files.lines(currPath)) {
        lines = ln.collect(Collectors.toList());
      }

      String header = lines.remove(0);
      columnHeadings = Arrays.asList(csvParser.parseLine(header));

      currLines.addAll(lines);
    }

    String line = currLines.remove(0);
    String[] cols = csvParser.parseLine(line);
    currentLine++;

    StringJoiner sj = new StringJoiner("\n\n");
    Map<String, String> meta = new HashMap<>();

    for (int i = 0; i < columnHeadings.size(); i++) {
      if (inArray(columnHeadings.get(i), textColumn)) {
        sj.add(cols[i]);
      } else {
        meta.put(columnHeadings.get(i), cols[i]);
      }
    }

    jCas.setDocumentText(sj.toString());
    for (Map.Entry<String, String> e : meta.entrySet()) {
      Metadata md = new Metadata(jCas);
      md.setKey(e.getKey());
      md.setValue(e.getValue());
      md.addToIndexes();
    }

    DocumentAnnotation doc = UimaSupport.getDocumentAnnotation(jCas);
    doc.setSourceUri(currPath.toString() + "#" + currentLine);
    doc.setTimestamp(System.currentTimeMillis());
  }

  @Override
  public void doClose() throws IOException {
    if (watcher != null) {
      watcher.close();
      watcher = null;
    }

    watchKeys.clear();
    queue.clear();
  }

  /**
   * Every time doHasNext() is called, check the WatchService for new events and add all new events
   * to the queue. Then return true if there are files on the queue, or false otherwise.
   *
   * <p>If the event indicates that a file has been deleted, ensure it is removed from the queue.
   */
  @Override
  public boolean doHasNext() throws IOException, CollectionException {
    WatchKey key;
    while ((key = watcher.poll()) != null) {
      for (WatchEvent<?> event : key.pollEvents()) {
        processEvent(key, event);
        getMonitor().meter("events").mark();
      }

      key.reset();
    }

    return !currLines.isEmpty() || !queue.isEmpty();
  }

  private void processEvent(WatchKey key, WatchEvent<?> event) {
    @SuppressWarnings("unchecked")
    WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;

    if (event.kind() == OVERFLOW) {
      getMonitor().warn("OVERFLOW event received - some files may be missing from the queue");
    } else if (event.kind() == ENTRY_DELETE) {
      getMonitor()
          .debug(
              "ENTRY_DELETE event received - file '{}' will be removed from queue",
              pathEvent.context());

      try {
        Path dir = watchKeys.get(key);
        if (dir != null) {
          Path resolved = dir.resolve(pathEvent.context());
          queue.remove(resolved);
        } else {
          getMonitor()
              .warn(
                  "WatchKey not found - file '{}' will not be removed from the queue",
                  pathEvent.context());
        }
      } catch (Exception ioe) {
        getMonitor()
            .warn(
                "An error occurred - file '{}' will not be removed from the queue",
                pathEvent.context(),
                ioe);
      }

      queue.remove(pathEvent.context());
    } else {
      getMonitor()
          .debug(
              event.kind().name() + " event received - file '{}' will be added to the queue",
              pathEvent.context());
      try {
        Path dir = watchKeys.get(key);
        if (dir != null) {
          Path resolved = dir.resolve(pathEvent.context());
          if (resolved.toFile().isDirectory()) {
            if (recursive) {
              addFilesFromDir(resolved.toFile());
              registerDirectory(resolved);
            }
          } else {
            addFile(resolved);
          }
        } else {
          getMonitor()
              .warn(
                  "WatchKey not found - file '{}' will not be added to the queue",
                  pathEvent.context());
        }
      } catch (Exception ioe) {
        getMonitor()
            .warn(
                "An error occurred - file '{}' will not be added to the queue",
                pathEvent.context(),
                ioe);
      }
    }
  }

  private boolean addFile(Path path) {
    if (path.toString().toLowerCase().endsWith("." + extension)) {
      queue.add(path);
      return true;
    } else {
      return false;
    }
  }

  /** Check whether the needle appears in the haystack (case insensitive) */
  private static boolean inArray(String needle, String[] haystack) {
    for (String h : haystack) {
      if (needle.equalsIgnoreCase(h)) {
        return true;
      }
    }

    return false;
  }
}
