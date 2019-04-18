// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.serialisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.types.BaleenAnnotation;
import uk.gov.dstl.baleen.uima.UimaMonitor;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * A builder for creating a {@link JsonJCasConverter}. Optional whitelist and blacklist can be added
 * using the string names of the types, optionally including the package.
 */
public class JsonJCasConverterBuilder {

  private final JCas jCas;
  private final UimaMonitor monitor;
  private Collection<String> whitelist = Collections.emptyList();
  private Collection<String> blacklist = Collections.emptyList();

  /**
   * Construct the builder with the mandatory arguments
   *
   * @param monitor the {@link UimaMonitor}
   * @throws ResourceInitializationException if unable to initialise
   */
  public JsonJCasConverterBuilder(UimaMonitor monitor) throws ResourceInitializationException {
    jCas = createJas();
    this.monitor = monitor;
  }

  private JCas createJas() throws ResourceInitializationException {
    try {
      return JCasFactory.createJCas(TypeSystemSingleton.getTypeSystemDescriptionInstance());
    } catch (UIMAException e) {
      throw new ResourceInitializationException(e);
    }
  }

  /**
   * Add a whitelist
   *
   * @param whitelist collection of types names (optionally including the package) to whitelist
   * @return this
   */
  public JsonJCasConverterBuilder withWhitelist(Collection<String> whitelist) {
    if (whitelist != null) {
      this.whitelist = whitelist;
    }
    return this;
  }

  /**
   * Add a blacklist
   *
   * @param blacklist collection of types names (optionally including the package) to blacklist
   * @return this
   */
  public JsonJCasConverterBuilder withBlacklist(Collection<String> blacklist) {
    if (blacklist != null) {
      this.blacklist = blacklist;
    }
    return this;
  }

  /**
   * Build the {@link JsonJCasConverter}
   *
   * @return the {@link JsonJCasConverter}
   */
  public JsonJCasConverter build() {
    Collection<Class<? extends BaleenAnnotation>> white = createList(whitelist);
    Collection<Class<? extends BaleenAnnotation>> black = createList(blacklist);
    return new JsonJCasConverter(monitor, white, black);
  }

  @SuppressWarnings("unchecked")
  private Collection<Class<? extends BaleenAnnotation>> createList(Collection<String> types) {
    Collection<Class<? extends BaleenAnnotation>> list = new ArrayList<>();
    for (String type : types) {
      list.add((Class<? extends BaleenAnnotation>) TypeUtils.getType(type, jCas));
    }
    return list;
  }
}
