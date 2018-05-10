// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.mallet;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.MaxEntTrainer;

public class ClassifierTrainerTest {

  @Test
  public void testFactory() throws ResourceInitializationException {
    ClassifierTrainerFactory factory =
        new ClassifierTrainerFactory("MaxEnt,gaussianPriorVariance=10.0,numIterations=20");
    ClassifierTrainer<?> trainer = factory.createTrainer();
    assertNotNull(trainer);
  }

  @Test
  public void testParameterSettings() throws ResourceInitializationException {

    MaxEntTrainer mock = mock(MaxEntTrainer.class);
    ClassifierTrainerFactory factory =
        new ClassifierTrainerFactory("MaxEnt,gaussianPriorVariance=10.0,numIterations=20");
    factory.setParameterValues(
        new String[] {"MaxEnt", "gaussianPriorVariance=10.0", "numIterations=20"}, mock);

    verify(mock, times(1)).setGaussianPriorVariance(10);
    verify(mock, times(1)).setNumIterations(20);
  }
}
