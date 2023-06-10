package org.scheduler.onsite.vo;

public class LocationVO {

	private double latitude;

	private double longitude;

	public LocationVO() {
		this.latitude = 0;
		this.longitude = 0;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return "LocationVO [latitude=" + latitude + ", longitude=" + longitude + "]";
	}

}
