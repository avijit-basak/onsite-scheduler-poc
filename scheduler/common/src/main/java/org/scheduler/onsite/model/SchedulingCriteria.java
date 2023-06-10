package org.scheduler.onsite.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SchedulingCriteria {

	private final List<Worker> workers;

	private final List<Job> jobs;

	public SchedulingCriteria(List<Worker> workers, List<Job> jobs) {
		Objects.requireNonNull(workers);
		Objects.requireNonNull(jobs);

		List<Worker> sequencedWorkers = new ArrayList<Worker>();
		int sequence = 0;
		for (Worker worker : workers) {
			sequencedWorkers.add(new Worker(sequence++, worker));
		}
		this.workers = Collections.unmodifiableList(sequencedWorkers);

		List<Job> sequencedJobs = new ArrayList<Job>();
		sequence = 0;
		for (Job job : jobs) {
			sequencedJobs.add(new Job(sequence++, job));
		}
		this.jobs = Collections.unmodifiableList(sequencedJobs);
	}

	public List<Worker> getWorkers() {
		return workers;
	}

	public List<Job> getJobs() {
		return jobs;
	}

}
