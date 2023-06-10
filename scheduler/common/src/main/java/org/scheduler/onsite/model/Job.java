package org.scheduler.onsite.model;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public final class Job {

	private final int id;

	private final Date slaTime;

	private final int expectedCompletionDuration;

	private final int priority;

	private final List<Integer> skillCodes;

	private final Location location;

	public Job(Date slaTime, int expectedCompletionDuration, int priority, List<Integer> skillCodes,
			Location location) {
		Objects.requireNonNull(slaTime);
		Objects.requireNonNull(skillCodes);
		Objects.requireNonNull(location);
		this.id = 0;
		this.slaTime = slaTime;
		this.expectedCompletionDuration = expectedCompletionDuration;
		this.priority = priority;
		this.skillCodes = Collections.unmodifiableList(skillCodes);
		this.location = location;
	}

	public Job(int id, Date slaTime, int expectedCompletionDuration, int priority, List<Integer> skillCodes,
			Location location) {
		Objects.requireNonNull(slaTime);
		Objects.requireNonNull(skillCodes);
		Objects.requireNonNull(location);
		this.id = id;
		this.slaTime = slaTime;
		this.expectedCompletionDuration = expectedCompletionDuration;
		this.priority = priority;
		this.skillCodes = Collections.unmodifiableList(skillCodes);
		this.location = location;
	}

	public Job(int id, Job job) {
		this.id = id;
		this.slaTime = job.slaTime;
		this.expectedCompletionDuration = job.expectedCompletionDuration;
		this.priority = job.priority;
		this.skillCodes = job.skillCodes;
		this.location = job.location;
	}

	public int getId() {
		return id;
	}

	public Date getSlaTime() {
		return slaTime;
	}

	public int getExpectedCompletionDuration() {
		return expectedCompletionDuration;
	}

	public int getPriority() {
		return priority;
	}

	public List<Integer> getSkillCodes() {
		return skillCodes;
	}

	public Location getLocation() {
		return location;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Job other = (Job) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Job [id=" + id + ", slaTime=" + slaTime + ", expectedCompletionDuration=" + expectedCompletionDuration
				+ ", priority=" + priority + ", skillCodes=" + skillCodes + ", location=" + location + "]";
	}

}
