package org.scheduler.criteria.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.scheduler.onsite.distancematrix.vo.DistanceMatrixResponse;
import org.scheduler.onsite.distancematrix.vo.DistanceMatrixVO;
import org.scheduler.onsite.distancematrix.vo.DistanceMatrixVO.MergeType;
import org.scheduler.onsite.request.vo.AvailabilitySlotVO;
import org.scheduler.onsite.request.vo.BingMapRequestVO;
import org.scheduler.onsite.request.vo.JobVO;
import org.scheduler.onsite.request.vo.LocationVO;
import org.scheduler.onsite.request.vo.SchedulingCriteriaVO;
import org.scheduler.onsite.request.vo.WorkerVO;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RequestGenerator {

	private static final int DIMENSION = 500;

	public static void main(String[] args) throws ClientProtocolException, IOException, ParseException {
		System.setOut(new PrintStream(new File(
				"C:\\Personal\\Work\\opensource\\OnsiteScheduler\\workspace\\onsite-scheduler\\criteria-generator\\output.log")));
		RequestGenerator requestGenerator = new RequestGenerator();
		BingMapRequestVO bingMapRequestVO = requestGenerator.generateBingMapRequest(DIMENSION);
//		System.out.println(bingMapRequestVO);
		requestGenerator.generateScheduleCriteria(bingMapRequestVO, DIMENSION / 5, DIMENSION * 4 / 5);
		ObjectMapper mapper = new ObjectMapper();

		BingMapRequestVO[][] bingMapRequestVOMatrix = bingMapRequestVO.split(50, 50);
		DistanceMatrixVO[] distanceMatrixVOs = new DistanceMatrixVO[DIMENSION / 50];

		for (int i = 0; i < bingMapRequestVOMatrix.length; i++) {
			DistanceMatrixVO existingDistanceMatrixVO = null;
			for (int j = 0; j < bingMapRequestVOMatrix[i].length; j++) {
				DistanceMatrixResponse distanceMatrixResponse = requestGenerator
						.getDistanceMatrix(mapper.writeValueAsString(bingMapRequestVOMatrix[i][j]));
				DistanceMatrixVO distanceMatrixVO = distanceMatrixResponse.getResourceSets()[0].getResources()[0];
				if (j == 0) {
					existingDistanceMatrixVO = distanceMatrixVO;
				} else {
					existingDistanceMatrixVO = existingDistanceMatrixVO.merge(distanceMatrixVO, MergeType.DESTINATION);
				}
			}
			distanceMatrixVOs[i] = existingDistanceMatrixVO;
		}
		DistanceMatrixVO existingDistanceMatrixVO = null;
		for (int i = 0; i < distanceMatrixVOs.length; i++) {
			if (i == 0) {
				existingDistanceMatrixVO = distanceMatrixVOs[0];
			} else {
				existingDistanceMatrixVO = existingDistanceMatrixVO.merge(distanceMatrixVOs[i], MergeType.ORIGIN);
			}
		}
		mapper.writeValue(new File(System.getProperty("FILE_PATH") + "\\distance-matrix.json"),
				existingDistanceMatrixVO);
	}

	private DistanceMatrixResponse getDistanceMatrix(String bingMapRequest)
			throws ClientProtocolException, IOException {
		HttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost(
				"https://dev.virtualearth.net/REST/v1/Routes/DistanceMatrix?key=" + System.getProperty("KEY"));
		post.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		HttpEntity entity = new StringEntity(bingMapRequest);
		post.setEntity(entity);
		return httpClient.execute(HttpHost.create("dev.virtualearth.net"), post,
				new ResponseHandler<DistanceMatrixResponse>() {

					@Override
					public DistanceMatrixResponse handleResponse(HttpResponse response)
							throws ClientProtocolException, IOException {
						try (InputStream is = response.getEntity().getContent()) {
							ObjectMapper mapper = new ObjectMapper();
							return mapper.readValue(is, DistanceMatrixResponse.class);
						}
					}
				});
	}

	private void generateScheduleCriteria(BingMapRequestVO requestVO, int noOfWorkers, int noOfJobs)
			throws ParseException, StreamWriteException, DatabindException, IOException {
		SchedulingCriteriaVO schedulingCriteriaVO = new SchedulingCriteriaVO();
		int[] skillCodes = Constants.SKILL_CODES;
		Set<Integer> skillCodeSet = new HashSet<>();
		for (int skillCode : skillCodes) {
			skillCodeSet.add(skillCode);
		}

		LocationVO[] locations = requestVO.getOrigins();
		List<WorkerVO> workers = new ArrayList<WorkerVO>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (int i = 0; i < noOfWorkers; i++) {
			WorkerVO worker = new WorkerVO();
			worker.setSequence(i + 1);
			worker.setAvailabilitySlots(generateAvailabilitySlots(sdf));
			// worker.setExpectedTimeToBeAvailable(sdf.parse(Constants.availabilitySlotStartTime));
			worker.setLocation(locations[i]);
			worker.setSkillCodeLevelMap(generateWorkerSkillCodeLevelMap(skillCodes, skillCodeSet));
			workers.add(worker);
		}

		// Assign unused skill codes to workers.
		Iterator<Integer> itr = skillCodeSet.iterator();
		while (itr.hasNext()) {
			int skillCode = itr.next();
			int index = (int) (Math.random() * workers.size());
			int skillLevel = Constants.MIN_SKILL_LEVEL
					+ (int) (Math.random() * (Constants.MAX_SKILL_lEVEL - Constants.MIN_SKILL_LEVEL));
			workers.get(index).getSkillCodeLevelMap().put(skillCode, skillLevel);
			itr.remove();
		}
		schedulingCriteriaVO.setWorkers(workers);

		List<JobVO> jobs = new ArrayList<JobVO>();
		for (int i = 0; i < noOfJobs; i++) {
			JobVO jobVO = new JobVO();
			jobVO.setId(i + 1);
			jobVO.setLocation(locations[noOfWorkers + i]);
			jobVO.setPriority(Constants.MIN_JOB_PRIORITY
					+ (int) (Math.random() * (Constants.MAX_JOB_PRIORITY - Constants.MIN_JOB_PRIORITY)));
			jobVO.setExpectedCompletionDuration(Constants.MIN_JOB_DURATION
					+ (int) (Math.random() * (Constants.MAX_JOB_DURATION - Constants.MIN_JOB_DURATION)));
			Calendar slaTime = Calendar.getInstance();
			slaTime.setTime(sdf.parse(Constants.availabilitySlotEndTime));
			jobVO.setSlaTime(slaTime);
			List<Integer> jobSkillCodes = new ArrayList<>();
			jobSkillCodes.add(skillCodes[(int) (Math.random() * skillCodes.length)]);
			jobVO.setSkillCodes(jobSkillCodes);

			jobs.add(jobVO);
		}
		schedulingCriteriaVO.setJobs(jobs);

		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(new File(System.getProperty("FILE_PATH") + "ScheduleCriteria.json"), schedulingCriteriaVO);
	}

	private Map<Integer, Integer> generateWorkerSkillCodeLevelMap(int[] skillCodes, Set<Integer> skillCodeSet) {
		int noOfSkill = 1 + (int) (Math.random() * Constants.MAX_NO_OF_SKILLS_PER_WORKER);
		Map<Integer, Integer> skillCodeMap = new HashMap<>();
		for (int j = 0; j < noOfSkill; j++) {
			int skillCode = skillCodes[(int) (Math.random() * skillCodes.length)];
			int skillLevel = Constants.MIN_SKILL_LEVEL
					+ (int) (Math.random() * (Constants.MAX_SKILL_lEVEL - Constants.MIN_SKILL_LEVEL));
			skillCodeSet.remove(skillCode);
			skillCodeMap.put(skillCode, skillLevel);
		}
		return skillCodeMap;
	}

	private List<AvailabilitySlotVO> generateAvailabilitySlots(SimpleDateFormat sdf) throws ParseException {
		List<AvailabilitySlotVO> availabilitySlotVOs = new ArrayList<AvailabilitySlotVO>();
		AvailabilitySlotVO availabilitySlotVO = new AvailabilitySlotVO();
		Calendar startTime = Calendar.getInstance();
		startTime.setTime(sdf.parse(Constants.availabilitySlotStartTime));
		availabilitySlotVO.setStartTime(startTime);
		Calendar endTime = Calendar.getInstance();
		endTime.setTime(sdf.parse(Constants.availabilitySlotEndTime));
		availabilitySlotVO.setEndTime(endTime);
		availabilitySlotVOs.add(availabilitySlotVO);

		return availabilitySlotVOs;
	}

	private BingMapRequestVO generateBingMapRequest(int dimension) {
		BingMapRequestVO request = new BingMapRequestVO();
		LocationVO[] origins = generateLocations(dimension);
		LocationVO[] destinations = origins;
		request.setOrigins(origins);
		request.setDestinations(destinations);
		return request;
	}

//	private BingMapRequestVO generateBingMapRequest() {
//		ObjectMapper objectMapper = new ObjectMapper();
//		BingMapRequestVO request = new BingMapRequestVO();
//		LocationVO[] origins = new LocationVO[Constants.NO_OF_JOBS + Constants.NO_OF_WORKERS];
//		LocationVO[] destinations = new LocationVO[Constants.NO_OF_JOBS + Constants.NO_OF_WORKERS];
//		LocationVO[] workerLocations = generateWorkerLocations();
//		LocationVO[] jobLocations = generateJobLocations();
//		for (int i = 0; i < workerLocations.length; i++) {
//			origins[i] = workerLocations[i];
//			destinations[i] = workerLocations[i];
//		}
//		for (int i = 0; i < jobLocations.length; i++) {
//			origins[i + Constants.NO_OF_WORKERS] = jobLocations[i];
//			destinations[i + Constants.NO_OF_WORKERS] = jobLocations[i];
//		}
//
//		request.setOrigins(origins);
//		request.setDestinations(destinations);
//
//		try {
//			objectMapper.writeValue(new File(
//					"C:\\Personal\\Work\\opensource\\OnsiteScheduler\\workspace\\onsite-scheduler\\criteria-generator\\input.json"),
//					request);
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return request;
//	}

	private static LocationVO[] generateLocations(int dimension) {
		LocationVO[] location = new LocationVO[dimension];
		double latDiff = Constants.END_LATITUDE - Constants.START_LATITUDE;
		double longDiff = Constants.END_LONGITUDE - Constants.START_LONGITUDE;
		for (int i = 0; i < dimension; i++) {
			double latitude = ((long) ((Math.random() * latDiff + Constants.START_LATITUDE) * 100000)) / 100000d;
			double longitude = ((long) ((Math.random() * longDiff + Constants.START_LONGITUDE) * 100000)) / 100000d;
			location[i] = new LocationVO(latitude, longitude);
		}

		return location;
	}

//	private static LocationVO[] generateWorkerLocations() {
//		LocationVO[] workerOrigins = new LocationVO[Constants.NO_OF_WORKERS];
//		double latDiff = Constants.END_LATITUDE - Constants.START_LATITUDE;
//		double longDiff = Constants.END_LONGITUDE - Constants.START_LONGITUDE;
//		for (int i = 0; i < Constants.NO_OF_WORKERS; i++) {
//			double latitude = ((long) ((Math.random() * latDiff + Constants.START_LATITUDE) * 100000)) / 100000d;
//			double longitude = ((long) ((Math.random() * longDiff + Constants.START_LONGITUDE) * 100000)) / 100000d;
//			workerOrigins[i] = new LocationVO(latitude, longitude);
//		}
//
//		return workerOrigins;
//	}

//	private static LocationVO[] generateJobLocations() {
//		LocationVO[] jobLocations = new LocationVO[Constants.NO_OF_JOBS];
//		double latDiff = Constants.END_LATITUDE - Constants.START_LATITUDE;
//		double longDiff = Constants.END_LONGITUDE - Constants.START_LONGITUDE;
//		for (int i = 0; i < Constants.NO_OF_JOBS; i++) {
//			double latitude = ((long) ((Math.random() * latDiff + Constants.START_LATITUDE) * 100000)) / 100000d;
//			double longitude = ((long) ((Math.random() * longDiff + Constants.START_LONGITUDE) * 100000)) / 100000d;
//			jobLocations[i] = new LocationVO(latitude, longitude);
//		}
//
//		return jobLocations;
//	}

}
