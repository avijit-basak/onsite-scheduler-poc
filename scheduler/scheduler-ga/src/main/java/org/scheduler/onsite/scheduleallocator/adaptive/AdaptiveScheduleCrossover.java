package org.scheduler.onsite.scheduleallocator.adaptive;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ChromosomePair;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.scheduler.onsite.scheduleallocator.ScheduleChromosome;

public class AdaptiveScheduleCrossover extends AbstractAdaptiveCrossoverPolicy {

	public ChromosomePair crossover(Chromosome first, Chromosome second, double probability)
			throws MathIllegalArgumentException {

		RandomGenerator randomGenerator = new JDKRandomGenerator();
		if (randomGenerator.nextDouble() >= probability) {
			return new ChromosomePair(first, second);
		}
		if (!ScheduleChromosome.class.isAssignableFrom(first.getClass())
				|| !ScheduleChromosome.class.isAssignableFrom(second.getClass())) {
			throw new IllegalArgumentException();
		}
		ScheduleChromosome firstChromosome = (ScheduleChromosome) first;
		ScheduleChromosome secondChromosome = (ScheduleChromosome) second;

		final List<Double> parent1Rep = firstChromosome.getRepresentation();
		final List<Double> parent2Rep = secondChromosome.getRepresentation();

		final int length = parent1Rep.size();

		final int crossoverIndex = 1 + (GeneticAlgorithm.getRandomGenerator().nextInt(length - 1));

		List<Double> child1Rep = new ArrayList<>();
		List<Double> child2Rep = new ArrayList<>();

		// copy the first part
		for (int i = 0; i < crossoverIndex; i++) {
			child1Rep.add(parent1Rep.get(i));
			child2Rep.add(parent2Rep.get(i));
		}
		// and switch the second part
		for (int i = crossoverIndex; i < length; i++) {
			child1Rep.add(parent2Rep.get(i));
			child2Rep.add(parent1Rep.get(i));
		}

		return new ChromosomePair(firstChromosome.newFixedLengthChromosome(child1Rep),
				secondChromosome.newFixedLengthChromosome(child2Rep));
	}

}
