package org.scheduler.onsite.request.vo;

import java.util.List;

public class SchedulingCriteriaVO {

	private List<WorkerVO> workers;

	private List<JobVO> jobs;

	public SchedulingCriteriaVO() {
	}

	public List<WorkerVO> getWorkers() {
		return workers;
	}

	public void setWorkers(List<WorkerVO> workers) {
		this.workers = workers;
	}

	public List<JobVO> getJobs() {
		return jobs;
	}

	public void setJobs(List<JobVO> jobs) {
		this.jobs = jobs;
	}

}
