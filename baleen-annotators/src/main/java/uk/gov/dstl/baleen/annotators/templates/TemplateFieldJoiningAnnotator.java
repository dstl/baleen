//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.templates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.ImmutableSet;
import com.samskivert.mustache.DefaultCollector;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.Collector;
import com.samskivert.mustache.Mustache.Compiler;
import com.samskivert.mustache.Mustache.VariableFetcher;
import com.samskivert.mustache.Template;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.templates.TemplateField;
import uk.gov.dstl.baleen.types.templates.TemplateRecord;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Creates a new TemplateField with the given fieldName, based on fields in the
 * Record of the given recordName, using the supplied template.
 * <p>
 * Optionally, a source can be provided to disambiguate records/fields created
 * from multiple definition configurations.
 * </p>
 * <p>
 * The template is a simple mustache template, where the fields of the given
 * record are exposed as root level properties in the mustache context ready for
 * direct binding.
 * </p>
 * <p>
 * Example configuration:
 * </p>
 *
 * <pre>
...
annotators:
- class templates.TemplateAnnotator:
  ...
- class templates.TempalteFieldJoiningAnnotator:
  fieldName: fullName
  record: report
  template: {{surname}}, {{firstName}}
  source: peopleReportDefinitions
 * </pre>
 *
 */
public class TemplateFieldJoiningAnnotator extends BaleenAnnotator {

	/** The Constant PARAM_TEMPLATE. */
	public static final String PARAM_TEMPLATE = "template";

	/**
	 * The template to use to create the new value.
	 */
	@ConfigurationParameter(name = PARAM_TEMPLATE)
	private String mustacheTemplate;

	/** The Constant PARAM_RECORD. */
	public static final String PARAM_RECORD = "record";

	/**
	 * The record to use when matching field names.
	 */
	@ConfigurationParameter(name = PARAM_RECORD)
	private String recordName;

	/** The Constant PARAM_FIELD_NAME. */
	public static final String PARAM_FIELD_NAME = "fieldName";

	/**
	 * The field name to create.
	 *
	 * @baleen.config field
	 */
	@ConfigurationParameter(name = PARAM_FIELD_NAME, defaultValue = "field")
	private String fieldName;

	/** The Constant PARAM_SOURCE. */
	public static final String PARAM_SOURCE = "source";

	/**
	 * The source type to search for the record.
	 */
	@ConfigurationParameter(name = PARAM_SOURCE, mandatory = false)
	private String source;

	/** The compiled template. */
	private Template compiledTemplate;

	/** The touched fields. */
	private Collection<String> touchedFields;

	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		super.doInitialize(aContext);
		touchedFields = gatherReferencedFields();
		compiledTemplate = Mustache.compiler().compile(mustacheTemplate);
	}

	/**
	 * Gather fields that are referenced in the mustache template.
	 *
	 * @return the collection
	 */
	private Collection<String> gatherReferencedFields() {
		Collection<String> fields = new ArrayList<>();
		Collector collector = new DefaultCollector() {

			@Override
			public VariableFetcher createFetcher(Object ctx, String name) {
				fields.add(name);
				return super.createFetcher(ctx, name);
			}
		};

		Compiler compiler = Mustache.compiler().defaultValue("").withCollector(collector);
		Template mockTemplate = compiler.compile(mustacheTemplate);
		mockTemplate.execute(new HashMap<>());
		return fields;
	}

	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		Map<String, String> recordFieldValues = new HashMap<>();
		Map<String, TemplateField> recordFields = new HashMap<>();
		Collection<TemplateRecord> records = JCasUtil.select(jCas, TemplateRecord.class);
		for (TemplateRecord record : records) {
			if (!StringUtils.equals(recordName, record.getName())
					|| !StringUtils.isEmpty(source) && !source.equalsIgnoreCase(record.getSource())) {
				continue;
			}
			List<TemplateField> fields = JCasUtil.selectCovered(TemplateField.class, record);
			for (TemplateField field : fields) {
				// only keep fields used in the template - simplifies later
				// begin/end calculation
				if (!touchedFields.contains(field.getName())) {
					continue;
				}
				recordFieldValues.put(field.getName(), field.getCoveredText());
				recordFields.put(field.getName(), field);
			}
		}

		OptionalInt min = recordFields.values().stream().mapToInt(TemplateField::getBegin).min();
		OptionalInt max = recordFields.values().stream().mapToInt(TemplateField::getEnd).max();

		if (min.isPresent() && max.isPresent()) {
			int begin = min.getAsInt();
			int end = max.getAsInt();

			TemplateField newField = new TemplateField(jCas);
			newField.setName(fieldName);
			newField.setBegin(begin);
			newField.setEnd(end);
			newField.setValue(compiledTemplate.execute(recordFieldValues));
			addToJCasIndex(newField);
		}

	}

	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(ImmutableSet.of(TemplateRecord.class, TemplateField.class), ImmutableSet.of(TemplateField.class));
	}
}