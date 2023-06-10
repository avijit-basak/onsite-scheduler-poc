package org.scheduler.onsite.scheduleallocator.adaptive;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.scheduler.onsite.model.Jobs;
import org.scheduler.onsite.model.Worker;
import org.scheduler.onsite.scheduleallocator.ScheduleChromosome;
import org.scheduler.onsite.utils.JobSkillToWorkerMap;

public class AdaptiveScheduleMutation extends AbstractAdaptiveMutationPolicy {

	private JobSkillToWorkerMap jobSkillToWorkerMap;

	private Jobs jobs;

	public AdaptiveScheduleMutation(JobSkillToWorkerMap jobSkillToWorkerMap, Jobs jobs) {
		this.jobSkillToWorkerMap = jobSkillToWorkerMap;
		this.jobs = jobs;
	}

	public Chromosome mutate(Chromosome original, double mutationProbability) throws MathIllegalArgumentException {
		if (!original.getClass().isAssignableFrom(ScheduleChromosome.class)) {
			throw new IllegalArgumentException();
		}
		ScheduleChromosome chromosome = (ScheduleChromosome) original;
		List<Worker> allocations = chromosome.getWorkers();
		List<Worker> mutatedAllocations = new ArrayList<Worker>(allocations);
		final int alleleCount = chromosome.getLength();

		final int noOfMutation = (int) (allocations.size() * mutationProbability);

		int[] indexes = new int[noOfMutation];
		for (int i = 0; i < noOfMutation; i++) {
			indexes[i] = GeneticAlgorithm.getRandomGenerator().nextInt(alleleCount);
		}
		// mutate the corresponding alleles.
		for (int index : indexes) {
			final List<Worker> workers = jobSkillToWorkerMap.getWorkers(jobs.get(index).getSkillCodes());
			final Worker selectedWorker = workers.get(GeneticAlgorithm.getRandomGenerator().nextInt(workers.size()));
			mutatedAllocations.set(index, selectedWorker);
		}

		return new ScheduleChromosome(chromosome.getRepresentation(), mutatedAllocations,
				chromosome.getFitnessFunction());
	}

}
