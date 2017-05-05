//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import uk.gov.dstl.baleen.annotators.templates.TemplateAnnotator;
import uk.gov.dstl.baleen.annotators.templates.TemplateFieldConfiguration;
import uk.gov.dstl.baleen.annotators.templates.TemplateFieldDefinitionAnnotator;
import uk.gov.dstl.baleen.annotators.templates.TemplateRecordConfiguration;
import uk.gov.dstl.baleen.annotators.templates.TemplateRecordDefinitionAnnotator;
import uk.gov.dstl.baleen.consumers.utils.SourceUtils;
import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.types.templates.TemplateFieldDefinition;
import uk.gov.dstl.baleen.types.templates.TemplateRecordDefinition;
import uk.gov.dstl.baleen.uima.BaleenConsumer;
import uk.gov.dstl.baleen.uima.utils.CoveringStructureHierarchy;
import uk.gov.dstl.baleen.uima.utils.StructureUtil;
import uk.gov.dstl.baleen.uima.utils.select.ItemHierarchy;

/**
 * Writes RecordDefinitions, and the TemplateFieldDefinitions that they cover,
 * to YAML files for subsequent use in {@link TemplateAnnotator}.
 * <p>
 * See {@link TemplateAnnotator} for a description of the format.
 * </p>
 *
 * <p>
 * This consumer should be used with {@link TemplateRecordDefinitionAnnotator}
 * and {@link TemplateFieldDefinitionAnnotator}.
 * </p>
 */
public class TemplateRecordConfigurationCreatingConsumer extends BaleenConsumer {

	/**
	 * A list of structural types which will be considered during record path
	 * analysis.
	 *
	 * @baleen.config Paragraph,TableCell,ListItem,Aside, ...
	 */
	public static final String PARAM_TYPE_NAMES = "types";

	/** The type names. */
	@ConfigurationParameter(name = PARAM_TYPE_NAMES, mandatory = false)
	private String[] typeNames;

	/** The structural classes. */
	private Set<Class<? extends Structure>> structuralClasses;

	/** The Constant PARAM_OUTPUT_DIRECTORY. */
	public static final String PARAM_OUTPUT_DIRECTORY = "outputDirectory";

	/** The output directory. */
	@ConfigurationParameter(name = PARAM_OUTPUT_DIRECTORY, defaultValue = "recordDefinitions")
	private String outputDirectory = "recordDefinitions";

	/** The object mapper. */
	private final ObjectMapper objectMapper;

	/**
	 * Instantiates a new record definition configuration creating consumer.
	 */
	public TemplateRecordConfigurationCreatingConsumer() {
		objectMapper = new ObjectMapper(new YAMLFactory());
		objectMapper.setSerializationInclusion(Include.NON_NULL);
	}

	@Override
	public void doInitialize(final UimaContext aContext) throws ResourceInitializationException {
		super.doInitialize(aContext);
		structuralClasses = StructureUtil.getStructureClasses(typeNames);
	}

