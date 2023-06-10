package org.scheduler.onsite.scheduleallocator;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scheduler.onsite.model.AvailabilitySlot;
import org.scheduler.onsite.model.Job;
import org.scheduler.onsite.model.Jobs;
import org.scheduler.onsite.model.Location;
import org.scheduler.onsite.model.LocationDistanceCalculator;
import org.scheduler.onsite.model.Worker;
import org.scheduler.onsite.utils.Constants;
import org.scheduler.onsite.utils.MatrixBasedLocationDistanceCalculator;

public class ScheduleFitnessFunction implements FitnessFunction {

	private final Jobs jobs;

	private final LocationDistanceCalculator locationDistanceCalculator;

	private final double maxPossibleDistance;

	private final double defaultMaxPossibleDistance = 100; // In KM

	private final double MAX_SKILL_LEVEL;

	private final double DEFAULT_MAX_SLA;

	private final double AVG_JOB_PRIORITY = 5;

	private final double maxOvertime;

	private final double overtimeWeight;

	private final double travelWeight;

	private final double slaWeight;

	private final double bufferDueToSkillLevel;

	public ScheduleFitnessFunction(Jobs jobs, LocationDistanceCalculator locationDistanceCalculator,
			double maxPossibleDistance, double travelWeight, double slaWeight, double bufferDueToSkillLevel,
			double maxSkillLevel, double maxSla, double overtimeWeight, double maxOvertimeDuration) {
		this.jobs = jobs;
		this.locationDistanceCalculator = locationDistanceCalculator;
		this.maxPossibleDistance = maxPossibleDistance;
		this.travelWeight = travelWeight;
		this.slaWeight = slaWeight;
		this.bufferDueToSkillLevel = bufferDueToSkillLevel;
		this.MAX_SKILL_LEVEL = maxSkillLevel;
		this.DEFAULT_MAX_SLA = maxSla;
		this.overtimeWeight = overtimeWeight;
		this.maxOvertime = maxOvertimeDuration;
	}

