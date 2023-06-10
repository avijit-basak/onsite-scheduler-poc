package org.scheduler.onsite.distancematrix.vo;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
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

	public DistanceMatrixVO merge(DistanceMatrixVO inputDistanceMatrixVO, MergeType mergeType) {
		DistanceMatrixVO mergedDistanceMatrixVO = new DistanceMatrixVO();

		if (mergeType == MergeType.DESTINATION) {
			LocationVO[] mergedOrigins = new LocationVO[this.origins.length];
			System.arraycopy(origins, 0, mergedOrigins, 0, origins.length);
			mergedDistanceMatrixVO.setOrigins(mergedOrigins);

			LocationVO[] mergedDestinations = new LocationVO[this.destinations.length
					+ inputDistanceMatrixVO.getDestinations().length];
			System.arraycopy(this.destinations, 0, mergedDestinations, 0, destinations.length);
			System.arraycopy(inputDistanceMatrixVO.getDestinations(), 0, mergedDestinations, destinations.length,
					inputDistanceMatrixVO.getDestinations().length);
			mergedDistanceMatrixVO.setDestinations(mergedDestinations);

			ResultVO[] mergedResults = new ResultVO[this.results.length + inputDistanceMatrixVO.getResults().length];
			System.arraycopy(this.results, 0, mergedResults, 0, this.results.length);
			for (int i = 0; i < inputDistanceMatrixVO.getResults().length; i++) {
				ResultVO inputResultVO = inputDistanceMatrixVO.getResults()[i];
				inputResultVO.destinationIndex = this.destinations.length + inputResultVO.destinationIndex;
				mergedResults[this.results.length + i] = inputResultVO;
			}
			mergedDistanceMatrixVO.setResults(mergedResults);
		} else if (mergeType == MergeType.ORIGIN) {
			LocationVO[] mergedOrigins = new LocationVO[this.origins.length
					+ inputDistanceMatrixVO.getOrigins().length];
			System.arraycopy(origins, 0, mergedOrigins, 0, origins.length);
			System.arraycopy(inputDistanceMatrixVO.getOrigins(), 0, mergedOrigins, origins.length,
					inputDistanceMatrixVO.getOrigins().length);
			mergedDistanceMatrixVO.setOrigins(mergedOrigins);

			LocationVO[] mergedDestinations = new LocationVO[this.destinations.length];
			System.arraycopy(this.destinations, 0, mergedDestinations, 0, destinations.length);
			mergedDistanceMatrixVO.setDestinations(mergedDestinations);

			ResultVO[] mergedResults = new ResultVO[this.results.length + inputDistanceMatrixVO.getResults().length];
			System.arraycopy(this.results, 0, mergedResults, 0, this.results.length);
			for (int i = 0; i < inputDistanceMatrixVO.getResults().length; i++) {
				ResultVO inputResultVO = inputDistanceMatrixVO.getResults()[i];
				inputResultVO.originIndex = this.origins.length + inputResultVO.originIndex;
				mergedResults[this.results.length + i] = inputResultVO;
			}
			mergedDistanceMatrixVO.setResults(mergedResults);
		}

		return mergedDistanceMatrixVO;
	}

	public enum MergeType {
		DESTINATION, ORIGIN
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ResultVO implements Cloneable {

		private int originIndex;

		private int destinationIndex;

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

		@Override
		protected Object clone() throws CloneNotSupportedException {
			return super.clone();
		}

		@Override
		public String toString() {
			return "ResultVO [originIndex=" + originIndex + ", destinationIndex=" + destinationIndex
					+ ", totalWalkDuration=" + totalWalkDuration + ", travelDistance=" + travelDistance
					+ ", travelDuration=" + travelDuration + "]";
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
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

	@Override
	public String toString() {
		return "DistanceMatrixVO [destinations=" + Arrays.toString(destinations) + ", origins="
				+ Arrays.toString(origins) + ", results=" + Arrays.toString(results) + "]";
	}

}
