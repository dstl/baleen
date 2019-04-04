package uk.gov.dstl.baleen.annotators.triage;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;
import uk.gov.dstl.baleen.annotators.language.OpenNLP;
import uk.gov.dstl.baleen.annotators.language.WordNetLemmatizer;
import uk.gov.dstl.baleen.annotators.testing.AbstractMultiAnnotatorTest;
import uk.gov.dstl.baleen.resources.SharedOpenNLPModel;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.resources.SharedWordNetResource;
import uk.gov.dstl.baleen.types.metadata.Metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TokenFrequencySummarisationTest extends AbstractMultiAnnotatorTest {

  // Text copied from https://en.wikipedia.org/wiki/Large_mole
  private static final String DOCUMENT_TEXT =
      "The large mole or Ussuri mole (Mogera robusta), is a species of mammal in the family Talpidae (treated as a subspecies of the Japanese mole by some authorities). It is found in China, North Korea, South Korea, and Russia and lives in a long underground burrow, seldom emerging on the surface of the ground during the day.\n"
          + "\n"
          + "Description\n"
          + "\n"
          + "This mole grows to a total length of 154 to 172 mm (6 to 7 in) with a tail of about 20 mm (0.8 in). It is adapted for underground life; the body is cylindrical, the fore-feet are spade-like, the nails are flattened and the eyes are small. The short, dense, dorsal pelage is brownish-grey with a metallic sheen and the underparts are silvery-yellow, with a grey patch on the chest. The bare skin on the muzzle and the feet is yellowish. The short tail is well-covered with hair.\n"
          + "\n"
          + "Distribution and habitat\n"
          + "\n"
          + "The large mole occurs in northeastern China, the Korean peninsula and southeastern Russia. Its typical habitat is montane forest and woodland, pasture and agricultural land but it is seldom found on steep rocky slopes.\n"
          + "\n"
          + "Ecology\n"
          + "\n"
          + "This mole is solitary and mainly nocturnal, but is sometimes active on cloudy or rainy days. It feeds mainly on earthworms, insects, spiders, slugs and snails. It excavates feeding passages about 10 cm (4 in) below the surface of the soil, periodically throwing up a \"mole hill\", a pile of soil on the surface. Main passages may have a total length of 450 m (1,500 ft) and be at least 30 cm (12 in) below the surface; they connect feeding areas with drinking places and the nest. The breeding chamber contains a globular nest of leaves and grasses. The litter size is from 2 to 6 young with a gestation period of 28 days. Large moles live for up to four years, but sometimes fall prey to owls, snakes and weasels.\n"
          + "\n"
          + "Status\n"
          + "\n"
          + "Mogera robusta has a wide range and is said to be abundant in some areas and common in others. Although it used to be hunted for its pelt, this is no longer the case and it now faces no particular threats. The International Union for Conservation of Nature has rated its conservation status as being of \"least concern\" because, although its population may be in slow decline, this is not at a fast enough rate to warrant listing it in a more threatened category.";

  @Override
  protected AnalysisEngine[] createAnalysisEngines() throws ResourceInitializationException {
    ExternalResourceDescription tokensDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "tokens", SharedOpenNLPModel.class);
    ExternalResourceDescription sentencesDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "sentences", SharedOpenNLPModel.class);
    ExternalResourceDescription posDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "posTags", SharedOpenNLPModel.class);
    ExternalResourceDescription chunksDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "phraseChunks", SharedOpenNLPModel.class);

    ExternalResourceDescription stopwordsDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "stopwords", SharedStopwordResource.class);

    ExternalResourceDescription wordnetDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "wordnet", SharedWordNetResource.class);

    AnalysisEngineDescription openNlpAnalysisEngineDescription =
        AnalysisEngineFactory.createEngineDescription(
            OpenNLP.class,
            "tokens",
            tokensDesc,
            "sentences",
            sentencesDesc,
            "posTags",
            posDesc,
            "phraseChunks",
            chunksDesc);

    AnalysisEngineDescription wordNetLemmatizerAnalysisEngineDescription =
        AnalysisEngineFactory.createEngineDescription(
            WordNetLemmatizer.class, "wordnet", wordnetDesc);

    AnalysisEngineDescription tokenFrequencySummarisationAnalysisEngineDescription =
        AnalysisEngineFactory.createEngineDescription(
            TokenFrequencySummarisation.class, "stopwords", stopwordsDesc);

    AnalysisEngine openNlpAnalysisEngine =
        AnalysisEngineFactory.createEngine(openNlpAnalysisEngineDescription);
    AnalysisEngine wordNetLemmatizerAnalysisEngine =
        AnalysisEngineFactory.createEngine(wordNetLemmatizerAnalysisEngineDescription);
    AnalysisEngine tokenFrequencySummarisationAnalysisEngine =
        AnalysisEngineFactory.createEngine(tokenFrequencySummarisationAnalysisEngineDescription);

    return new AnalysisEngine[] {
      openNlpAnalysisEngine,
      wordNetLemmatizerAnalysisEngine,
      tokenFrequencySummarisationAnalysisEngine
    };
  }

  @Test
  public void test() throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText(DOCUMENT_TEXT);
    processJCas();

    Metadata md = JCasUtil.selectByIndex(jCas, Metadata.class, 0);
    assertEquals("autoSummary", md.getKey());
    assertNotNull(md.getValue());
  }
}
