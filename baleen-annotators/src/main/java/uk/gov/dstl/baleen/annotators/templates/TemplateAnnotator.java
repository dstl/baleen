// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.templates;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.templates.TemplateRecordConfiguration.Kind;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.types.templates.TemplateField;
import uk.gov.dstl.baleen.types.templates.TemplateRecord;
import uk.gov.dstl.baleen.uima.utils.SelectorPath;
import uk.gov.dstl.baleen.uima.utils.StructureHierarchy;
import uk.gov.dstl.baleen.uima.utils.StructureUtil;

/**
 * Using previously created record definitions, creates annotations for records and the the fields
 * contained within them.
 *
 * <p>Each YAML configuration file contains multiple definitions in an array/list, with each
 * definition being an object with following fields:
 *
 * <p>
 *
 * <dl>
 *   <dt>fields
 *   <dd>A list of field definitions. Fields must have a <code>name</code> and <code>path</code>,
 *       and can optionally have a regular expression (<code>regex</code>) a <code>defaultValue
 *       </code> and declare if they are <code>required</code>. A TemplateField annotation is
 *       created for each matched path and restrictions.
 *   <dt>kind
 *   <dd>Whether the field selectors above should be used to create a <code>NAMED</code> record, in
 *       which case a name field will also be supplied, or these are not part of an explicit record,
 *       and thus gathered into a <code>DEFAULT</code> record, so they are still annotated as
 *       TemplateFields.
 *   <dt>name
 *   <dd>Only present on <code>NAMED</code> RecordDefinitions, and is populated with the name of the
 *       record.
 *   <dd>
 * </dl>
 *
 * An example YAML configuration could be:
 *
 * <pre>
 * ---
 * - name: "NamedRecord"
 * kind: "NAMED"
 * order: 1
 * fields:
 * - name: "Description"
 * path: "Paragraph:nth-of-type(8)"
 * - name: "FullName"
 * path: "Table:nth-of-type(2) &gt; TableBody &gt; TableRow:nth-of-type(2) &gt;\
 * \ TableCell:nth-of-type(2) &gt; Paragraph"
 * required: "true"
 * precedingPath: "Paragraph:nth-of-type(6)"
 * followingPath: "Paragraph:nth-of-type(10)"
 * - name: "row"
 * kind: "NAMED"
 * order: 2
 * fields:
 * - name: "title"
 * path: "Document &gt; Table:nth-of-type(2) &gt; TableBody &gt; TableRow:nth-of-type(1) &gt; TableCell:nth-of-type(1)"
 * required: false
 * - name: "FirstName"
 * path: "Document &gt; Table:nth-of-type(2) &gt; TableBody &gt; TableRow:nth-of-type(1) &gt; TableCell:nth-of-type(2)"
 * required: false
 * - name: "Surname"
 * path: "Document &gt; Table:nth-of-type(2) &gt; TableBody &gt; TableRow:nth-of-type(1) &gt; TableCell:nth-of-type(3)"
 * required: false
 * - name: "DoB"
 * path: "Document &gt; Table:nth-of-type(2) &gt; TableBody &gt; TableRow:nth-of-type(1) &gt; TableCell:nth-of-type(4)"
 * required: false
 * precedingPath: "Document &gt; Heading:nth-of-type(4)"
 * followingPath: "Document &gt; Paragraph:nth-of-type(4)"
 * repeat: true
 * coveredPaths:
 * - "Document &gt; Table:nth-of-type(2)"
 * minimalRepeat: "Document &gt; Table:nth-of-type(2) &gt; TableBody &gt; TableRow:nth-of-type(1)"
 * - kind: "DEFAULT"
 * order: 3
 * fields:
 * - name: "DocumentTitle"
 * path: "Heading:nth-of-type(2)"
 * - name: "DocumentDate"
 * path: "Paragraph:nth-of-type(3)"
 * regex: "\d{1,2}\/\d{1,2}\/\d{4}"
 * </pre>
 *
 * <p>Configurations are typically created by running a pipeline with the
 * RecordDefinitionConfigurationCreatingConsumer, which uses annotations created by
 * RecordDefinitionAnnotation and TemplateFieldDefinitionAnnotator running over template documents.
 */
public class TemplateAnnotator extends AbstractTemplateAnnotator {

  /**
   * A list of structural types which will be considered during record path analysis.
   *
   * @baleen.config Paragraph,TableCell,ListItem,Aside, ...
   */
  public static final String PARAM_TYPE_NAMES = "types";

  /** The type names. */
  @ConfigurationParameter(name = PARAM_TYPE_NAMES, mandatory = false)
  private String[] typeNames;

  /**
   * Metadata key used to store templates matched
   *
   * @baleen.config topic
   */
  public static final String PARAM_METADTA_KEY = "key";

  @ConfigurationParameter(name = PARAM_METADTA_KEY, defaultValue = "label")
  private String metadataKey;

