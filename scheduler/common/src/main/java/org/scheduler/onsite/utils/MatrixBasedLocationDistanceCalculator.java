package org.scheduler.onsite.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.scheduler.onsite.model.Location;
import org.scheduler.onsite.model.LocationDistanceCalculator;
import org.scheduler.onsite.vo.DistanceMatrixVO;
import org.scheduler.onsite.vo.DistanceMatrixVO.LocationVO;
import org.scheduler.onsite.vo.DistanceMatrixVO.ResultVO;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MatrixBasedLocationDistanceCalculator implements LocationDistanceCalculator {

	private Map<Location, Map<Location, DistanceDurationWrapper>> distanceMatrix = new HashMap<>();

	private MatrixBasedLocationDistanceCalculator() {
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("distance-matrix.json");) {
			byte[] data = new byte[1024];
			int len = -1;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			while ((len = is.read(data)) > 0) {
				bos.write(data, 0, len);
			}
			bos.flush();
			ObjectMapper mapper = new ObjectMapper();
			DistanceMatrixVO matrix = mapper.readValue(new String(bos.toByteArray()), DistanceMatrixVO.class);
			LocationVO[] origins = matrix.getOrigins();
			LocationVO[] destinations = matrix.getDestinations();
			ResultVO[] results = matrix.getResults();
			for (int i = 0; i < results.length; i++) {
				ResultVO result = results[i];
				Location origin = new Location(origins[result.getOriginIndex()].getLatitude(),
						origins[result.getOriginIndex()].getLongitude());
				Location destination = new Location(destinations[result.getDestinationIndex()].getLatitude(),
						destinations[result.getDestinationIndex()].getLongitude());
				Map<Location, DistanceDurationWrapper> destinationDistanceDurationMap = null;
				if (!distanceMatrix.containsKey(origin)) {
					destinationDistanceDurationMap = new HashMap<>();
					distanceMatrix.put(origin, destinationDistanceDurationMap);
				} else {
					destinationDistanceDurationMap = distanceMatrix.get(origin);
				}
				destinationDistanceDurationMap.put(destination,
						new DistanceDurationWrapper(result.getTravelDistance(), result.getTravelDuration()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static MatrixBasedLocationDistanceCalculator getInstance() {
		return new MatrixBasedLocationDistanceCalculator();
	}

	@Override
	public double calculateDistance(Location from, Location to) {
		if (!distanceMatrix.containsKey(from)) {
			throw new IllegalArgumentException("Location " + from.toString() + " not found");
		}
		Map<Location, DistanceDurationWrapper> distanceDurationMap = distanceMatrix.get(from);
		if (!distanceDurationMap.containsKey(to)) {
			throw new IllegalArgumentException("Location " + to.toString() + " not found");
		}
		return distanceDurationMap.get(to).distance;
	}

	@Override
	public double calculateTravelTime(Location from, Location to) {
		return distanceMatrix.get(from).get(to).duration;
	}

	@Override
	public int calculateTravelTime(double travelDistance) {
		throw new RuntimeException("Operation not supported");
	}

	private class DistanceDurationWrapper {

		private DistanceDurationWrapper(double distance, double duration) {
			this.distance = distance;
			this.duration = duration;
		}

		private double distance;

		private double duration;
	}

}
