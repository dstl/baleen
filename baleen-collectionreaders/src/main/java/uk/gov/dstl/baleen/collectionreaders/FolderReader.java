// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.uima.BaleenCollectionReader;

/**
 * Inspect a folder for unprocessed files, and process them through the pipeline. Currently, the
 * list of previously processed files is held in memory and so will be lost if the server is
 * restarted. This can be avoided by using the MoveSourceConsumer (for example), and removing the
 * files after processing
 *
 * @baleen.javadoc
 */
public class FolderReader extends BaleenCollectionReader {

  /**
   * A list of folders to watch
   *
   * @baleen.config <i>Current directory</i>
   */
  public static final String PARAM_FOLDERS = "folders";

  @ConfigurationParameter(
    name = PARAM_FOLDERS,
    defaultValue = {}
  )
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
   * A list of patterns that the filename must match. This can be used for specifying allowed file
   * extensions (e.g. .*\\.txt would accept all text files).
   *
   * <p>Files are accepted if they match any of the specified patterns. If no patterns are
   * specified, then all files will be accepted.
   *
   * <p>Patterns are treated as case insensitive.
   *
   * @baleen.config
   */
  public static final String PARAM_ACCEPTED_PATTERNS = "acceptedFilenames";

  @ConfigurationParameter(
    name = PARAM_ACCEPTED_PATTERNS,
    defaultValue = {}
  )
  private String[] acceptedFilenames;

  private List<Pattern> acceptedFilenamesSet = new ArrayList<>();

  private WatchService watcher;
  private Map<WatchKey, Path> watchKeys = new HashMap<>();
  private List<Path> queue = new ArrayList<>();

  @Override
  public void doInitialize(UimaContext context) throws ResourceInitializationException {
    if (folders == null || folders.length == 0) {
      folders = new String[1];
      folders[0] = System.getProperty("user.dir");
    }

    for (String pattern : acceptedFilenames) {
      try {
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

        acceptedFilenamesSet.add(p);
      } catch (PatternSyntaxException pse) {
        getMonitor()
            .warn(
                "Could not compile pattern '{}', it will not be included in the set of accepted filenames",
                pattern,
                pse);
      }
    }

    try {
      watcher = FileSystems.getDefault().newWatchService();
    } catch (IOException ioe) {
      throw new ResourceInitializationException(ioe);
    }

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

    for (int i = 0; i < files.length; i++) {
      if (!files[i].isDirectory()) {
        addFile(files[i].toPath());
        getMonitor().counter("files").inc();
      } else if (recursive) {
        addFilesFromDir(files[i]);
      }
    }
  }

  @Override
  public void doGetNext(JCas jCas) throws IOException, CollectionException {
    if (queue.isEmpty()) {
      getMonitor().error("No documents on the queue - this method should not have been called");
      throw new CollectionException();
    }
    Path path = queue.remove(0);
    getMonitor().info("Processing file {}", path.toString());
    try (InputStream is = new FileInputStream(path.toFile()); ) {
      extractContent(is, path.toString(), jCas);
    }
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

    return !queue.isEmpty();
  }

  private void processEvent(WatchKey key, WatchEvent<?> event) {
    @SuppressWarnings("unchecked")
    WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;

    if (event.kind() == OVERFLOW) {
      getMonitor().warn("OVERFLOW event received - some files may be missing from the queue");
    } else if (event.kind() == ENTRY_DELETE) {
      removeFileFromQueue(key, pathEvent);
    } else {
      addFileToQueue(key, event, pathEvent);
      getMonitor()
          .warn(
              "WatchKey not found - file '{}' will not be removed from the queue",
              pathEvent.context());
    }
  }

  private void addFileToQueue(WatchKey key, WatchEvent<?> event, WatchEvent<Path> pathEvent) {
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

  private void removeFileFromQueue(WatchKey key, WatchEvent<Path> pathEvent) {
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
  }

  private void addFile(Path path) {
    if (acceptedFilenamesSet.isEmpty()) {
      queue.add(path);
    } else {
      for (Pattern p : acceptedFilenamesSet) {
        Matcher m = p.matcher(path.getFileName().toString());
        if (m.matches()) {
          queue.add(path);
          return;
        }
      }
    }
  }
}
