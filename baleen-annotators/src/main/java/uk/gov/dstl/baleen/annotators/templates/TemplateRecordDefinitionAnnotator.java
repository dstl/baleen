//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.templates;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.templates.TemplateRecordDefinition;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Annotates record definitions found in documents using a regular expression.
 * <p>
 * RecordDefinitions are regions surrounded by &lt;&lt;record:NAME&gt;&gt; and
 * &lt;&lt;record:NAME&gt;&gt; marker text, where NAME is a user defined record
 * type name and must be consistent in the begin and end marker text. Each
 * RecordDefinition should cover one or more TemplateFieldDefinition annotations
 * to be useful downstream.
 * </p>
 * <p>
 * Markers for begin and end may be used to make it clearer in the document but
 * are not required. e.g. &lt;&lt;record:NAME:begin&gt;&gt; and
 * &lt;&lt;record:NAME:end&gt;&gt; or &lt;&lt;record:NAME begin&gt;&gt; and
 * &lt;&lt;record:NAME end&gt;&gt;
 * <p>
 *
 * <p>
 * A repeating record is indicated with the attribute <code>repeat</code> in the
 * begin record marker. e.g. &lt;&lt;record:NAME repeat&gt;&gt; or
 * &lt;&lt;record:NAME repeat="true" &gt;&gt;
 * <p>
 * This annotator should be used in conjunction with
 * {@link TemplateFieldDefinitionAnnotator}.
 * </p>
 */
public class TemplateRecordDefinitionAnnotator extends BaleenAnnotator {

	/** Regular expression used to match records. */
	private static final String RECORD_TOKEN_REGEX = "<<record:([A-Za-z0-9]+)([:\\s].+?)?(?=>>)>>(.*?)<<record:\\1([:\\s].+?)?(?=>>)>>";

	/** The Constant REPEAT_ATTRIBUTE */
	private static final String REPEAT_ATTRIBUTE = "repeat";

	/**
	 * The compiled regular expression - compiled with the DOTALL option
	 * (effectively '(?s)' in the regex) to enable matches over multiple lines.
	 */
	private static final Pattern RECORD_TOKEN_PATTERN = Pattern.compile(RECORD_TOKEN_REGEX, Pattern.DOTALL);

	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		String documentText = jCas.getDocumentText();
		Matcher matcher = RECORD_TOKEN_PATTERN.matcher(documentText);
		while (matcher.find()) {
			createRecordDefinitionAnnotation(jCas, matcher);
		}
	}

	/**
	 * Creates a record definition annotation and adds it to the JCas indexes.
	 *
	 * @param jCas
	 *            the JCas
	 * @param matcher
	 *            the matcher that triggered the creation, which must have two
	 *            groups (first being the name, and the second being the content
	 *            within the record)
	 */
	private void createRecordDefinitionAnnotation(JCas jCas, Matcher matcher) {
		TemplateRecordDefinition recordDefinition = new TemplateRecordDefinition(jCas);
		recordDefinition.setName(matcher.group(1));
		recordDefinition.setBegin(matcher.start(3));
		recordDefinition.setEnd(matcher.end(3));
		recordDefinition.setConfidence(1.0);
		addAttributes(recordDefinition, "<record:" + matcher.group(1) + matcher.group(2) + " />");
		addToJCasIndex(recordDefinition);
	}

	/**
	 * Add the attributes to the given record definition
	 * <p>
	 * Uses Jsoup to parse the tag as if html
	 *
	 * @param recordDefinition
	 *            the record definition
	 * @param beginText
	 *            the begin tag of the record definition
	 */
	private void addAttributes(TemplateRecordDefinition recordDefinition, String beginText) {

		Document doc = Jsoup.parseBodyFragment(beginText);
		Element fieldElement = doc.body().child(0);
		Attributes attributes = fieldElement.attributes();
		if (attributes.hasKey(REPEAT_ATTRIBUTE)) {
			String required = attributes.get(REPEAT_ATTRIBUTE);
			recordDefinition.setRepeat(Strings.isNullOrEmpty(required) ? true : Boolean.valueOf(required));
		}

	}

	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(TemplateRecordDefinition.class));
	}

}