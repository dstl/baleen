//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.cpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.core.jobs.JobTestHelper.build;
import static uk.gov.dstl.baleen.core.jobs.JobTestHelper.getAeName;
import static uk.gov.dstl.baleen.core.jobs.JobTestHelper.getAeParam;

import org.apache.uima.collection.CollectionProcessingEngine;
import org.junit.Test;

import uk.gov.dstl.baleen.core.jobs.JobTestHelper;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.schedules.Other;
import uk.gov.dstl.baleen.testing.DummyTask;
import uk.gov.dstl.baleen.testing.DummyTaskParams;

/**
 *
 */
public class JobCpeBuilderTest {

	@Test(expected = BaleenException.class)
	public void testMissingJob() throws Exception {
		build("job_missing_job.yaml");
	}

	// This is ignored / uncomment, because we don't have the DefautlSchedulder at the time of
	// testing (as its in the Uima project). Its in the defaultscheduler.
	// @Test
	// @Ignore
	// public void testNoSchedule() throws Exception {
	// CollectionProcessingEngine cpe = build("job_no_schedule.yaml");
	// assertTrue(cpe.getCollectionReader() instanceof DefaultScheduler);
	// assertEquals(1, cpe.getCasProcessors().length);
	// assertEquals("task:" + DummyTask.class.getName(), getAeName(cpe, 0));
	// }

	@Test
	public void testScheduleNoParams() throws Exception {
		CollectionProcessingEngine cpe = build("job_schedule_no_params.yaml");
		assertTrue(cpe.getCollectionReader() instanceof Other);
		assertEquals("default", ((Other) cpe.getCollectionReader()).getValue());
		assertEquals(1, cpe.getCasProcessors().length);
		assertEquals("task:" + DummyTask.class.getName(), getAeName(cpe, 0));
	}

	@Test
	public void testScheduleWithParams() throws Exception {
		CollectionProcessingEngine cpe = build("job_schedule_params.yaml");
		assertTrue(cpe.getCollectionReader() instanceof Other);
		assertEquals("value", ((Other) cpe.getCollectionReader()).getValue());
		assertEquals(1, cpe.getCasProcessors().length);
		assertEquals("task:" + DummyTask.class.getName(), getAeName(cpe, 0));
	}

	@Test
	public void testTaskWithClass() throws Exception {
		CollectionProcessingEngine cpe = build("job_task_class.yaml");
		assertEquals(1, cpe.getCasProcessors().length);
		assertEquals("task:" + DummyTask.class.getName(), getAeName(cpe, 0));
	}

	@Test
	public void testTaskWithoutClass() throws Exception {
		CollectionProcessingEngine cpe = build("job_task_string.yaml");
		assertEquals(1, cpe.getCasProcessors().length);
		assertEquals("task:" + DummyTask.class.getName(), getAeName(cpe, 0));
	}

	@Test
	public void testTaskWithParams() throws Exception {
		CollectionProcessingEngine cpe = build("job_task_params.yaml");
		assertEquals(1, cpe.getCasProcessors().length);
		assertEquals("task:" + DummyTaskParams.class.getName(), getAeName(cpe, 0));
		assertEquals("value", getAeParam(cpe, 0, "key"));
	}

	@Test
	public void testTwoTasks() throws Exception {
		CollectionProcessingEngine cpe = build("job_two_tasks.yaml");
		assertEquals(2, cpe.getCasProcessors().length);
		assertEquals("task:" + DummyTask.class.getName(), JobTestHelper.getAeName(cpe, 0));
		assertEquals("task:" + DummyTaskParams.class.getName(), JobTestHelper.getAeName(cpe, 1));
	}

	@Test
	public void testTaskWithGlobal() throws Exception {
		CollectionProcessingEngine cpe = JobTestHelper.build("job_task_global.yaml");
		assertEquals(1, cpe.getCasProcessors().length);
		assertEquals("task:" + DummyTaskParams.class.getName(), JobTestHelper.getAeName(cpe, 0));
		assertEquals("global", JobTestHelper.getAeParam(cpe, 0, "key"));
	}

}
