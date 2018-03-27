// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.collector;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.Multimap;

import uk.gov.dstl.baleen.entity.linking.EntityInformation;
import uk.gov.dstl.baleen.entity.linking.InformationCollector;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.uima.utils.ReferentUtils;

/** Collects basic information about the entity from the JCas */
public class JCasInformationCollector implements InformationCollector {

  @Override
  public <T extends Entity> Set<EntityInformation<T>> getEntityInformation(
      JCas jCas, Class<T> clazz) {
    Multimap<ReferenceTarget, T> map = ReferentUtils.createReferentMap(jCas, clazz);
    Map<T, Collection<Sentence>> index = JCasUtil.indexCovering(jCas, clazz, Sentence.class);

    Set<EntityInformation<T>> infos = new HashSet<>();
    for (Map.Entry<ReferenceTarget, Collection<T>> entry : map.asMap().entrySet()) {
      Collection<Sentence> sentences =
          entry.getValue().stream().flatMap(m -> index.get(m).stream()).collect(Collectors.toSet());

      infos.add(new EntityInformation<T>(entry.getKey(), entry.getValue(), sentences));
    }

    return infos;
  }
}
