// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.mallet;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import cc.mallet.classify.ClassifierTrainer;

@RunWith(Parameterized.class)
public class ParameterClassifierTrainerTest {

  @Parameters
  public static Iterable<? extends Object> data() {
    return Arrays.asList(
        "C45Trainer",
        "NaiveBayes",
        "DecisionTreeTrainer,maxDepth=5",
        "MaxEnt,gaussianPriorVariance=10.0,numIterations=20",
        "MaxEnt, gaussianPriorVariance=10.0, numIterations=20",
        "MCMaxEnt, hyperbolicPriorSharpness=1.23, UseHyperbolicPrior = false");
  }

  @Parameter(0)
  public String trainerDescriptor;

  @Test
  public void testFactory() throws ResourceInitializationException {
    ClassifierTrainerFactory factory = new ClassifierTrainerFactory(trainerDescriptor);
    ClassifierTrainer<?> trainer = factory.createTrainer();
    assertNotNull(trainer);
  }
}
