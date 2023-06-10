package org.scheduler.onsite.request.vo;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WorkerVO {

	private int sequence;

	private Map<Integer, Integer> skillCodeLevelMap;

	private LocationVO location;

	private double distanceTravelled;

	private List<AvailabilitySlotVO> availabilitySlots;

	public WorkerVO() {
	};

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public Map<Integer, Integer> getSkillCodeLevelMap() {
		return skillCodeLevelMap;
	}

	public void setSkillCodeLevelMap(Map<Integer, Integer> skillCodeLevelMap) {
		this.skillCodeLevelMap = skillCodeLevelMap;
	}

	public LocationVO getLocation() {
		return location;
	}

	public void setLocation(LocationVO location) {
		this.location = location;
	}

	public double getDistanceTravelled() {
		return distanceTravelled;
	}

	public void setDistanceTravelled(double distanceTravelled) {
		this.distanceTravelled = distanceTravelled;
	}

	public List<AvailabilitySlotVO> getAvailabilitySlots() {
		return availabilitySlots;
	}

	public void setAvailabilitySlots(List<AvailabilitySlotVO> availabilitySlots) {
		this.availabilitySlots = availabilitySlots;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WorkerVO other = (WorkerVO) obj;
		return sequence == other.sequence;
	}

	@Override
	public int hashCode() {
		return Objects.hash(sequence);
	}

	@Override
	public String toString() {
		return "Worker [sequence=" + sequence + ", skillCodeLevelMap=" + skillCodeLevelMap + ", location=" + location
				+ ", distanceTravelled=" + distanceTravelled + "]";
	}

}
