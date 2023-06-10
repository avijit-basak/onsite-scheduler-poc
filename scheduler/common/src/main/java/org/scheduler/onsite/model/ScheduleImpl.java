package org.scheduler.onsite.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ScheduleImpl implements Schedule {

	private final Map<Job, Worker> jobAllocations = new HashMap<>();

	public ScheduleImpl(List<Job> jobs, List<Integer> jobSequence, List<Worker> workers) {
		for (int i = 0; i < jobs.size(); i++) {
			int jobId = jobSequence.get(i);
			this.jobAllocations.put(jobs.get(jobId), workers.get(jobId));
		}
	}

	public ScheduleImpl(List<JobAllocation> jobAllocations) {
		for (JobAllocation jobAllocation : jobAllocations) {
			this.jobAllocations.put(jobAllocation.getJob(), jobAllocation.getWorker());
		}
	}

	public Worker getWorkerForJob(Job job) {
		return jobAllocations.get(job);
	}

	public Iterator<Job> iterateJobs() {
		return this.jobAllocations.keySet().iterator();
	}

}
