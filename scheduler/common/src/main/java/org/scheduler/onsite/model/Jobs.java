package org.scheduler.onsite.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Jobs implements Iterable<Job> {

	private final List<Job> jobs;

	private Jobs(List<Job> jobs) {
		this.jobs = Collections.unmodifiableList(jobs);
	}

	public static Jobs newJobs(List<Job> jobs) {
		return new Jobs(jobs);
	}

	public Job get(int index) {
		return jobs.get(index);
	}

	public Iterator<Job> iterate() {
		return jobs.iterator();
	}

	public Iterator<Job> iterator() {
		return jobs.iterator();
	}

	public Jobs permute(List<Integer> permutatedJobIds) {
		Jobs sortedJobs = sortById();
		List<Job> permutatedJobs = new ArrayList<>();
		for (Integer jobId : permutatedJobIds) {
			permutatedJobs.add(sortedJobs.get(jobId));
		}
		return new Jobs(permutatedJobs);
	}

	public Jobs sortById() {
		List<Job> sortedJobs = new ArrayList<>(jobs);
		Collections.sort(sortedJobs, new Comparator<Job>() {

			@Override
			public int compare(Job job1, Job job2) {
				return job1.getId() - job2.getId();
			}
		});
		return new Jobs(sortedJobs);
	}

	public int getJobcount() {
		return jobs.size();
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("[");
		for (Job job : jobs) {
			str.append(job.getId());
			str.append(",");
		}
		str.replace(str.lastIndexOf(","), str.length(), "");
		str.append("]");
		return "Jobs [jobs=" + str + "]";
	}

}
