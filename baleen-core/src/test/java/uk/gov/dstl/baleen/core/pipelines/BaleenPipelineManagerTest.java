// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.pipelines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.gov.dstl.baleen.core.utils.yaml.YamlConfiguration;

/** Basic test of baleen pipline manager, */
public class BaleenPipelineManagerTest {

  @Test
  public void testWithoutConfiguration() throws Exception {
    BaleenPipelineManager manager = new BaleenPipelineManager();
    try {
      manager.configure(new YamlConfiguration());
      assertEquals(0, manager.getAll().size());
    } finally {
      manager.stop();
    }
  }

  @Test
  public void testWithSinglePipline() throws Exception {
    BaleenPipelineManager manager = new BaleenPipelineManager();
    try {
      YamlConfiguration config = new YamlConfiguration(getClass(), "single.yaml");
      manager.configure(config);
      assertEquals(1, manager.getAll().size());
      assertTrue(manager.get("single").isPresent());
    } finally {
      manager.stop();
    }
  }

  @Test
  public void testWithDoublePipline() throws Exception {
    BaleenPipelineManager manager = new BaleenPipelineManager();
    try {
      YamlConfiguration config = new YamlConfiguration(getClass(), "double.yaml");
      manager.configure(config);
      assertEquals(2, manager.getAll().size());
      assertTrue(manager.get("one").isPresent());
      assertTrue(manager.get("two").isPresent());
    } finally {
      manager.stop();
    }
  }

  @Test
  public void testRemoveSinglePipline() throws Exception {
    BaleenPipelineManager manager = new BaleenPipelineManager();
    try {
      YamlConfiguration config = new YamlConfiguration(getClass(), "single.yaml");
      manager.configure(config);
      manager.remove("single");
      assertEquals(0, manager.getAll().size());
    } finally {
      manager.stop();
    }
  }
}
