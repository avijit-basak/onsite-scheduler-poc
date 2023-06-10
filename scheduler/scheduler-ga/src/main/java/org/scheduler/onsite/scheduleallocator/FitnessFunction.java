package org.scheduler.onsite.scheduleallocator;

public interface FitnessFunction {

	public double evaluate(ScheduleChromosome scheduleChromosome);

	public double evaluate(ScheduleChromosome scheduleChromosome, boolean enablePrint);
}
