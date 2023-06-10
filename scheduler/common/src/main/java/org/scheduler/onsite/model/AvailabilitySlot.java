package org.scheduler.onsite.model;

import java.util.Calendar;

public class AvailabilitySlot {

	private final Calendar startTime;

	private final Calendar endTime;

	public AvailabilitySlot(Calendar startTime, Calendar endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public Calendar getStartTime() {
		return startTime;
	}

	public Calendar getEndTime() {
		return endTime;
	}

}
