package org.scheduler.onsite.scheduleallocator.adaptive;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ChromosomePair;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.StoppingCondition;
import org.apache.commons.math3.genetics.TournamentSelection;
import org.scheduler.onsite.scheduleallocator.ScheduleChromosome;

public class AdaptiveSchedulerGeneticAlgorithm extends GeneticAlgorithm {

	private int generationsEvolved;

	private AdaptiveMutationPolicy adaptiveMutationPolicy;

	private AdaptiveCrossoverPolicy adaptiveCrossoverPolicy;

	private AdaptiveMutationProbabilityGenerator mutationProbabilityGenerator;

	private AdaptiveCrossoverProbabilityGenerator crossoverProbabilityGenerator;

	public AdaptiveSchedulerGeneticAlgorithm(AdaptiveCrossoverPolicy adaptiveCrossoverPolicy,
			AdaptiveCrossoverProbabilityGenerator crossoverProbabilityGenerator,
			AdaptiveMutationPolicy adaptiveMutationPolicy,
			AdaptiveMutationProbabilityGenerator mutationProbabilityGenerator, int tournamentSize)
			throws OutOfRangeException {
		super(adaptiveCrossoverPolicy, 0.0, adaptiveMutationPolicy, 0.0, new TournamentSelection(tournamentSize));
		this.crossoverProbabilityGenerator = crossoverProbabilityGenerator;
		this.mutationProbabilityGenerator = mutationProbabilityGenerator;
		this.adaptiveCrossoverPolicy = adaptiveCrossoverPolicy;
		this.adaptiveMutationPolicy = adaptiveMutationPolicy;
	}

	@Override
	public Population evolve(Population initial, StoppingCondition condition) {
		Population current = initial;
		generationsEvolved = 0;
		while (!condition.isSatisfied(current)) {
			current = nextGeneration(current);
			generationsEvolved += 1;
		}
		return current;
	}

	@Override
	public Population nextGeneration(Population current) {

		SimulationPopulation nextGeneration = (SimulationPopulation) current.nextGeneration();
		((SimulationPopulation) current).setChromosomeRanks();
		while (nextGeneration.getPopulationSize() < nextGeneration.getPopulationLimit()) {
			ChromosomePair pair = getSelectionPolicy().select(current);
			pair = adaptiveCrossoverPolicy.crossover(pair.getFirst(), pair.getSecond(),
					crossoverProbabilityGenerator.generate(pair.getFirst(), pair.getSecond(), nextGeneration));
			nextGeneration.addChromosome(pair.getFirst());

			if (nextGeneration.getPopulationSize() < nextGeneration.getPopulationLimit()) {
				nextGeneration.addChromosome(pair.getSecond());
			}
		}

		nextGeneration.setChromosomeRanks();
		SimulationPopulation mutatedNextGeneration = new SimulationPopulation(nextGeneration.getPopulationLimit(),
				nextGeneration.getElitismRate());
		for (Chromosome chromosome : nextGeneration) {
			mutatedNextGeneration.addChromosome(adaptiveMutationPolicy.mutate(chromosome,
					mutationProbabilityGenerator.generate((ScheduleChromosome) chromosome, nextGeneration)));
		}

		return mutatedNextGeneration;
	}

	@Override
	public int getGenerationsEvolved() {
		return generationsEvolved;
	}

}