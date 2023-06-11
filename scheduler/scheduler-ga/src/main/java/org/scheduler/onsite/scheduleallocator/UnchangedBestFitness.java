/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.scheduler.onsite.scheduleallocator;

import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.StoppingCondition;
import org.scheduler.onsite.utils.GraphPlotter;

/**
 * This class represents a stopping condition based on best fitness value.
 * Convergence will be stopped once best fitness remains unchanged for
 * predefined number of generations.
 * 
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public class UnchangedBestFitness implements StoppingCondition {

	/** best fitness of previous generation. **/
	private double lastBestFitness = Double.MIN_VALUE;

	private GraphPlotter scheduleCostPlotter;

	private GraphPlotter distanceCostPlotter;

	private GraphPlotter delayCostPlotter;

	private GraphPlotter overtimeCostPlotter;

	private String plotNameSuffix;

	/**
	 * The configured number of generations for which optimization process will
	 * continue with unchanged best fitness value.
	 **/
	private final int maxGenerationsWithUnchangedBestFitness;

	/** Number of generations the best fitness value has not been changed. **/
	private int generationsHavingUnchangedBestFitness;

	private int generationCount;

	/**
	 * @param maxGenerationsWithUnchangedBestFitness maximum number of generations
	 *                                               with unchanged best fitness
	 * @param distanceCostPlotter                    TODO
	 * @param delayCostPlotter                       TODO
	 */
	public UnchangedBestFitness(final int maxGenerationsWithUnchangedBestFitness, GraphPlotter scheduleCostPlotter,
			GraphPlotter distanceCostPlotter, GraphPlotter delayCostPlotter, GraphPlotter overtimeCostPlotter,
			String plotNameSuffix) {
		this.maxGenerationsWithUnchangedBestFitness = maxGenerationsWithUnchangedBestFitness;
		this.scheduleCostPlotter = scheduleCostPlotter;
		this.distanceCostPlotter = distanceCostPlotter;
		this.delayCostPlotter = delayCostPlotter;
		this.plotNameSuffix = plotNameSuffix;
		this.overtimeCostPlotter = overtimeCostPlotter;
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

		if (lastBestFitness == currentBestFitness) {
			generationsHavingUnchangedBestFitness++;
			if (generationsHavingUnchangedBestFitness == maxGenerationsWithUnchangedBestFitness) {
				return true;
			}
		} else {
			this.generationsHavingUnchangedBestFitness = 0;
			lastBestFitness = currentBestFitness;
		}

		return false;
	}
}
