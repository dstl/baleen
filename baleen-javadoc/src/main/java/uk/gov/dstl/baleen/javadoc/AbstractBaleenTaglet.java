// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.javadoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * An abstract class implementing common functionality between the different Baleen Javadoc taglets.
 */
public abstract class AbstractBaleenTaglet implements Taglet {

  @Override
  public boolean inConstructor() {
    return false;
  }

  @Override
  public boolean inField() {
    return false;
  }

  @Override
  public boolean inMethod() {
    return false;
  }

  @Override
  public boolean inOverview() {
    return false;
  }

  @Override
  public boolean inPackage() {
    return false;
  }

  @Override
  public boolean inType() {
    return false;
  }

  @Override
  public boolean isInlineTag() {
    return false;
  }

  @Override
  public String toString(Tag tag) {
    Tag[] tags = new Tag[1];
    tags[0] = tag;

    return toString(tags);
  }

  /** Return a list of fields for this class and all it's superclasses */
  protected static List<FieldDoc> getFields(ClassDoc classDoc) {
    List<FieldDoc> ret = new ArrayList<>();
    ClassDoc parent = classDoc.superclass();
    if (parent != null) {
      ret.addAll(getFields(parent));
    }

    ret.addAll(Arrays.asList(classDoc.fields()));

    return ret;
  }
}
