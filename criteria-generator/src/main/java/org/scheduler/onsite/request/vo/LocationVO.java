package org.scheduler.onsite.request.vo;

public class LocationVO {

	private double latitude;

	private double longitude;

	public LocationVO() {
		// TODO Auto-generated constructor stub
	}

	public LocationVO(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
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