	public ScheduleFitnessFunction(Jobs jobs, double travelWeight, double slaWeight, double bufferDueToSkillLevel,
			double maxSkillLevel, double maxSla, double overtimeWeight, double maxOvertimeDuration) {
		try {
			this.jobs = jobs;
			this.maxPossibleDistance = defaultMaxPossibleDistance;
			this.travelWeight = travelWeight;
			this.slaWeight = slaWeight;
			this.MAX_SKILL_LEVEL = maxSkillLevel;
			this.DEFAULT_MAX_SLA = maxSla;
			this.overtimeWeight = overtimeWeight;
			this.maxOvertime = maxOvertimeDuration;
			String distanceCalculatorClassName = System.getProperty(Constants.DISTANCE_CALCULATOR_VM_ARG);
			if (distanceCalculatorClassName != null) {
				Class<?> distanceCalculatorClass = Class.forName(distanceCalculatorClassName);
				if (!LocationDistanceCalculator.class.isAssignableFrom(distanceCalculatorClass)) {
					throw new RuntimeException(
							"Provided class does not implement " + LocationDistanceCalculator.class.getCanonicalName());
				}
				this.locationDistanceCalculator = (LocationDistanceCalculator) distanceCalculatorClass
						.getDeclaredConstructor().newInstance();
			} else {
				this.locationDistanceCalculator = MatrixBasedLocationDistanceCalculator.getInstance();
			}
			this.bufferDueToSkillLevel = bufferDueToSkillLevel;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public double evaluate(ScheduleChromosome scheduleChromosome) {
		return evaluate(scheduleChromosome, false);
	}

	public double evaluateTravelCost(ScheduleChromosome scheduleChromosome, boolean enablePrint) {

		List<Worker> workers = scheduleChromosome.getWorkers();
		List<Integer> jobExecutionSequence = scheduleChromosome.decode(getJobIds(workers.size()));

		Calendar workerAvailabilityTime = null;
		Location currentLocation = null;

		double jobExecutionCost = 0;
		Map<Worker, Calendar> workerAvailabilityMap = new HashMap<>();
		Map<Worker, Location> workerLocationMap = new HashMap<>();

		for (Integer jobId : jobExecutionSequence) {
			final Job job = jobs.get(jobId);
			final Worker worker = workers.get(jobId);

			if (workerLocationMap.containsKey(worker)) {
				currentLocation = workerLocationMap.get(worker);
			} else {
				currentLocation = worker.getLocation();
				workerLocationMap.put(worker, currentLocation);
			}

			final double travelDistance = locationDistanceCalculator.calculateDistance(workerLocationMap.get(worker),
					job.getLocation());

			// first param for cost computation 'normalized travel distance'.
//			final double normalizedTravelDistance = travelDistance / maxPossibleDistance;

			final int travelTime = (int) locationDistanceCalculator.calculateTravelTime(workerLocationMap.get(worker),
					job.getLocation());

			if (workerAvailabilityMap.containsKey(worker)) {
				workerAvailabilityTime = workerAvailabilityMap.get(worker);
			} else {
				workerAvailabilityTime = Calendar.getInstance();
				workerAvailabilityTime
						.setTime(new Date(worker.getAvailabilitySlots().get(0).getStartTime().getTimeInMillis()));
			}

			final double skillLevelFactor = 1
					+ bufferDueToSkillLevel * (1 - getAverageSkillLevel(job, worker) / MAX_SKILL_LEVEL);
			int expectedCompletionDuration = (int) (skillLevelFactor * job.getExpectedCompletionDuration());

			Calendar expectedJobCompletionTime = Calendar.getInstance();
			expectedJobCompletionTime.setTimeInMillis(workerAvailabilityTime.getTimeInMillis());
			expectedJobCompletionTime.add(Calendar.MINUTE, travelTime);
			expectedJobCompletionTime.add(Calendar.MINUTE, expectedCompletionDuration);

			// If no slots are available for the worker return -Infinity to reject the
			// schedule.
			if (worker.findSlot(workerAvailabilityTime, expectedJobCompletionTime) == null) {
				return -Double.MAX_VALUE;
			} else {
				workerAvailabilityMap.put(worker, expectedJobCompletionTime);
				workerLocationMap.put(worker, job.getLocation());

				// calculate job execution cost.
				jobExecutionCost += travelDistance;
				if (enablePrint) {
					System.out.println("normalizedTravelDistance : " + travelDistance + ", jobExecutionCost : "
							+ jobExecutionCost);
				}
			}
		}
		for (Worker worker : workerLocationMap.keySet()) {
			jobExecutionCost += locationDistanceCalculator.calculateDistance(worker.getLocation(),
					workerLocationMap.get(worker));
			if (enablePrint) {
				System.out.println("jobExecutionCost : " + jobExecutionCost);
			}
		}

		return -jobExecutionCost;

	}

	public double evaluateDelayCost(ScheduleChromosome scheduleChromosome, boolean enablePrint) {

		List<Worker> workers = scheduleChromosome.getWorkers();
		List<Integer> jobExecutionSequence = scheduleChromosome.decode(getJobIds(workers.size()));

		Calendar workerAvailabilityTime = null;
		Location currentLocation = null;

		double jobExecutionCost = 0;
		Map<Worker, Calendar> workerAvailabilityMap = new HashMap<>();
		Map<Worker, Location> workerLocationMap = new HashMap<>();

		for (Integer jobId : jobExecutionSequence) {

			final Job job = jobs.get(jobId);
			final Worker worker = workers.get(jobId);

			if (workerLocationMap.containsKey(worker)) {
				currentLocation = workerLocationMap.get(worker);
			} else {
				currentLocation = worker.getLocation();
				workerLocationMap.put(worker, currentLocation);
			}

			final int travelTime = (int) locationDistanceCalculator.calculateTravelTime(workerLocationMap.get(worker),
					job.getLocation());

			if (workerAvailabilityMap.containsKey(worker)) {
				workerAvailabilityTime = workerAvailabilityMap.get(worker);
			} else {
				workerAvailabilityTime = Calendar.getInstance();
				workerAvailabilityTime
						.setTime(new Date(worker.getAvailabilitySlots().get(0).getStartTime().getTimeInMillis()));
			}

			final double skillLevelFactor = 1
					+ bufferDueToSkillLevel * (1 - getAverageSkillLevel(job, worker) / MAX_SKILL_LEVEL);
			int expectedCompletionDuration = (int) (skillLevelFactor * job.getExpectedCompletionDuration());

			Calendar expectedJobCompletionTime = Calendar.getInstance();
			expectedJobCompletionTime.setTimeInMillis(workerAvailabilityTime.getTimeInMillis());
			expectedJobCompletionTime.add(Calendar.MINUTE, travelTime);
			expectedJobCompletionTime.add(Calendar.MINUTE, expectedCompletionDuration);

			// If no slots are available for the worker return -Infinity to reject the
			// schedule.
			if (worker.findSlot(workerAvailabilityTime, expectedJobCompletionTime) == null) {
				return -Double.MAX_VALUE;
			} else {
				workerAvailabilityMap.put(worker, expectedJobCompletionTime);
				workerLocationMap.put(worker, job.getLocation());

				// second param for cost computation 'delay in job completion'.
				int delayInCompletion = (int) ((expectedJobCompletionTime.getTimeInMillis()
						- job.getSlaTime().getTime()) / 60000L);

				if (delayInCompletion > 0) {
					return -Double.MAX_VALUE;
				}
				// calculate job execution delay cost.
				jobExecutionCost += job.getPriority() / AVG_JOB_PRIORITY
						* Math.exp(delayInCompletion / DEFAULT_MAX_SLA);
				if (enablePrint) {
					System.out.println(
							"delayInCompletion : " + delayInCompletion + ", jobExecutionCost : " + jobExecutionCost);
				}
			}
		}

		return -jobExecutionCost;

	}

	public double evaluate(ScheduleChromosome scheduleChromosome, boolean enablePrint) {
		List<Worker> workers = scheduleChromosome.getWorkers();
		List<Integer> jobExecutionSequence = scheduleChromosome.decode(getJobIds(workers.size()));

		Calendar workerAvailabilityTime = null;
		Location currentLocation = null;

		double jobExecutionCost = 0;
		Map<Worker, Calendar> workerAvailabilityMap = new HashMap<>();
		Map<Worker, Location> workerLocationMap = new HashMap<>();

		for (Integer jobId : jobExecutionSequence) {
			final Job job = jobs.get(jobId);
			final Worker worker = workers.get(jobId);

			if (workerLocationMap.containsKey(worker)) {
				currentLocation = workerLocationMap.get(worker);
			} else {
				currentLocation = worker.getLocation();
				workerLocationMap.put(worker, currentLocation);
			}
			final double travelDistance = locationDistanceCalculator.calculateDistance(workerLocationMap.get(worker),
					job.getLocation());
			// first param for cost computation 'normalized travel distance'.
			final double normalizedTravelDistance = travelDistance / maxPossibleDistance;

			final int travelTime = (int) locationDistanceCalculator.calculateTravelTime(workerLocationMap.get(worker),
					job.getLocation());

			if (workerAvailabilityMap.containsKey(worker)) {
				workerAvailabilityTime = workerAvailabilityMap.get(worker);
			} else {
				workerAvailabilityTime = Calendar.getInstance();
				workerAvailabilityTime
						.setTime(new Date(worker.getAvailabilitySlots().get(0).getStartTime().getTimeInMillis()));
			}

			/**
			 * Skill level factor introduces a multiplicative factor based on worker's skill
			 * level. Lower skill level would add additional time for job completion.
			 */
			final double skillLevelFactor = 1
					+ bufferDueToSkillLevel * (1 - getAverageSkillLevel(job, worker) / MAX_SKILL_LEVEL);
			int expectedCompletionDuration = (int) (skillLevelFactor * job.getExpectedCompletionDuration());

			Calendar expectedJobCompletionTime = Calendar.getInstance();
			expectedJobCompletionTime.setTimeInMillis(workerAvailabilityTime.getTimeInMillis());
			expectedJobCompletionTime.add(Calendar.MINUTE, travelTime);
			expectedJobCompletionTime.add(Calendar.MINUTE, expectedCompletionDuration);

			// If no slots are available for the worker return -Infinity to reject the
			// schedule.
//			if (worker.findSlot(workerAvailabilityTime, expectedJobCompletionTime) == null) {
//				return -Double.MAX_VALUE;
//			} else {
			workerAvailabilityMap.put(worker, expectedJobCompletionTime);
			workerLocationMap.put(worker, job.getLocation());

			// second param for cost computation 'delay in job completion'.
			int delayInCompletion = (int) ((expectedJobCompletionTime.getTimeInMillis() - job.getSlaTime().getTime())
					/ 60000L);

			if (delayInCompletion > 0) {
				return -Double.MAX_VALUE;
			}
			// calculate job execution cost.
			jobExecutionCost += travelWeight * normalizedTravelDistance
					+ slaWeight * job.getPriority() / AVG_JOB_PRIORITY * Math.exp(delayInCompletion / DEFAULT_MAX_SLA);

//				System.out.println("travelWeight * normalizedTravelDistance : "
//						+ travelWeight * normalizedTravelDistance + ", delayInCompletion : " + delayInCompletion
//						+ ", slaWeight * job.getPriority() * Math.exp(delayInCompletion) / defaultMaxSLA: "
//						+ slaWeight * job.getPriority() * Math.exp(delayInCompletion / DEFAULT_MAX_SLA)
//						+ ", jobExecutionCost : " + jobExecutionCost);

			if (enablePrint) {
				System.out.println("normalizedTravelDistance : " + normalizedTravelDistance + ", delayInCompletion : "
						+ delayInCompletion + ", jobExecutionCost : " + jobExecutionCost);
			}
//			}
		}
		for (Worker worker : workerLocationMap.keySet()) {
			double distance = locationDistanceCalculator.calculateDistance(worker.getLocation(),
					workerLocationMap.get(worker));
			jobExecutionCost += travelWeight * distance / maxPossibleDistance;
			// Add home return time
			workerAvailabilityMap.get(worker).add(Calendar.MINUTE, (int) locationDistanceCalculator
					.calculateTravelTime(worker.getLocation(), workerLocationMap.get(worker)));
			if (enablePrint) {
				System.out.println("jobExecutionCost : " + jobExecutionCost);
			}
		}

		long overtimeInMinutes = 0;
		List<AvailabilitySlot> availabilitySlots = null;
		for (Worker worker : workerAvailabilityMap.keySet()) {
			availabilitySlots = worker.getAvailabilitySlots();
			Calendar slotEndTime = availabilitySlots.get(availabilitySlots.size() - 1).getEndTime();
			Calendar expectedJobCompletionTime = workerAvailabilityMap.get(worker);
			overtimeInMinutes += expectedJobCompletionTime.after(slotEndTime)
					? (expectedJobCompletionTime.getTimeInMillis() - slotEndTime.getTimeInMillis()) / 1000 / 60
					: 0;
		}

		if (overtimeInMinutes > maxOvertime) {
			return -Double.MAX_VALUE;
		}

		jobExecutionCost += overtimeWeight * overtimeInMinutes / maxOvertime;
//		System.out.println("completed fitness computation");

		return -jobExecutionCost;
	}

	public double evaluateOvertimeCost(ScheduleChromosome scheduleChromosome, boolean enablePrint) {

		List<Worker> workers = scheduleChromosome.getWorkers();
		List<Integer> jobExecutionSequence = scheduleChromosome.decode(getJobIds(workers.size()));

		Calendar workerAvailabilityTime = null;
		Location currentLocation = null;

		double jobExecutionCost = 0;
		Map<Worker, Calendar> workerAvailabilityMap = new HashMap<>();
		Map<Worker, Location> workerLocationMap = new HashMap<>();

		for (Integer jobId : jobExecutionSequence) {

			final Job job = jobs.get(jobId);
			final Worker worker = workers.get(jobId);

			if (workerLocationMap.containsKey(worker)) {
				currentLocation = workerLocationMap.get(worker);
			} else {
				currentLocation = worker.getLocation();
				workerLocationMap.put(worker, currentLocation);
			}

			final int travelTime = (int) locationDistanceCalculator.calculateTravelTime(workerLocationMap.get(worker),
					job.getLocation());

			if (workerAvailabilityMap.containsKey(worker)) {
				workerAvailabilityTime = workerAvailabilityMap.get(worker);
			} else {
				workerAvailabilityTime = Calendar.getInstance();
				workerAvailabilityTime
						.setTime(new Date(worker.getAvailabilitySlots().get(0).getStartTime().getTimeInMillis()));
			}

			final double skillLevelFactor = 1
					+ bufferDueToSkillLevel * (1 - getAverageSkillLevel(job, worker) / MAX_SKILL_LEVEL);
			int expectedCompletionDuration = (int) (skillLevelFactor * job.getExpectedCompletionDuration());

			Calendar expectedJobCompletionTime = Calendar.getInstance();
			expectedJobCompletionTime.setTimeInMillis(workerAvailabilityTime.getTimeInMillis());
			expectedJobCompletionTime.add(Calendar.MINUTE, travelTime);
			expectedJobCompletionTime.add(Calendar.MINUTE, expectedCompletionDuration);

			workerAvailabilityMap.put(worker, expectedJobCompletionTime);
			workerLocationMap.put(worker, job.getLocation());
		}

		for (Worker worker : workerLocationMap.keySet()) {
			// Add home return time
			workerAvailabilityMap.get(worker).add(Calendar.MINUTE, (int) locationDistanceCalculator
					.calculateTravelTime(worker.getLocation(), workerLocationMap.get(worker)));
		}

		long overtimeInMinutes = 0;
		List<AvailabilitySlot> availabilitySlots = null;
		for (Worker worker : workerAvailabilityMap.keySet()) {
			availabilitySlots = worker.getAvailabilitySlots();
			Calendar slotEndTime = availabilitySlots.get(availabilitySlots.size() - 1).getEndTime();
			Calendar expectedJobCompletionTime = workerAvailabilityMap.get(worker);
			overtimeInMinutes += expectedJobCompletionTime.after(slotEndTime)
					? (expectedJobCompletionTime.getTimeInMillis() - slotEndTime.getTimeInMillis()) / 1000 / 60
					: 0;
		}

		return -overtimeInMinutes;

	}

	private List<Integer> getJobIds(int length) {
		List<Integer> jobIds = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			jobIds.add(i);
		}
		return jobIds;
	}

	private int getAverageSkillLevel(Job job, Worker worker) {
		List<Integer> skillCodes = job.getSkillCodes();
		int totalSkillLevel = 0;
		for (Integer skillCode : skillCodes) {
			totalSkillLevel += worker.getSkillCodeLevelMap().get(skillCode);
		}
		return totalSkillLevel / skillCodes.size();
	}

}
