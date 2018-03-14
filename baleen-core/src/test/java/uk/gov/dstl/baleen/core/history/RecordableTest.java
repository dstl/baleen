// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.history;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Vector;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.junit.Test;

public class RecordableTest {

  @Test
  public void testNoTypeGetType() {
    Recordable r = new NoTypeTestRecordable();

    assertEquals("", r.getTypeName());
  }

  @Test
  public void testTypeGetType() {
    Recordable r = new TypeTestRecordable();

    assertEquals("Test", r.getTypeName());
  }
}

class NoTypeTestRecordable implements Recordable {

  @Override
  public long getInternalId() {
    return 123;
  }

  @Override
  public String getCoveredText() {
    return "Covered Text";
  }

  @Override
  public int getBegin() {
    return 0;
  }

  @Override
  public int getEnd() {
    return 5;
  }

  @Override
  public Type getType() {
    return null;
  }
}

class TypeTestRecordable implements Recordable {

  @Override
  public long getInternalId() {
    return 123;
  }

  @Override
  public String getCoveredText() {
    return "Covered Text";
  }

  @Override
  public int getBegin() {
    return 0;
  }

  @Override
  public int getEnd() {
    return 5;
  }

  @Override
  public Type getType() {
    return new Type() {

      @Override
      public boolean isPrimitive() {
        return false;
      }

      @Override
      public boolean isInheritanceFinal() {
        return false;
      }

      @Override
      public boolean isFeatureFinal() {
        return false;
      }

      @Override
      public boolean isArray() {
        return false;
      }

      @Override
      public String getShortName() {
        return null;
      }

      @Override
      public int getNumberOfFeatures() {
        return 0;
      }

      @Override
      public String getName() {
        return "Test";
      }

      @Override
      public List<Feature> getFeatures() {
        return null;
      }

      @Override
      public Feature getFeatureByBaseName(String featureName) {
        return null;
      }

      @Override
      public Type getComponentType() {
        return null;
      }

      @Override
      public Vector<Feature> getAppropriateFeatures() {
        return null;
      }
    };
  }
}
