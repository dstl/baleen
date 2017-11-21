//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.utils;

/**
 * Hold default values for settings within Baleen
 */
public class BaleenDefaults {
	
	/**
	 * Default package for Collection Readers
	 */
	public static final String DEFAULT_READER_PACKAGE = "uk.gov.dstl.baleen.collectionreaders";
	
	/**
	 * Default package for Annotators
	 */
	public static final String DEFAULT_ANNOTATOR_PACKAGE = "uk.gov.dstl.baleen.annotators";
	
	/**
	 * Default package for Consumers
	 */
	public static final String DEFAULT_CONSUMERS_PACKAGE = "uk.gov.dstl.baleen.consumers";
	
	/**
	 * Default package for Pipeline Orderers
	 */
	public static final String DEFAULT_ORDERER_PACKAGE = "uk.gov.dstl.baleen.orderers";

	/**
	 * Default package for Schedules
	 */
	public static final String DEFAULT_SCHEDULE_PACKAGE = "uk.gov.dstl.baleen.schedules";
	
	/**
	 * Default package for Tasks
	 */
	public static final String DEFAULT_TASK_PACKAGE = "uk.gov.dstl.baleen.jobs";
			
	/**
	 * Default Schedule
	 */
	public static final String DEFAULT_SCHEDULER = "uk.gov.dstl.baleen.schedules.Once";
	
	/**
	 * Default Pipeline Orderer
	 */
	public static final String DEFAULT_ORDERER = "uk.gov.dstl.baleen.orderers.DependencyGraph";
	
	/**
	 * Default Content Extractor
	 */
	public static final String DEFAULT_CONTENT_EXTRACTOR = "uk.gov.dstl.baleen.contentextractors.StructureContentExtractor";
	
	/** 
	 * Default package for Content Mappers
	 */
	public static final String DEFAULT_CONTENT_MAPPER_PACKAGE = "uk.gov.dstl.baleen.contentmappers";

	/** 
	 * Default package for Content Manipulators
	 */
	public static final String DEFAULT_CONTENT_MANIPULATOR_PACKAGE = "uk.gov.dstl.baleen.contentmanipulators";

	/**
	 * Default package for Content Extractors
	 */
	public static final String DEFAULT_CONTENT_EXTRACTOR_PACKAGE = "uk.gov.dstl.baleen.contentextractors";

	private BaleenDefaults(){
		//Private constructor
	}
}