package org.scheduler.onsite.scheduleallocator.adaptive;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.Population;
import org.scheduler.onsite.model.Job;
import org.scheduler.onsite.model.Jobs;
import org.scheduler.onsite.model.Schedule;
import org.scheduler.onsite.model.ScheduleImpl;
import org.scheduler.onsite.model.ScheduleOptimizer;
import org.scheduler.onsite.model.SchedulingCriteria;
import org.scheduler.onsite.model.Worker;
import org.scheduler.onsite.scheduleallocator.FitnessFunction;
import org.scheduler.onsite.scheduleallocator.FixedGenerationCountStoppingCondition;
import org.scheduler.onsite.scheduleallocator.ScheduleChromosome;
import org.scheduler.onsite.scheduleallocator.ScheduleFitnessFunction;
import org.scheduler.onsite.scheduleallocator.UnchangedBestFitness;
import org.scheduler.onsite.utils.GraphPlotter;
import org.scheduler.onsite.utils.JobSkillToWorkerMap;

public class AdaptiveGAScheduleOptimizer implements ScheduleOptimizer {

	private final int populationLimit;

	private final double elitismRate;

	private final double travelWeight;

	private final double slaWeight;

	private final double minCrossoverProbability;

	private final double maxCrossoverProbability;

	private final double maximumMutationProbability;

	private final double bufferDueToSkillLevel;

	private final double maxSkillLevel;

	private final double maxSla;

	private final double maxOvertime;

	private final double overtimeWeight;

	private final int tournamentSize;

	public AdaptiveGAScheduleOptimizer(int populationLimit, double elitismRate, double travelWeight, double slaWeight,
			double minCrossoverProbability, double maxCrossoverProbability, double maximumMutationProbability,
			double bufferDueToSkillLevel, double maxSkillLevel, double maxSla, double overtimeWeight,
			double maxOvertime, int tournamentSize) {
		this.populationLimit = populationLimit;
		this.elitismRate = elitismRate;
		this.travelWeight = travelWeight;
		this.slaWeight = slaWeight;
		this.minCrossoverProbability = minCrossoverProbability;
		this.maxCrossoverProbability = maxCrossoverProbability;
		this.maximumMutationProbability = maximumMutationProbability;
		this.bufferDueToSkillLevel = bufferDueToSkillLevel;
		this.maxSkillLevel = maxSkillLevel;
		this.maxSla = maxSla;
		this.overtimeWeight = overtimeWeight;
		this.maxOvertime = maxOvertime;
		this.tournamentSize = tournamentSize;
	}

	public Schedule optimize(SchedulingCriteria criteria, GraphPlotter scheduleCostPlotter,
			GraphPlotter distancePlotter, GraphPlotter delayPlotter, GraphPlotter overtimeCostPlotter,
			int maxGenerationsWithUnchangedBestFitness, String plotNameSuffix) {
		final JobSkillToWorkerMap jobSkillToWorkerMap = new JobSkillToWorkerMap(criteria.getWorkers());
		final Jobs jobs = Jobs.newJobs(criteria.getJobs());
		final AdaptiveSchedulerGeneticAlgorithm schedulerGeneticAlgorithm = new AdaptiveSchedulerGeneticAlgorithm(
				new AdaptiveScheduleCrossover(),
				new AdaptiveAverageRankBasedCrossoverRateGenerator(minCrossoverProbability, maxCrossoverProbability),
				new AdaptiveScheduleMutation(jobSkillToWorkerMap, jobs),
				new AdaptiveRankBasedMutationProbabilityGenerator(maximumMutationProbability), tournamentSize);
		final Population initialPopulation = getInitialPopulation(criteria, jobSkillToWorkerMap, jobs);

//		final Population convergedPopulation = schedulerGeneticAlgorithm.evolve(initialPopulation,
//				new UnchangedBestFitness(maxGenerationsWithUnchangedBestFitness, scheduleCostPlotter, distancePlotter,
//						delayPlotter, overtimeCostPlotter, plotNameSuffix));
		final Population convergedPopulation = schedulerGeneticAlgorithm.evolve(initialPopulation,
				new FixedGenerationCountStoppingCondition(scheduleCostPlotter, distancePlotter, delayPlotter,
						overtimeCostPlotter, plotNameSuffix));
		final ScheduleChromosome scheduleChromosome = (ScheduleChromosome) convergedPopulation.getFittestChromosome();
		final List<Worker> assignedWorkers = scheduleChromosome.getWorkers();
		final List<Integer> jobSequence = scheduleChromosome.decode(getJobIds(jobs.getJobcount()));

		return new ScheduleImpl(criteria.getJobs(), jobSequence, assignedWorkers);
	}

	private List<Integer> getJobIds(int length) {
		List<Integer> jobIds = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			jobIds.add(i);
		}
		return jobIds;
	}

	private Population getInitialPopulation(SchedulingCriteria criteria, JobSkillToWorkerMap jobSkillToWorkerMap,
			Jobs jobs) {
		final FitnessFunction fitnessFunction = new ScheduleFitnessFunction(jobs, travelWeight, slaWeight,
				bufferDueToSkillLevel, maxSkillLevel, maxSla, overtimeWeight, maxOvertime);
		final SimulationPopulation schedulePopulation = new SimulationPopulation(populationLimit, elitismRate);
		for (int i = 0; i < populationLimit; i++) {
			final List<Worker> workers = new ArrayList<Worker>();
			for (Job job : jobs) {
				final List<Worker> jobWorkers = jobSkillToWorkerMap.getWorkers(job.getSkillCodes());
				workers.add(jobWorkers.get(GeneticAlgorithm.getRandomGenerator().nextInt(jobWorkers.size())));
			}
			schedulePopulation.addChromosome(new ScheduleChromosome(
					ScheduleChromosome.randomPermutation(jobs.getJobcount()), workers, fitnessFunction));
		}
		return schedulePopulation;
	}

	@Override
	public Schedule optimize(SchedulingCriteria criteria, GraphPlotter scheduleCostPlotter,
			GraphPlotter distancePlotter, GraphPlotter delayPlotter, int maxGenerationsWithUnchangedBestFitness,
			String plotNameSuffix) {
		// TODO Auto-generated method stub
		return null;
	}

}
