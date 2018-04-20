// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.renoun;

import static uk.gov.dstl.baleen.annotators.renoun.DependencyTreeMatcher.create;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.annotators.language.MaltParser;
import uk.gov.dstl.baleen.uima.grammar.DependencyParseException;

/**
 * A dependency tree based relation extractor to obtain seed data for ReNoun. This looks for
 * Subject, Attribute, Object triples where the attribute is expressed as a Noun.
 *
 * <p>The seed patterns are built into the class, the attribute list to look for must be supplied.
 *
 * <p>Note that these seeds are tuned to the default model of the {@link MaltParser}, changing the
 * model may require update to the seeds.
 *
 * <p>In the paper, the noun attribute is checked for coreference with the object this is an option
 * here by setting requireCoreference to true.
 *
 * @baleen.javadoc
 * @see http://emnlp2014.org/papers/pdf/EMNLP2014038.pdf
 */
public class ReNounDefaultSeedsRelationshipAnnotator extends AbstractReNounRelationshipAnnotator {

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);
    type = "Seed";
  }

  @Override
  @SuppressWarnings(
      "squid:S1192") // Suppress sonarqube duplicate String warnings for DependencyTree Strings
  protected Supplier<Stream<DependencyTreeMatcher>> createPatternsSupplier()
      throws DependencyParseException {

    // @formatter:off
    final List<DependencyTreeMatcher> seeds =
        ImmutableList.of(
            create(
                "_NN:attribute\n"
                    + "  name _NN:subject\n"
                    + "  punct ,_,\n"
                    + "  appos _NN:object\n"),
            create(
                "_NN:object\n"
                    + "  punct ,_,\n"
                    + "  appos _NN:subject\n"
                    + "  list _NN:attribute\n"),
            create(
                "_NN:object\n"
                    + " punct ,_,\n"
                    + " parataxis _VBD\n"
                    + "  nsubj _NN:attribute\n"
                    + "   nmod:poss _NN:subject\n"
                    + "    case 's_POS\n"),
            create(
                "_NN:object\n"
                    + "  punct ,_,\n"
                    + "  list _NNP:attribute\n"
                    + "    compound _NNP:subject\n"),
            create(
                "_NN:object\n"
                    + " nsubj _NN:attribute\n"
                    + "  det [Tt]he_DT\n"
                    + "  prep of_IN\n"
                    + "   pobj _NN:subject\n"
                    + " cop is_VB"),
            create(
                "_NN:object\n"
                    + " nsubj _NN:attribute\n"
                    + "  det [Tt]he_DT\n"
                    + "  nmod _NN:subject\n"
                    + "   case of_IN\n"
                    + " cop is_VB\n"),
            create(
                "_NN:subject\n"
                    + " list _NN:attribute\n"
                    + "  acl:relcl _VB\n"
                    + "   nsubj _NN:object\n"),
            create(
                "_NN:subject\n"
                    + "  list _NN:attribute\n"
                    + "  punct ,_,\n"
                    + "  appos _NN:object\n"),
            create(
                "_VB\n"
                    + "  nsubj _NN:object\n"
                    + "    punct ,_,\n"
                    + "    appos _NN:attribute\n"
                    + "      nmod:poss _NN:subject\n"
                    + "        case 's_POS\n"),
            create(
                "_VB\n"
                    + "  nsubj _NN:object\n"
                    + "    punct ,_,\n"
                    + "    appos _NN:attribute\n"
                    + "      det [Tt]he_DT\n"
                    + "      nmod _NN:subject\n"
                    + "         case of_IN\n"),
            create(
                "_VB\n"
                    + "  nsubj _NN:object\n"
                    + "      compound _NN:subject\n"
                    + "      compound _NN:attribute\n"),
            create(
                "_VB\n"
                    + "  nsubj _NN:attribute\n"
                    + "    det [Tt]he_DT\n"
                    + "    nmod _NN:subject\n"
                    + "      case of_IN\n"
                    + "  punct ,_,\n"
                    + "  nsubj _NN:object\n"),
            create(
                "_VB\n"
                    + "  nsubj _NN:attribute\n"
                    + "      nmod:poss _NN:subject\n"
                    + "          case 's_POS\n"
                    + "      punct ,_,\n"
                    + "      appos _NN:object\n"),
            create(
                "_VB\n"
                    + "  nsubj _NN:attribute\n"
                    + "      nmod:poss _NN:subject\n"
                    + "          case 's_POS\n"
                    + "  punct ,_,\n"
                    + "  nsubj _NN:object\n"));

    return seeds::stream;
    // @formatter:on
  }
}
