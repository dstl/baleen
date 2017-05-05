//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.common.structure;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.language.Text;
import uk.gov.dstl.baleen.types.structure.Aside;
import uk.gov.dstl.baleen.types.structure.Caption;
import uk.gov.dstl.baleen.types.structure.DefinitionDescription;
import uk.gov.dstl.baleen.types.structure.DefinitionItem;
import uk.gov.dstl.baleen.types.structure.Details;
import uk.gov.dstl.baleen.types.structure.Heading;
import uk.gov.dstl.baleen.types.structure.ListItem;
import uk.gov.dstl.baleen.types.structure.Paragraph;
import uk.gov.dstl.baleen.types.structure.Preformatted;
import uk.gov.dstl.baleen.types.structure.Quotation;
import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.types.structure.Summary;
import uk.gov.dstl.baleen.types.structure.TableCell;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;
import uk.gov.dstl.baleen.uima.utils.StructureUtil;

/**
 * Converts selected Structure annotations to Text annotations.
 *
 * Rather than annotators need to deal with every time of structure type in order to get the right
 * text form a document. This annotator maps selected (configurable) structural types to Text
 * annotations.
 *
 * The list of structure types to map is Paragraph, Aside, Details, ListItem, TableCell, Summary,
 * Quotation, Heading, Caption, DefinitionItem, DefinitionList, Preformatted.
 *
 * This list can be configured by providing class names (or full qualified classes) to the types
 * field.
 *
 * This annotator ensures that no Text annotation overlap. If they did overlap then other annotator
 * would process the same text (within two different text field) resulting in duplicate annotations.
 * You can control the overlap removal by setting the keepSmallest parameter.
 *
 * NOTE: Test cases are in baleen-annotators
 *
 * @baleen.javadoc
 *
 */
public class TextBlocks extends BaleenAnnotator {

	private static final Set<Class<? extends Structure>> DEFAULT_STRUCTURAL_CLASSES = ImmutableSet.of(
			Paragraph.class,
			Aside.class,
			Details.class,
			ListItem.class,
			TableCell.class,
			Summary.class,
			Quotation.class,
			Heading.class,
			Caption.class,
			DefinitionItem.class,
			DefinitionDescription.class,
			Preformatted.class);

	/**
	 * A list of structural types which will be mapped to TextBlocks.
	 *
	 * @baleen.config Paragraph,TableCell,ListItem,Aside, ...
	 */
	public static final String PARAM_TYPE_NAMES = "types";
	@ConfigurationParameter(name = PARAM_TYPE_NAMES, mandatory = false)
	private String[] typeNames;

	/**
	 * In order to remove overlapping Text annotations we can either remove the annotation covering
	 * (biggest) or the annotations covered (smallest).
	 *
	 * We default to picking the smallest units of text.
	 *
	 * @baleen.config true
	 */
	public static final String PARAM_KEEP_SMALLEST = "keepSmallest";
	@ConfigurationParameter(name = PARAM_KEEP_SMALLEST, defaultValue = "true")
	private boolean keepSmallest;

	private Set<Class<? extends Structure>> structuralClasses;

	@Override
	public void doInitialize(final UimaContext aContext) throws ResourceInitializationException {
		super.doInitialize(aContext);

		if (typeNames == null || typeNames.length == 0) {
			structuralClasses = Sets.newHashSet(DEFAULT_STRUCTURAL_CLASSES);
		} else {
			structuralClasses = StructureUtil.getStructureClasses(typeNames);
		}

	}

	@Override
	protected void doProcess(final JCas jCas) throws AnalysisEngineProcessException {

		final Collection<Structure> structures = JCasUtil.select(jCas, Structure.class);

		if (structures.isEmpty()) {
			// If the jCas has no structural annotations then the entire text should be marked as a text
			// block

			final int end = jCas.getDocumentText().length();
			final Text t = new Text(jCas, 0, end);
			addToJCasIndex(t);

		} else {
			// Otherwise add the types we want...

			structures.stream().filter(s -> structuralClasses.contains(s.getClass()))
			.map(s -> new Text(jCas, s.getBegin(), s.getEnd())).forEach(this::addToJCasIndex);


			// Now remove any that cover others, so we keep only biggest/most detailed as per request
			final Map<Text, Collection<Text>> cover;
			if (keepSmallest) {
				cover = JCasUtil.indexCovering(jCas, Text.class, Text.class);
			} else {
				cover = JCasUtil.indexCovered(jCas, Text.class, Text.class);
			}
            cover.forEach((t, c) -> c.remove(t));    //Remove where x has been pulled out as covering itself (potential bug introduced in UIMAfit 2.3.0)
			cover.values().stream().flatMap(Collection::stream).forEach(this::removeFromJCasIndex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public AnalysisEngineAction getAction() {
		Set<Class<? extends Annotation>> classes = new HashSet<>();
		for(Class<?> c : structuralClasses){
			classes.add((Class<? extends Annotation>) c);
		}

		return new AnalysisEngineAction(classes, Collections.emptySet());
	}

}
