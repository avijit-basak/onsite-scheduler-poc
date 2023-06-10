package org.scheduler.onsite.model;

public class JobAllocation {

	private final Job job;

	private final Worker worker;

	public JobAllocation(Job job, Worker worker) {
		this.job = job;
		this.worker = worker;
	}

	public Job getJob() {
		return job;
	}

	public Worker getWorker() {
		return worker;
	}

}
