// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis.converters;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import uk.gov.dstl.baleen.consumers.analysis.data.BaleenEntity;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenMention;

/**
 * Merges information from Baleen mentions with the same entity id (of the same reference target)
 * into the an 'entity object'.
 *
 * <p>Types, value, properties, etc from each mention are combined into a single merged entity POJO.
 *
 * <p>This class contains the 'merging' logic, and does not look at reference targets or the JCas.
 * It assumes the mentions have been created correctly before passing.
 */
public class EntityConverter {

  /**
   * Create a series of entity from the mentions in a document.
   *
   * @param mentions converted mentions in this document.
   * @return map of entityId to entity
   */
  public Map<String, BaleenEntity> convert(final Map<String, BaleenMention> mentions) {

    final Multimap<String, BaleenMention> groupedMentions = collateMentionsIntoEntities(mentions);

    return groupedMentions.asMap().entrySet().stream()
        .map(
            e -> {
              final String entityId = e.getKey();
              return createEntity(entityId, e.getValue());
            })
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toMap(BaleenEntity::getExternalId, e -> e));
  }

  private Multimap<String, BaleenMention> collateMentionsIntoEntities(
      final Map<String, BaleenMention> mentions) {
    final Multimap<String, BaleenMention> map =
        MultimapBuilder.hashKeys().arrayListValues().build();
    mentions.values().forEach(m -> map.put(m.getEntityId(), m));
    return map;
  }

  private Optional<BaleenEntity> createEntity(
      final String entityId, final Collection<BaleenMention> mentions) {
    // by construction we know that mentions is not empty however for the future
    if (mentions.isEmpty()) {
      return Optional.empty();
    }

    final BaleenEntity entity = new BaleenEntity();
    entity.setExternalId(entityId);

    // There are a lot of ways we could merge the mentions together, taking the most popular value ,
    // etc
    // we opt for a simple one initially so we can see how it works in practise

    for (final BaleenMention m : mentions) {

      entity.setBaleenDocId(m.getBaleenDocId());
      entity.setBaleenId(m.getBaleenEntityId());

      entity.setDocId(m.getDocId());
      entity.setType(m.getType());
      entity.setSubType(m.getSubType());
      // take longest value
      if (entity.getValue() == null
          || (m.getValue() != null && entity.getValue().length() < m.getValue().length())) {
        entity.setValue(m.getValue());
      }

      entity.getMentionIds().add(m.getExternalId());
      entity.getProperties().putAll(m.getProperties());
    }

    return Optional.of(entity);
  }
}
