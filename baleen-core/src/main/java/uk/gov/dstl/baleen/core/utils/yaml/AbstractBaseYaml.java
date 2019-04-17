// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.utils.yaml;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;

import com.google.common.base.Splitter;

/** A abstract helper to deal with YAML files */
public abstract class AbstractBaseYaml implements Yaml {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBaseYaml.class);

  private static final Splitter NEWLINE_SPLITTER = Splitter.on(Pattern.compile("\r?\n"));
  private static final String TAB_AS_SPACES = "    ";

  protected final org.yaml.snakeyaml.Yaml yamlProcessor;

  /** New instance. */
  public AbstractBaseYaml() {
    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(FlowStyle.BLOCK);
    options.setPrettyFlow(true);
    yamlProcessor = new org.yaml.snakeyaml.Yaml(options);
  }

  protected abstract String getSource() throws IOException;

  @Override
  public String original() throws IOException {
    return getSource();
  }

  @Override
  public String formatted() throws IOException {
    return yamlProcessor.dump(dataTree());
  }

  protected String cleanTabsFromBeginning(String yaml) {

    if (yaml.contains("\t")) {
      LOGGER.warn(
          "Yaml contains a tab characters, automatically converting to {} spaces "
              + "(if they occur at the beginning of a sentence). This may cause parsing"
              + " errors, please reformat the Yaml to use spaces only.",
          TAB_AS_SPACES.length());
      List<String> lines = NEWLINE_SPLITTER.splitToList(yaml);

      StringBuilder sb = new StringBuilder();
      for (String line : lines) {

        String cleanLine = replaceStartingTabsWithSpaces(line);

        sb.append(cleanLine);
        sb.append("\n");
      }
      return sb.toString();
    } else {
      return yaml;
    }
  }

  private static String replaceStartingTabsWithSpaces(String line) {
    int index = StringUtils.indexOfAnyBut(line, "\t ");
    if (index == -1) {
      return line;
    } else {
      String start = line.substring(0, index).replaceAll("\t", TAB_AS_SPACES);
      String end = line.substring(index);
      return start + end;
    }
  }

  @Override
  public String toString() {
    try {
      return formatted();
    } catch (IOException e) {
      return super.toString();
    }
  }
}
