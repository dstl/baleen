// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.mallet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.primitives.Primitives;

import uk.gov.dstl.baleen.core.utils.BuilderUtils;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;

/**
 * Factory for {@link ClassifierTrainer}s based on the Mallet string format.
 *
 * <pre>
 * ClassName,parameterName=parameterValue,parameterName=parameterValue
 * </pre>
 *
 * For example
 *
 * <pre>
 * MaxEnt,gaussianPriorVariance=10,numIterations=20
 * </pre>
 */
public class ClassifierTrainerFactory {

  private final String trainerDescriptor;

  /**
   * New instance
   *
   * @param trainerDescriptor {@link String} based on the Mallet string format.
   *     <pre>
   * ClassName,parameterName=parameterValue,parameterName=parameterValue
   * </pre>
   *     For example
   *     <pre>
   * MaxEnt,gaussianPriorVariance=1.0,numIterations=1000
   * </pre>
   */
  public ClassifierTrainerFactory(String trainerDescriptor) {
    this.trainerDescriptor = trainerDescriptor;
  }

  /** {@link ClassifierTrainer} according to the specification */
  @SuppressWarnings("unchecked")
  public <T extends Classifier> ClassifierTrainer<T> createTrainer() {
    String[] fields = trainerDescriptor.split(",");
    ClassifierTrainer<T> trainer =
        (ClassifierTrainer<T>) createTrainer(resolveTrainerClassName(fields[0]));
    setParameterValues(fields, trainer);
    return trainer;
  }

  @VisibleForTesting
  protected void setParameterValues(String[] fields, ClassifierTrainer<?> trainer) {
    for (int i = 1; i < fields.length; i++) {
      setParameterValue(trainer, fields[i]);
    }
  }

  private void setParameterValue(ClassifierTrainer<?> trainer, String parameterDescription) {
    String[] nameValuePair = parameterDescription.split("=");
    String parameterName = nameValuePair[0].trim();
    String parameterValue = nameValuePair[1].trim();
    Object parameterValueObject = convertToParameterValue(parameterValue);
    String setterName =
        "set" + Character.toUpperCase(parameterName.charAt(0)) + parameterName.substring(1);
    try {
      Method method =
          trainer
              .getClass()
              .getMethod(setterName, Primitives.unwrap(parameterValueObject.getClass()));
      method.invoke(trainer, parameterValueObject);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e1) {
      try {
        Method method = trainer.getClass().getMethod(setterName, parameterValueObject.getClass());
        method.invoke(trainer, parameterValueObject);
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e2) {
        throw new IllegalArgumentException(
            String.format(
                "Error constructing trainer %s, setter for %s not found", trainer, parameterName),
            e2);
      }
    }
  }

  private Object convertToParameterValue(String parameterValue) {
    try {
      return Integer.parseInt(parameterValue);
    } catch (NumberFormatException e) {
      // IGNORE
    }
    try {
      return Double.parseDouble(parameterValue);
    } catch (NumberFormatException e) {
      // IGNORE
    }
    if ("false".equals(parameterValue)) {
      return false;
    }
    if ("true".equals(parameterValue)) {
      return false;
    }
    return parameterValue;
  }

  private String resolveTrainerClassName(String classArg) {
    String className;
    int indexOf = classArg.indexOf('(');
    if (indexOf != -1) {
      className = classArg.substring(0, indexOf);
    } else {
      if (classArg.endsWith("Trainer")) {
        className = classArg;
      } else {
        className = classArg + "Trainer";
      }
    }
    return className.trim();
  }

  private ClassifierTrainer<?> createTrainer(String className) {
    try {
      return (ClassifierTrainer<?>)
          BuilderUtils.getClassFromString(className, "cc.mallet.classify").newInstance();
    } catch (ClassCastException
        | InstantiationException
        | IllegalAccessException
        | InvalidParameterException e) {
      throw new IllegalArgumentException(String.format("Unknown trainer %s", className), e);
    }
  }
}
