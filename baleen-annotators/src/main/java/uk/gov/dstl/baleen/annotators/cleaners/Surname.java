// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * From Person entities found in the document, extract surnames (taken to be the last word in
 * multi-word names) and identify other occurrences. If only one person has that surname in the
 * document, then coreference it.
 *
 * @baleen.javadoc
 */
public class Surname extends BaleenAnnotator {

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
    Map<String, Person> surnames = new HashMap<>();

    for (Person p : JCasUtil.select(jCas, Person.class)) {
      String name = p.getCoveredText();
      if (!name.contains(" ")) {
        continue;
      }

      String[] nameParts = name.split(" ");
      String surname = nameParts[nameParts.length - 1].toLowerCase();

      if (surnames.containsKey(surname)) {
        // Existing surname, so ensure existing entity is removed so we don't do coreference
        // Unless the ReferenceTarget is the same, in which case no need to do anything
        Person pers = surnames.get(surname);
        if (pers == null) {
          continue;
        }

        ReferenceTarget rt = pers.getReferent();
        if (rt == null || !rt.equals(p.getReferent())) {
          surnames.put(surname, null);
        }
      } else {
        // New surname
        surnames.put(surname, p);
      }
    }

    for (Entry<String, Person> entry : surnames.entrySet()) {
      findSurname(jCas, entry.getKey(), entry.getValue());
    }
  }

  private void findSurname(JCas jCas, String surname, Person original) {
    Pattern pSurname =
        Pattern.compile("\\b" + Pattern.quote(surname) + "\\b", Pattern.CASE_INSENSITIVE);
    Matcher m = pSurname.matcher(jCas.getDocumentText());
    while (m.find()) {
      if (!JCasUtil.selectCovering(jCas, Person.class, m.start(), m.end()).isEmpty()) {
        continue;
      }

      Person p = new Person(jCas, m.start(), m.end());

      if (original != null) {
        ReferenceTarget rt = original.getReferent();
        if (rt == null) {
          rt = new ReferenceTarget(jCas);
          original.setReferent(rt);
        }
        p.setReferent(rt);
      }

      p.addToIndexes();
    }
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(ImmutableSet.of(Person.class), ImmutableSet.of(Person.class));
  }
}
