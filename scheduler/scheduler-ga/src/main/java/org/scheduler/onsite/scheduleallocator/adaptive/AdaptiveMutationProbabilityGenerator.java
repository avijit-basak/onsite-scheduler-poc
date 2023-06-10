package org.scheduler.onsite.scheduleallocator.adaptive;

import org.scheduler.onsite.scheduleallocator.ScheduleChromosome;

public interface AdaptiveMutationProbabilityGenerator {

	public double generate(ScheduleChromosome chromosome, SimulationPopulation population);

}
