// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.utils.yaml;

import java.io.IOException;

/** A abstract helper to deal with YAML files */
public abstract class AbstractYaml extends AbstractBaseYaml {

  @Override
  public Object dataTree() throws IOException {
    return yamlProcessor.load(cleanTabsFromBeginning(getSource()));
  }
}
