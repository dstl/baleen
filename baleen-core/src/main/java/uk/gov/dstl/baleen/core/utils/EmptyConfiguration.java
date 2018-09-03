// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.core.utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

/** An empty implementation of the configuration interface. */
public class EmptyConfiguration implements Configuration {

  @Override
  public <T> List<T> getAsList(String path) {
    return ImmutableList.of();
  }

  @Override
  public List<Map<String, Object>> getAsListOfMaps(String path) {
    return ImmutableList.of();
  }

  @Override
  public <T> Optional<T> get(Class<T> clazz, String path) {
    return Optional.empty();
  }

  @Override
  public <T> T get(Class<T> clazz, String path, T defaultValue) {
    return defaultValue;
  }

  @Override
  public String originalConfig() throws IOException {
    return "";
  }

  @Override
  public <T> Optional<T> getFirst(Class<T> clazz, String... paths) {
    return Optional.empty();
  }
}
