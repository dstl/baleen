// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * Class for handling a varying amount of String arguments by putting them into a Map of String key
 * value pairs
 */
public class StringArgumentsHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(StringArgumentsHandler.class);
  private String[] argumentPairs;

  /**
   * Constructor for string arguments handler
   *
   * @param argumentPairs
   */
  public StringArgumentsHandler(String[] argumentPairs) {
    this.argumentPairs = argumentPairs;
  }

  /**
   * Get a Map of String key/values from a varied number of String arguments
   *
   * @return Map of Strings of key value pairs
   * @throws BaleenException if an odd number of arguments are provided
   */
  public Map<String, String> createStringsMap() throws BaleenException {
    if (argumentPairs.length % 2 != 0) {
      LOGGER.error("String arguments must be key value pairs");
      throw new BaleenException("Invalid number of arguments. Arguments must be key value pairs");
    }
    Map<String, String> map = new HashMap<>();

    for (int i = 0; i < argumentPairs.length - 1; i += 2) {
      map.put(argumentPairs[i], argumentPairs[i + 1]);
    }

    return map;
  }
}