	@Override
	protected void doProcess(final JCas jCas) throws AnalysisEngineProcessException {

		CoveringStructureHierarchy structureHierarchy = CoveringStructureHierarchy.build(jCas, structuralClasses);
		Collection<TemplateRecordDefinition> recordDefinitions = JCasUtil.select(jCas, TemplateRecordDefinition.class);
		Collection<TemplateFieldDefinition> fieldDefinitions = new HashSet<>(
				JCasUtil.select(jCas, TemplateFieldDefinition.class));

		Map<String, TemplateRecordConfiguration> recordConfigurations = new HashMap<>();

		for (TemplateRecordDefinition recordDefinition : recordDefinitions) {

			String name = recordDefinition.getName();
			if (recordConfigurations.containsKey(name)) {
				throw new AnalysisEngineProcessException(
						new IllegalArgumentException("Record name not unique: " + name));
			}

			Optional<Structure> startStructure = JCasUtil
					.selectCovering(jCas, Structure.class, recordDefinition.getBegin(), recordDefinition.getBegin())
					.stream().max(Comparator.comparingInt(Structure::getDepth));
			Optional<Structure> coveringStructure = structureHierarchy.getCoveringStructure(recordDefinition);
			Optional<Structure> endStructure = JCasUtil
					.selectCovering(jCas, Structure.class, recordDefinition.getEnd(), recordDefinition.getEnd())
					.stream().max(Comparator.comparingInt(Structure::getDepth));

			if (!startStructure.isPresent() || !endStructure.isPresent()) {
				getMonitor().warn("Could not find start or end structure elements for record definition {} - giving up",
						name);
				continue;
			}

			List<TemplateFieldDefinition> definitions = JCasUtil.selectCovered(TemplateFieldDefinition.class,
					recordDefinition);
			fieldDefinitions.removeAll(definitions);

			List<TemplateFieldConfiguration> fields = makeFields(structureHierarchy, definitions);

			Optional<Structure> preceding = structureHierarchy.getPrevious(startStructure.get());
			Optional<Structure> following = structureHierarchy.getNext(endStructure.get());

			List<Structure> coveredStructures = JCasUtil.selectBetween(Structure.class, getPreceding(jCas, preceding),
					getFollowing(jCas, following));

			String precedingPath = preceding.isPresent()
					? structureHierarchy.getSelectorPath(preceding.get()).toString() : "";

			String followingPath = following.isPresent()
					? structureHierarchy.getSelectorPath(following.get()).toString() : "";

			if (recordDefinition.getRepeat()) {
				int depth = Math.max(preceding.map(Structure::getDepth).orElse(0),
						following.map(Structure::getDepth).orElse(0));
				List<String> coveredPaths = generateCoveredPaths(structureHierarchy, coveredStructures, depth);

				String minimalRepeat = null;
				if (coveringStructure.isPresent() && coveredStructures.contains(coveringStructure.get())) {
					Structure repeatingUnit = coveringStructure.get();
					minimalRepeat = structureHierarchy.getSelectorPath(repeatingUnit).toString();
				}

				recordConfigurations.put(name, new TemplateRecordConfiguration(name, precedingPath, coveredPaths,
						minimalRepeat, followingPath, fields, recordDefinition.getBegin()));
			} else {
				recordConfigurations.put(name, new TemplateRecordConfiguration(name, precedingPath, followingPath,
						fields, recordDefinition.getBegin()));
			}
		}

		List<TemplateRecordConfiguration> configurations = new ArrayList<>(recordConfigurations.values());

		if (!fieldDefinitions.isEmpty()) {
			for (TemplateFieldDefinition field : fieldDefinitions) {
				configurations.add(new TemplateRecordConfiguration(
						makeFields(structureHierarchy, ImmutableList.of(field)), field.getBegin()));
			}
		}

		String documentSourceName = SourceUtils.getDocumentSourceBaseName(jCas, getSupport());
		try (Writer w = createOutputWriter(documentSourceName)) {

			Collections.sort(configurations, Comparator.comparing(TemplateRecordConfiguration::getOrder));
			objectMapper.writeValue(w, configurations);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	/**
	 * Get the preceding annotation from the optional or return a substitute
	 * annotation at the start of the document.
	 *
	 * @param jCas
	 *            the jCas
	 * @param structure
	 *            the optional structure
	 * @return a preceding annotations
	 */
	private Structure getPreceding(JCas jCas, Optional<Structure> structure) {
		if (structure.isPresent()) {
			return structure.get();
		}
		return new Structure(jCas, 0, 0);
	}

	/**
	 * Get the following annotation from the optional or return a substitute
	 * annotation at the end of the document
	 *
	 * @param jCas
	 *            the jCas
	 * @param structure
	 *            the optional structure
	 * @return a following annotations
	 */
	private Structure getFollowing(JCas jCas, Optional<Structure> structure) {
		if (structure.isPresent()) {
			return structure.get();
		}
		int length = jCas.getDocumentText().length();
		return new Structure(jCas, length, length);
	}

	/**
	 * Generate the covered paths, reducing to the lowest depth.
	 *
	 * @param structureHierarchy
	 *            the structure hierarchy
	 * @param coveredStructures
	 *            the covered structures
	 * @param depth
	 *            the maximum depth
	 * @return list of paths for the covered structures
	 */
	private List<String> generateCoveredPaths(ItemHierarchy<Structure> structureHierarchy,
			List<Structure> coveredStructures, int depth) {
		LinkedHashSet<String> collect = coveredStructures.stream()
				.map(s -> structureHierarchy.getSelectorPath(s).toDepth(depth).toString())
				.collect(Collectors.toCollection(LinkedHashSet::new));

		Builder<String> builder = ImmutableList.<String>builder();
		String parent = collect.iterator().next();
		builder.add(parent);
		for (String path : collect) {
			if (!path.startsWith(parent)) {
				builder.add(path);
				parent = path;
			}
		}

		return builder.build();
	}

	/**
	 * Make fields from definitions and look up the location in the structure
	 *
	 * @param structureHierarchy
	 *            the structure hierarchy
	 * @param fields
	 *            the fields
	 * @return the list of configurations
	 */
	private List<TemplateFieldConfiguration> makeFields(final CoveringStructureHierarchy structureHierarchy,
			Collection<TemplateFieldDefinition> definitions) {
		List<TemplateFieldConfiguration> fields = new ArrayList<>();

		for (TemplateFieldDefinition templateFieldDefinition : definitions) {
			String fieldPath = structureHierarchy.generatePath(templateFieldDefinition).toString();
			TemplateFieldConfiguration field = makeField(templateFieldDefinition, fieldPath);
			fields.add(field);
		}
		return fields;
	}

	/**
	 * Make field from definition and path
	 *
	 * @param templateFieldDefinition
	 *            the field definition
	 * @param fields
	 *            the fields
	 * @return the configuration
	 */
	private TemplateFieldConfiguration makeField(TemplateFieldDefinition templateFieldDefinition, String fieldPath) {
		TemplateFieldConfiguration field = new TemplateFieldConfiguration(templateFieldDefinition.getName(), fieldPath);
		field.setRequired(templateFieldDefinition.getRequired());
		field.setRepeat(templateFieldDefinition.getRepeat());
		field.setRegex(templateFieldDefinition.getRegex());
		return field;
	}

	/**
	 * Creates the output writer for the configuration yaml files.
	 *
	 * @param documentSourceName
	 *            the document source name
	 * @return the writer
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private Writer createOutputWriter(final String documentSourceName) throws IOException {
		Path directoryPath = Paths.get(outputDirectory);
		if (!directoryPath.toFile().exists()) {
			Files.createDirectories(directoryPath);
		}
		String baseName = FilenameUtils.getBaseName(documentSourceName);
		Path outputFilePath = directoryPath.resolve(baseName + ".yaml");

		if (outputFilePath.toFile().exists()) {
			getMonitor().warn("Overwriting existing output properties file {}", outputFilePath);
		}
		return Files.newBufferedWriter(outputFilePath, StandardCharsets.UTF_8);
	}

}
