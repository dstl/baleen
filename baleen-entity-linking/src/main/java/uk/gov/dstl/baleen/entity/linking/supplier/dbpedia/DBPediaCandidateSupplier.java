// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.supplier.dbpedia;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.entity.linking.CandidateSupplier;
import uk.gov.dstl.baleen.entity.linking.util.StringArgumentsHandler;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.semantic.Entity;

/** Abstract DBPediaCandidateSupplier class. Contains constructors and configure methods */
public abstract class DBPediaCandidateSupplier<T extends Entity> implements CandidateSupplier<T> {

  /** Language Parameter name */
  public static final String PARAM_LANGUAGE = "language";

  /** Results limit parameter name */
  public static final String PARAM_RESULTS_LIMIT = "resultsLimit";

  static final int DEFAULT_RESULTS_LIMIT = 100;
  private static final Logger LOGGER = LoggerFactory.getLogger(DBPediaCandidateSupplier.class);

  final DBPediaService dbPediaService;
  int resultsLimit;

  DBPediaCandidateSupplier() {
    this.dbPediaService = new DBPediaService();
    this.resultsLimit = DEFAULT_RESULTS_LIMIT;
  }

  DBPediaCandidateSupplier(DBPediaService dbPediaService, int resultsLimit) {
    this.dbPediaService = dbPediaService;
    this.resultsLimit = resultsLimit;
  }

  /**
   * Configure language and resultsLimit settings
   *
   * @param argumentPairs a Map of arguments
   * @throws Exception
   */
  @Override
  public void configure(String[] argumentPairs) throws BaleenException {
    Map<String, String> configKeyValuePairs =
        new StringArgumentsHandler(argumentPairs).createStringsMap();

    configureLanguage(configKeyValuePairs);
    configureResultsLimit(configKeyValuePairs);
  }

  private void configureLanguage(Map<String, String> configKeyValuePairs) {
    if (configKeyValuePairs.containsKey(PARAM_LANGUAGE)) {
      String language = configKeyValuePairs.get(PARAM_LANGUAGE);
      dbPediaService.setLanguage(language);
      LOGGER.info("Setting results language to {}", language);
    }
    LOGGER.info("No language parameter set, defaulting to {}", DBPediaService.DEFAULT_LANGUAGE);
  }

  private void configureResultsLimit(Map<String, String> configKeyValuePairs) {
    if (configKeyValuePairs.containsKey(PARAM_RESULTS_LIMIT)) {
      int limit = Integer.parseInt(configKeyValuePairs.get(PARAM_RESULTS_LIMIT));
      this.resultsLimit = limit;
      LOGGER.info("Setting results limit to {}", limit);
    }
    LOGGER.info("No resultsLimit parameter set, defaulting to {}", DEFAULT_RESULTS_LIMIT);
  }

  @Override
  public void close() {
    // DO NOTHING
  }
}
