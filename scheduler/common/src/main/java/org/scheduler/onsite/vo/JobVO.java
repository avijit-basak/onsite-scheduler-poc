package org.scheduler.onsite.vo;

import java.util.Calendar;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public final class JobVO {

	private int id;

	@JsonSerialize(using = CustomCalendarSerializer.class)
	@JsonDeserialize(using = CustomCalendarDeserializer.class)
	private Calendar slaTime;

	private int expectedCompletionDuration;

	private int priority;

	private List<Integer> skillCodes;

	private LocationVO location;

	public JobVO() {
		this.id = 0;
		this.slaTime = Calendar.getInstance();
		this.expectedCompletionDuration = 0;
		this.priority = 0;
		this.skillCodes = null;
		this.location = new LocationVO();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Calendar getSlaTime() {
		return slaTime;
	}

	public void setSlaTime(Calendar slaTime) {
		this.slaTime = slaTime;
	}

	public int getExpectedCompletionDuration() {
		return expectedCompletionDuration;
	}

	public void setExpectedCompletionDuration(int expectedCompletionDuration) {
		this.expectedCompletionDuration = expectedCompletionDuration;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public List<Integer> getSkillCodes() {
		return skillCodes;
	}

	public void setSkillCodes(List<Integer> skillCodes) {
		this.skillCodes = skillCodes;
	}

	public LocationVO getLocation() {
		return location;
	}

	public void setLocation(LocationVO location) {
		this.location = location;
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
		JobVO other = (JobVO) obj;
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
