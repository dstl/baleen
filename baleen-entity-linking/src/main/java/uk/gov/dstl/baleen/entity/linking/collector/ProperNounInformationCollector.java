// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.collector;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.Multimap;

import uk.gov.dstl.baleen.entity.linking.EntityInformation;
import uk.gov.dstl.baleen.entity.linking.InformationCollector;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.uima.utils.ReferentUtils;

/**
 * Collects basic information about the entity from the JCas and restrict interesting mentions to
 * proper nouns.
 *
 * <p>This requires part of speech tagging to be applied
 */
public class ProperNounInformationCollector implements InformationCollector {

  @Override
  public <T extends Entity> Set<EntityInformation<T>> getEntityInformation(
      JCas jCas, Class<T> clazz) {
    Multimap<ReferenceTarget, T> map = ReferentUtils.createReferentMap(jCas, clazz);
    Map<T, Collection<Sentence>> index = JCasUtil.indexCovering(jCas, clazz, Sentence.class);
    Map<T, Collection<WordToken>> tokens = JCasUtil.indexCovered(jCas, clazz, WordToken.class);

    Set<EntityInformation<T>> infos = new HashSet<>();
    for (Map.Entry<ReferenceTarget, Collection<T>> entry : map.asMap().entrySet()) {
      Collection<Sentence> sentences =
          entry.getValue().stream().flatMap(m -> index.get(m).stream()).collect(Collectors.toSet());

      List<T> properNouns =
          entry
              .getValue()
              .stream()
              .filter(
                  e ->
                      tokens
                          .get(e)
                          .stream()
                          .map(WordToken::getPartOfSpeech)
                          .anyMatch("NNP"::equals))
              .collect(toList());

      infos.add(new EntityInformation<T>(entry.getKey(), properNouns, sentences));
    }

    return infos;
  }
}
