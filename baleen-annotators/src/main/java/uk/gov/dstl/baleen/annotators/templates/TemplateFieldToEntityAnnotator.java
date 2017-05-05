//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.templates;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.templates.TemplateField;
import uk.gov.dstl.baleen.types.templates.TemplateRecord;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * Creates a new Entity of the configured type for each field of a given name in
 * each record with a given name.
 * 
 * Optionally, a source can be provided to disambiguate records/fields created
 * from multiple definition configurations.
 * 
 * Example configuration:
 * 
 * <pre>
... 
annotators:
- class: templates.TemplateAnnotator
  ...
- class: templates.TemplateFieldToEntityAnnotator
  entityType: Person
  recordName: report
  fieldName: athlete
  source: athleteReportDefinitions
 * </pre>
 * 
 */
public class TemplateFieldToEntityAnnotator extends BaleenAnnotator {

	/** The Constant PARAM_ENTITY_TYPE. */
	public static final String PARAM_ENTITY_TYPE = "entityType";

	/**
	 * The entity type to create.
	 * 
	 * @baleen.config semantic.Entity
	 */
	@ConfigurationParameter(name = PARAM_ENTITY_TYPE, mandatory = true)
	private String entityType;
	
	private Class<? extends Entity> et = null;

	/** The Constant PARAM_RECORD_NAME. */
	public static final String PARAM_RECORD_NAME = "recordName";

	/**
	 * The record type to search for the field.
	 * 
	 * @baleen.config record
	 */
	@ConfigurationParameter(name = PARAM_RECORD_NAME, mandatory = true)
	private String recordName;

	/** The Constant PARAM_FIELD_NAME. */
	public static final String PARAM_FIELD_NAME = "fieldName";

	/**
	 * The field name to find.
	 * 
	 * @baleen.config field
	 */
	@ConfigurationParameter(name = PARAM_FIELD_NAME, mandatory = true)
	private String fieldName;

	/** The Constant PARAM_SOURCE. */
	public static final String PARAM_SOURCE = "source";

	/**
	 * The source type to search for the record.
	 */
	@ConfigurationParameter(name = PARAM_SOURCE, mandatory = false)
	private String source;

	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		try{
			et = TypeUtils.getEntityClass(entityType, JCasFactory.createJCas(TypeSystemSingleton.getTypeSystemDescriptionInstance()));
		}catch(UIMAException | BaleenException e){
			throw new ResourceInitializationException(e);
		}
	}
	
	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		Collection<TemplateRecord> records = JCasUtil.select(jCas, TemplateRecord.class);
		for (TemplateRecord record : records) {
			if (!StringUtils.equals(recordName, record.getName())
					|| (!StringUtils.isEmpty(source) && !source.equalsIgnoreCase(record.getSource()))) {
				continue;
			}

			List<TemplateField> fields = JCasUtil.selectCovered(TemplateField.class, record);
			for (TemplateField field : fields) {
				if (!StringUtils.equals(fieldName, field.getName())) {
					continue;
				}
				try {
					createEntity(jCas, field);
				} catch (BaleenException e) {
					getMonitor().warn("Failed to process entity for record " + recordName + " field " + fieldName, e);
				}
			}
		}
	}

	/**
	 * Creates a new entity of the configured type, setting the value to the
	 * covered text of the matched template field.
	 *
	 * @param jCas
	 *            the jCas
	 * @param field
	 *            the field
	 * @throws BaleenException
	 *             the baleen exception
	 */
	private void createEntity(JCas jCas, TemplateField field) throws BaleenException {
		try {
			Constructor<? extends Entity> constructor = et.getConstructor(JCas.class);
			Entity entity = constructor.newInstance(jCas);
			entity.setBegin(field.getBegin());
			entity.setEnd(field.getEnd());
			entity.setValue(field.getCoveredText());
			addToJCasIndex(entity);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new BaleenException("Failed to create entity of type " + entityType, e);
		}
	}

	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(ImmutableSet.of(TemplateRecord.class, TemplateField.class), ImmutableSet.of(et));
	}
}