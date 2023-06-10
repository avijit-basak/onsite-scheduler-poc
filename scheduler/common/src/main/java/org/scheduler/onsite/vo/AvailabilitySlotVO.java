package org.scheduler.onsite.vo;

import java.util.Calendar;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class AvailabilitySlotVO {

	@JsonSerialize(using = CustomCalendarSerializer.class)
	@JsonDeserialize(using = CustomCalendarDeserializer.class)
	private Calendar startTime;

	@JsonSerialize(using = CustomCalendarSerializer.class)
	@JsonDeserialize(using = CustomCalendarDeserializer.class)
	private Calendar endTime;

	public AvailabilitySlotVO() {
	}

	public Calendar getStartTime() {
		return startTime;
	}

	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}

	public Calendar getEndTime() {
		return endTime;
	}

	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
	}

}
