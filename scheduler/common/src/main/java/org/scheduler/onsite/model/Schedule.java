package org.scheduler.onsite.model;

import java.util.Iterator;

public interface Schedule {

	public Iterator<Job> iterateJobs();

	public Worker getWorkerForJob(Job job);

}
