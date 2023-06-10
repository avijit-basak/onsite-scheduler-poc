package org.scheduler.onsite.scheduleallocator.adaptive;

import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.FastMath;
import org.scheduler.onsite.scheduleallocator.ScheduleChromosome;

public class SimulationPopulation extends ElitisticListPopulation {

	public SimulationPopulation(int populationLimit, double elitismRate)
			throws NotPositiveException, OutOfRangeException {
		super(populationLimit, elitismRate);
	}

	public SimulationPopulation(List<Chromosome> chromosomes, int populationLimit, double elitismRate) {
		super(chromosomes, populationLimit, elitismRate);
	}

	public double getAverageFitness() {
		return getFitnessStats().getMean();
	}

	public Chromosome getWorstChromosome() {
		List<Chromosome> chromosomes = getChromosomeList();
		Chromosome poorestChromosome = (Chromosome) chromosomes.get(0);
		for (Chromosome chromosome : chromosomes) {
			if (chromosome.compareTo(poorestChromosome) < 0) {
				poorestChromosome = chromosome;
			}
		}
		return poorestChromosome;
	}

	public SimulationPopulation nextGeneration() {
		SimulationPopulation nextGeneration = new SimulationPopulation(getPopulationLimit(), getElitismRate());
		List<Chromosome> oldChromosomes = getChromosomeList();
		Collections.sort(oldChromosomes);
		int boundIndex = (int) FastMath.ceil((1.0D - getElitismRate()) * oldChromosomes.size());
		for (int i = boundIndex; i < oldChromosomes.size(); i++) {
			nextGeneration.addChromosome((Chromosome) oldChromosomes.get(i));
		}
		nextGeneration.setChromosomeRanks();

		return nextGeneration;
	}

	public DescriptiveStatistics getFitnessStats() {
		List<Chromosome> chromosomes = getChromosomes();
		double[] fitnesses = new double[chromosomes.size()];
		int i = 0;
		for (Chromosome chromosome : chromosomes) {
			fitnesses[i++] = chromosome.fitness();
		}
		return new DescriptiveStatistics(fitnesses);
	}

	public boolean isUniform() {
		return getFitnessStats().getMax() == getFitnessStats().getMin();
	}

	public void setChromosomeRanks() {
		List<Chromosome> chromosomes = getChromosomeList();
		Collections.sort(chromosomes);
		for (int i = 0; i < getPopulationSize(); i++) {
			((ScheduleChromosome) chromosomes.get(i)).setRank(i + 1);
		}
	}

	@Override
	public String toString() {
		StringBuilder populationString = new StringBuilder();
		populationString.append("\r\n");
		for (Chromosome chromosome : getChromosomeList()) {
			populationString.append(chromosome.toString()).append("\r\n");
		}
		return populationString.toString();
	}

}