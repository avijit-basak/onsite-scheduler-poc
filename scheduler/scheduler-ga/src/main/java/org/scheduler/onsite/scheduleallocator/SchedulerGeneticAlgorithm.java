package org.scheduler.onsite.scheduleallocator;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.genetics.ChromosomePair;
import org.apache.commons.math3.genetics.CrossoverPolicy;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.MutationPolicy;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.SelectionPolicy;
import org.apache.commons.math3.random.RandomGenerator;

public class SchedulerGeneticAlgorithm extends GeneticAlgorithm {

	private int generationCount = 0;

	public SchedulerGeneticAlgorithm(CrossoverPolicy crossoverPolicy, double crossoverRate,
			MutationPolicy mutationPolicy, double mutationRate, SelectionPolicy selectionPolicy)
			throws OutOfRangeException {
		super(crossoverPolicy, crossoverRate, mutationPolicy, mutationRate, selectionPolicy);
	}

	@Override
	public Population nextGeneration(Population current) {
//		System.out.println("Generation : " + generationCount);
//		System.out.println("the fittest chromosome: " + current.getFittestChromosome());
		generationCount = generationCount + 1;

		Population nextGeneration = current.nextGeneration();
		RandomGenerator randGen = getRandomGenerator();

		while (nextGeneration.getPopulationSize() < nextGeneration.getPopulationLimit()) {
			// select parent chromosomes
			ChromosomePair pair = getSelectionPolicy().select(current);

			// crossover
			if (randGen.nextDouble() < getCrossoverRate()) {
				// apply crossover policy to create two offspring
				pair = getCrossoverPolicy().crossover(pair.getFirst(), pair.getSecond());
			}

			// apply mutation policy to the chromosomes
			pair = new ChromosomePair(getMutationPolicy().mutate(pair.getFirst()),
					getMutationPolicy().mutate(pair.getSecond()));

			// add the first chromosome to the population
			nextGeneration.addChromosome(pair.getFirst());
			// is there still a place for the second chromosome?
			if (nextGeneration.getPopulationSize() < nextGeneration.getPopulationLimit()) {
				// add the second chromosome to the population
				nextGeneration.addChromosome(pair.getSecond());
			}
		}

		return nextGeneration;
	}

}
