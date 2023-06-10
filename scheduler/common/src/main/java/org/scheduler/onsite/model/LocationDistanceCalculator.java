package org.scheduler.onsite.model;

public interface LocationDistanceCalculator {

	/**
	 * Calculates distance between from and to in meters.
	 * 
	 * @param from
	 * @param to
	 * @return distance
	 */
	public double calculateDistance(Location from, Location to);

	/**
	 * Calculates travel time between from and to in minutes.
	 * 
	 * @param from
	 * @param to
	 * @return travel time in minutes
	 */
	public double calculateTravelTime(Location from, Location to);

	/**
	 * Calculates travel time in minutes for specified distance.
	 * 
	 * @param travelDistance
	 * @return
	 */
	public int calculateTravelTime(double travelDistance);
}
