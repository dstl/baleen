// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.mallet;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;
import com.google.common.collect.ImmutableSet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

/** A pipe to remove the given stopwords */
public class RemoveStopwords extends Pipe implements Serializable {

  private ImmutableSet<String> stopwords;

  /**
   * Create remove stopwords pipe
   *
   * @param stopwords to remove
   */
  public RemoveStopwords(Collection<String> stopwords) {
    this.stopwords = ImmutableSet.copyOf(stopwords);
  }

  @Override
  public Instance pipe(Instance carrier) {
    TokenSequence input = (TokenSequence) carrier.getData();
    TokenSequence output = new TokenSequence();
    for (int i = 0; i < input.size(); i++) {
      Token t = input.get(i);
      if (!stopwords.contains(t.getText())) {
        output.add(t);
      }
    }
    carrier.setData(output);
    return carrier;
  }

  // Serialization

  private static final long serialVersionUID = 1;
  private static final int CURRENT_SERIAL_VERSION = 0;

  private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeInt(CURRENT_SERIAL_VERSION);
    out.writeObject(stopwords);
  }

  @SuppressWarnings("unchecked")
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    int version = in.readInt();
    if (version == 0) {
      stopwords = (ImmutableSet<String>) in.readObject();
    }
  }
}
