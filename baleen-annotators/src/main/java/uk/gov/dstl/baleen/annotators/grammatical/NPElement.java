// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.grammatical;

import java.util.Arrays;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Chemical;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/** Find noun phrases that contain an element (e.g. carbon), and annotate as a Chemical */
public class NPElement extends BaleenAnnotator {
  private static final List<String> ELEMENTS =
      Arrays.asList(
          "hydrogen",
          "helium",
          "lithium",
          "beryllium",
          "boron",
          "carbon",
          "nitrogen",
          "oxygen",
          "fluorine",
          "neon",
          "sodium",
          "magnesium",
          "aluminium",
          "silicon",
          "phosphorus",
          "sulfur",
          "chlorine",
          "argon",
          "potassium",
          "calcium",
          "scandium",
          "titanium",
          "vanadium",
          "chromium",
          "manganese",
          "iron",
          "cobalt",
          "nickel",
          "copper",
          "zinc",
          "gallium",
          "germanium",
          "arsenic",
          "selenium",
          "bromine",
          "krypton",
          "rubidium",
          "strontium",
          "yttrium",
          "zirconium",
          "niobium",
          "molybdenum",
          "technetium",
          "ruthenium",
          "rhodium",
          "palladium",
          "silver",
          "cadmium",
          "indium",
          "tin",
          "antimony",
          "tellurium",
          "iodine",
          "xenon",
          "caesium",
          "barium",
          "lanthanum",
          "cerium",
          "praseodymium",
          "neodymium",
          "promethium",
          "samarium",
          "europium",
          "gadolinium",
          "terbium",
          "dysprosium",
          "holmium",
          "erbium",
          "thulium",
          "ytterbium",
          "lutetium",
          "hafnium",
          "tantalum",
          "tungsten",
          "rhenium",
          "osmium",
          "iridium",
          "platinum",
          "gold",
          "mercury",
          "thallium",
          "lead",
          "bismuth",
          "polonium",
          "astatine",
          "radon",
          "francium",
          "radium",
          "actinium",
          "thorium",
          "protactinium",
          "uranium",
          "neptunium",
          "plutonium",
          "americium",
          "curium",
          "berkelium",
          "californium",
          "einsteinium",
          "fermium",
          "mendelevium",
          "neblium",
          "lawrencium",
          "rutherfordium",
          "dubnium",
          "seaborgium",
          "bohrium",
          "hassium",
          "meitnerium",
          "darmstadtium",
          "roentgenium",
          "copernicium",
          "nihonium",
          "flerovium",
          "moscovium",
          "livermorium",
          "tennessine",
          "oganesson");

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
    for (PhraseChunk chunk : JCasUtil.select(jCas, PhraseChunk.class)) {
      if (!"NP".equals(chunk.getChunkType())) continue;

      String coveredText = chunk.getCoveredText().toLowerCase();
      String[] parts = coveredText.split("[^a-z]");

      for (String part : parts) {
        if (ELEMENTS.contains(part)) {
          Chemical c = new Chemical(jCas, chunk.getBegin(), chunk.getEnd());
          addToJCasIndex(c);
        }
      }
    }
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(PhraseChunk.class), ImmutableSet.of(Chemical.class));
  }
}
