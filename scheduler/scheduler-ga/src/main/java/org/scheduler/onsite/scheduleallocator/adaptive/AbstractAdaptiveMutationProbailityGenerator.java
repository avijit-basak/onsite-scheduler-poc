package org.scheduler.onsite.scheduleallocator.adaptive;

public abstract class AbstractAdaptiveMutationProbailityGenerator implements AdaptiveMutationProbabilityGenerator {

	private double maximumMutationProbability;

	protected AbstractAdaptiveMutationProbailityGenerator(double maximumMutationProbability) {
		this.maximumMutationProbability = maximumMutationProbability;
	}

	public double getMaximumMutationProbability() {
		return maximumMutationProbability;
	}

	public void setMaximumMutationProbability(double maximumMutationProbability) {
		this.maximumMutationProbability = maximumMutationProbability;
	}

}
