package org.scheduler.onsite.request.vo;

import java.util.Arrays;

public class BingMapRequestVO {

	private LocationVO[] origins;

	private LocationVO[] destinations;

	private String travelMode = "driving";

	public LocationVO[] getOrigins() {
		return origins;
	}

	public void setOrigins(LocationVO[] origins) {
		this.origins = origins;
	}

	public LocationVO[] getDestinations() {
		return destinations;
	}

	public void setDestinations(LocationVO[] destinations) {
		this.destinations = destinations;
	}

	public String getTravelMode() {
		return travelMode;
	}

	public void setTravelMode(String travelMode) {
		this.travelMode = travelMode;
	}

	public BingMapRequestVO[][] split(int originDimension, int destinationDimension) {
		BingMapRequestVO[][] bingMapRequestVOs = new BingMapRequestVO[this.origins.length
				/ originDimension][this.destinations.length / destinationDimension];
		for (int i = 0; i < origins.length / originDimension; i++) {
			for (int j = 0; j < destinations.length / destinationDimension; j++) {
				BingMapRequestVO bingMapRequestVO = new BingMapRequestVO();
				bingMapRequestVO.setTravelMode(this.travelMode);
				// Set origins
				LocationVO[] origins = new LocationVO[originDimension];
				System.arraycopy(this.origins, i * originDimension, origins, 0, originDimension);
				bingMapRequestVO.setOrigins(origins);
				// Set destinations
				LocationVO[] destinations = new LocationVO[destinationDimension];
				System.arraycopy(this.destinations, j * destinationDimension, destinations, 0, destinationDimension);
				bingMapRequestVO.setDestinations(destinations);
				bingMapRequestVOs[i][j] = bingMapRequestVO;
			}
		}
		return bingMapRequestVOs;
	}

	@Override
	public String toString() {
		return "BingMapRequestVO [origins=" + Arrays.toString(origins) + ", destinations="
				+ Arrays.toString(destinations) + ", travelMode=" + travelMode + "]";
	}

}
