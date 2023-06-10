package org.scheduler.onsite.scheduleallocator.adaptive;

import org.scheduler.onsite.scheduleallocator.ScheduleChromosome;

public class AdaptiveRankBasedMutationProbabilityGenerator extends AbstractAdaptiveMutationProbailityGenerator {

	public AdaptiveRankBasedMutationProbabilityGenerator(double maximumMutationProbability) {
		super(maximumMutationProbability);
	}

	@Override
	public double generate(ScheduleChromosome chromosome, SimulationPopulation population) {
		double rank = chromosome.getRank();
		double populationSize = population.getPopulationSize();
		double mutationProbability = getMaximumMutationProbability() * (1.0 - (rank - 1) / (populationSize - 1));

		return mutationProbability;
	}

}