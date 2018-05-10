// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.jsoup.nodes.Element;

import uk.gov.dstl.baleen.consumers.utils.AbstractHtmlConsumer;
import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * Creates HTML5 versions of the document, with entities annotated as spans. The original formatting
 * of the document is lost, and only the content is kept.
 *
 * <p>Relationships are not currently supported.
 *
 * @baleen.javadoc
 */
public class Html5 extends AbstractHtmlConsumer {
  private Map<Integer, String> getEntityInsertPositions(JCas jCas) {
    Map<Integer, String> insertPositions = new TreeMap<>();
    Map<Integer, List<Entity>> entityStartPositions = new HashMap<>();
    for (Entity e : JCasUtil.select(jCas, Entity.class)) {
      if (insertPositions.containsKey(e.getBegin())) {
        List<Entity> entities = entityStartPositions.getOrDefault(e.getBegin(), new ArrayList<>());

        long eCount = entities.stream().filter(e2 -> e2.getEnd() > e.getEnd()).count();

        String[] spans = insertPositions.get(e.getBegin()).split("(?<=>)");
        insertPositions.put(e.getBegin(), joinSpans(eCount, e, spans));
      } else {
        insertPositions.put(e.getBegin(), generateSpanStart(e));
      }

      List<Entity> entities = entityStartPositions.getOrDefault(e.getBegin(), new ArrayList<>());
      entities.add(e);
      entityStartPositions.put(e.getBegin(), entities);

      String end = insertPositions.getOrDefault(e.getEnd(), "");
      end = "</span>" + end;
      insertPositions.put(e.getEnd(), end);
    }

    return insertPositions;
  }

  /**
   * @param eCount The number of entities starting in the same position as e, but finishing
   *     afterwards
   * @param e The entity of interest
   * @param spans The array of spans that we already have
   * @return
   */
  private String joinSpans(long eCount, Entity e, String[] spans) {
    StringBuilder joinedSpans = new StringBuilder(eCount == 0 ? generateSpanStart(e) : "");

    Integer i = 0;
    for (String span : spans) {
      joinedSpans.append(span);
      i++;

      if (i == eCount) {
        joinedSpans.append(generateSpanStart(e));
      }
    }

    return joinedSpans.toString();
  }

  private String generateSpanStart(Entity e) {
    String value = e.getValue() == null ? "" : e.getValue().replaceAll("\"", "'");
    String referent = e.getReferent() == null ? "" : Long.toString(e.getReferent().getInternalId());

    return String.format(
        "<span class=\"baleen %s\" id=\"%s\" value=\"%s\" data-referent=\"%s\">",
        e.getClass().getSimpleName(), e.getExternalId(), value, referent);
  }

  @Override
  protected void writeBody(JCas jCas, Element body) {
    // Entities
    Map<Integer, String> insertPositions = getEntityInsertPositions(jCas);

    Element div = body.appendElement("div");
    div.attr("style", "white-space: pre-line");

    String text = jCas.getDocumentText();
    Integer offset = 0;
    for (Entry<Integer, String> pos : insertPositions.entrySet()) {
      String insert = pos.getValue();
      text =
          text.substring(0, pos.getKey() + offset) + insert + text.substring(pos.getKey() + offset);
      offset += insert.length();
    }

    div.append(text);
  }
}
