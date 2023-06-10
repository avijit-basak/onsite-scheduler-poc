package org.scheduler.onsite.utils;

import org.scheduler.onsite.model.Location;
import org.scheduler.onsite.model.LocationDistanceCalculator;

public class DefaultLocationDistanceCalculator implements LocationDistanceCalculator {

	private final double DEGREE_TO_METER_CONVERTER = 111139;

	private final double travelTimePerMeter;

	public DefaultLocationDistanceCalculator() {
		this.travelTimePerMeter = .002;
	}

	public DefaultLocationDistanceCalculator(double travelTimePerMeter) {
		this.travelTimePerMeter = travelTimePerMeter;
	}

	@Override
	public double calculateDistance(Location from, Location to) {
		return DEGREE_TO_METER_CONVERTER * Math.pow(Math.pow(to.getLatDegree() - from.getLatDegree(), 2)
				+ Math.pow(to.getLongDegree() - from.getLongDegree(), 2), .5);
	}

	@Override
	public double calculateTravelTime(Location from, Location to) {
		return (int) (travelTimePerMeter * calculateDistance(from, to));
	}

	@Override
	public int calculateTravelTime(double travelDistance) {
		return (int) (travelTimePerMeter * travelDistance);
	}

}
