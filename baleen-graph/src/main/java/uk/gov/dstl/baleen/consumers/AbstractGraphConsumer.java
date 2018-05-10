// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.core.utils.BuilderUtils;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.graph.coerce.ValueCoercer;
import uk.gov.dstl.baleen.uima.BaleenConsumer;

/** Abstract common graph properties. */
public abstract class AbstractGraphConsumer extends BaleenConsumer {

  /**
   * Should a hash of the content be used to generate the ID? If false, then a hash of the Source
   * URI is used instead.
   *
   * @baleen.config true
   */
  public static final String PARAM_CONTENT_HASH_AS_ID = "contentHashAsId";

  @ConfigurationParameter(name = PARAM_CONTENT_HASH_AS_ID, defaultValue = "false")
  protected boolean contentHashAsId;

  /**
   * Should we output the events to the graph?
   *
   * @baleen.config true
   */
  public static final String PARAM_OUTPUT_EVENTS = "outputEvents";

  @ConfigurationParameter(name = PARAM_OUTPUT_EVENTS, defaultValue = "true")
  protected boolean outputEvents = true;

  /**
   * Filter the given features from the output to the graph
   *
   * @baleen.config
   */
  public static final String PARAM_FILTER_FEATURES = "filterFeatures";

  @ConfigurationParameter(name = PARAM_FILTER_FEATURES, defaultValue = "")
  protected String[] filterFeatures;

  /**
   * Use only the given types to output to the graph
   *
   * <p>Do not set for all types.
   *
   * @baleen.config Person,Location,...
   */
  public static final String PARAM_TYPE_NAMES = "typeNames";

  @ConfigurationParameter(name = PARAM_TYPE_NAMES, mandatory = false)
  protected String[] typeNames;

  /**
   * Use the given value coercer to coerce value types the graph supports
   *
   * @baleen.config Id, ToString...
   */
  public static final String PARAM_VALUE_COERCER = "valueCoercer";

  @ConfigurationParameter(name = PARAM_VALUE_COERCER, defaultValue = "Id")
  protected String valueCoercerType;

  protected ValueCoercer valueCoercer;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    valueCoercer = createValueCoercer(valueCoercerType);
    super.doInitialize(aContext);
  }

  protected ValueCoercer createValueCoercer(String valueCoercerType)
      throws ResourceInitializationException {
    try {
      Class<? extends ValueCoercer> classFromString =
          BuilderUtils.getClassFromString(
              valueCoercerType, ValueCoercer.class.getPackage().getName());
      return classFromString.newInstance();
    } catch (InvalidParameterException | InstantiationException | IllegalAccessException e) {
      throw new ResourceInitializationException(e);
    }
  }
}
