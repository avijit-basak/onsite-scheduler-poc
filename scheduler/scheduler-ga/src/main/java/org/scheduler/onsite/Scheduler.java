package org.scheduler.onsite;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.scheduler.onsite.model.AvailabilitySlot;
import org.scheduler.onsite.model.Job;
import org.scheduler.onsite.model.Location;
import org.scheduler.onsite.model.LocationDistanceCalculator;
import org.scheduler.onsite.model.Schedule;
import org.scheduler.onsite.model.SchedulingCriteria;
import org.scheduler.onsite.model.Worker;
import org.scheduler.onsite.scheduleallocator.GAScheduleOptimizer;
import org.scheduler.onsite.scheduleallocator.adaptive.AdaptiveGAScheduleOptimizer;
import org.scheduler.onsite.utils.Constants;
import org.scheduler.onsite.utils.GraphPlotter;
import org.scheduler.onsite.utils.MatrixBasedLocationDistanceCalculator;
import org.scheduler.onsite.vo.AvailabilitySlotVO;
import org.scheduler.onsite.vo.JobVO;
import org.scheduler.onsite.vo.SchedulingCriteriaVO;
import org.scheduler.onsite.vo.WorkerVO;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Scheduler {

	public static final int POPULATION_SIZE = 500;

	public static final double ELITISM_RATE = .1;

	public static final double TRAVEL_WEIGHT = .5;

	public static final double SLA_WEIGHT = .3;

	public static final double OVERTIME_WEIGHT = .2;

	public static final double MIN_CROSSOVER_PROBABILITY = .8;

	public static final double MAX_CROSSOVER_PROBABILITY = 1.0;

	public static final double BUFFER_DUE_TO_SKILL_LEVEL = .2;

	public static final double MAX_SKILL_LEVEL = 10;

	public static final double MAX_SLA = 1440;// In minutes

	public static final double MAX_OVERTIME = 120;// In minutes

	public static int maxGenerationsWithUnchangedBestFitness = 50;

	public static void main(String args[]) throws Exception {
		Scheduler scheduler = new Scheduler();
		ObjectMapper mapper = new ObjectMapper();
		SchedulingCriteria schedulingCriteria = scheduler.buildRequest(mapper);
		GraphPlotter scheduleCostPlotter = new GraphPlotter("schedule cost", "generation", "wieghted cost");
		GraphPlotter travelCostPlotter = new GraphPlotter("travel cost", "generation", "travelled distance");
		GraphPlotter delayCostPlotter = new GraphPlotter("delay cost", "generation", "delay cost");
		GraphPlotter overtimeCostPlotter = new GraphPlotter("overtime cost", "generation", "overtime cost");

		Schedule schedule1 = null;

		// creating new immutable schedulecriteria for feeding into algorithm
		schedule1 = scheduler.optimizeScheduleAdaptive(schedulingCriteria, POPULATION_SIZE, ELITISM_RATE, TRAVEL_WEIGHT,
				SLA_WEIGHT, MIN_CROSSOVER_PROBABILITY, MAX_CROSSOVER_PROBABILITY, .1, BUFFER_DUE_TO_SKILL_LEVEL,
				MAX_SKILL_LEVEL, MAX_SLA, scheduleCostPlotter, travelCostPlotter, delayCostPlotter, overtimeCostPlotter,
				maxGenerationsWithUnchangedBestFitness, "1", 25);

		scheduler.printSchedule(mapper, schedule1);
	}

	public SchedulingCriteria buildRequest(ObjectMapper mapper)
			throws IOException, JsonParseException, JsonMappingException, JsonProcessingException {
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("ScheduleCriteria.json")) {
			SchedulingCriteriaVO scheduleCriteriaVO = mapper.readValue(is, SchedulingCriteriaVO.class);

			List<Worker> workers = new ArrayList<Worker>();
			List<Job> jobs = new ArrayList<Job>();

			List<WorkerVO> workersvo = scheduleCriteriaVO.getWorkers();
			List<JobVO> jobsvo = scheduleCriteriaVO.getJobs();

			// converting mutable workersVO to immutable workers
			for (WorkerVO wvo : workersvo) {
				List<AvailabilitySlot> availSlots = new ArrayList<AvailabilitySlot>();

				for (AvailabilitySlotVO availSlotsVO : wvo.getAvailabilitySlots()) {
					AvailabilitySlot availSlot = new AvailabilitySlot(availSlotsVO.getStartTime(),
							availSlotsVO.getEndTime());
					availSlots.add(availSlot);
				}

				Location loc = new Location(wvo.getLocation().getLatitude(), wvo.getLocation().getLongitude());
				Worker worker = new Worker(wvo.getSkillCodeLevelMap(), loc, availSlots);
				workers.add(worker);
			}

			// converting mutable jobsVO to immutable jobs
			for (JobVO jvo : jobsvo) {
				Location loc = new Location(jvo.getLocation().getLatitude(), jvo.getLocation().getLongitude());
				Job job = new Job(jvo.getSlaTime().getTime(), jvo.getExpectedCompletionDuration(), jvo.getPriority(),
						jvo.getSkillCodes(), loc);
				jobs.add(job);
			}

			SchedulingCriteria schedulingCriteria = new SchedulingCriteria(workers, jobs);

			return schedulingCriteria;
		}
	}

	public void printSchedule(ObjectMapper mapper, Schedule schedule)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, JsonProcessingException {
		Job job = null;
		Worker worker = null;
		Map<Worker, List<Job>> currentWorkerJobMap = new HashMap<>();
		Iterator<Job> itr = schedule.iterateJobs();
		while (itr.hasNext()) {
			job = itr.next();
			worker = schedule.getWorkerForJob(job);
			if (!currentWorkerJobMap.containsKey(worker)) {
				currentWorkerJobMap.put(worker, new ArrayList<>());
			}
			currentWorkerJobMap.get(worker).add(job);
		}

		// update worker time and travel.
		Map<Worker, List<Job>> modifiedWorkerJobMap = new HashMap<>();
		LocationDistanceCalculator locationDistanceCalculator = getLocationDistanceCalculator();
		Iterator<Worker> workerItr = currentWorkerJobMap.keySet().iterator();
		List<Job> jobsAnother = null;
		while (workerItr.hasNext()) {
			worker = workerItr.next();
			jobsAnother = currentWorkerJobMap.get(worker);
			double distanceTravelled = 0;
			Location currentLocationOfWorker = worker.getLocation();
			Location jobLocation = null;
			for (Job assignedJob : jobsAnother) {
				jobLocation = assignedJob.getLocation();
				distanceTravelled += locationDistanceCalculator.calculateDistance(currentLocationOfWorker, jobLocation);
				currentLocationOfWorker = jobLocation;
			}
			modifiedWorkerJobMap.put(new Worker(worker, currentLocationOfWorker, distanceTravelled), jobsAnother);
		}

		String jsonString3 = mapper.writeValueAsString(modifiedWorkerJobMap);
		System.out.println(jsonString3);
	}

	public Schedule optimizeScheduleAdaptive(SchedulingCriteria scheduleCriteria, int populationSize,
			double elitismRate, double travelWeight, double slaWeight, double minCrossoverProbability,
			double maxCrossoverProbability, double mutationProbability, double bufferDueToSkillLevel,
			double maxSkillLevel, double maxSLA, GraphPlotter scheduleCostPlotter, GraphPlotter distancePlotter,
			GraphPlotter delayPlotter, GraphPlotter overtimeCostPlotter, int maxGenerationsWithUnchangedBestFitness,
			String plotNameSuffix, int tournamentSize) {
		AdaptiveGAScheduleOptimizer adaptiveOptimizer = new AdaptiveGAScheduleOptimizer(populationSize, elitismRate,
				travelWeight, slaWeight, minCrossoverProbability, maxCrossoverProbability, mutationProbability,
				bufferDueToSkillLevel, maxSkillLevel, maxSLA, OVERTIME_WEIGHT, MAX_OVERTIME, tournamentSize);

		return adaptiveOptimizer.optimize(scheduleCriteria, scheduleCostPlotter, distancePlotter, delayPlotter,
				overtimeCostPlotter, maxGenerationsWithUnchangedBestFitness, plotNameSuffix);
	}

	public Schedule optimizeSchedule(SchedulingCriteria scheduleCriteria, int populationSize, double elitismRate,
			double travelWeight, double slaWeight, double crossoverProbability, double mutationProbability,
			double bufferDueToSkillLevel, double maxSkillLevel, double maxSLA, GraphPlotter scheduleCostPlotter,
			GraphPlotter distancePlotter, GraphPlotter delayPlotter, GraphPlotter overtimeCostPlotter,
			int maxGenerationsWithUnchangedBestFitness, String plotNameSuffix, int crossoverPeriodicInterval,
			int mutationPeriodicInterval, double overtimeWeight, double maxOvertime) {
		GAScheduleOptimizer optimizer = new GAScheduleOptimizer(populationSize, elitismRate, travelWeight, slaWeight,
				crossoverProbability, mutationProbability, bufferDueToSkillLevel, maxSkillLevel, maxSLA, overtimeWeight,
				maxOvertime);

		return optimizer.optimize(scheduleCriteria, scheduleCostPlotter, distancePlotter, delayPlotter,
				overtimeCostPlotter, maxGenerationsWithUnchangedBestFitness, plotNameSuffix);
	}

	private static LocationDistanceCalculator getLocationDistanceCalculator()
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		String distanceCalculatorClassName = System.getProperty(Constants.DISTANCE_CALCULATOR_VM_ARG);
		if (distanceCalculatorClassName != null) {
			Class distanceCalculatorClass = Class.forName(distanceCalculatorClassName);
			if (!LocationDistanceCalculator.class.isAssignableFrom(distanceCalculatorClass)) {
				throw new RuntimeException(
						"Provided class does not implement " + LocationDistanceCalculator.class.getCanonicalName());
			}
			return (LocationDistanceCalculator) distanceCalculatorClass.getDeclaredConstructor().newInstance();
		} else {
			return MatrixBasedLocationDistanceCalculator.getInstance();
		}
	}
}
