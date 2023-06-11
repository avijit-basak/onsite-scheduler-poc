package org.scheduler.onsite.scheduleallocator;

import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.StoppingCondition;
import org.scheduler.onsite.utils.GraphPlotter;

public class FixedGenerationCountStoppingCondition implements StoppingCondition {

	/** best fitness of previous generation. **/
//	private double lastBestFitness = Double.MIN_VALUE;

	private GraphPlotter scheduleCostPlotter;

	private GraphPlotter distanceCostPlotter;

	private GraphPlotter delayCostPlotter;

	private GraphPlotter overtimeCostPlotter;

	private String plotNameSuffix;

	/**
	 * The configured number of generations for which optimization process will
	 * continue with unchanged best fitness value.
	 **/
	private final int MAX_GEN_COUNT = 1000;

	/** Number of generations the best fitness value has not been changed. **/
	private int generationsHavingUnchangedBestFitness;

	private int generationCount;

	/**
	 * @param maxGenerationsWithUnchangedBestFitness maximum number of generations
	 *                                               with unchanged best fitness
	 * @param distanceCostPlotter                    TODO
	 * @param delayCostPlotter                       TODO
	 */
	public FixedGenerationCountStoppingCondition(GraphPlotter scheduleCostPlotter, GraphPlotter distanceCostPlotter,
			GraphPlotter delayCostPlotter, GraphPlotter overtimeCostPlotter, String plotNameSuffix) {
		this.scheduleCostPlotter = scheduleCostPlotter;
		this.distanceCostPlotter = distanceCostPlotter;
		this.delayCostPlotter = delayCostPlotter;
		this.overtimeCostPlotter = overtimeCostPlotter;
		this.plotNameSuffix = plotNameSuffix;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSatisfied(Population population) {
		ScheduleChromosome chromosome = (ScheduleChromosome) population.getFittestChromosome();
		ScheduleFitnessFunction scheduleFitnessFunction = (ScheduleFitnessFunction) chromosome.getFitnessFunction();

		generationCount++;
		final double currentBestFitness = population.getFittestChromosome().getFitness();
		if (currentBestFitness != -Double.MAX_VALUE) {
			scheduleCostPlotter.addDataPoint("schedule cost-" + plotNameSuffix, generationCount,
					Math.abs(currentBestFitness));
			distanceCostPlotter.addDataPoint("distance-" + plotNameSuffix, generationCount,
					Math.abs(scheduleFitnessFunction.evaluateTravelCost(chromosome, false)));
			delayCostPlotter.addDataPoint("delay-" + plotNameSuffix, generationCount,
					Math.abs(scheduleFitnessFunction.evaluateDelayCost(chromosome)));
			overtimeCostPlotter.addDataPoint("overtime-" + plotNameSuffix, generationCount,
					Math.abs(scheduleFitnessFunction.evaluateOvertimeCost(chromosome, false)));
		}
		if (generationCount == MAX_GEN_COUNT) {
			return true;
		}
		return false;
	}

}
