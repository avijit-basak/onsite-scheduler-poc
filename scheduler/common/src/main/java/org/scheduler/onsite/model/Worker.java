package org.scheduler.onsite.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.scheduler.onsite.exception.SchedulerException;

public class Worker {

	private final int sequence;

	private final Map<Integer, Integer> skillCodeLevelMap;

	private final Location location;

	private double distanceTravelled;

	private final List<AvailabilitySlot> availabilitySlots;

	public Worker(Map<Integer, Integer> skillCodeLevelMap, Location location,
			List<AvailabilitySlot> availabilitySlots) {
		Objects.requireNonNull(skillCodeLevelMap);
		// Objects.requireNonNull(expectedTimeToBeAvailable);
		Objects.requireNonNull(location);
		Objects.requireNonNull(availabilitySlots);
		if (availabilitySlots.size() <= 0) {
			throw new SchedulerException("No of availability slots cannot be zero.");
		}
		checkOverlap(availabilitySlots);

		this.sequence = 0;
		this.skillCodeLevelMap = Collections.unmodifiableMap(skillCodeLevelMap);
		// this.expectedTimeToBeAvailable = expectedTimeToBeAvailable;
		this.location = location;
		this.availabilitySlots = availabilitySlots;
	}

	private void checkOverlap(List<AvailabilitySlot> availabilitySlots) {
		Collections.sort(availabilitySlots, new Comparator<AvailabilitySlot>() {

			@Override
			public int compare(AvailabilitySlot o1, AvailabilitySlot o2) {
				return o1.getStartTime().compareTo(o2.getStartTime());
			}
		});
		for (int i = 1; i < availabilitySlots.size(); i++) {
			if (availabilitySlots.get(i - 1).getEndTime().compareTo(availabilitySlots.get(i).getStartTime()) > 0) {
				throw new SchedulerException(
						"Endtime of " + (i - 1) + " th slot cannot be higher than StartTime of " + i + " th slot.");
			}
		}
	}

	public Worker(int sequence, Worker worker) {
		this.sequence = sequence;
		this.skillCodeLevelMap = worker.skillCodeLevelMap;
		this.location = worker.location;
		this.availabilitySlots = Collections.unmodifiableList(worker.availabilitySlots);
	}

	public Worker(Worker worker, Location location, double distanceTravelled) {
		this.sequence = worker.sequence;
		this.skillCodeLevelMap = worker.skillCodeLevelMap;
		this.location = location;
		this.distanceTravelled = distanceTravelled;
		this.availabilitySlots = new ArrayList<>();
	}

	public Map<Integer, Integer> getSkillCodeLevelMap() {
		return skillCodeLevelMap;
	}

	public Location getLocation() {
		return location;
	}

	public final int getSequence() {
		return sequence;
	}

	public double getDistanceTravelled() {
		return distanceTravelled;
	}

	public List<AvailabilitySlot> getAvailabilitySlots() {
		return availabilitySlots;
	}

	public AvailabilitySlot findSlot(Calendar workerTravelStartTime, Calendar expectedJobCompletionTime) {
		for (AvailabilitySlot availabilitySlot : availabilitySlots) {
			if (availabilitySlot.getStartTime().compareTo(workerTravelStartTime) <= 0
					&& availabilitySlot.getEndTime().compareTo(expectedJobCompletionTime) > 0) {
				return availabilitySlot;
			}
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Worker other = (Worker) obj;
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
