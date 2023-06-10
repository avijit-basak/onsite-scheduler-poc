package org.scheduler.onsite.model;

import java.util.Objects;

public class Location {

	private final double latDegree;

	private final double longDegree;

	public Location(double latDegree, double longDegree) {
		this.latDegree = latDegree;
		this.longDegree = longDegree;
	}

	public double getLatDegree() {
		return latDegree;
	}

	public double getLongDegree() {
		return longDegree;
	}

	@Override
	public String toString() {
		return "Location [latDegree=" + latDegree + ", longDegree=" + longDegree + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(latDegree, longDegree);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		return Double.doubleToLongBits(latDegree) == Double.doubleToLongBits(other.latDegree)
				&& Double.doubleToLongBits(longDegree) == Double.doubleToLongBits(other.longDegree);
	}

}
