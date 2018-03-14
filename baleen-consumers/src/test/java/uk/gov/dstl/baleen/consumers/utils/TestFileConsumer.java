// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.exceptions.BaleenException;

public class TestFileConsumer extends AbstractBaleenFileConsumer {
  @ConfigurationParameter(defaultValue = "")
  private String extension;

  @Override
  protected void writeToFile(JCas jCas, File file) throws BaleenException {
    try {
      FileUtils.writeStringToFile(file, jCas.getDocumentText());
    } catch (IOException ioe) {
      throw new BaleenException(ioe);
    }
  }

  @Override
  protected String getExtension() {
    return extension;
  }
}
