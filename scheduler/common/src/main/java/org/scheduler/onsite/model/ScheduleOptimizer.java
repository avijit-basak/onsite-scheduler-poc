package org.scheduler.onsite.model;

import org.scheduler.onsite.utils.GraphPlotter;

public interface ScheduleOptimizer {

	public Schedule optimize(SchedulingCriteria criteria, GraphPlotter scheduleCostPlotter,
			GraphPlotter distancePlotter, GraphPlotter delayPlotter, int maxGenerationsWithUnchangedBestFitness,
			String plotNameSuffix);

	public Schedule optimize(SchedulingCriteria criteria, GraphPlotter scheduleCostPlotter,
			GraphPlotter distancePlotter, GraphPlotter delayPlotter, GraphPlotter overtimeCostPlotter,
			int maxGenerationsWithUnchangedBestFitness, String plotNameSuffix);

}
