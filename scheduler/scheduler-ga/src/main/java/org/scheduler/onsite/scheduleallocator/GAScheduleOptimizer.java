package org.scheduler.onsite.scheduleallocator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.ListPopulation;
import org.apache.commons.math3.genetics.NPointCrossover;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.TournamentSelection;
import org.scheduler.onsite.model.Job;
import org.scheduler.onsite.model.Jobs;
import org.scheduler.onsite.model.Schedule;
import org.scheduler.onsite.model.ScheduleImpl;
import org.scheduler.onsite.model.ScheduleOptimizer;
import org.scheduler.onsite.model.SchedulingCriteria;
import org.scheduler.onsite.model.Worker;
import org.scheduler.onsite.utils.GraphPlotter;
import org.scheduler.onsite.utils.JobSkillToWorkerMap;

public class GAScheduleOptimizer implements ScheduleOptimizer {

	private final int populationLimit;

	private final double elitismRate;

	private final double travelWeight;

	private final double slaWeight;

	private final double crossoverProbability;

	private final double mutationProbability;

	private final double bufferDueToSkillLevel;

	private final double maxSkillLevel;

	private final double maxSla;

	private final double maxOvertime;

	private final double overtimeWeight;

	public GAScheduleOptimizer(int populationLimit, double elitismRate, double travelWeight, double slaWeight,
			double crossoverProbability, double mutationProbability, double bufferDueToSkillLevel, double maxSkillLevel,
			double maxSla, double overtimeWeight, double maxOvertime) {
		this.populationLimit = populationLimit;
		this.elitismRate = elitismRate;
		this.travelWeight = travelWeight;
		this.slaWeight = slaWeight;
		this.crossoverProbability = crossoverProbability;
		this.mutationProbability = mutationProbability;
		this.bufferDueToSkillLevel = bufferDueToSkillLevel;
		this.maxSkillLevel = maxSkillLevel;
		this.maxSla = maxSla;
		this.overtimeWeight = overtimeWeight;
		this.maxOvertime = maxOvertime;
	}

	public Schedule optimize(SchedulingCriteria criteria, GraphPlotter scheduleCostPlotter,
			GraphPlotter distancePlotter, GraphPlotter delayPlotter, GraphPlotter overtimeCostPlotter,
			int maxGenerationsWithUnchangedBestFitness, String plotNameSuffix) {
		final JobSkillToWorkerMap jobSkillToWorkerMap = new JobSkillToWorkerMap(criteria.getWorkers());
		final Jobs jobs = Jobs.newJobs(criteria.getJobs());
		final SchedulerGeneticAlgorithm schedulerGeneticAlgorithm = new SchedulerGeneticAlgorithm(
				new ScheduleCrossover(), crossoverProbability,
				new ScheduleMutation(jobSkillToWorkerMap, mutationProbability, jobs), mutationProbability,
				new TournamentSelection(2));
		final Population initialPopulation = getInitialPopulation(criteria, jobSkillToWorkerMap, jobs);

		final Population convergedPopulation = schedulerGeneticAlgorithm.evolve(initialPopulation,
				new UnchangedBestFitness(maxGenerationsWithUnchangedBestFitness, scheduleCostPlotter, distancePlotter,
						delayPlotter, overtimeCostPlotter, plotNameSuffix));
//		final Population convergedPopulation = schedulerGeneticAlgorithm.evolve(initialPopulation,
//				new FixedGenerationCountStoppingCondition(scheduleCostPlotter, distancePlotter,
//						delayPlotter, plotNameSuffix));
		final ScheduleChromosome scheduleChromosome = (ScheduleChromosome) convergedPopulation.getFittestChromosome();
		final List<Worker> assignedWorkers = scheduleChromosome.getWorkers();
		final List<Integer> jobSequence = scheduleChromosome.decode(getJobIds(jobs.getJobcount()));

		return new ScheduleImpl(criteria.getJobs(), jobSequence, assignedWorkers);
	}

	public Schedule optimizePSO(SchedulingCriteria criteria, GraphPlotter scheduleCostPlotter,
			GraphPlotter distancePlotter, GraphPlotter delayPlotter, GraphPlotter overtimeCostPlotter,
			int maxGenerationsWithUnchangedBestFitness, String plotNameSuffix, int crossoverPeriodicInterval,
			int mutationPeriodicInterval) {
		final JobSkillToWorkerMap jobSkillToWorkerMap = new JobSkillToWorkerMap(criteria.getWorkers());
		final Jobs jobs = Jobs.newJobs(criteria.getJobs());
		final SchedulerGeneticAlgorithm schedulerGeneticAlgorithm = new SchedulerGeneticAlgorithm(
				new NPointCrossover<>(2), crossoverProbability,
				new ScheduleMutation(jobSkillToWorkerMap, mutationProbability, jobs), mutationProbability,
				new TournamentSelection(2));
		final Population initialPopulation = getInitialPopulation(criteria, jobSkillToWorkerMap, jobs);

		final Population convergedPopulation = schedulerGeneticAlgorithm.evolve(initialPopulation,
				new UnchangedBestFitness(maxGenerationsWithUnchangedBestFitness, scheduleCostPlotter, distancePlotter,
						delayPlotter, overtimeCostPlotter, plotNameSuffix));
//		final Population convergedPopulation = schedulerGeneticAlgorithm.evolve(initialPopulation,
//				new FixedGenerationCountStoppingCondition(scheduleCostPlotter, distancePlotter,
//						delayPlotter, plotNameSuffix));
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
		final ListPopulation schedulePopulation = new ElitisticListPopulation(populationLimit, elitismRate);
		for (int i = 0; i < populationLimit; i++) {
			final List<Worker> workers = new ArrayList<Worker>();
			for (Job job : jobs) {
				final List<Worker> jobWorkers = jobSkillToWorkerMap.getWorkers(job.getSkillCodes());

				/*
				 * Add the logic of dynamic probability update for worker selection.
				 */
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
