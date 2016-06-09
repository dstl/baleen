package uk.gov.dstl.baleen.cpe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;

import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * This class provides methods to convert a Baleen YAML configuration file into a
 * {@link org.apache.uima.collection.CollectionProcessingEngine} that can be executed by Baleen.
 * <p>
 * The YAML configuration file should contain a single <i>collectionreader</i> 'object' and a list
 * of <i>annotators</i> and <i>consumers</i> objects. Each analysis engine should have a
 * <i>class</i> property, which refers to the class of the annotator. If the class cannot be found
 * as specified, then the default Baleen package for that type is searched instead (e.g.
 * uk.gov.dstl.baleen.annotators). If an collection reader, annotator or consumer has no properties
 * then the class property prefix is optional (i.e. the list item can consist solely of the
 * annotator class).
 * <p>
 * Any additional properties on the analysis engine are passed as Params to the analysis engine.
 * Additionally, any top level objects that aren't expected are assumed to be global parameters that
 * are passed to all analysis engines. Where locally specified parameters have the same name as
 * global ones, the local versions take precedent.
 * <p>
 * Analysis engines are added to the CPE in the same order that they are listed, with annotators
 * listed before consumers.
 * <p>
 * For example:
 *
 * <pre>
 * shape:
 *   color: red
 *   size: large
 *
 * job:
 *   schedule:
 *     class: Repeat
 *     count: 5
 *   tasks:
 *   - class: DummyTask
 *   - class: DummyTaskWithParams
 *     param: value
 *
 * </pre>
 *
 * As the {@link PipelineCpeBuilder} the configuration will use local variables for parameters
 * first, and then fall back to globals. Similarly as per {@link PipelineCpeBuilder} jobs are looked
 * up first under uk.gov.dstl.baleen.jobs, or include the full qualified package. Schedules are
 * looked up under uk.gov.dstl.baleen.jobs.schedules (unless fully qualified).
 *
 * You may omit the class if your schedule or task is simple:
 *
 * <pre>
 *
 * job:
 *   schedule: Repeat
 *   tasks:
 *   - DummyTask
 * </pre>
 *
 * If you don not include any schedule then the job will just run once. (the same as including the
 * Once scheduler).
 *
 * Resources are automatically detected (assuming the analysis engine has used the @ExternalResource
 * annotation) and created. Resources should use global parameters (e.g. shape.color in the above
 * example) to initialise themselves, as these are the only ones that will be passed to them.
 *
 */
@SuppressWarnings("unchecked")
public class JobCpeBuilder extends AbstractCpeBuilder {

	private static final String DEFAULT_SCHEDULER = "uk.gov.dstl.baleen.schedules.Once";

	/**
	 * Initiate a CpeBuilder with a YAML configuration file
	 *
	 * @param pipelineName
	 *            The name of the pipeline
	 * @param yamlFile
	 *            The file containing the configuration
	 * @throws IOException
	 */
	public JobCpeBuilder(String name, File yamlFile) throws BaleenException {
		super(name, yamlFile);
	}

	/**
	 * Initiate a CpeBuilder with an input stream
	 *
	 * @param pipelineName
	 *            The name of the pipeline
	 * @param inputStream
	 *            The input stream containing the YAML configuration
	 * @throws IOException
	 *             if the input stream can not be read
	 */
	public JobCpeBuilder(String name, InputStream inputStream) throws BaleenException {
		super(name, inputStream);
	}

	/**
	 * Initiate a CpeBuilder with a YAML string
	 *
	 * @param pipelineName
	 *            The name of the pipeline
	 * @param yamlString
	 *            A string containing the configuration
	 */
	public JobCpeBuilder(String name, String yamlString) throws BaleenException {
		super(name, yamlString);
	}

	@Override
	protected void configure(String name, Map<String, Object> config) throws BaleenException {

		Object jobObject = config.get("job");
		if (jobObject == null || !(jobObject instanceof Map)) {
			throw new BaleenException("No valid job configuration found");
		}

		Map<String, Object> jobConfig = (Map<String, Object>) jobObject;

		Object scheduleConfig = jobConfig.get("schedule");
		String scheduleClass = CpeBuilderUtils.getClassNameFromConfig(scheduleConfig);
		// Default to the Once scheduler
		if (scheduleClass == null) {
			scheduleClass = DEFAULT_SCHEDULER;
		}
		Map<String, Object> scheduleParams = CpeBuilderUtils.getParamsFromConfig(scheduleConfig);

		Optional<CollectionReaderDescription> scheduler = createCollectionReader(scheduleClass, scheduleParams,
				SCHEDULE_DEFAULT_PACKAGE);
		if (scheduler.isPresent()) {
			setCollectorReader(scheduler.get());
		}

		Object tasksConfig = jobConfig.get("tasks");
		if (tasksConfig instanceof List) {
			for (Object task : (List<Object>) tasksConfig) {
				String taskClass = CpeBuilderUtils.getClassNameFromConfig(task);
				Map<String, Object> taskParams = CpeBuilderUtils.getParamsFromConfig(task);

				Optional<AnalysisEngineDescription> ae = createAnnotator(taskClass, taskParams, JOB_DEFAULT_PACKAGE);
				if (ae.isPresent()) {
					String componentName = CpeBuilderUtils.getComponentName(getAnnotatorNames(), "task:" + taskClass);
					addAnnotator(componentName, ae.get());
				}
			}
		}
	}

}
