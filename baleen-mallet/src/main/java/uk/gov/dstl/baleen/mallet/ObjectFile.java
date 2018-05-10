// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.mallet;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/** Object writer for persistence of Mallet objects */
public class ObjectFile {

  private final Object o;
  private final String path;

  /**
   * Construct the Object file
   *
   * @param o The object to write
   * @param path The path to write to
   */
  public ObjectFile(Object o, String path) {
    this.o = o;
    this.path = path;
  }

  /** Write the object file to the path */
  public void write() {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
      oos.writeObject(o);
    } catch (Exception e) {
      throw new IllegalArgumentException("Couldn't write classifier to filename " + path);
    }
  }
}
