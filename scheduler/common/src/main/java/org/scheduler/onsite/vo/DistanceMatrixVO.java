package org.scheduler.onsite.vo;

public class DistanceMatrixVO {

	private LocationVO[] destinations;

	private LocationVO[] origins;

	private ResultVO[] results;

	public LocationVO[] getDestinations() {
		return destinations;
	}

	public void setDestinations(LocationVO[] destinations) {
		this.destinations = destinations;
	}

	public LocationVO[] getOrigins() {
		return origins;
	}

	public void setOrigins(LocationVO[] origins) {
		this.origins = origins;
	}

	public ResultVO[] getResults() {
		return results;
	}

	public void setResults(ResultVO[] results) {
		this.results = results;
	}

	public static class ResultVO {

		private int destinationIndex;

		private int originIndex;

		private int totalWalkDuration;

		private double travelDistance;

		private double travelDuration;

		public int getDestinationIndex() {
			return destinationIndex;
		}

		public void setDestinationIndex(int destinationIndex) {
			this.destinationIndex = destinationIndex;
		}

		public int getOriginIndex() {
			return originIndex;
		}

		public void setOriginIndex(int originIndex) {
			this.originIndex = originIndex;
		}

		public int getTotalWalkDuration() {
			return totalWalkDuration;
		}

		public void setTotalWalkDuration(int totalWalkDuration) {
			this.totalWalkDuration = totalWalkDuration;
		}

		public double getTravelDistance() {
			return travelDistance;
		}

		public void setTravelDistance(double travelDistance) {
			this.travelDistance = travelDistance;
		}

		public double getTravelDuration() {
			return travelDuration;
		}

		public void setTravelDuration(double travelDuration) {
			this.travelDuration = travelDuration;
		}

	}

	public static class LocationVO {

		private double latitude;

		private double longitude;

		public LocationVO() {
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

}
