// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.util;

import static java.util.stream.Collectors.toSet;

import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.entity.linking.EntityInformation;
import uk.gov.dstl.baleen.entity.linking.InformationCollector;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

@SuppressWarnings({"unchecked", "rawtypes"})
public class MockInformationCollector implements InformationCollector {

  @Override
  public <T extends Entity> Set<EntityInformation<T>> getEntityInformation(
      JCas jCas, Class<T> clazz) {
    return (Set)
        JCasUtil.select(jCas, ReferenceTarget.class)
            .stream()
            .map(EntityInformation::new)
            .collect(toSet());
  }
}
