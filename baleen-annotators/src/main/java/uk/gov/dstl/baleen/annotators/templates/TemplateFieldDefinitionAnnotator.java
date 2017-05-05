//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.templates;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.uima.jcas.JCas;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition;

/**
 * Annotates template fields found in documents using a regular expression.
 * <p>
 * Template fields are text surrounded by ASCII double angle brackets, eg
 * &lt;&lt;field:fieldname&gt;&gt; for the field "fieldname".
 * </p>
 */
public class TemplateFieldDefinitionAnnotator extends AbstractRegexAnnotator<TemplateFieldDefinition> {

	/** The Constant TEMPLATE_TOKEN_REGEX. */
	private static final String TEMPLATE_TOKEN_REGEX = "<<field:([A-Za-z0-9]+)(\\s.+?)?(?=>>)>>";

	/** The Constant TEMPLATE_TOKEN_PATTERN. */
	private static final Pattern TEMPLATE_TOKEN_PATTERN = Pattern.compile(TEMPLATE_TOKEN_REGEX);

	/** The Constant DEFAULT_VALUE_ATTRIBUTE */
	private static final String DEFAULT_VALUE_ATTRIBUTE = "defaultValue";

	/** The Constant REGEX_ATTRIBUTE */
	private static final String REGEX_ATTRIBUTE = "regex";

	/** The Constant REPEAT_ATTRIBUTE */
	private static final String REPEAT_ATTRIBUTE = "repeat";

	/** The Constant REQUIRED_ATTRIBUTE */
	private static final String REQUIRED_ATTRIBUTE = "required";

	/**
	 * Instantiates a new template field definition annotator which will
	 * assigning confidence 1.0 to all matched field definitions.
	 */
	public TemplateFieldDefinitionAnnotator() {
		super(TEMPLATE_TOKEN_PATTERN, 1.0);
	}

	@Override
	protected TemplateFieldDefinition create(JCas jCas, Matcher matcher) {
		TemplateFieldDefinition field = new TemplateFieldDefinition(jCas);
		field.setName(matcher.group(1));

		if (matcher.group(2) != null) {
			addFieldAttributes(field, matcher.group());
		}
		return field;
	}

	/**
	 * Add the attributes of the field definition
	 *
	 * @param field
	 *            the filed definition object under construction
	 * @param coveredText
	 *            the covered text defining the field definition
	 */
	private void addFieldAttributes(TemplateFieldDefinition field, String coveredText) {

		Document doc = Jsoup.parseBodyFragment(coveredText.substring(1, coveredText.length() - 2) + " />");
		Element fieldElement = doc.body().child(0);
		Attributes attributes = fieldElement.attributes();

		if (attributes.hasKey(DEFAULT_VALUE_ATTRIBUTE)) {
			field.setDefaultValue(attributes.get(DEFAULT_VALUE_ATTRIBUTE));
		}
		if (attributes.hasKey(REGEX_ATTRIBUTE)) {
			String regex = attributes.get(REGEX_ATTRIBUTE);
			checkRegexCompiles(regex);
			field.setRegex(regex);
		}
		if (attributes.hasKey(REPEAT_ATTRIBUTE)) {
			String required = attributes.get(REPEAT_ATTRIBUTE);
			field.setRepeat(Strings.isNullOrEmpty(required) ? true : Boolean.valueOf(required));
		}
		if (attributes.hasKey(REQUIRED_ATTRIBUTE)) {
			String required = attributes.get(REQUIRED_ATTRIBUTE);
			field.setRequired(Strings.isNullOrEmpty(required) ? true : Boolean.valueOf(required));
		}

	}

	/**
	 * Check that the regex compiles
	 *
	 * @param regex
	 * @throws PatternSyntaxException
	 *             If the expression's syntax is invalid
	 */
	private void checkRegexCompiles(String regex) {
		Pattern.compile(regex);
	}

	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(TemplateFieldDefinition.class));
	}

}