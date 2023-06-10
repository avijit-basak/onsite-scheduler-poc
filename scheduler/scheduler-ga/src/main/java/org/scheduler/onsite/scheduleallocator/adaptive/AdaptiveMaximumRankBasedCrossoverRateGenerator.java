package org.scheduler.onsite.scheduleallocator.adaptive;

import org.apache.commons.math3.genetics.Chromosome;
import org.scheduler.onsite.scheduleallocator.ScheduleChromosome;

public class AdaptiveMaximumRankBasedCrossoverRateGenerator implements AdaptiveCrossoverProbabilityGenerator {

	/** minimum crossover probability. **/
	private final double minimumProbability;

	/** maximum crossover probability. **/
	private final double maximumProbability;

	public AdaptiveMaximumRankBasedCrossoverRateGenerator(double minimumRate, double maximumRate) {
		this.maximumProbability = maximumRate;
		this.minimumProbability = minimumRate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double generate(Chromosome first, Chromosome second, SimulationPopulation simulationPopulation) {
		if (minimumProbability == maximumProbability) {
			return minimumProbability;
		}
		ScheduleChromosome scheduleChromosome1 = (ScheduleChromosome) first;
		ScheduleChromosome scheduleChromosome2 = (ScheduleChromosome) second;
		final int rank = Math.max(scheduleChromosome1.getRank(), scheduleChromosome2.getRank());
		double populationSize = simulationPopulation.getPopulationSize();
		double crossoverProbability = minimumProbability
				+ (maximumProbability - minimumProbability) * (1.0 - (rank - 1) / (populationSize - 1));
		return crossoverProbability;
	}

}
