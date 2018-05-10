// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.mallet;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/** Object reader for persistence of Mallet objects */
public class FileObject<T> {

  private final String path;

  /**
   * Construct the FileObject
   *
   * @param path to the object
   */
  public FileObject(String path) {
    this.path = path;
  }

  /** @return the object from the file */
  @SuppressWarnings("unchecked")
  public T object() {
    try (ObjectInputStream ois =
        new ObjectInputStream(new BufferedInputStream(new FileInputStream(path)))) {
      return (T) ois.readObject();
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Problem loading object from file " + path + ": " + e.getMessage());
    }
  }
}
