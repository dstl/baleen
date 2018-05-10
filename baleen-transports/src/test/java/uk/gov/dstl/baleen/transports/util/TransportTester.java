// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.util;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;

import uk.gov.dstl.baleen.uima.BaleenCollectionReader;

public class TransportTester extends JCasSerializationTester {

  private AnalysisEngine sender;

  private BaleenCollectionReader reader;

  public TransportTester(AnalysisEngine sender, BaleenCollectionReader reader)
      throws UIMAException {
    this.sender = sender;
    this.reader = reader;
  }

  public void run() throws UIMAException, IOException {
    sender.process(getIn());
    reader.getNext(getOut());
  }

  public void close() throws IOException {
    sender.destroy();
    reader.close();
  }
}
