// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.csv.internals;

import java.util.Collection;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.consumers.csv.AbstractCsvConsumer;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.semantic.Relation;

/**
 * Write relations to CSV.
 *
 * <p>Format is:
 *
 * <ul>
 *   <li>source
 *   <li>sentence
 *   <li>type
 *   <li>subtype
 *   <li>source
 *   <li>target
 *   <li>sourceText
 *   <li>targetText
 *   <li>sourceType
 *   <li>targetType
 * </ul>
 *
 * @baleen.javadoc
 */
public class CsvRelation extends AbstractCsvConsumer {

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {

    super.doInitialize(aContext);

    write(
        "source",
        "sentence",
        "type",
        "subtype",
        "source",
        "target",
        "sourceText",
        "targetText",
        "sourceType",
        "targetType");
  }

  @Override
  protected void write(JCas jCas) {
    final String source = getDocumentAnnotation(jCas).getSourceUri();

    final Map<Relation, Collection<Sentence>> coveringSentence =
        JCasUtil.indexCovering(jCas, Relation.class, Sentence.class);

    JCasUtil.select(jCas, Relation.class)
        .stream()
        .map(
            r -> {
              String sentence = "";
              final Collection<Sentence> sentences = coveringSentence.get(r);
              if (!sentences.isEmpty()) sentence = sentences.iterator().next().getCoveredText();

              return new String[] {
                source,
                sentence,
                r.getRelationshipType(),
                r.getRelationSubType(),
                normalize(r.getSource().getValue()),
                normalize(r.getTarget().getValue()),
                normalize(r.getSource().getCoveredText()),
                normalize(r.getTarget().getCoveredText()),
                r.getSource().getType().getShortName(),
                r.getTarget().getType().getShortName()
              };
            })
        .forEach(this::write);
  }
}
