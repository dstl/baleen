//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.structural;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.core.utils.ConfigUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.structure.TableCell;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * Extract entities from tables using a regular expression to find columns by
 * their name.
 *
 * <p>
 * The regular expression supplied by the user is run over the table headers (or
 * row 1 if missing) to identify columns. TableCells from that column are then
 * annotated as a user specified type, which must inherit from the Entity class.
 * Users can supply a confidence to assign to annotations created by this
 * annotator.
 * </p>
 *
 *
 * @baleen.javadoc
 */
public class TableEntity extends BaleenAnnotator {

	/**
	 * Is the regular expression case sensitive?
	 *
	 * @baleen.config false
	 */
	public static final String PARAM_CASE_SENSITIVE = "caseSensitive";
	@ConfigurationParameter(name = PARAM_CASE_SENSITIVE, defaultValue = "false")
	private boolean caseSensitive = false;

	/**
	 * The regular expression to search for
	 *
	 * @baleen.config
	 */
	public static final String PARAM_PATTERN = "pattern";
	@ConfigurationParameter(name = PARAM_PATTERN, defaultValue = "")
	private String pattern;

	/**
	 * The entity type to use for matched entities
	 *
	 * @baleen.config uk.gov.dstl.baleen.types.semantic.Entity
	 */
	public static final String PARAM_TYPE = "type";
	@ConfigurationParameter(name = PARAM_TYPE, defaultValue = "uk.gov.dstl.baleen.types.semantic.Entity")
	private String type;

	/**
	 * The entity subType to use for matched entities
	 *
	 * @baleen.config
	 */
	public static final String PARAM_SUB_TYPE = "subType";
	@ConfigurationParameter(name = PARAM_SUB_TYPE, defaultValue = "")
	private String subType;

	/**
	 * The confidence to assign to matched entities
	 *
	 * @baleen.config 1.0
	 */
	public static final String PARAM_CONFIDENCE = "confidence";
	@ConfigurationParameter(name = PARAM_CONFIDENCE, defaultValue = "1.0")
	private String confidenceString;

	// Parse the confidence config parameter into this variable to avoid issues
	// with parameter types
	private Float confidence;

	private Pattern p = null;

	private Constructor<? extends Entity> constructor;

	@Override
	public void doInitialize(final UimaContext aContext) throws ResourceInitializationException {
		confidence = ConfigUtils.stringToFloat(confidenceString, 1.0f);

		if (!caseSensitive) {
			pattern = "(?i)" + pattern;
		}
		p = Pattern.compile(pattern);
		getMonitor().debug("The regular expression is \"{}\"", p.pattern());
		try {
			final Class<? extends Entity> et = TypeUtils.getEntityClass(type,
					JCasFactory.createJCas(TypeSystemSingleton.getTypeSystemDescriptionInstance()));
			constructor = et.getConstructor(JCas.class);
		} catch (UIMAException | BaleenException | NoSuchMethodException | SecurityException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		Stream<TableCell> rows = new Tables(jCas).withColumn(p).getFilteredCells();

		rows.forEach(cell -> {
			String text = cell.getCoveredText();

			if (StringUtils.isNotBlank(text)) {
				Entity ret;
				try {
					ret = constructor.newInstance(jCas);
					ret.setBegin(cell.getBegin());
					ret.setEnd(cell.getEnd());
					ret.setValue(text);
					ret.setConfidence(confidence);
					if (!Strings.isNullOrEmpty(subType)) {
						ret.setSubType(subType);
					}
					addToJCasIndex(ret);
				} catch (Exception e) {
					throw new RuntimeException("Can not create entity type " + type, e);
				}
			}
		});
	}

	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(constructor.getDeclaringClass()));
	}
}