  /** The structural classes. */
  private Set<Class<? extends Structure>> structuralClasses;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);
    structuralClasses = StructureUtil.getStructureClasses(typeNames);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  protected void doProcess(final JCas jCas) throws AnalysisEngineProcessException {
    for (Entry<String, Collection<TemplateRecordConfiguration>> entry :
        recordDefinitions.asMap().entrySet()) {
      doProcessRecordDefinitions(jCas, entry.getKey(), (List) entry.getValue());
    }
  }

  /**
   * Process for the given source and record definition. The passed JCas object contains information
   * about the document and any existing annotations.
   *
   * @param jCas JCas object to process
   * @param source the source of this record definition
   * @param recordDefinitions the recordDefinitions
   */
  protected void doProcessRecordDefinitions(
      final JCas jCas, String source, List<TemplateRecordConfiguration> recordDefinitions) {
    RecordStructureManager manager =
        new RecordStructureManager(StructureHierarchy.build(jCas, structuralClasses));

    Collections.sort(
        recordDefinitions, Comparator.comparing(TemplateRecordConfiguration::getOrder));

    for (TemplateRecordConfiguration recordDefinition : recordDefinitions) {
      doProcessRecordDefinition(jCas, manager, source, recordDefinition);
    }
  }

  /**
   * Process the given record definition.
   *
   * @param jCas JCas object to process
   * @param manager the record structure manager
   * @param source the source of the record
   * @param recordDefinition the record definition
   */
  private void doProcessRecordDefinition(
      JCas jCas,
      RecordStructureManager manager,
      String source,
      TemplateRecordConfiguration recordDefinition) {
    if (recordDefinition.getKind() == Kind.NAMED) {
      createRecord(jCas, manager, source, recordDefinition);
    } else {
      createTemplateFields(
          jCas, manager, source, recordDefinition.getFields(), jCas.getDocumentText().length());
    }
  }

  /**
   * Creates the record based on the paths in the record definition.
   *
   * <p>If errors occur during selection these are logged.
   *
   * @param jCas JCas object to process
   * @param manager the record structure manager
   * @param source the source
   * @param recordDefinition the record definition
   * @throws InvalidParameterException
   */
  private void createRecord(
      JCas jCas,
      RecordStructureManager manager,
      String source,
      TemplateRecordConfiguration recordDefinition) {

    Optional<Structure> preceding = Optional.empty();
    boolean matched = false;
    try {
      preceding = manager.select(recordDefinition.getPrecedingPath());
    } catch (InvalidParameterException e) {
      getMonitor()
          .warn("Failed to select structure preceeding record " + recordDefinition.getName(), e);
    }

    if (recordDefinition.isRepeat()) {
      Optional<Structure> last;
      RepeatSearch repeatSearch;
      try {
        repeatSearch = manager.createRepeatSearch(recordDefinition);
      } catch (InvalidParameterException e) {
        getMonitor()
            .warn(
                "Error while generating repeating unit for record " + recordDefinition.getName(),
                e);
        return;
      }
      boolean isFirst = true;
      do {
        last = manager.repeatRecord(preceding, repeatSearch, isFirst);

        if (last.isPresent()) {
          matched = true;
          createRecordAnnotation(
              jCas,
              source,
              recordDefinition.getName(),
              getPreceedingEnd(preceding),
              last.get().getEnd());
          createTemplateFields(
              jCas, manager, source, recordDefinition.getFields(), last.get().getEnd());
        }
        isFirst = false;
        preceding = last;
      } while (last.isPresent());

    } else {

      Optional<Structure> following = Optional.empty();
      try {
        following = manager.select(recordDefinition.getFollowingPath());
      } catch (InvalidParameterException e) {
        getMonitor()
            .warn("Failed to select structure following record " + recordDefinition.getName(), e);
      }

      int end = getFollowingBegining(jCas, following);
      createRecordAnnotation(
          jCas, source, recordDefinition.getName(), getPreceedingEnd(preceding), end);

      createTemplateFields(jCas, manager, source, recordDefinition.getFields(), end);

      matched = true;
    }
    if (matched) {
      Metadata md = new Metadata(jCas);
      md.setKey(metadataKey);
      md.setValue(recordDefinition.getName());
      addToJCasIndex(md);
    }
  }

  /**
   * Get the begin based on the preceding
   *
   * @param preceding optional of the preceding
   * @return the end of the preceding or the start of the document
   */
  private int getPreceedingEnd(Optional<Structure> preceding) {
    if (preceding.isPresent()) {
      return preceding.get().getEnd();
    } else {
      return 0;
    }
  }

  /**
   * Get the begin based on the following
   *
   * @param jCas the jcas
   * @param following optional of the following
   * @return the begin of the following or the end of the document
   */
  private int getFollowingBegining(JCas jCas, Optional<Structure> following) {
    if (following.isPresent()) {
      return following.get().getBegin();
    } else {
      return jCas.getDocumentText().length();
    }
  }

  /**
   * Creates the template fields based on the field configurations.
   *
   * @param jCas JCas object to process
   * @param manager the record structure manager
   * @param source the source
   * @param fields the fields
   * @param end the end to stop at
   */
  private void createTemplateFields(
      JCas jCas,
      RecordStructureManager manager,
      String source,
      List<TemplateFieldConfiguration> fields,
      int end) {
    for (TemplateFieldConfiguration field : fields) {
      createTemplateField(jCas, manager, source, field, end);
    }
  }

  /**
   * Create the template field from the configuration
   *
   * @param jCas the jCas
   * @param manager the record structure manager
   * @param source the source
   * @param field the field configuration
   * @param end the end to stop at
   */
  private void createTemplateField(
      JCas jCas,
      RecordStructureManager manager,
      String source,
      TemplateFieldConfiguration field,
      int end) {
    String fieldName = field.getName();
    String fieldPath = field.getPath();
    boolean matched = false;
    try {
      SelectorPath path = SelectorPath.parse(fieldPath);
      Optional<Structure> fieldStructure = manager.select(path);
      if (fieldStructure.isPresent()) {
        matched = createFieldAnnotation(jCas, source, field, fieldStructure.get());
      } else {
        manager.recordMissing(path);
        getMonitor()
            .warn(
                "Expected single structure element for field {} with path {} - ignoring",
                fieldName,
                path);
      }

      if (field.isRepeat()) {
        SelectorPath parentPath = path.toDepth(path.getDepth() - 1);
        Optional<Structure> parent = manager.select(parentPath);
        int fieldEnd = Math.min(end, parent.map(Structure::getEnd).orElse(Integer.MAX_VALUE));
        while (field.isRepeat() && fieldStructure.isPresent()) {
          fieldStructure = manager.repeatField(fieldStructure, path, fieldEnd);
          if (fieldStructure.isPresent()) {
            matched |= createFieldAnnotation(jCas, source, field, fieldStructure.get());
          }
        }
      }
    } catch (InvalidParameterException e) {
      getMonitor().warn("Failed to match structure for field " + fieldName, e);
    }
    if (matched) {
      Metadata md = new Metadata(jCas);
      md.setKey(metadataKey);
      md.setValue(fieldName);
      addToJCasIndex(md);
    }
  }

  /**
   * Create field annotation for the given field definition and matched structural element.
   *
   * @param jCas JCas object to process
   * @param manager the record structure manager
   * @param source the source template definition file name
   * @param field the field
   * @param structure the structure
   * @return true if created
   */
  private boolean createFieldAnnotation(
      JCas jCas, String source, TemplateFieldConfiguration field, Structure structure) {

    String defaultValue = field.getDefaultValue();

    if (structure.getCoveredText().isEmpty()) {
      if (field.isRequired() && defaultValue == null) {
        getMonitor().info("Required field missing {} in {}", field.getName(), source);
        return false;
      } else {
        createFieldAnnotation(
            jCas, source, field.getName(), structure.getBegin(), structure.getEnd(), defaultValue);
        return true;
      }
    }

    String regex = field.getRegex();

    if (regex == null) {
      createFieldAnnotation(
          jCas,
          source,
          field.getName(),
          structure.getBegin(),
          structure.getEnd(),
          structure.getCoveredText());
      return true;
    } else {
      Pattern pattern = Pattern.compile(regex);
      String coveredText = structure.getCoveredText();
      Matcher matcher = pattern.matcher(coveredText);
      if (matcher.find()) {
        createFieldAnnotation(
            jCas,
            source,
            field.getName(),
            structure.getBegin() + matcher.start(),
            structure.getBegin() + matcher.end(),
            matcher.group());
        return true;
      } else if (defaultValue != null) {
        getMonitor()
            .info(
                "Failed to match pattern {} in {} - using default value {}",
                regex,
                coveredText,
                defaultValue);
        createFieldAnnotation(
            jCas,
            source,
            field.getName(),
            structure.getBegin(),
            structure.getBegin(),
            defaultValue);
        return true;
      } else {
        getMonitor().warn("Failed to match pattern {} in {} - ignoring", regex, coveredText);
        return false;
      }
    }
  }

  /**
   * Creates the field annotation.
   *
   * @param jCas the JCas
   * @param source the source
   * @param name the name
   * @param begin the begin
   * @param end the end
   * @param value the value
   * @return the created field
   */
  protected TemplateField createFieldAnnotation(
      JCas jCas, String source, String name, int begin, int end, String value) {
    TemplateField field = new TemplateField(jCas);
    field.setBegin(begin);
    field.setEnd(end);
    field.setName(name);
    field.setSource(source);
    field.setValue(value);
    addToJCasIndex(field);
    return field;
  }

  /**
   * Creates the record annotation.
   *
   * @param jCas the JCas
   * @param source the source
   * @param name the name
   * @param begin the begin
   * @param end the end
   * @return the created Record
   */
  protected TemplateRecord createRecordAnnotation(
      JCas jCas, String source, String name, int begin, int end) {
    TemplateRecord record = new TemplateRecord(jCas);
    record.setBegin(begin);
    record.setSource(source);
    record.setEnd(end);
    record.setName(name);
    addToJCasIndex(record);
    return record;
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        new HashSet<>(structuralClasses),
        ImmutableSet.of(TemplateField.class, TemplateRecord.class));
  }
}
