// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Nationality;
import uk.gov.dstl.baleen.types.common.Organisation;
import uk.gov.dstl.baleen.types.common.Quantity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Converts the following cases into organisations:
 *
 * <ul>
 *   <li>People of **LOCATION**
 *   <li>**NATIONALITY** people
 *   <li>**QUANTITY** people
 * </ul>
 *
 * Optionally removes the original entity
 *
 * @baleen.javadoc
 */
public class People extends BaleenAnnotator {

  /**
   * Should the original entity be removed?
   *
   * @baleen.config false
   */
  public static final String PARAM_REMOVE_ORIGINAL = "removeOriginal";

  @ConfigurationParameter(name = PARAM_REMOVE_ORIGINAL, defaultValue = "true")
  boolean removeOriginal;

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
    peopleOfLocation(jCas);
    nationalityPeople(jCas);
    quantityPeople(jCas);
  }

  private void peopleOfLocation(JCas jCas) {
    List<Location> toRemove = new ArrayList<>();

    for (Location loc : JCasUtil.select(jCas, Location.class)) {
      String precedingText = jCas.getDocumentText().substring(0, loc.getBegin()).toLowerCase();
      if (precedingText.endsWith("people of ")) {
        Organisation o = new Organisation(jCas, loc.getBegin() - 10, loc.getEnd());
        addToJCasIndex(o);

        if (removeOriginal) toRemove.add(loc);
      }
    }

    removeFromJCasIndex(toRemove);
  }

  private void nationalityPeople(JCas jCas) {
    List<Nationality> toRemove = new ArrayList<>();

    for (Nationality nat : JCasUtil.select(jCas, Nationality.class)) {
      String followingText = jCas.getDocumentText().substring(nat.getEnd()).toLowerCase();
      if (followingText.startsWith(" people")) {
        Organisation o = new Organisation(jCas, nat.getBegin(), nat.getEnd() + 7);
        addToJCasIndex(o);

        if (removeOriginal) toRemove.add(nat);
      }
    }

    removeFromJCasIndex(toRemove);
  }

  private void quantityPeople(JCas jCas) {
    List<Quantity> toRemove = new ArrayList<>();

    for (Quantity quant : JCasUtil.select(jCas, Quantity.class)) {
      String followingText = jCas.getDocumentText().substring(quant.getEnd()).toLowerCase();
      if (followingText.startsWith(" people")) {
        Organisation o = new Organisation(jCas, quant.getBegin(), quant.getEnd() + 7);
        addToJCasIndex(o);

        if (removeOriginal) toRemove.add(quant);
      }
    }

    removeFromJCasIndex(toRemove);
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(Location.class, Nationality.class, Quantity.class),
        ImmutableSet.of(Organisation.class));
  }
}
